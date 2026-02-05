package com.cebolao.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.model.UserUsageStats
import com.cebolao.domain.repository.UserStatisticsRepository
import com.cebolao.domain.result.AppResult
import com.cebolao.domain.result.appResultSuspend
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.statsDataStore by preferencesDataStore(name = "user_stats")

@Singleton
class UserStatisticsRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : UserStatisticsRepository {
        private val statsKey = stringPreferencesKey("user_stats_json")
        private val json = Json { ignoreUnknownKeys = true }

        override fun observeStats(): Flow<List<UserUsageStats>> =
            context.statsDataStore.data.map { prefs ->
                val raw = prefs[statsKey] ?: "[]"
                try {
                    json.decodeFromString<List<UserUsageStats>>(raw)
                } catch (e: Exception) {
                    emptyList()
                }
            }

        override suspend fun recordUsage(presetName: String): AppResult<Unit> =
            updateStats(presetName) { current ->
                current.copy(
                    usageCount = current.usageCount + 1,
                    lastUsed = System.currentTimeMillis(),
                )
            }

        override suspend fun recordSavedGames(
            presetName: String,
            count: Int,
        ): AppResult<Unit> =
            updateStats(presetName) { current ->
                current.copy(savedGamesCount = current.savedGamesCount + count)
            }

        override suspend fun recordHits(
            presetName: String,
            hits: Int,
        ): AppResult<Unit> =
            updateStats(presetName) { current ->
                current.copy(totalHits = current.totalHits + hits)
            }

        override suspend fun getBestPreset(type: LotteryType): UserUsageStats? {
            val statsList = observeStats().firstOrNull() ?: emptyList()
            if (statsList.isEmpty()) return null

            return statsList.maxByOrNull { it.totalHits }
        }

        private suspend fun updateStats(
            presetName: String,
            update: (UserUsageStats) -> UserUsageStats,
        ): AppResult<Unit> =
            appResultSuspend {
                context.statsDataStore.edit { prefs ->
                    val raw = prefs[statsKey] ?: "[]"
                    val list =
                        try {
                            json.decodeFromString<MutableList<UserUsageStats>>(raw)
                        } catch (_: Exception) {
                            mutableListOf()
                        }

                    val idx = list.indexOfFirst { it.presetName == presetName }
                    if (idx >= 0) {
                        list[idx] = update(list[idx])
                    } else {
                        val newStats = UserUsageStats(presetName = presetName)
                        list.add(update(newStats))
                    }
                    prefs[statsKey] = json.encodeToString(list)
                }
            }
    }
