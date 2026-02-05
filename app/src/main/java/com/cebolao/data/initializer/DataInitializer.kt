package com.cebolao.data.initializer

import android.util.Log
import com.cebolao.app.di.ApplicationScope
import com.cebolao.app.di.IoDispatcher
import com.cebolao.data.local.AssetsReader
import com.cebolao.data.local.JsonFileStore
import com.cebolao.data.local.room.dao.LotteryDao
import com.cebolao.data.remote.mapper.ContestMapper
import com.cebolao.data.remote.mapper.GameMapper
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.rules.LotteryRulesRegistry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Inicializa dados na primeira execução do app.
 *
 * Responsabilidades:
 * 1. Migrar dados legados (JSON -> Room) quando existirem
 * 2. Seed de concursos via assets em instalações novas (Room vazio)
 *
 * Deve ser chamado no Application.onCreate().
 */
@Singleton
class DataInitializer
    @Inject
    constructor(
        private val lotteryDao: LotteryDao,
        private val jsonFileStore: JsonFileStore,
        private val assetsReader: AssetsReader,
        @ApplicationScope private val applicationScope: CoroutineScope,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) {
        /**
         * Inicializa dados de forma assíncrona.
         * Não bloqueia a thread principal.
         */
        fun initialize() {
            applicationScope.launch(ioDispatcher) {
                try {
                    migrateLegacyJsonIfPresent()
                    seedFromAssetsIfEmpty()
                } catch (e: Exception) {
                    Log.e(TAG, "Falha ao inicializar dados (seed/migração). O app seguirá sem seed inicial completa.", e)
                }
            }
        }

        /**
         * Migra o arquivo legado `lottery_data.json` (quando existir) para o Room.
         *
         * Observação: mantemos a leitura via `JsonFileStore` por compatibilidade com versões antigas.
         */
        private suspend fun migrateLegacyJsonIfPresent() {
            if (!jsonFileStore.exists()) return

            try {
                val legacy = jsonFileStore.read() ?: return
                Log.d(TAG, "Migrando dados legados (JSON -> Room)")

                if (legacy.contests.isNotEmpty()) {
                    val contestEntities =
                        legacy.contests.flatMap { (_, list) ->
                            list.map { contest -> ContestMapper.toEntity(contest) }
                        }
                    contestEntities.chunked(500).forEach { chunk ->
                        lotteryDao.insertContests(chunk)
                    }
                }

                if (legacy.games.isNotEmpty()) {
                    val gameEntities = legacy.games.map { GameMapper.toEntity(it) }
                    gameEntities.chunked(500).forEach { chunk ->
                        lotteryDao.insertGames(chunk)
                    }
                }

                jsonFileStore.clear()
                Log.d(TAG, "Migração concluída e arquivo legado removido")
            } catch (e: Exception) {
                Log.e(TAG, "Falha ao migrar arquivo legado; mantendo JSON para tentativa futura", e)
            }
        }

        /**
         * Seed inicial (instalação nova): popula o banco com concursos dos assets quando o Room estiver vazio.
         *
         * Importante: isso garante offline-first real (dados aparecem sem depender de refresh de rede).
         */
        private suspend fun seedFromAssetsIfEmpty() {
            // Usamos uma modalidade como "proxy" para detectar banco vazio.
            val hasAnyData = lotteryDao.getLatestContest(LotteryType.LOTOFACIL) != null
            if (hasAnyData) return

            Log.d(TAG, "Banco vazio: aplicando seed a partir dos assets")

            LotteryRulesRegistry.supportedTypes().forEach { type ->
                val entities = assetsReader.readContests(type).map { contest -> ContestMapper.toEntity(contest) }
                entities.chunked(500).forEach { chunk ->
                    lotteryDao.insertContests(chunk)
                }
            }
        }

        companion object {
            private const val TAG = "DataInitializer"
        }
    }
