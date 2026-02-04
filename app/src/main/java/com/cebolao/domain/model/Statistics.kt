package com.cebolao.domain.model

data class NumberStat(
    val number: Int,
    val frequency: Int,
    val delay: Int, // Concursos desde a última aparição
)

data class PrizeStat(
    val hits: Int,
    val count: Int,
)

data class GameInsight(
    val sum: Int,
    val evenCount: Int,
    val oddCount: Int,
    val repeatsFromLast: Int,
    val hotNumbersCount: Int, // Números que saíram muito recentemente
    val average: Double = 0.0,
    val longestSequence: Int = 0,
    val multiplesOf3: Int = 0,
    val primeCount: Int = 0,
)

data class DistributionStats(
    val decadeDistribution: Map<String, Int>, // Key: "00-09", Value: count
    val quadrantDistribution: List<Int>, // TopLeft, TopRight, BottomLeft, BottomRight
)
