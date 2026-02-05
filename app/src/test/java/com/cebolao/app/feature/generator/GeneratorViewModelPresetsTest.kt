package com.cebolao.app.feature.generator

import com.cebolao.domain.model.GenerationFilter
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.model.UserFilterPreset
import com.cebolao.domain.repository.ProfileRepository
import com.cebolao.domain.repository.UserPresetRepository
import com.cebolao.domain.result.AppResult
import com.cebolao.domain.service.GameValidator
import com.cebolao.domain.usecase.GenerateGamesUseCase
import com.cebolao.test.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private class FakeUserPresetRepo : UserPresetRepository {
    private val state = MutableStateFlow<List<UserFilterPreset>>(emptyList())

    override suspend fun savePreset(preset: UserFilterPreset): AppResult<Unit> {
        val current = state.value.toMutableList()
        val index = current.indexOfFirst { it.name == preset.name }
        if (index >= 0) {
            current[index] = preset
        } else {
            current.add(preset)
        }
        state.value = current
        return AppResult.Success(Unit)
    }

    override suspend fun deletePreset(name: String): AppResult<Unit> {
        state.value = state.value.filterNot { it.name == name }
        return AppResult.Success(Unit)
    }

    override suspend fun getAllPresets(): AppResult<List<UserFilterPreset>> = AppResult.Success(state.value)

    override fun observePresets(): Flow<List<UserFilterPreset>> = state
}

@OptIn(ExperimentalCoroutinesApi::class)
class GeneratorViewModelPresetsTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `saving a user preset updates state`() =
        runTest {
            val profileRepo =
                object : ProfileRepository {
                    override fun getProfile(type: LotteryType): LotteryProfile =
                        LotteryProfile(type, "X", 1, 10, 5, listOf(5, 4, 3), costPerGame = 100)

                    override fun getAllProfiles(): List<LotteryProfile> = listOf(getProfile(LotteryType.LOTOFACIL))
                }

            val viewModel =
                GeneratorViewModel(
                    profileRepo,
                    FakeLotteryRepo(),
                    FakeUserPresetRepo(),
                    FakeUserStatsRepo(),
                    GenerateGamesUseCase(GameValidator()),
                    mainDispatcherRule.testDispatcher,
                )

            advanceUntilIdle()
            assertTrue(viewModel.uiState.value.userPresets.isEmpty())

            viewModel.onFilterToggled(GenerationFilter.PARITY_BALANCE)
            viewModel.onSaveUserPreset("my-preset")

            advanceUntilIdle()
            assertTrue(viewModel.uiState.value.userPresets.any { it.name == "my-preset" })
        }
}
