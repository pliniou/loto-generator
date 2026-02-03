package com.cebolao.data.util

import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.rules.LotteryRulesRegistry

/**
 * Mapeamentos centralizados por tipo de loteria para evitar duplicação entre
 * fontes (assets) e integração de rede (API).
 *
 * Mantém a "single source of truth" dessas strings na camada de dados.
 *
 * Refatorado para usar LotteryRulesRegistry (Strategy Pattern).
 */
object LotteryTypeMappings {
    fun apiSlug(type: LotteryType): String = LotteryRulesRegistry.getRules(type).apiSlug

    fun assetFilename(type: LotteryType): String = LotteryRulesRegistry.getRules(type).assetFilename
}
