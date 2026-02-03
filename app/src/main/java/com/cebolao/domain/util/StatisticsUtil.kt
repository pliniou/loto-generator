package com.cebolao.domain.util

import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.GameInsight

object StatisticsUtil {
    /**
     * Gera insights completos sobre um jogo proposto.
     */
    fun analyzeGame(
        numbers: List<Int>,
        lastContest: Contest?,
        hotNumbers: List<Int> = emptyList(),
    ): GameInsight {
        if (numbers.isEmpty()) {
            return GameInsight(
                sum = 0,
                evenCount = 0,
                oddCount = 0,
                repeatsFromLast = 0,
                hotNumbersCount = 0,
            )
        }

        val sum = numbers.sum()
        val evens = numbers.count { it % 2 == 0 }
        val odds = numbers.size - evens
        val average = numbers.average()

        val repeats =
            if (lastContest != null) {
                numbers.intersect(lastContest.getAllNumbers().toSet()).size
            } else {
                0
            }

        val hotCount = numbers.intersect(hotNumbers.toSet()).size

        // Calcula a maior sequência consecutiva
        val longestSequence = calculateLongestSequence(numbers.sorted())

        // Conta múltiplos de 3
        val multiplesOf3 = numbers.count { it % 3 == 0 }

        // Conta números primos
        val primeCount = numbers.count { isPrime(it) }

        return GameInsight(
            sum = sum,
            evenCount = evens,
            oddCount = odds,
            repeatsFromLast = repeats,
            hotNumbersCount = hotCount,
            average = average,
            longestSequence = longestSequence,
            multiplesOf3 = multiplesOf3,
            primeCount = primeCount,
        )
    }

    /**
     * Calcula a maior sequência de números consecutivos
     */
    private fun calculateLongestSequence(sortedNumbers: List<Int>): Int {
        if (sortedNumbers.isEmpty()) return 0

        var maxSequence = 1
        var currentSequence = 1

        for (i in 1 until sortedNumbers.size) {
            if (sortedNumbers[i] == sortedNumbers[i - 1] + 1) {
                currentSequence++
                maxSequence = maxOf(maxSequence, currentSequence)
            } else {
                currentSequence = 1
            }
        }

        return maxSequence
    }

    /**
     * Verifica se um número é primo
     */
    private fun isPrime(n: Int): Boolean {
        if (n < 2) return false
        if (n == 2) return true
        if (n % 2 == 0) return false
        for (i in 3..kotlin.math.sqrt(n.toDouble()).toInt() step 2) {
            if (n % i == 0) return false
        }
        return true
    }

    /**
     * Calcula distribuição por dezenas (0-9, 10-19, etc.)
     */
    fun calculateDecadeDistribution(numbers: List<Int>): Map<Int, Int> {
        return numbers.groupingBy { it / 10 }.eachCount()
    }

    /**
     * Calcula distribuição por quadrantes para matriz visual
     */
    fun calculateQuadrantDistribution(numbers: List<Int>, maxNumber: Int): IntArray {
        val half = maxNumber / 2
        var topLeft = 0
        var topRight = 0
        var bottomLeft = 0
        var bottomRight = 0

        numbers.forEach { n ->
            val isLeft = (n - 1) % 10 < 5
            val isTop = n <= half

            when {
                isTop && isLeft -> topLeft++
                isTop && !isLeft -> topRight++
                !isTop && isLeft -> bottomLeft++
                else -> bottomRight++
            }
        }

        return intArrayOf(topLeft, topRight, bottomLeft, bottomRight)
    }
}
