package com.cebolao.domain.usecase

import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.DistributionStats
import com.cebolao.domain.model.Game
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.NumberStat
import com.cebolao.domain.model.PrizeStat
import com.cebolao.domain.util.StatisticsUtil
import javax.inject.Inject

class CalculateStatisticsUseCase
    @Inject
    constructor(
        private val checkGameUseCase: CheckGameUseCase,
    ) {
        /**
         * Calcula frequência e atraso de cada número em um range de concursos.
         * @param contests Lista de concursos, idealmente ordenada pro mais recente no final, mas vamos ordenar aqui.
         * @param profile Perfil da loteria para saber o range de números (1..60, etc).
         */
        fun calculateNumberStats(
            contests: List<Contest>,
            profile: LotteryProfile,
        ): List<NumberStat> {
            if (contests.isEmpty()) return emptyList()

            val sortedContests = contests.sortedBy { it.id }
            val lastContestId = sortedContests.last().id

            // Intervalo de números da loteria
            val allNumbers = (profile.minNumber..profile.maxNumber).toList()

            val counts = allNumbers.associateWith { 0 }.toMutableMap()
            val lastSeen =
                allNumbers
                    .associateWith { 0 } // 0 significa nunca visto ou muito antigo
                    .toMutableMap()

            sortedContests.forEach { contest ->
                contest.getAllNumbers().forEach { num ->
                    counts[num] = (counts[num] ?: 0) + 1
                    lastSeen[num] = contest.id
                }
            }

            return allNumbers.map { num ->
                val freq = counts[num] ?: 0
                val lastId = lastSeen[num] ?: 0
                val delay = if (lastId == 0) -1 else (lastContestId - lastId)
                NumberStat(num, freq, delay)
            }
        }

        /**
         * Calcula quantos acertos o jogo selecionado teria feito nos concursos passados.
         * Útil para "Se eu tivesse jogado esses números..."
         */
        fun checkHistory(
            selectedNumbers: List<Int>,
            contests: List<Contest>,
            profile: LotteryProfile,
        ): List<PrizeStat> {
            val hitCounts = mutableMapOf<Int, Int>()
            val game = buildHistoryGame(selectedNumbers, profile)

            contests.forEach { contest ->
                val result =
                    checkGameUseCase(
                        game = game,
                        contest = contest,
                        profile = profile,
                    )
                val hits = result.hits
                if (hits > 0) {
                    hitCounts[hits] = (hitCounts[hits] ?: 0) + 1
                }
            }

            return hitCounts.map { (hits, count) -> PrizeStat(hits, count) }.sortedByDescending { it.hits }
        }

        /**
         * Calcula distribuição por dezenas e quadrantes para os concursos fornecidos.
         */
        fun calculateDistributionStats(
            contests: List<Contest>,
            profile: LotteryProfile,
        ): DistributionStats {
            if (contests.isEmpty()) {
                return DistributionStats(emptyMap(), listOf(0, 0, 0, 0))
            }

            // Aggregate all numbers from all contests
            val allNumbers = contests.flatMap { it.getAllNumbers() }

            // Calculate decades
            // Ensure labels respect the lottery max number (e.g., "20-25" for Lotofácil)
            val decadeBuckets =
                buildMap<String, Int> {
                    val start = (profile.minNumber / 10) * 10
                    var current = start
                    while (current <= profile.maxNumber) {
                        val end = minOf(current + 9, profile.maxNumber)
                        val label = "%02d-%02d".format(current, end)
                        put(label, 0)
                        current += 10
                    }
                }.toMutableMap()

            allNumbers.forEach { number ->
                val start = (number / 10) * 10
                val end = minOf(start + 9, profile.maxNumber)
                val label = "%02d-%02d".format(start, end)
                decadeBuckets[label] = (decadeBuckets[label] ?: 0) + 1
            }

            // Calculate quadrants
            // Need max number from profile to know where the center is
            val quadrants = StatisticsUtil.calculateQuadrantDistribution(allNumbers, profile.maxNumber)

            return DistributionStats(
                decadeDistribution = decadeBuckets.toSortedMap(),
                quadrantDistribution = quadrants.toList(),
            )
        }

        private fun buildHistoryGame(
            selectedNumbers: List<Int>,
            profile: LotteryProfile,
        ): Game =
            Game(
                id = TEMP_GAME_ID,
                lotteryType = profile.type,
                numbers = selectedNumbers,
                teamNumber = null,
                createdAt = TEMP_CREATED_AT,
            )

        companion object {
            private const val TEMP_GAME_ID = "temp"
            private const val TEMP_CREATED_AT = 1L
        }
    }
