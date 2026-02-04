package com.cebolao.domain.model

/**
 * Constantes para geração de jogos.
 */
object GenerationConstants {
    /**
     * Número máximo de tentativas ao gerar jogos antes de desistir.
     * Este valor pode ser sobrescrito ao chamar o use case.
     */
    const val DEFAULT_MAX_RETRY = 5000
}
