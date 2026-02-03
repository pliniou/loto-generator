package com.cebolao.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrizeDto(
    @SerialName("faixa") val range: String = "", // ex: "15 acertos"
    @SerialName("numero_ganhadores") val winners: Int = 0,
    @SerialName("valor_premio") val prizeValue: Double = 0.0,
    @SerialName("descricao_faixa") val description: String? = null,
)
