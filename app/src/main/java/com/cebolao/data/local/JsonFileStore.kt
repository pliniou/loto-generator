package com.cebolao.data.local

import android.content.Context
import com.cebolao.domain.model.LotteryData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persistência JSON legada (thread-safe) com escrita atômica.
 *
 * No estado atual do projeto, o arquivo `lottery_data.json` é mantido apenas para migração
 * de versões antigas. A persistência principal é via Room.
 *
 * Estratégia:
 * 1. Escreve em arquivo temporário (.tmp)
 * 2. Faz rename atômico apenas se escrita bem-sucedida
 * 3. Usa Mutex para garantir thread-safety
 *
 * @property context Application context para acessar filesDir
 */
@Singleton
class JsonFileStore
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val mutex = Mutex()
        private val json =
            Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            }

        private val dataFile: File
            get() = File(context.filesDir, FILENAME)

        private val tempFile: File
            get() = File(context.filesDir, "$FILENAME.tmp")

        /**
         * Lê dados do arquivo JSON.
         * Retorna null se arquivo não existir.
         *
         * @throws Exception se arquivo existir mas estiver corrompido
         */
        suspend fun read(): LotteryData? =
            mutex.withLock {
                if (!dataFile.exists()) {
                    return@withLock null
                }

                try {
                    val jsonString = dataFile.readText()
                    json.decodeFromString<LotteryData>(jsonString)
                } catch (e: Exception) {
                    throw IllegalStateException("Failed to read lottery data from ${dataFile.path}", e)
                }
            }

        /**
         * Limpa todos os dados (deleta arquivo).
         */
        suspend fun clear() =
            mutex.withLock {
                if (dataFile.exists()) {
                    dataFile.delete()
                }
                if (tempFile.exists()) {
                    tempFile.delete()
                }
            }

        /**
         * Retorna true se arquivo de dados existe.
         */
        fun exists(): Boolean = dataFile.exists()

        companion object {
            const val FILENAME = "lottery_data.json"
        }
    }
