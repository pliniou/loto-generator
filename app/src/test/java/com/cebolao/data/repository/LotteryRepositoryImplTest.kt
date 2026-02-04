package com.cebolao.data.repository

import com.cebolao.data.local.JsonFileStore
import com.cebolao.data.local.room.dao.LotteryDao
import com.cebolao.data.local.room.entity.ContestEntity
import com.cebolao.data.remote.api.LotteryApi
import com.cebolao.data.remote.dto.ContestDto
import com.cebolao.domain.model.Game
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.result.AppResult
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LotteryRepositoryImplTest {
    private val lotteryDao: LotteryDao = mockk(relaxed = true)
    private val jsonFileStore: JsonFileStore = mockk(relaxed = true)
    private val lotteryApi: LotteryApi = mockk(relaxed = true)

    private lateinit var repository: LotteryRepositoryImpl

    @Before
    fun setup() {
        every { jsonFileStore.exists() } returns false
        every { lotteryDao.observeAllGames() } returns flowOf(emptyList())
        every { lotteryDao.observeGamesByType(any()) } returns flowOf(emptyList())
        every { lotteryDao.observeContests(any()) } returns flowOf(emptyList())
        every { lotteryDao.observeLatestContest(any()) } returns flowOf(null)
        repository = LotteryRepositoryImpl(lotteryDao, jsonFileStore, lotteryApi, UnconfinedTestDispatcher())
    }

    @Test
    fun `saveGame delega para o DAO e retorna Success`() =
        runTest {
            val game =
                Game(
                    id = "game1",
                    lotteryType = LotteryType.MEGA_SENA,
                    numbers = listOf(1, 2, 3, 4, 5, 6),
                    createdAt = 123456L,
                )

            coEvery { lotteryDao.insertGame(any()) } just Runs

            val result = repository.saveGame(game)
            assertTrue(result is AppResult.Success)

            coVerify(exactly = 1) {
                lotteryDao.insertGame(
                    match {
                        it.id == "game1" &&
                            it.lotteryType == LotteryType.MEGA_SENA &&
                            it.numbers == listOf("1", "2", "3", "4", "5", "6") &&
                            it.createdAt == 123456L
                    },
                )
            }
        }

    @Test
    fun `observeLatestContest mapeia entity para domain`() =
        runTest {
            val flow =
                MutableStateFlow(
                    ContestEntity(
                        id = "MEGA_SENA_1",
                        lotteryType = LotteryType.MEGA_SENA,
                        contestNumber = 1,
                        date = "01/01/2024",
                        numbers = listOf("1", "2", "3", "4", "5", "6"),
                    ),
                )
            every { lotteryDao.observeLatestContest(LotteryType.MEGA_SENA) } returns flow

            val latest = repository.observeLatestContest(LotteryType.MEGA_SENA).first()
            assertEquals(1, latest?.id)
            assertEquals(LotteryType.MEGA_SENA, latest?.lotteryType)
            assertEquals(listOf(1, 2, 3, 4, 5, 6), latest?.numbers)
        }

    @Test
    fun `refresh retorna Success quando ao menos uma modalidade sincroniza`() =
        runTest {
            coEvery { jsonFileStore.exists() } returns false
            coEvery { lotteryDao.getLatestContest(any()) } returns null
            coEvery { lotteryDao.insertContests(any()) } just Runs
            coEvery { lotteryDao.insertContest(any()) } just Runs

            // Apenas Mega-Sena responde; as demais modalidades falham e são ignoradas pelo refresh.
            coEvery { lotteryApi.getLatestContest(any()) } answers {
                val slug = firstArg<String>()
                if (slug == "mega-sena") {
                    ContestDto(
                        id = 5,
                        date = "01/01/2024",
                        numbers = listOf("1", "2", "3", "4", "5", "6"),
                    )
                } else {
                    throw RuntimeException("not stubbed: $slug")
                }
            }

            // Backfill: devolve concursos coerentes com o `id` solicitado.
            coEvery { lotteryApi.getContest(any(), any()) } answers {
                val id = secondArg<Int>()
                ContestDto(
                    id = id,
                    date = "01/01/2024",
                    numbers = listOf("1", "2", "3", "4", "5", "6"),
                )
            }

            val result = repository.refresh()
            assertTrue(result is AppResult.Success)

            coVerify(atLeast = 1) { lotteryDao.insertContests(any()) }
        }

    @Test
    fun `refresh retorna Failure quando migração falha com JSON corrompido`() =
        runTest {
            // Arrange: simular arquivo JSON existente mas com dados corrompidos
            every { jsonFileStore.exists() } returns true
            coEvery { jsonFileStore.read() } throws kotlinx.serialization.SerializationException("Malformed JSON")

            // Act
            val result = repository.refresh()

            // Assert
            assertTrue(result is AppResult.Failure)
            val failure = result as AppResult.Failure
            assertTrue(failure.error is com.cebolao.domain.error.AppError.DataCorruption)
            assertEquals(
                "Erro ao carregar dados salvos. Os dados podem estar corrompidos.",
                (failure.error as com.cebolao.domain.error.AppError.DataCorruption).message
            )

            // Verificar que não tentou sincronizar quando a migração falhou
            coVerify(exactly = 0) { lotteryApi.getLatestContest(any()) }
        }
}
