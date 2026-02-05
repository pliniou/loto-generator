package com.cebolao.app.feature.games

import com.cebolao.domain.model.Game
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.repository.LotteryRepository
import io.mockk.coEvery
import io.mockk.coVerify
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
        coEvery { repository.observeGamesByType(any()) } returns flowOf(emptyList())
        viewModel = GamesViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() =
        runTest {
            val state = viewModel.uiState.value
            assertEquals(null, state.filterType)
            assertEquals(emptyList<Game>(), state.games)
        }

    @Test
    fun `changing filter updates repository call`() =
        runTest {
            viewModel.onFilterChanged(LotteryType.MEGA_SENA)

            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { repository.observeGamesByType(LotteryType.MEGA_SENA) }
            assertEquals(LotteryType.MEGA_SENA, viewModel.uiState.value.filterType)
        }
}
