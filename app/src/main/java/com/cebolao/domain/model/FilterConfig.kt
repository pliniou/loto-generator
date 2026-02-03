package com.cebolao.domain.model

import kotlinx.serialization.Serializable

/**
 * Configuração específica de filtros de geração.
 */
@Serializable
data class FilterConfig(
    val minParityRatio: Double? = null, // ex.: 0.2
    val maxParityRatio: Double? = null, // ex.: 0.8
    val maxRepeatsFromPrevious: Int? = null, // ex.: 4
)
