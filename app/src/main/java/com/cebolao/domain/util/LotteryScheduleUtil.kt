package com.cebolao.domain.util

import com.cebolao.domain.model.LotteryType
import java.util.Calendar

object LotteryScheduleUtil {
    private val schedule =
        mapOf(
            Calendar.SUNDAY to emptyList(),
            Calendar.MONDAY to listOf(LotteryType.DUPLA_SENA, LotteryType.LOTOFACIL, LotteryType.LOTOMANIA, LotteryType.QUINA, LotteryType.SUPER_SETE),
            Calendar.TUESDAY to listOf(LotteryType.LOTOFACIL, LotteryType.MEGA_SENA, LotteryType.QUINA, LotteryType.TIMEMANIA),
            Calendar.WEDNESDAY to listOf(LotteryType.DUPLA_SENA, LotteryType.LOTOFACIL, LotteryType.LOTOMANIA, LotteryType.QUINA, LotteryType.SUPER_SETE),
            Calendar.THURSDAY to listOf(LotteryType.LOTOFACIL, LotteryType.MEGA_SENA, LotteryType.QUINA, LotteryType.TIMEMANIA),
            Calendar.FRIDAY to listOf(LotteryType.DUPLA_SENA, LotteryType.LOTOFACIL, LotteryType.LOTOMANIA, LotteryType.QUINA, LotteryType.SUPER_SETE),
            Calendar.SATURDAY to listOf(LotteryType.LOTOFACIL, LotteryType.MEGA_SENA, LotteryType.QUINA, LotteryType.TIMEMANIA),
        )

    fun getLotteriesForDay(dayOfWeek: Int): List<LotteryType> {
        return schedule[dayOfWeek] ?: emptyList()
    }

    /**
     * Retorna lista de pares (DiaSemana, Lista<Lottery>) para a semana atual ou genérica.
     * Retorna nomes curtos: "Dom", "Seg", etc.
     */
    fun getWeeklySchedule(): List<DaySchedule> {
        val days =
            listOf(
                Calendar.SUNDAY to "Dom",
                Calendar.MONDAY to "Seg",
                Calendar.TUESDAY to "Ter",
                Calendar.WEDNESDAY to "Qua",
                Calendar.THURSDAY to "Qui",
                Calendar.FRIDAY to "Sex",
                Calendar.SATURDAY to "Sáb",
            )

        return days.map { (calDay, name) ->
            DaySchedule(
                dayOfWeekConstant = calDay,
                name = name,
                lotteries = getLotteriesForDay(calDay),
            )
        }
    }
}

data class DaySchedule(
    val dayOfWeekConstant: Int,
    val name: String,
    val lotteries: List<LotteryType>,
)
