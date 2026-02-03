package com.cebolao.domain.model

/**
 * Resultado da conferência de um jogo.
 *
 * @property hits Quantidade de acertos numéricos.
 * @property teamHit Se acertou o time (Timemania).
 * @property prizeTier Faixa de premiação alcançada (0 se nenhuma).
 */
data class CheckResult(
    val hits: Int,
    val teamHit: Boolean = false,
    val prizeTier: Int = 0, // 0 = sem prêmio
    val isPrize: Boolean = false, // Indica se é premiado (ex: Lotomania 0 hits)
)
