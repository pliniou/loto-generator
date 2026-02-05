package com.cebolao.app.feature.generator

import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.Game
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.model.UserFilterPreset
import com.cebolao.domain.model.UserUsageStats
import com.cebolao.domain.repository.LotteryRepository
import com.cebolao.domain.repository.ProfileRepository
import com.cebolao.domain.repository.UserPresetRepository
import com.cebolao.domain.repository.UserStatisticsRepository
import com.cebolao.domain.result.AppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeProfileRepo : ProfileRepository {
    override fun getProfile(type: LotteryType): LotteryProfile = LotteryProfile(type, "X", 1, 10, 5, listOf(5, 4, 3), costPerGame = 100)

    override fun getAllProfiles(): List<LotteryProfile> = LotteryType.entries.map { getProfile(it) }
}

class FakeLotteryRepo : LotteryRepository {
    override fun observeContests(type: LotteryType): Flow<List<Contest>> = flowOf(emptyList())

    override fun observeLatestContest(type: LotteryType): Flow<Contest?> = flowOf(null)

    override fun observeGames(): Flow<List<Game>> = flowOf(emptyList())

    override fun observeGamesByType(type: LotteryType): Flow<List<Game>> = flowOf(emptyList())

    override suspend fun getLastContest(type: LotteryType): AppResult<Contest?> = AppResult.Success(null)

    override suspend fun getRecentContests(
        type: LotteryType,
        limit: Int,
    ): AppResult<List<Contest>> = AppResult.Success(emptyList())

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
    override suspend fun savePreset(preset: UserFilterPreset): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun deletePreset(name: String): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun getAllPresets(): AppResult<List<UserFilterPreset>> = AppResult.Success(emptyList())

    override fun observePresets(): Flow<List<UserFilterPreset>> = flowOf(emptyList())
}

class FakeUserStatsRepo : UserStatisticsRepository {
    override fun observeStats(): Flow<List<UserUsageStats>> = flowOf(emptyList())

    override suspend fun recordUsage(presetName: String): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun recordSavedGames(
        presetName: String,
        count: Int,
    ): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun recordHits(
        presetName: String,
        hits: Int,
    ): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun getBestPreset(type: LotteryType): UserUsageStats? = null
}
