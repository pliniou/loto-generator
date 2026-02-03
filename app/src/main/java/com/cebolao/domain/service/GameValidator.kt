package com.cebolao.domain.service

import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.FilterConfig
import com.cebolao.domain.model.Game
import com.cebolao.domain.model.GenerationFilter
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

private typealias FilterValidator = (game: Game, profile: LotteryProfile, lastContest: Contest?, config: FilterConfig?) -> Boolean

@Singleton
class GameValidator
    @Inject
    constructor() {
        private val filterValidators: Map<GenerationFilter, FilterValidator> =
            mapOf(
                GenerationFilter.PARITY_BALANCE to { game, _, _, config -> validateParity(game, config) },
                GenerationFilter.MULTIPLES_OF_3 to { game, _, _, _ -> validateMultiplesOf3(game) },
                GenerationFilter.REPEATED_FROM_PREVIOUS to { game, _, lastContest, config ->
                    validateRepeatLimit(game, lastContest, config)
                },
                GenerationFilter.MOLDURA_MIOLO to { game, profile, _, _ -> validateMolduraMiolo(game, profile) },
                GenerationFilter.PRIME_NUMBERS to { game, _, _, _ -> validatePrimes(game) },
            )

        private val defaultMinParityRatio = 0.2
        private val defaultMaxParityRatio = 0.8

        private val lotofacilFrameNumbers =
            setOf(
                1, 2, 3, 4, 5,
                6, 10, 11, 15, 16, 20,
                21, 22, 23, 24, 25,
            )

        /**
         * Validação com filtros configuráveis.
         */
        fun validate(
            game: Game,
            filters: List<GenerationFilter>,
            profile: LotteryProfile,
            lastContest: Contest? = null,
            configs: Map<GenerationFilter, FilterConfig> = emptyMap(),
        ): Boolean = firstFailingFilter(game, filters, profile, lastContest, configs) == null

        /**
         * Retorna o primeiro filtro que rejeita o jogo, ou null se todos passarem.
         */
        fun firstFailingFilter(
            game: Game,
            filters: List<GenerationFilter>,
            profile: LotteryProfile,
            lastContest: Contest? = null,
            configs: Map<GenerationFilter, FilterConfig> = emptyMap(),
        ): GenerationFilter? {
            if (game.numbers.isEmpty() || filters.isEmpty()) return null

            return filters.asSequence()
                .filter { it.isApplicable(profile) }
                .firstOrNull { filter ->
                    val validator = filterValidators[filter] ?: return@firstOrNull false
                    val config = configs[filter]
                    !validator(game, profile, lastContest, config)
                }
        }

        private fun validateParity(
            game: Game,
            cfg: FilterConfig? = null,
        ): Boolean {
            if (game.lotteryType == LotteryType.SUPER_SETE) return true

            val numbers = game.numbers
            if (numbers.isEmpty()) return false

            val total = numbers.size
            val evens = numbers.count { it % 2 == 0 }
            val ratio = evens.toDouble() / total

            val min = cfg?.minParityRatio ?: defaultMinParityRatio
            val max = cfg?.maxParityRatio ?: defaultMaxParityRatio
            return ratio in min..max
        }

        private fun validateMultiplesOf3(game: Game): Boolean {
            if (game.lotteryType == LotteryType.SUPER_SETE) return true
            val numbers = game.numbers
            val count = numbers.count { it % 3 == 0 }
            return count != 0 && count != numbers.size
        }

        private fun validateRepeatLimit(
            game: Game,
            lastContest: Contest?,
            cfg: FilterConfig? = null,
        ): Boolean {
            lastContest ?: return true

            val previousNumbers = lastContest.getAllNumbers().toSet()
            val repeats = game.numbers.count(previousNumbers::contains)
            val maxRepeats = cfg?.maxRepeatsFromPrevious ?: (game.numbers.size - 2)
            return repeats <= maxRepeats
        }

        private fun validateMolduraMiolo(
            game: Game,
            profile: LotteryProfile,
        ): Boolean {
            if (profile.type != LotteryType.LOTOFACIL) return true
            val numbers = game.numbers
            val containsFrame = numbers.any { it in lotofacilFrameNumbers }
            val containsInner = numbers.any { it !in lotofacilFrameNumbers }
            return containsFrame && containsInner
        }

        private fun validatePrimes(game: Game): Boolean {
            return game.numbers.any(::isPrime)
        }

        private fun isPrime(value: Int): Boolean {
            if (value < 2) return false
            val limit = sqrt(value.toDouble()).toInt()
            for (candidate in 2..limit) {
                if (value % candidate == 0) return false
            }
            return true
        }
    }
