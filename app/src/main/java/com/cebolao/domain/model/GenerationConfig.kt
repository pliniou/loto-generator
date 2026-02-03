package com.cebolao.domain.model

import kotlinx.serialization.Serializable

/**
 * Configuração para geração de jogos via Engine.
 *
 * @property quantity Quantidade de jogos a gerar.
 * @property filters Filtros ativos para a geração.
 * @property filterConfigs Configuração específica por filtro.
 * @property fixedNumbers Números fixos que devem aparecer em todos os jogos.
 * @property fixedTeam Time do Coração fixo (apenas Timemania). Se null, gera aleatório.
 */
@Serializable
data class GenerationConfig(
    val quantity: Int = 1,
    val filters: List<GenerationFilter> = emptyList(),
    val filterConfigs: Map<GenerationFilter, FilterConfig> = emptyMap(),
    val fixedNumbers: List<Int> = emptyList(),
    val fixedTeam: Int? = null, // ID do time (1..80) para Timemania
)
