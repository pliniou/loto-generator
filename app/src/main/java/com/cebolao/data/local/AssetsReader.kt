package com.cebolao.data.local

import android.content.Context
import android.util.Log
import com.cebolao.data.util.LotteryTypeMappings
import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Lê profiles de loteria e dados iniciais dos assets.
 *
 * Assets esperados:
 * - assets/lotofacil.json
 * - assets/mega_sena.json
 * - assets/quina.json
 * - assets/lotomania.json
 * - assets/dupla_sena.json
 * - assets/timemania.json
 * - assets/super_sete.json
 *
 * Cada arquivo contém array de objetos com formato:
 * ```json
 * [
 *   {
 *     "id": 1,
 *     "date": "29/09/2003",
 *     "numbers": [2, 3, 5, ...]
 *   }
 * ]
 * ```
 */
@Singleton
class AssetsReader
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val json =
            Json {
                ignoreUnknownKeys = true
            }

        /**
         * Lê contests de uma modalidade dos assets.
         *
         * @param type Tipo da loteria
         * @return Lista de contests ou lista vazia se arquivo não existir
         */
        fun readContests(type: LotteryType): List<Contest> {
            val filename = LotteryTypeMappings.assetFilename(type)
            return try {
                val jsonString = context.assets.open(filename).bufferedReader().use { it.readText() }

                // Suporta dois formatos nos assets:
                // 1) Array simples: [ { id..., numbers: [...] }, ...]
                // 2) Objeto com schemaVersion e contests: { "schemaVersion": "1.0", "contests": [ ... ] }
                // Detecta formato pelo primeiro caractere
                val firstChar = jsonString.find { !it.isWhitespace() }
                val rawContests: List<RawContest> =
                    if (firstChar == '[') {
                        json.decodeFromString<List<RawContest>>(jsonString)
                    } else {
                        // Assume formato com encapsulador { }
                        val wrapper = json.decodeFromString<Wrapper>(jsonString)
                        // Se houver schemaVersion e for diferente de 1.0, falha rápido
                        if (wrapper.schemaVersion != null && wrapper.schemaVersion != "1.0") {
                            val msg = "schemaVersion não suportada: ${wrapper.schemaVersion}"
                            throw IllegalArgumentException(msg)
                        }
                        wrapper.contests
                            ?: throw IllegalArgumentException("Formato de objeto inválido ou sem 'contests' em $filename")
                    }

                // Validação simples do esquema (campos obrigatórios e tipos mínimos)
                rawContests.forEach { validateRawContest(it) }

                rawContests.map { raw ->
                    Contest(
                        id = raw.finalId,
                        lotteryType = type,
                        drawDate = raw.date,
                        numbers = raw.finalNumbers.sorted(),
                        secondDrawNumbers = raw.secondDrawNumbers?.sorted(),
                        teamNumber = raw.teamNumber,
                    )
                }
            } catch (e: Exception) {
                // Falhar alto para não mascarar seeds quebrados em produção.
                Log.e(TAG, "Falha ao ler assets de $type ($filename). Interrompendo seed.", e)
                throw IllegalStateException("Erro ao ler assets de $type", e)
            }
        }

        /**
         * Lê profile de uma modalidade.
         * Perfis são fixos no código pois não estão nos assets JSON.
         *
         * @param type Tipo da loteria
         * @return Profile configurado
         */
        fun readProfile(type: LotteryType): LotteryProfile =
            when (type) {
                LotteryType.LOTOFACIL ->
                    LotteryProfile(
                        type = LotteryType.LOTOFACIL,
                        name = "Lotofácil",
                        minNumber = 1,
                        maxNumber = 25,
                        numbersPerGame = 15,
                        prizeRanges = listOf(15, 14, 13, 12, 11),
                        costPerGame = 350, // R$ 3,50
                        probabilityOfWinning = "1 em 3.268.760",
                        bolaoInfo =
                            com.cebolao.domain.model.BolaoInfo(
                                minPoolPrice = 1250, // R$ 12,50
                                minSharePrice = 400, // R$ 4,00
                                minShares = 2,
                                maxShares = 100, // Depende dos números jogados, limite geral
                            ),
                    )
                LotteryType.MEGA_SENA ->
                    LotteryProfile(
                        type = LotteryType.MEGA_SENA,
                        name = "Mega-Sena",
                        minNumber = 1,
                        maxNumber = 60,
                        numbersPerGame = 6,
                        prizeRanges = listOf(6, 5, 4),
                        costPerGame = 600, // R$ 6,00 (Julho/2025)
                        probabilityOfWinning = "1 em 50.063.860",
                        bolaoInfo =
                            com.cebolao.domain.model.BolaoInfo(
                                minPoolPrice = 1800, // R$ 18,00
                                minSharePrice = 700, // R$ 7,00
                                minShares = 2,
                                maxShares = 100,
                            ),
                    )
                LotteryType.QUINA ->
                    LotteryProfile(
                        type = LotteryType.QUINA,
                        name = "Quina",
                        minNumber = 1,
                        maxNumber = 80,
                        numbersPerGame = 5,
                        prizeRanges = listOf(5, 4, 3, 2),
                        costPerGame = 300, // R$ 3,00
                        probabilityOfWinning = "1 em 24.040.016",
                        bolaoInfo =
                            com.cebolao.domain.model.BolaoInfo(
                                minPoolPrice = 1500, // R$ 15,00
                                minSharePrice = 400, // R$ 4,00
                                minShares = 2,
                                maxShares = 50,
                            ),
                    )
                LotteryType.LOTOMANIA ->
                    LotteryProfile(
                        type = LotteryType.LOTOMANIA,
                        name = "Lotomania",
                        minNumber = 0,
                        maxNumber = 99,
                        numbersPerGame = 50,
                        prizeRanges = listOf(20, 19, 18, 17, 16, 0), // Inclui faixa "0 acertos"
                        costPerGame = 300,
                        probabilityOfWinning = "1 em 11.372.635",
                        bolaoInfo = null, // Lotomania não possui bolão oficial
                    )
                LotteryType.DUPLA_SENA ->
                    LotteryProfile(
                        type = LotteryType.DUPLA_SENA,
                        name = "Dupla Sena",
                        minNumber = 1,
                        maxNumber = 50,
                        numbersPerGame = 6,
                        prizeRanges = listOf(6, 5, 4, 3),
                        hasSecondDraw = true,
                        costPerGame = 300, // R$ 3,00
                        probabilityOfWinning = "1 em 15.890.700",
                        bolaoInfo =
                            com.cebolao.domain.model.BolaoInfo(
                                minPoolPrice = 1000, // R$ 10,00
                                minSharePrice = 300, // R$ 3,00
                                minShares = 2,
                                maxShares = 50,
                            ),
                    )
                LotteryType.TIMEMANIA ->
                    LotteryProfile(
                        type = LotteryType.TIMEMANIA,
                        name = "Timemania",
                        minNumber = 1,
                        maxNumber = 80,
                        numbersPerGame = 10,
                        prizeRanges = listOf(7, 6, 5, 4, 3),
                        hasTeam = true,
                        costPerGame = 350, // R$ 3,50
                        probabilityOfWinning = "1 em 26.472.637",
                        bolaoInfo =
                            com.cebolao.domain.model.BolaoInfo(
                                minPoolPrice = 700, // R$ 7,00
                                minSharePrice = 350, // R$ 3,50
                                minShares = 2,
                                maxShares = 15, // Máximo de 15 cotas
                            ),
                    )
                LotteryType.SUPER_SETE ->
                    LotteryProfile(
                        type = LotteryType.SUPER_SETE,
                        name = "Super Sete",
                        minNumber = 0,
                        maxNumber = 9,
                        numbersPerGame = 7, // 7 colunas
                        prizeRanges = listOf(7, 6, 5, 4, 3),
                        isSuperSete = true,
                        costPerGame = 300, // R$ 3,00
                        probabilityOfWinning = "1 em 10.000.000",
                        bolaoInfo =
                            com.cebolao.domain.model.BolaoInfo(
                                minPoolPrice = 1000, // R$ 10,00
                                minSharePrice = 600, // R$ 6,00 (cotas a partir de R$ 6,00)
                                minShares = 2,
                                maxShares = 100,
                            ),
                    )
            }

        /**
         * Lê todos os profiles.
         */
        fun readAllProfiles(): List<LotteryProfile> = LotteryType.entries.map { readProfile(it) }

        /**
         * Classe auxiliar para deserializar contests dos assets.
         */
        @kotlinx.serialization.Serializable
        private data class RawContest(
            @kotlinx.serialization.SerialName("contestNumber") val contestNumber: Int? = null,
            val id: Int? = null,
            val date: String,
            @kotlinx.serialization.SerialName("draw") val draw: List<Int>? = null,
            val numbers: List<Int>? = null,
            val secondDrawNumbers: List<Int>? = null,
            val teamNumber: Int? = null,
        ) {
            val finalId: Int get() = id ?: contestNumber ?: 0
            val finalNumbers: List<Int> get() = numbers ?: draw ?: emptyList()
        }

        private fun validateRawContest(raw: RawContest) {
            // Validar campos mínimos
            require(raw.finalId > 0) { "O id do concurso deve ser positivo" }
            require(raw.date.isNotBlank()) { "A data do concurso não pode estar em branco" }
            require(raw.finalNumbers.isNotEmpty()) { "O concurso deve conter números" }
            // secondDrawNumbers e teamNumber são opcionais; se presentes, validar seus tamanhos
            raw.secondDrawNumbers?.let { require(it.isNotEmpty()) { "secondDrawNumbers não pode estar vazio quando presente" } }
        }

        @kotlinx.serialization.Serializable
        private data class Wrapper(
            val schemaVersion: String? = null,
            val contests: List<RawContest>? = null,
        )

        companion object {
            private const val TAG = "AssetsReader"
        }
    }
