package com.cebolao.app.feature.generator

import com.cebolao.domain.model.GenerationFilter
import com.cebolao.domain.service.GameValidator
import com.cebolao.domain.usecase.GenerateGamesUseCase
import com.cebolao.test.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GeneratorViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `toggling filter updates ui state`() =
        runTest {
            val viewModel =
                GeneratorViewModel(
                    FakeProfileRepo(),
                    FakeLotteryRepo(),
                    FakeUserPresetRepository(),
                    FakeUserStatsRepo(),
                    GenerateGamesUseCase(GameValidator()),
                    mainDispatcherRule.testDispatcher,
                )

            advanceUntilIdle()
            assertTrue(viewModel.uiState.value.activeFilters.isEmpty())

            viewModel.onFilterToggled(GenerationFilter.PARITY_BALANCE)
            assertTrue(viewModel.uiState.value.activeFilters.contains(GenerationFilter.PARITY_BALANCE))

            viewModel.onFilterToggled(GenerationFilter.PARITY_BALANCE)
            assertFalse(viewModel.uiState.value.activeFilters.contains(GenerationFilter.PARITY_BALANCE))
        }
}
