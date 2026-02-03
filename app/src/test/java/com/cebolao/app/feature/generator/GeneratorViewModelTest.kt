package com.cebolao.app.feature.generator

import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.Game
import com.cebolao.domain.model.GenerationFilter
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.repository.LotteryRepository
import com.cebolao.domain.repository.ProfileRepository
import com.cebolao.domain.repository.UserPresetRepository
import com.cebolao.domain.result.AppResult
import com.cebolao.test.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FakeProfileRepo : ProfileRepository {
    override fun getProfile(type: LotteryType): LotteryProfile = LotteryProfile(type, "X", 1, 10, 5, listOf(5, 4, 3), costPerGame = 100)

    override fun getAllProfiles(): List<LotteryProfile> = LotteryType.entries.map { getProfile(it) }
}

class FakeLotteryRepo : LotteryRepository {
    override fun observeContests(type: LotteryType) = kotlinx.coroutines.flow.flowOf(emptyList<Contest>())

    override fun observeLatestContest(type: LotteryType) = kotlinx.coroutines.flow.flowOf(null)

    override fun observeGames() = kotlinx.coroutines.flow.flowOf(emptyList<Game>())

    override fun observeGamesByType(type: LotteryType) = kotlinx.coroutines.flow.flowOf(emptyList<Game>())

    override suspend fun getLastContest(type: LotteryType): Contest? = null

    override suspend fun saveGame(game: Game): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun saveGames(games: List<Game>): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun deleteGame(gameId: String): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun deleteAllGames(type: LotteryType): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun togglePinGame(gameId: String): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun updateContests(
        type: LotteryType,
        newContests: List<Contest>,
    ): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun refresh(): AppResult<Unit> = AppResult.Success(Unit)
}

class FakeUserPresetRepository : UserPresetRepository {
    override suspend fun savePreset(preset: com.cebolao.domain.model.UserFilterPreset): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun deletePreset(name: String): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun getAllPresets(): AppResult<List<com.cebolao.domain.model.UserFilterPreset>> = AppResult.Success(emptyList())

    override fun observePresets(): Flow<List<com.cebolao.domain.model.UserFilterPreset>> = kotlinx.coroutines.flow.flowOf(emptyList())
}

@OptIn(ExperimentalCoroutinesApi::class)
class GeneratorViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `toggling filter updates ui state`() =
        runTest {
            val vm =
                GeneratorViewModel(
                    FakeProfileRepo(),
                    FakeLotteryRepo(),
                    FakeUserPresetRepository(),
                    com.cebolao.domain.usecase.GenerateGamesUseCase(com.cebolao.domain.service.GameValidator()),
                    mainDispatcherRule.testDispatcher,
                )

            advanceUntilIdle()
            // Initially no filters
            assertTrue(vm.uiState.value.activeFilters.isEmpty())

            vm.onFilterToggled(GenerationFilter.PARITY_BALANCE)
            assertTrue(vm.uiState.value.activeFilters.contains(GenerationFilter.PARITY_BALANCE))

            vm.onFilterToggled(GenerationFilter.PARITY_BALANCE)
            assertFalse(vm.uiState.value.activeFilters.contains(GenerationFilter.PARITY_BALANCE))
        }
}
