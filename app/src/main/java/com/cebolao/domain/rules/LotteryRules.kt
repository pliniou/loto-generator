package com.cebolao.domain.rules

import com.cebolao.domain.model.FilterPreset
import com.cebolao.domain.model.LotteryType

/**
 * Interface que define regras e configurações específicas de cada modalidade de loteria.
 * Implementação do Strategy Pattern para centralizar lógica por tipo.
 */
interface LotteryRules {
    /** Tipo da loteria */
    val type: LotteryType

    /** Nome do slug usado na API remota */
    val apiSlug: String

    /** Nome do arquivo de seed nos assets */
    val assetFilename: String

    /** Preset padrão para esta modalidade (pode ser null) */
    val defaultPreset: FilterPreset?
}
