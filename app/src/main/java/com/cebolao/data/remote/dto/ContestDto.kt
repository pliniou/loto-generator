package com.cebolao.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO (objeto de transferência de dados) para representar concursos recebidos da API.
 * Baseado no formato comum de APIs de loteria (ex: loteriascaixa-api).
 */
@Serializable
data class ContestDto(
    @SerialName("concurso") val id: Int,
    @SerialName("data") val date: String, // Formato esperado: "dd/mm/yyyy"
    @SerialName("dezenas") val numbers: List<String>, // API pode retornar strings
    @SerialName("dezenas_2") val secondDrawNumbers: List<String>? = null, // Dupla sena (pode não vir ou ser null)
    @SerialName("time_coracao") val teamName: String? = null, // Timemania (nome do time)
    @SerialName("nome") val lotteryName: String? = null, // Para validação opcional
    @SerialName("proximoConcurso") val nextContest: Int? = null,
    @SerialName("dataProximoConcurso") val nextContestDate: String? = null,
    @SerialName("valorEstimadoProximoConcurso") val nextContestEstimatedPrize: Double? = null,
    @SerialName("acumulou") val accumulated: Boolean? = null,
    @SerialName("premiacoes") val prizeList: List<PrizeDto>? = null,
)
