package com.cebolao.domain.usecase

import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.Game
import com.cebolao.domain.model.GenerationConfig
import com.cebolao.domain.Constants
import com.cebolao.domain.model.GenerationFilter
import com.cebolao.domain.model.GenerationReport
import com.cebolao.domain.model.GenerationResult
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.service.GameValidator
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

/**
 * Use case responsible for generating new games.
 */
class GenerateGamesUseCase
    @Inject
    constructor(
        private val gameValidator: GameValidator,
    ) {
        operator fun invoke(
            profile: LotteryProfile,
            config: GenerationConfig,
            lastContest: Contest? = null,
            maxRetry: Int = Constants.DEFAULT_MAX_RETRY,
            random: Random = Random,
        ): GenerationResult {
            validateConfig(profile, config)
            val fixedNumbers = config.fixedNumbers.distinct()

            val games = mutableListOf<Game>()
            var attempts = 0
            var rejectedByFilter = 0
            val rejectedPerFilter = mutableMapOf<GenerationFilter, Int>()
            val rejectedExamples = mutableMapOf<GenerationFilter, MutableList<List<Int>>>()

            while (games.size < config.quantity && attempts < maxRetry) {
                attempts++
                val numbers = if (profile.isSuperSete) generateSuperSete(random) else generateStandard(profile, fixedNumbers, random)
                val team = generateTeam(profile, config, random)

                val candidate =
                    Game(
                        id = UUID.randomUUID().toString(),
                        lotteryType = profile.type,
                        numbers = numbers,
                        teamNumber = team,
                        createdAt = System.currentTimeMillis(),
                    )

                // Evita duplicatas no lote (números + time quando aplicável)
                val isDuplicate =
                    games.any { existing ->
                        existing.numbers == candidate.numbers && (existing.teamNumber == candidate.teamNumber)
                    }

                if (isDuplicate) continue

                // Filtra usando GameValidator
                val failing = gameValidator.firstFailingFilter(candidate, config.filters, profile, lastContest, config.filterConfigs)
                if (failing != null) {
                    rejectedByFilter++
                    rejectedPerFilter[failing] = (rejectedPerFilter[failing] ?: 0) + 1
                    // registra exemplos para ajudar o diagnóstico, mantendo até 3 exemplos por filtro
                    val examplesList = rejectedExamples.getOrPut(failing) { mutableListOf() }
                    if (examplesList.size < 3) examplesList.add(candidate.numbers)
                    continue
                }

                games.add(candidate)
            }
            val partial = games.size < config.quantity
            val report =
                GenerationReport(
                    attempts = attempts,
                    generated = games.size,
                    rejectedByFilter = rejectedByFilter,
                    rejectedPerFilter = rejectedPerFilter,
                    rejectedExamples = rejectedExamples.mapValues { it.value.toList() },
                    partial = partial,
                )

            return GenerationResult(games = games, report = report)
        }

        private fun generateStandard(
            profile: LotteryProfile,
            fixedNumbers: List<Int>,
            random: Random,
        ): List<Int> {
            val available = (profile.minNumber..profile.maxNumber).filter { !fixedNumbers.contains(it) }
            val needed = profile.numbersPerGame - fixedNumbers.size

            require(needed >= 0) { "fixedNumbers não pode exceder numbersPerGame" }
            require(available.size >= needed) { "Quantidade de números disponíveis insuficiente para completar o jogo" }

            if (needed == 0) return fixedNumbers.sorted()

            val randomPart = available.shuffled(random).take(needed)
            return (fixedNumbers + randomPart).sorted()
        }

        private fun generateSuperSete(random: Random): List<Int> {
            // 7 colunas, 0..9 em cada
            return List(7) {
                random.nextInt(0, 10)
            }
        }

        private fun generateTeam(
            profile: LotteryProfile,
            config: GenerationConfig,
            random: Random,
        ): Int? {
            if (!profile.hasTeam) return null
            val teamRange = requireNotNull(profile.teamRange) { "teamRange deve estar definido para loterias com time" }

            // Se tem time fixo na config, usa ele. Senão gera aleatório do teamRange
            return config.fixedTeam ?: random.nextInt(teamRange.first, teamRange.last + 1)
        }

        private fun validateConfig(
            profile: LotteryProfile,
            config: GenerationConfig,
        ) {
            require(config.quantity > 0) { "quantity deve ser maior que zero" }

            if (profile.isSuperSete && config.fixedNumbers.isNotEmpty()) {
                throw IllegalArgumentException("fixedNumbers não é suportado para Super Sete (seleção por coluna)")
            }

            val fixedNumbers = config.fixedNumbers
            require(fixedNumbers.size == fixedNumbers.distinct().size) { "fixedNumbers não pode conter duplicados" }
            require(fixedNumbers.all { it in profile.numberRange() }) { "fixedNumbers fora do range permitido" }
            require(fixedNumbers.size <= profile.numbersPerGame) { "fixedNumbers não pode exceder numbersPerGame" }

            if (profile.hasTeam && config.fixedTeam != null) {
                val teamRange = requireNotNull(profile.teamRange) { "teamRange deve estar definido" }
                require(config.fixedTeam in teamRange) { "fixedTeam deve estar entre ${teamRange.first} e ${teamRange.last}" }
            }
        }
    }
