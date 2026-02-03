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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cebolao.app.ui.LotteryColors
import com.cebolao.domain.model.LotteryType

/**
 * Bolinha colorida com número.
 */
@Composable
fun LotteryBall(
    number: Int,
    lotteryType: LotteryType,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    style: androidx.compose.ui.text.TextStyle = androidx.compose.material3.MaterialTheme.typography.labelLarge,
) {
    val backgroundColor = LotteryColors.getColor(lotteryType)
    val contentColor = LotteryColors.getOnColor(lotteryType)

    Box(
        modifier =
            modifier
                .size(size)
                .clip(CircleShape)
                .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = number.toString().padStart(2, '0'),
            color = contentColor,
            style = style,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center), // Ensure centering
        )
    }
}

/**
 * Versão simplificada pequena para listas densas.
 */
@Composable
fun SmallLotteryBall(
    number: Int,
    lotteryType: LotteryType,
) {
    LotteryBall(
        number = number,
        lotteryType = lotteryType,
        size = 24.dp,
        style =
            androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
            ),
        // 10sp explicitly if needed or just labelSmall (11sp)
    )
}
