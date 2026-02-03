package com.cebolao.app.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Sistema de espaçamento e dimensões do Cebolão.
 * Baseado em escala de 4dp para consistência visual.
 */
data class CebolaoSpacing(
    val none: Dp = 0.dp,
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 48.dp,
    val huge: Dp = 64.dp,
)

/**
 * Tokens de elevação para componentes.
 * Seguem diretrizes Material 3 para profundidade visual.
 */
data class CebolaoElevation(
    val none: Dp = 0.dp,
    val level1: Dp = 1.dp,
    val level2: Dp = 3.dp,
    val level3: Dp = 6.dp,
    val level4: Dp = 8.dp,
    val level5: Dp = 12.dp,
)

/**
 * Tokens de border radius para componentes.
 */
data class CebolaoCornerRadius(
    val none: Dp = 0.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 20.dp,
    val xxl: Dp = 28.dp,
    val full: Dp = 9999.dp, // Círculo perfeito
)

/**
 * Layout constraints para conteúdo.
 */
object CebolaoLayout {
    val contentMaxWidth: Dp = 840.dp
}

val LocalSpacing = compositionLocalOf { CebolaoSpacing() }
