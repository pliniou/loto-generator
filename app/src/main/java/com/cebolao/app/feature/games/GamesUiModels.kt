package com.cebolao.app.feature.games

import com.cebolao.domain.model.Game

data class SavedGameCardUiState(
    val game: Game,
    val recentHitRateProgress: Float,
    val recentHitRatePercent: Int,
    val showRecentHitRate: Boolean,
)

internal fun Game.toSavedGameCardUiState(): SavedGameCardUiState {
    val normalizedProgress = recentHitRate.coerceIn(0f, 1f)
    return SavedGameCardUiState(
        game = this,
        recentHitRateProgress = normalizedProgress,
        recentHitRatePercent = (normalizedProgress * 100).toInt(),
        showRecentHitRate = normalizedProgress > 0f,
    )
}
