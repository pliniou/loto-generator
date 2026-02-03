package com.cebolao.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Filtros disponíveis para a geração de jogos.
 */
@Serializable
enum class GenerationFilter {
    MULTIPLES_OF_3,
    REPEATED_FROM_PREVIOUS,

    @SerialName("MOLDURA_MI0LO") // compatibilidade: nome antigo persistido em presets
    MOLDURA_MIOLO, // Lotofácil: mistura de moldura e miolo
    PRIME_NUMBERS,
    PARITY_BALANCE,
    ;

    fun isApplicable(profile: LotteryProfile): Boolean =
        when (this) {
            MOLDURA_MIOLO -> profile.type == LotteryType.LOTOFACIL
            PARITY_BALANCE -> !profile.isSuperSete
            MULTIPLES_OF_3 -> !profile.isSuperSete
            PRIME_NUMBERS -> true
            REPEATED_FROM_PREVIOUS -> true
        }
}
