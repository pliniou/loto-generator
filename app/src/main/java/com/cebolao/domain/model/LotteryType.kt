package com.cebolao.domain.model

import androidx.annotation.Keep

/**
 * Tipos de loteria suportados pelo app.
 * Enum Kotlin puro (sem dependências Android).
 */
@Keep
enum class LotteryType {
    LOTOFACIL,
    MEGA_SENA,
    QUINA,
    LOTOMANIA,
    DUPLA_SENA,
    TIMEMANIA,
    SUPER_SETE,
    DIA_DE_SORTE,
    MAIS_MILIONARIA,
}
