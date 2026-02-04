package com.cebolao.domain.usecase

import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.Game
import com.cebolao.domain.model.DistributionStats
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
        private val tempGameId = "temp"
        private val tempCreatedAt = 1L

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
            val counts = mutableMapOf<Int, Int>()
            val lastSeen = mutableMapOf<Int, Int>()

            // Intervalo de números da loteria
            val allNumbers = (profile.minNumber..profile.maxNumber).toList()

            // Inicializa maps
            allNumbers.forEach {
                counts[it] = 0
                lastSeen[it] = 0 // 0 significa nunca visto ou muito antigo
            }

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
            val hitCounts = mutableMapOf<Int, Int>() // acertos -> quantidade

            contests.forEach { contest ->
                val result =
                    checkGameUseCase(
                        game =
                            Game(
                                id = tempGameId,
                                lotteryType = profile.type,
                                numbers = selectedNumbers,
                                teamNumber = null,
                                createdAt = tempCreatedAt,
                            ),
                        contest = contest,
                        profile = profile,
                    )
                val hits = result.hits
                // Só conta se for relevante (ex: > 0 ou > faixa mínima de prêmio, mas usuário quer ver estatística geral)
                // Vamos contar todos > 0 para ter gráfico bonito, ou filtrar pelos que premiam?
                // O requisito diz "número de acertos por faixa de premiação".
                // Vamos guardar todos os hits para flexibilidade.
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
            // This needs to be smarter to handle different lotteries ranges
            // For now, simple 0-9, 10-19 grouping
            val decades =
                allNumbers
                    .groupingBy { (it / 10) * 10 }
                    .eachCount()
                    .mapKeys { (start, _) ->
                        val end = start + 9
                        "$start-$end"
                    }
                    .toSortedMap()

            // Calculate quadrants
            // Need max number from profile to know where the center is
            val quartrants = StatisticsUtil.calculateQuadrantDistribution(allNumbers, profile.maxNumber)

            return DistributionStats(
                decadeDistribution = decades.toMap(),
                quadrantDistribution = quartrants.toList(),
            )
        }
    }
