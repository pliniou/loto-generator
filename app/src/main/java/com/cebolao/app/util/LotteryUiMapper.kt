package com.cebolao.app.util

import androidx.annotation.StringRes
import com.cebolao.R
import com.cebolao.domain.model.LotteryType

/**
 * Mapeador de tipos de loteria para recursos de string.
 * Centraliza a internacionalização das modalidades.
 */
object LotteryUiMapper {
    @StringRes
    fun getNameRes(type: LotteryType): Int =
        when (type) {
            LotteryType.MEGA_SENA -> R.string.lottery_mega_sena
            LotteryType.LOTOFACIL -> R.string.lottery_lotofacil
            LotteryType.QUINA -> R.string.lottery_quina
            LotteryType.LOTOMANIA -> R.string.lottery_lotomania
            LotteryType.TIMEMANIA -> R.string.lottery_timemania
            LotteryType.DUPLA_SENA -> R.string.lottery_dupla_sena
            LotteryType.SUPER_SETE -> R.string.lottery_super_sete
        }
}
