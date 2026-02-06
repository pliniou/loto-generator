package com.cebolao.app.feature.games

import com.cebolao.domain.model.Game
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.repository.LotteryRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GamesViewModelTest {
    private val repository: LotteryRepository = mockk(relaxed = true)
    private lateinit var viewModel: GamesViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.observeGames() } returns flowOf(emptyList())
        viewModel = GamesViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() =
        runTest {
            testDispatcher.scheduler.advanceUntilIdle()
            val state = viewModel.uiState.value
            assertEquals(null, state.filterType)
            assertEquals(emptyList<SavedGameCardUiState>(), state.savedGames)
            assertEquals(0, state.totalCount)
        }

    @Test
    fun `changing filter updates games list and counts`() =
        runTest {
            val lotofacilGame =
                Game(
                    id = "1",
                    lotteryType = LotteryType.LOTOFACIL,
                    numbers = listOf(1, 2, 3),
                    createdAt = 1L,
                )
            val megaGame =
                Game(
                    id = "2",
                    lotteryType = LotteryType.MEGA_SENA,
                    numbers = listOf(4, 5, 6),
                    createdAt = 1L,
                )
            coEvery { repository.observeGames() } returns flowOf(listOf(lotofacilGame, megaGame))
            viewModel = GamesViewModel(repository)

            testDispatcher.scheduler.advanceUntilIdle()
            viewModel.onFilterChanged(LotteryType.MEGA_SENA)

            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(LotteryType.MEGA_SENA, state.filterType)
            assertEquals(listOf(megaGame), state.savedGames.map { it.game })
            assertEquals(2, state.totalCount)
            assertEquals(
                mapOf(
                    LotteryType.LOTOFACIL to 1,
                    LotteryType.MEGA_SENA to 1,
                ),
                state.countsByType,
            )
            assertEquals(0f, state.savedGames.first().recentHitRateProgress)
            assertEquals(0, state.savedGames.first().recentHitRatePercent)
        }
}
