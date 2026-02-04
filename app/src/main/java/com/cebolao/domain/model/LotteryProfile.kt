package com.cebolao.domain.model

import kotlinx.serialization.Serializable

/**
 * Perfil/Configuração de uma modalidade de loteria.
 * Fonte de verdade para limites e características da modalidade.
 *
 * Esta classe é imutável por design (todas as propriedades são `val` com tipos imutáveis),
 * garantindo thread-safety e compatibilidade com Compose sem anotações Android.
 *
 * Os profiles são definidos em código (ver `AssetsReader.readProfile(...)`).
 * Kotlin puro (sem dependências Android).
 *
 * @property type Tipo da loteria
 * @property name Nome em PT-BR
 * @property minNumber Menor número possível
 * @property maxNumber Maior número possível
 * @property numbersPerGame Quantidade de dezenas por jogo
 * @property prizeRanges Faixas de premiação (ex: [15, 14, 13, 12, 11] para Lotofácil)
 * @property hasSecondDraw Se tem segundo sorteio (Dupla Sena)
 * @property hasTeam Se tem Time do Coração (Timemania)
 * @property isSuperSete Se é Super Sete (seleção por colunas)
 * @property costPerGame Custo em centavos (ex: 300 = R$ 3,00)
 */
@Serializable
data class LotteryProfile(
    val type: LotteryType,
    val name: String,
    val minNumber: Int,
    val maxNumber: Int,
    val numbersPerGame: Int,
    val prizeRanges: List<Int>,
    val hasSecondDraw: Boolean = false,
    val hasTeam: Boolean = false,
    val teamRangeStart: Int? = null, // Início do range de times (ex: 1 para Timemania)
    val teamRangeEnd: Int? = null, // Fim do range de times (ex: 80 para Timemania)
    val isSuperSete: Boolean = false,
    val costPerGame: Int = 300, // padrão R$ 3,00
    val probabilityOfWinning: String = "", // Ex: "1 em 50.063.860"
    val bolaoInfo: BolaoInfo? = null,
) {
    /**
     * Range de times (ex: 1..80 para Timemania), null se não tiver time.
     */
    @kotlinx.serialization.Transient
    val teamRange: IntRange? = if (teamRangeStart != null && teamRangeEnd != null) teamRangeStart..teamRangeEnd else null

    init {
        require(minNumber >= 0) { "minNumber deve ser >= 0" }
        require(maxNumber > minNumber) { "maxNumber deve ser > minNumber" }
        require(numbersPerGame > 0) { "numbersPerGame deve ser > 0" }
        require(numbersPerGame <= (maxNumber - minNumber + 1)) { "numbersPerGame deve ser <= tamanho do range" }
        require(prizeRanges.isNotEmpty()) { "prizeRanges não pode ser vazio" }
        if (hasTeam) {
            require(teamRange != null) { "teamRange deve ser definido quando hasTeam = true" }
            require(!teamRange.isEmpty()) { "teamRange não pode ser vazio" }
        }
    }

    /**
     * Retorna o range completo de números possíveis.
     */
    fun numberRange(): IntRange = minNumber..maxNumber

    /**
     * Valida se um conjunto de números é válido para esta modalidade.
     */
    fun isValidGame(numbers: List<Int>): Boolean {
        if (numbers.size != numbersPerGame) return false
        if (isSuperSete) {
            return numbers.all { it in numberRange() }
        }
        if (numbers.distinct().size != numbers.size) return false
        return numbers.all { it in numberRange() }
    }
}
