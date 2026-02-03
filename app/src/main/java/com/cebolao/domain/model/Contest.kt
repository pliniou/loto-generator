package com.cebolao.domain.model

import kotlinx.serialization.Serializable

/**
 * Representa um concurso sorteado.
 *
 * Classe imutável por design (todas as propriedades são `val` com tipos imutáveis),
 * garantindo thread-safety e compatibilidade com Compose sem anotações Android.
 * Kotlin puro (sem dependências Android).
 */
@Serializable
data class Contest(
    val id: Int,
    val lotteryType: LotteryType,
    val drawDate: String,
    val numbers: List<Int>,
    val secondDrawNumbers: List<Int>? = null,
    val teamNumber: Int? = null,
    val nextContest: Int? = null,
    val nextContestDate: String? = null,
    val nextContestEstimatedPrize: Double? = null,
    val accumulated: Boolean = false,
    val prizeList: List<Prize>? = null,
) {
    init {
        require(numbers.isNotEmpty()) { "O concurso deve conter ao menos um número" }
        require(id > 0) { "O ID do concurso deve ser positivo" }
        teamNumber?.let { require(it in 1..80) { "teamNumber deve estar entre 1 e 80" } }
    }

    /**
     * Retorna todos os números do concurso.
     * Para Dupla Sena, retorna união do 1º e 2º sorteio.
     */
    fun getAllNumbers(): List<Int> =
        if (secondDrawNumbers != null) {
            (numbers + secondDrawNumbers).distinct().sorted()
        } else {
            numbers
        }
}

/**
 * Representa uma faixa de premiação de um concurso.
 *
 * Classe imutável por design.
 * Kotlin puro (sem dependências Android).
 */
@Serializable
data class Prize(
    val range: String, // ex: "15 acertos"
    val winners: Int,
    val prizeValue: Double,
    val description: String? = null,
)

