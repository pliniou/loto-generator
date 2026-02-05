package com.cebolao.domain

object Constants {
    const val DEFAULT_MAX_RETRY = 5000

    // Timemania
    val TIMEMANIA_TEAM_RANGE = 1..80

    // Game Validation Defaults
    const val DEFAULT_MIN_PARITY_RATIO = 0.2
    const val DEFAULT_MAX_PARITY_RATIO = 0.8

    val LOTOFACIL_FRAME_NUMBERS =
        setOf(
            1, 2, 3, 4, 5,
            6, 10, 11, 15, 16, 20,
            21, 22, 23, 24, 25,
        )
}
