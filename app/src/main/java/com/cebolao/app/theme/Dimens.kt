package com.cebolao.app.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
 * Sistema modernizado com níveis refinados para profundidade visual.
 */
object CebolaoElevation {
    val none: Dp = 0.dp
    val level1: Dp = 1.dp
    val level2: Dp = 2.dp
    val level3: Dp = 4.dp
    val level4: Dp = 6.dp
    val level5: Dp = 8.dp
    val level6: Dp = 12.dp
    val level7: Dp = 16.dp
    val level8: Dp = 24.dp
}

/**
 * Tokens de border radius para componentes.
 * Sistema consistente para todos os elementos.
 */
data class CebolaoCornerRadius(
    val none: Dp = 0.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 20.dp,
    val xxl: Dp = 28.dp,
    val xxxl: Dp = 36.dp,
    val full: Dp = 9999.dp, // Círculo perfeito
)

/**
 * Dimensões de componentes específicos.
 * Garante consistência visual em todo o app.
 */
object AnimationDurations {
    const val fast = 200
    const val medium = 300
    const val slow = 500
}

/**
 * Dimensões de componentes específicos.
 * Garante consistência visual em todo o app.
 */
object ComponentDimensions {
    // Botões
    val buttonHeightSmall = 40.dp
    val buttonHeightMedium = 48.dp
    val buttonHeightLarge = 56.dp
    val buttonHeightExtraLarge = 64.dp
    val generatorButtonHeight = 64.dp
    val bottomBarHeight = 56.dp
    val bottomContentPadding = 120.dp
    
    // Cards
    val cardPaddingSmall = 12.dp
    val cardPaddingMedium = 16.dp
    val cardPaddingLarge = 20.dp
    
    // Ícones
    val iconSizeSmall = 20.dp
    val iconSizeMedium = 24.dp
    val iconSizeLarge = 32.dp
    val iconSizeExtraLarge = 48.dp
    
    // Bolinhas (Lottery Balls)
    val ballSizeLarge = 32.dp
    val ballSizeSmall = 24.dp
    val ballTextSizeSmall = 10.sp

    // Avatares
    val avatarSizeSmall = 32.dp
    val avatarSizeMedium = 40.dp
    val avatarSizeLarge = 56.dp
    
    // Dividers
    val dividerThicknessThin = 0.5.dp
    val dividerThicknessMedium = 1.dp
    val dividerThicknessThick = 2.dp
    
    // Stroke widths
    val strokeWidthThin = 0.5.dp
    val strokeWidthMedium = 1.dp
    val strokeWidthThick = 2.dp
}

/**
 * Layout constraints para conteúdo.
 */
object CebolaoLayout {
    val contentMaxWidth: Dp = 840.dp
    val contentMinWidth: Dp = 320.dp
}

val LocalSpacing = compositionLocalOf { CebolaoSpacing() }
