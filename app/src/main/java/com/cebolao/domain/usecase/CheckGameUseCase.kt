package com.cebolao.domain.usecase

import com.cebolao.domain.model.CheckResult
import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.DuplaMode
import com.cebolao.domain.model.Game
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType
import javax.inject.Inject

class CheckGameUseCase
    @Inject
    constructor() {
        /**
         * Confere um jogo contra um resultado.
         */
        operator fun invoke(
            game: Game,
            contest: Contest,
            profile: LotteryProfile,
            duplaMode: DuplaMode = DuplaMode.BEST,
        ): CheckResult {
            // Validação básica de tipo
            if (game.lotteryType != contest.lotteryType) return CheckResult(0)

            val hits =
                when {
                    profile.isSuperSete -> countSuperSeteHits(game.numbers, contest.numbers)
                    profile.type == LotteryType.DUPLA_SENA -> {
                        // Dupla Sena: considerar modo selecionado
                        when (duplaMode) {
                            DuplaMode.FIRST -> game.numbers.intersect(contest.numbers.toSet()).size
                            DuplaMode.SECOND ->
                                game.numbers.intersect(
                                    (contest.secondDrawNumbers ?: emptyList()).toSet(),
                                ).size
                            DuplaMode.BEST -> {
                                val firstHits = game.numbers.intersect(contest.numbers.toSet()).size
                                val secondHits = game.numbers.intersect((contest.secondDrawNumbers ?: emptyList()).toSet()).size
                                maxOf(firstHits, secondHits)
                            }
                        }
                    }
                    else -> {
                        // Padrão: comparamos números do jogo com números do sorteio
                        game.numbers.intersect(contest.numbers.toSet()).size
                    }
                }

            var teamHit = false
            if (profile.hasTeam && game.teamNumber != null && contest.teamNumber != null) {
                teamHit = game.teamNumber == contest.teamNumber
            }

            // Calcular faixa de prêmio
            // Se hits está no range de prêmios, é premiado.
            // Para Lotomania, 0 está em prizeRanges, então hits=0 -> isPrize=true.
            val isPrize = profile.prizeRanges.contains(hits)
            val prizeTier = if (isPrize) hits else 0

            // Timemania: prêmio também por time
            val finalIsPrize = isPrize || teamHit

            return CheckResult(
                hits = hits,
                teamHit = teamHit,
                prizeTier = prizeTier,
                isPrize = finalIsPrize,
            )
        }

        private fun countSuperSeteHits(
            gameNumbers: List<Int>,
            resultNumbers: List<Int>,
        ): Int {
            var hits = 0
            val size = minOf(gameNumbers.size, resultNumbers.size)
            for (i in 0 until size) {
                if (gameNumbers[i] == resultNumbers[i]) hits++
            }
            return hits
        }
    }
