package com.cebolao.data.repository

import android.util.Log
import com.cebolao.app.di.IoDispatcher
import com.cebolao.data.local.JsonFileStore
import com.cebolao.data.local.room.dao.LotteryDao
import com.cebolao.data.local.room.entity.ContestEntity
import com.cebolao.data.remote.api.LotteryApi
import com.cebolao.data.remote.mapper.ContestMapper
import com.cebolao.data.remote.mapper.GameMapper
import com.cebolao.data.util.LotteryTypeMappings
import com.cebolao.domain.error.toAppError
import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.Game
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.repository.LotteryRepository
import com.cebolao.domain.result.AppResult
import com.cebolao.domain.result.AppResult.Failure
import com.cebolao.domain.result.AppResult.Success
import com.cebolao.domain.result.appResultSuspend
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private const val DEFAULT_BACKFILL_LOOKBACK = 10
private const val MAX_BACKFILL_FETCH = 20

@Singleton
class LotteryRepositoryImpl
    @Inject
    constructor(
        private val lotteryDao: LotteryDao,
        private val jsonFileStore: JsonFileStore, // For migration
        private val lotteryApi: LotteryApi,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : LotteryRepository {
        private fun logD(
            tag: String,
            message: String,
        ) {
            try {
                Log.d(tag, message)
            } catch (_: Throwable) {
            }
        }

        private fun logE(
            tag: String,
            message: String,
            error: Throwable? = null,
        ) {
            try {
                if (error != null) Log.e(tag, message, error) else Log.e(tag, message)
            } catch (_: Throwable) {
            }
        }

        // --- Reads ---

        override fun observeContests(type: LotteryType): Flow<List<Contest>> =
            lotteryDao.observeContests(type).map { entities ->
                entities.map { ContestMapper.toDomain(it) }
            }

        override fun observeLatestContest(type: LotteryType): Flow<Contest?> =
            lotteryDao.observeLatestContest(type).map { entity ->
                entity?.let { ContestMapper.toDomain(it) }
            }

        override fun observeGames(): Flow<List<Game>> =
            lotteryDao.observeAllGames().map { entities ->
                entities.map { GameMapper.toDomain(it) }
            }

        override fun observeGamesByType(type: LotteryType): Flow<List<Game>> =
            lotteryDao.observeGamesByType(type).map { entities ->
                entities.map { GameMapper.toDomain(it) }
            }

        override suspend fun getLastContest(type: LotteryType): AppResult<Contest?> =
            appResultSuspend {
                withContext(ioDispatcher) {
                    lotteryDao.getLatestContest(type)?.let { ContestMapper.toDomain(it) }
                }
            }

        override suspend fun getRecentContests(
            type: LotteryType,
            limit: Int,
        ): AppResult<List<Contest>> =
            appResultSuspend {
                withContext(ioDispatcher) {
                    lotteryDao.getRecentContests(type, limit).map { ContestMapper.toDomain(it) }
                }
            }

        // --- Writes ---

        override suspend fun saveGame(game: Game): AppResult<Unit> =
            appResultSuspend {
                lotteryDao.insertGame(GameMapper.toEntity(game))
            }

        override suspend fun saveGames(games: List<Game>): AppResult<Unit> =
            appResultSuspend {
                lotteryDao.insertGames(games.map { GameMapper.toEntity(it) })
            }

        override suspend fun deleteGame(gameId: String): AppResult<Unit> =
            appResultSuspend {
                lotteryDao.deleteGame(gameId)
            }

        override suspend fun deleteAllGames(type: LotteryType): AppResult<Unit> =
            appResultSuspend {
                lotteryDao.deleteGamesByType(type)
            }

        override suspend fun togglePinGame(gameId: String): AppResult<Unit> =
            appResultSuspend {
                lotteryDao.toggleGamePin(gameId)
            }

        override suspend fun updateContests(
            type: LotteryType,
            newContests: List<Contest>,
        ): AppResult<Unit> =
            appResultSuspend {
                require(newContests.all { it.lotteryType == type }) {
                    "Lista de concursos contém modalidades divergentes de $type"
                }
                val entities = newContests.map { domainContest -> ContestMapper.toEntity(domainContest) }
                lotteryDao.insertContests(entities)
            }

        // --- Sincronização e migração ---

        override suspend fun refresh(): AppResult<Unit> =
            withContext(ioDispatcher) {
                // Checagem de migração (legado JSON -> Room)
                val migrationResult = migrateIfNeeded()
                if (migrationResult is Failure) {
                    return@withContext migrationResult
                }

                var hadAnyNetworkSuccess = false
                var lastNetworkFailure: Throwable? = null

                LotteryType.entries.forEach { type ->
                    val result = runCatching { syncLotteryType(type) }
                    if (result.isSuccess) {
                        hadAnyNetworkSuccess = true
                    } else {
                        val error = result.exceptionOrNull()
                        if (error != null) {
                            logE("Sync", "Falha ao sincronizar $type", error)
                            lastNetworkFailure = error
                        }
                    }
                }

                if (!hadAnyNetworkSuccess && lastNetworkFailure != null) {
                    Failure(lastNetworkFailure.toAppError(), lastNetworkFailure)
                } else {
                    Success(Unit)
                }
            }

        private suspend fun syncLotteryType(type: LotteryType) {
            val slug = LotteryTypeMappings.apiSlug(type)
            val latestEntity = fetchLatestContestEntity(type, slug)
            val localLatest = lotteryDao.getLatestContest(type)

            val hasNewContest = localLatest?.let { latestEntity.contestNumber > it.contestNumber } ?: true

            if (hasNewContest) {
                val entities = collectBackfillEntities(type, slug, localLatest?.contestNumber, latestEntity)
                if (entities.isNotEmpty()) {
                    lotteryDao.insertContests(entities)
                }
            } else {
                // Atualiza o último concurso de qualquer forma para garantir que campos como nextContest estejam atualizados
                lotteryDao.insertContest(latestEntity)
            }
        }

        private suspend fun fetchLatestContestEntity(
            type: LotteryType,
            slug: String,
        ): ContestEntity {
            val latestDto = lotteryApi.getLatestContest(slug)
            return ContestMapper.toEntity(latestDto, type)
        }

        private suspend fun collectBackfillEntities(
            type: LotteryType,
            slug: String,
            localLatestNumber: Int?,
            latestEntity: ContestEntity,
        ): List<ContestEntity> {
            val latestNumber = latestEntity.contestNumber
            val fallbackStart = latestNumber - DEFAULT_BACKFILL_LOOKBACK
            val provisionalStart = (localLatestNumber ?: fallbackStart) + 1
            val startId = provisionalStart.coerceAtLeast(1)
            val boundedStart = maxOf(startId, latestNumber - (MAX_BACKFILL_FETCH - 1))

            logD("Sync", "Sincronizando $type de $boundedStart até $latestNumber")

            val entities = mutableListOf<ContestEntity>()
            for (id in boundedStart until latestNumber) {
                try {
                    val contestDto = lotteryApi.getContest(slug, id)
                    entities.add(ContestMapper.toEntity(contestDto, type))
                } catch (error: Exception) {
                    logE("Sync", "Error fetching contest $id for $type", error)
                }
            }
            entities.add(latestEntity)
            return entities
        }

        private suspend fun migrateIfNeeded(): AppResult<Unit> {
            if (!jsonFileStore.exists()) {
                return Success(Unit)
            }

            return runCatching {
                val data = jsonFileStore.read()
                if (data != null) {
                    logD("Migration", "Migrando dados JSON legados para Room")

                    // Migrate Games
                    if (data.games.isNotEmpty()) {
                        val gameEntities = data.games.map { GameMapper.toEntity(it) }
                        lotteryDao.insertGames(gameEntities)
                    }

                    // Migrate Contests
                    data.contests.forEach { (_, list) ->
                        val entities = list.map { domainContest -> ContestMapper.toEntity(domainContest) }
                        lotteryDao.insertContests(entities)
                    }

                    logD("Migration", "Migração concluída. Removendo arquivo JSON")
                    jsonFileStore.clear()
                }
                Success(Unit)
            }.getOrElse { error ->
                logE("Migration", "Falha ao migrar dados", error)
                Failure(
                    com.cebolao.domain.error.AppError.DataCorruption(
                        message = "Erro ao carregar dados salvos. Os dados podem estar corrompidos.",
                    ),
                    error,
                )
            }
        }
    }
