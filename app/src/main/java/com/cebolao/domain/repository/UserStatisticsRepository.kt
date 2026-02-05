package com.cebolao.domain.repository

import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.model.UserUsageStats
import com.cebolao.domain.result.AppResult
import kotlinx.coroutines.flow.Flow

interface UserStatisticsRepository {
    fun observeStats(): Flow<List<UserUsageStats>>

    suspend fun recordUsage(presetName: String): AppResult<Unit>

    suspend fun recordSavedGames(
        presetName: String,
        count: Int,
    ): AppResult<Unit>

    suspend fun recordHits(
        presetName: String,
        hits: Int,
    ): AppResult<Unit>

    suspend fun getBestPreset(type: LotteryType): UserUsageStats? // Logic to likely be implemented via finding stats related to presets of that type
}
