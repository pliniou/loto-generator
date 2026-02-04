package com.cebolao.domain.model

import kotlinx.serialization.Serializable

/**
 * Contém informações detalhadas sobre uma modalidade de loteria.
 * @property type O tipo da loteria.
 * @property howToPlay Texto explicativo de como jogar.
 * @property drawFrequency Quando ocorrem os sorteios.
 * @property betsInfo Informações sobre apostas (quantidade de números, teimosinha, surpresinha).
 * @property probabilityInfo Texto ou dados sobre probabilidades de ganho.
 * @property bolaoInfo Explicação sobre como funciona o bolão.
 * @property prizeAllocation Como a premiação é distribuída.
 */
@Serializable
data class LotteryInfo(
    val type: LotteryType,
    val howToPlay: String,
    val drawFrequency: String,
    val betsInfo: String,
    val probabilityInfo: List<ProbabilityRow>,
    val bolaoInfo: String,
    val prizeAllocation: String
)

@Serializable
data class ProbabilityRow(
    val numbersPlayed: Int,
    val probability: String // Ex: "1 em 50.063.860"
)
