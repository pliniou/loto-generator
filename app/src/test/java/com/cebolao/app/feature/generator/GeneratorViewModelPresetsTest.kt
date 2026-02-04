package com.cebolao.app.feature.generator

import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.model.UserFilterPreset
import com.cebolao.domain.repository.LotteryRepository
import com.cebolao.domain.repository.ProfileRepository
import com.cebolao.domain.repository.UserPresetRepository
import com.cebolao.domain.result.AppResult
import com.cebolao.test.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FakeUserPresetRepo : UserPresetRepository {
    private val state = MutableStateFlow<List<UserFilterPreset>>(emptyList())

    override suspend fun savePreset(preset: UserFilterPreset): AppResult<Unit> {
        val cur = state.value.toMutableList()
        val idx = cur.indexOfFirst { it.name == preset.name }
        if (idx >= 0) cur[idx] = preset else cur.add(preset)
        state.value = cur
        return AppResult.Success(Unit)
    }

    override suspend fun deletePreset(name: String): AppResult<Unit> {
        state.value = state.value.filterNot { it.name == name }
        return AppResult.Success(Unit)
    }

    override suspend fun getAllPresets(): AppResult<List<UserFilterPreset>> = AppResult.Success(state.value)

    override fun observePresets() = state
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
                    override fun getProfile(type: LotteryType) =
                        com.cebolao.domain.model.LotteryProfile(type, "X", 1, 10, 5, listOf(5, 4, 3), costPerGame = 100)

                    override fun getAllProfiles() = listOf(getProfile(LotteryType.LOTOFACIL))
                }
            val vm =
                GeneratorViewModel(
                    profileRepo,
                    object : LotteryRepository {
                        override fun observeContests(type: LotteryType) =
                            kotlinx.coroutines.flow.flowOf(emptyList<com.cebolao.domain.model.Contest>())

                        override fun observeLatestContest(type: LotteryType) = kotlinx.coroutines.flow.flowOf(null)

                        override fun observeGames() = kotlinx.coroutines.flow.flowOf(emptyList<com.cebolao.domain.model.Game>())

                        override fun observeGamesByType(type: LotteryType) =
                            kotlinx.coroutines.flow.flowOf(
                                emptyList<com.cebolao.domain.model.Game>(),
                            )

                        override suspend fun getLastContest(type: LotteryType) = AppResult.Success(null)

                        override suspend fun saveGame(game: com.cebolao.domain.model.Game): AppResult<Unit> = AppResult.Success(Unit)

                        override suspend fun saveGames(games: List<com.cebolao.domain.model.Game>): AppResult<Unit> =
                            AppResult.Success(
                                Unit,
                            )

                        override suspend fun deleteGame(gameId: String): AppResult<Unit> = AppResult.Success(Unit)

                        override suspend fun deleteAllGames(type: LotteryType): AppResult<Unit> = AppResult.Success(Unit)

                        override suspend fun togglePinGame(gameId: String): AppResult<Unit> = AppResult.Success(Unit)

                        override suspend fun updateContests(
                            type: LotteryType,
                            newContests: List<com.cebolao.domain.model.Contest>,
                        ): AppResult<Unit> =
                            AppResult.Success(
                                Unit,
                            )

                        override suspend fun refresh(): AppResult<Unit> = AppResult.Success(Unit)
                    },
                    FakeUserPresetRepo(),
                    com.cebolao.domain.usecase.GenerateGamesUseCase(com.cebolao.domain.service.GameValidator()),
                    mainDispatcherRule.testDispatcher,
                )

            advanceUntilIdle()
            // initially empty
            assertTrue(vm.uiState.value.userPresets.isEmpty())

            vm.onFilterToggled(com.cebolao.domain.model.GenerationFilter.PARITY_BALANCE)
            vm.onSaveUserPreset("my-preset")

            advanceUntilIdle()

            assertTrue(vm.uiState.value.userPresets.any { it.name == "my-preset" })
        }
}
