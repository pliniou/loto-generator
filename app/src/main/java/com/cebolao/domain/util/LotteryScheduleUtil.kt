package com.cebolao.domain.util

import com.cebolao.domain.model.LotteryType
import java.time.DayOfWeek

object LotteryScheduleUtil {
    private val schedule =
        mapOf(
            DayOfWeek.SUNDAY to emptyList(),
            DayOfWeek.MONDAY to listOf(LotteryType.DUPLA_SENA, LotteryType.LOTOFACIL, LotteryType.LOTOMANIA, LotteryType.QUINA, LotteryType.SUPER_SETE),
            DayOfWeek.TUESDAY to listOf(LotteryType.LOTOFACIL, LotteryType.MEGA_SENA, LotteryType.QUINA, LotteryType.TIMEMANIA),
            DayOfWeek.WEDNESDAY to listOf(LotteryType.DUPLA_SENA, LotteryType.LOTOFACIL, LotteryType.LOTOMANIA, LotteryType.QUINA, LotteryType.SUPER_SETE),
            DayOfWeek.THURSDAY to listOf(LotteryType.LOTOFACIL, LotteryType.MEGA_SENA, LotteryType.QUINA, LotteryType.TIMEMANIA),
            DayOfWeek.FRIDAY to listOf(LotteryType.DUPLA_SENA, LotteryType.LOTOFACIL, LotteryType.LOTOMANIA, LotteryType.QUINA, LotteryType.SUPER_SETE),
            DayOfWeek.SATURDAY to listOf(LotteryType.LOTOFACIL, LotteryType.MEGA_SENA, LotteryType.QUINA, LotteryType.TIMEMANIA),
        )

    fun getLotteriesForDay(dayOfWeek: DayOfWeek): List<LotteryType> {
        return schedule[dayOfWeek] ?: emptyList()
    }

    fun getWeeklySchedule(): List<DaySchedule> {
        val days =
            listOf(
                DayOfWeek.SUNDAY to "Dom",
                DayOfWeek.MONDAY to "Seg",
                DayOfWeek.TUESDAY to "Ter",
                DayOfWeek.WEDNESDAY to "Qua",
                DayOfWeek.THURSDAY to "Qui",
                DayOfWeek.FRIDAY to "Sex",
                DayOfWeek.SATURDAY to "Sáb",
            )

        return days.map { (day, name) ->
            DaySchedule(
                dayOfWeek = day,
                name = name,
                lotteries = getLotteriesForDay(day),
            )
        }
    }
}

data class DaySchedule(
    val dayOfWeek: DayOfWeek,
    val name: String,
    val lotteries: List<LotteryType>,
)
