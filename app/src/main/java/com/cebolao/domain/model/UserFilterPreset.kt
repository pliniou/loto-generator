package com.cebolao.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserFilterPreset(
    val name: String,
    val filters: List<GenerationFilter> = emptyList(),
    val filterConfigs: Map<GenerationFilter, FilterConfig> = emptyMap(),
    val description: String? = null,
) {
    init {
        require(name.isNotBlank()) { "Preset name cannot be blank" }
        require(name.length <= 48) { "Preset name is too long" }
    }
}
