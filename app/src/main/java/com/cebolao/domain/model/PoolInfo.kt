package com.cebolao.domain.model

import kotlinx.serialization.Serializable

/**
 * Informações sobre Bolão (Loterias CAIXA).
 *
 * @property minPoolPrice Preço mínimo do bolão em centavos.
 * @property minSharePrice Preço mínimo da cota em centavos.
 * @property minShares Quantidade mínima de cotas.
 * @property maxShares Quantidade máxima de cotas permitida.
 */
@Serializable
data class PoolInfo(
    val minPoolPrice: Int,
    val minSharePrice: Int,
    val minShares: Int,
    val maxShares: Int,
)
