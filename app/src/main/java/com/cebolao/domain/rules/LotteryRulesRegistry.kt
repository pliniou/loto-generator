package com.cebolao.domain.rules

import com.cebolao.domain.model.GenerationFilter
import com.cebolao.domain.model.LotteryType

// Implementações concretas para cada modalidade

internal object MegaSenaRules : LotteryRules {
    override val type = LotteryType.MEGA_SENA
    override val apiSlug = "mega-sena"
    override val assetFilename = "mega_sena.json"
    override val defaultPreset =
        com.cebolao.domain.model.FilterPreset(
            filters =
                listOf(
                    GenerationFilter.REPEATED_FROM_PREVIOUS,
                    GenerationFilter.PARITY_BALANCE,
                ),
        )
}

internal object LotofacilRules : LotteryRules {
    override val type = LotteryType.LOTOFACIL
    override val apiSlug = "lotofacil"
    override val assetFilename = "lotofacil.json"
    override val defaultPreset =
        com.cebolao.domain.model.FilterPreset(
            filters =
                listOf(
                    GenerationFilter.MOLDURA_MIOLO,
                    GenerationFilter.PARITY_BALANCE,
                ),
        )
}

internal object QuinaRules : LotteryRules {
    override val type = LotteryType.QUINA
    override val apiSlug = "quina"
    override val assetFilename = "quina.json"
    override val defaultPreset = null
}

internal object LotomaniaRules : LotteryRules {
    override val type = LotteryType.LOTOMANIA
    override val apiSlug = "lotomania"
    override val assetFilename = "lotomania.json"
    override val defaultPreset = null
}

internal object TimemaniaRules : LotteryRules {
    override val type = LotteryType.TIMEMANIA
    override val apiSlug = "timemania"
    override val assetFilename = "timemania.json"
    override val defaultPreset = null
}

internal object DuplaSenaRules : LotteryRules {
    override val type = LotteryType.DUPLA_SENA
    override val apiSlug = "duplasena"
    override val assetFilename = "dupla_sena.json"
    override val defaultPreset = null
}

internal object SuperSeteRules : LotteryRules {
    override val type = LotteryType.SUPER_SETE
    override val apiSlug = "supersete"
    override val assetFilename = "super_sete.json"
    override val defaultPreset = null
}

/**
 * Registry centralizado de regras por modalidade.
 * Elimina a necessidade de `when(lotteryType)` espalhado pelo código.
 */
object LotteryRulesRegistry {
    private val registry: Map<LotteryType, LotteryRules> =
        mapOf(
            LotteryType.MEGA_SENA to MegaSenaRules,
            LotteryType.LOTOFACIL to LotofacilRules,
            LotteryType.QUINA to QuinaRules,
            LotteryType.LOTOMANIA to LotomaniaRules,
            LotteryType.TIMEMANIA to TimemaniaRules,
            LotteryType.DUPLA_SENA to DuplaSenaRules,
            LotteryType.SUPER_SETE to SuperSeteRules,
        )

    /**
     * Obtém as regras para um tipo de loteria.
     * @throws IllegalArgumentException se o tipo não for suportado
     */
    fun getRules(type: LotteryType): LotteryRules = registry[type] ?: error("Tipo de loteria não suportado: $type")

    fun supportedTypes(): List<LotteryType> =
        LotteryType.entries.filter { type -> registry.containsKey(type) }
}
