package com.cebolao.data.remote.api

import com.cebolao.data.remote.dto.ContestDto
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interface Retrofit para a API de Loterias.
 * URL base esperada: https://loteriascaixa-api.herokuapp.com/api/
 */
interface LotteryApi {
    /**
     * Obtém o último concurso de uma modalidade específica.
     * @param lotteryType Slug da loteria (ex: "lotofacil", "mega-sena")
     */
    @GET("{lotteryType}/latest") // Ajustado para rota /latest comum em APIs intermediárias
    suspend fun getLatestContest(
        @Path("lotteryType") lotteryType: String,
    ): ContestDto

    @GET("{lotteryType}/{contestNumber}")
    suspend fun getContest(
        @Path("lotteryType") lotteryType: String,
        @Path("contestNumber") number: Int,
    ): ContestDto
}
