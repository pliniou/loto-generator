package com.cebolao.app.feature.checker

import com.cebolao.app.feature.generator.FakeLotteryRepo
import com.cebolao.app.feature.generator.FakeProfileRepo
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.usecase.CalculateStatisticsUseCase
import com.cebolao.domain.usecase.CheckGameUseCase
import com.cebolao.test.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CheckerViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `selecting type updates profile and state`() =
        runTest {
            val checkUseCase = CheckGameUseCase()
            val statsUseCase = CalculateStatisticsUseCase(checkUseCase)

            val vm =
                CheckerViewModel(
                    FakeLotteryRepo(),
                    FakeProfileRepo(),
                    checkUseCase,
                    statsUseCase,
                    mainDispatcherRule.testDispatcher,
                )

            advanceUntilIdle()

            // Initial state
            assertEquals(LotteryType.LOTOFACIL, vm.uiState.value.selectedType)

            // Change type
            vm.onTypeSelected(LotteryType.MEGA_SENA)
            advanceUntilIdle()

            assertEquals(LotteryType.MEGA_SENA, vm.uiState.value.selectedType)
            assertNotNull(vm.uiState.value.profile)
        }

    @Test
    fun `toggling numbers updates selection`() =
        runTest {
            val checkUseCase = CheckGameUseCase()
            val statsUseCase = CalculateStatisticsUseCase(checkUseCase)

            val vm =
                CheckerViewModel(
                    FakeLotteryRepo(),
                    FakeProfileRepo(),
                    checkUseCase,
                    statsUseCase,
                    mainDispatcherRule.testDispatcher,
                )
            advanceUntilIdle() // Load initial profile (Lotofacil)

            vm.onNumberToggle(1)
            vm.onNumberToggle(2)

            assertTrue(vm.uiState.value.selectedNumbers.contains(1))
            assertTrue(vm.uiState.value.selectedNumbers.contains(2))
            assertEquals(2, vm.uiState.value.selectedNumbers.size)

            // Toggle off
            vm.onNumberToggle(1)
            assertFalse(vm.uiState.value.selectedNumbers.contains(1))
            assertEquals(1, vm.uiState.value.selectedNumbers.size)
        }

    private fun assertTrue(condition: Boolean) {
        org.junit.Assert.assertTrue(condition)
    }

    private fun assertFalse(condition: Boolean) {
        org.junit.Assert.assertFalse(condition)
    }
}
