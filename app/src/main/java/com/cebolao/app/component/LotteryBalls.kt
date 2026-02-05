package com.cebolao.app.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.CebolaoElevation
import com.cebolao.app.theme.ComponentDimensions
import com.cebolao.app.theme.LotteryColors
import com.cebolao.domain.model.LotteryType

/**
 * Bolinha colorida com número.
 * Otimizada com animações suaves e sombras elegantes.
 */
@Composable
fun LotteryBall(
    number: Int,
    lotteryType: LotteryType,
    modifier: Modifier = Modifier,
    size: Dp = ComponentDimensions.ballSizeLarge,
    style: androidx.compose.ui.text.TextStyle = androidx.compose.material3.MaterialTheme.typography.labelLarge,
) {
    val backgroundColor = LotteryColors.getColor(lotteryType)
    val contentColor = LotteryColors.getOnColor(lotteryType)

    // Sem animação para valores constantes
    val elevation = CebolaoElevation.level2
    val animatedBackgroundColor = backgroundColor

    Box(
        modifier =
            modifier
                .size(size)
                .clip(CircleShape)
                .shadow(elevation, CircleShape, spotColor = backgroundColor.copy(alpha = AlphaLevels.GHOST))
                .background(animatedBackgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = number.toString().padStart(2, '0'),
            color = contentColor,
            style = style,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

/**
 * Versão simplificada pequena para listas densas.
 * Otimizada para performance com tamanho reduzido.
 */
@Composable
fun SmallLotteryBall(
    number: Int,
    lotteryType: LotteryType,
) {
    LotteryBall(
        number = number,
        lotteryType = lotteryType,
        size = ComponentDimensions.ballSizeSmall,
        style =
            androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(
                fontSize = ComponentDimensions.ballTextSizeSmall,
            ),
    )
}
