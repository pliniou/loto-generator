package com.cebolao.domain.model

import kotlinx.serialization.Serializable

/**
 * Representa um jogo salvo pelo usuário.
 *
 * Classe imutável por design (todas as propriedades são `val` com tipos imutáveis),
 * garantindo thread-safety e compatibilidade com Compose sem anotações Android.
 * Kotlin puro (sem dependências Android).
 *
 * @property id UUID único do jogo
 * @property lotteryType Modalidade
 * @property numbers Dezenas escolhidas
 * @property secondDrawNumbers Segundo sorteio (apenas Dupla Sena)
 * @property teamNumber Número do time (apenas Timemania, 1..80)
 * @property isPinned Se está fixado no topo
 * @property createdAt Timestamp de criação (Unix epoch millis)
 */
@Serializable
data class Game(
    val id: String,
    val lotteryType: LotteryType,
    val numbers: List<Int>,
    val secondDrawNumbers: List<Int>? = null,
    val teamNumber: Int? = null,
    val isPinned: Boolean = false,
    val createdAt: Long,
) {
    init {
        require(id.isNotBlank()) { "O ID do jogo não pode estar em branco" }
        require(numbers.isNotEmpty()) { "O jogo deve conter ao menos um número" }
        require(createdAt > 0) { "createdAt deve ser positivo" }
        teamNumber?.let { require(it > 0) { "teamNumber deve ser positivo" } }
    }

    /**
     * Cria cópia com pin toggle.
     */
    fun togglePin(): Game = copy(isPinned = !isPinned)
}

