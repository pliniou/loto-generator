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
 * @property isPinned Se está fixado no topo
 * @property createdAt Timestamp de criação (Unix epoch millis)
 * @property recentHitRate Taxa de acertos nos últimos concursos (0.0 a 1.0)
 * @property historicalHitRate Taxa de acertos histórica (0.0 a 1.0)
 * @property sourcePreset Nome do preset utilizado para gerar este jogo (opcional)
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
    val recentHitRate: Float = 0f,
    val historicalHitRate: Float = 0f,
    val sourcePreset: String? = null,
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
