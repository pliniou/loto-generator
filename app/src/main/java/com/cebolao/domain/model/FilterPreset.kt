package com.cebolao.domain.model

data class FilterPreset(
    val name: String = "",
    val filters: List<GenerationFilter>,
    val configs: Map<GenerationFilter, FilterConfig> = emptyMap(),
    val description: String? = null,
)
