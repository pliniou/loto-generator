package com.cebolao.domain.rules

import com.cebolao.domain.model.FilterConfig
import com.cebolao.domain.model.FilterPreset
import com.cebolao.domain.model.GenerationFilter
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType

/**
 * Exposes filter presets.
 */
object FilterPresets {
    // Legacy preset definitions (kept for compatibility)
    internal val lotofacilPreset =
        FilterPreset(
            name = "Lotofácil",
            filters = listOf(GenerationFilter.MOLDURA_MIOLO, GenerationFilter.PARITY_BALANCE),
            configs =
                mapOf(
                    GenerationFilter.PARITY_BALANCE to FilterConfig(minParityRatio = 0.3, maxParityRatio = 0.7),
                ),
            description = "Prefere mistura entre moldura e miolo; paridade equilibrada",
        )

    internal val megasenaPreset =
        FilterPreset(
            name = "Mega-Sena",
            filters = listOf(GenerationFilter.PARITY_BALANCE, GenerationFilter.MULTIPLES_OF_3),
            configs =
                mapOf(
                    GenerationFilter.PARITY_BALANCE to FilterConfig(minParityRatio = 0.2, maxParityRatio = 0.8),
                ),
            description = "Filtro básico para balancear par/ímpar e múltiplos de 3",
        )

    internal val quinaPreset =
        FilterPreset(
            name = "Quina",
            filters = listOf(GenerationFilter.PARITY_BALANCE, GenerationFilter.MULTIPLES_OF_3),
            configs = mapOf(GenerationFilter.PARITY_BALANCE to FilterConfig(minParityRatio = 0.2, maxParityRatio = 0.8)),
            description = "Filtro básico para Quina: paridade e múltiplos de 3",
        )

    internal val timemaniaPreset =
        FilterPreset(
            name = "Timemania",
            filters = listOf(GenerationFilter.PARITY_BALANCE),
            configs = mapOf(GenerationFilter.PARITY_BALANCE to FilterConfig(minParityRatio = 0.25, maxParityRatio = 0.75)),
            description = "Preserva equilíbrio de pares/ímpares (Timemania)",
        )

    internal val superSetePreset =
        FilterPreset(
            name = "Super Sete",
            filters = listOf(GenerationFilter.PRIME_NUMBERS),
            configs = emptyMap(),
            description = "Super Sete: garante presença de números primos nas colunas",
        )

    private val legacyPresetsByType: Map<LotteryType, FilterPreset> =
        mapOf(
            LotteryType.LOTOFACIL to lotofacilPreset,
            LotteryType.MEGA_SENA to megasenaPreset,
            LotteryType.QUINA to quinaPreset,
            LotteryType.TIMEMANIA to timemaniaPreset,
            LotteryType.SUPER_SETE to superSetePreset,
        )

    fun presetForProfile(profile: LotteryProfile): FilterPreset? =
        LotteryRulesRegistry.getRules(profile.type).defaultPreset
            ?: legacyPresetsByType[profile.type]
}
