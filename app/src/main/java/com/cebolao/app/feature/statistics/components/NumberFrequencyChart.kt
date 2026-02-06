package com.cebolao.app.feature.statistics.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import com.cebolao.domain.model.NumberStat

@Composable
fun NumberFrequencyChart(
    stats: List<NumberStat>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    highlightMaxNumbers: Set<Int> = emptySet(),
    highlightMinNumbers: Set<Int> = emptySet(),
) {
    if (stats.isEmpty()) return

    val density = LocalDensity.current
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val onSurfaceVariantArgb = onSurfaceVariantColor.toArgb()
    val onSurfaceArgb = MaterialTheme.colorScheme.onSurface.toArgb()
    val errorColor = MaterialTheme.colorScheme.error.copy(alpha = 0.75f)
    val avgLineColor = onSurfaceVariantColor.copy(alpha = 0.6f)
    val labelSmallTextSizePx = with(density) { MaterialTheme.typography.labelSmall.fontSize.toPx() }
    val textPaint =
        remember(onSurfaceVariantArgb, labelSmallTextSizePx) {
            Paint().apply {
                color = onSurfaceVariantArgb
                textSize = labelSmallTextSizePx
                textAlign = Paint.Align.CENTER
            }
        }

    val highlightPaint =
        remember(textPaint, onSurfaceArgb) {
            Paint(textPaint).apply {
                isFakeBoldText = true
                color = onSurfaceArgb
            }
        }

    val maxFreq = remember(stats) { (stats.maxOfOrNull { it.frequency }?.toFloat() ?: 1f).coerceAtLeast(1f) }
    val avgFreq = remember(stats) { stats.map { it.frequency }.average().toFloat() }
    val avgRatio = avgFreq / maxFreq
    // Sort by number to display in order (1, 2, 3...) not by freq
    val sortedStats = remember(stats) { stats.sortedBy { it.number } }
    val labelEvery =
        remember(sortedStats) {
            when {
                sortedStats.size > 80 -> 10
                sortedStats.size > 50 -> 5
                sortedStats.size > 30 -> 3
                else -> 1
            }
        }
    val animatedRatios =
        sortedStats.map { stat ->
            animateFloatAsState(
                targetValue = (stat.frequency / maxFreq).coerceIn(0f, 1f),
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
                label = "freq_${stat.number}",
            )
        }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp).height(220.dp)) {
            val barWidth = size.width / (sortedStats.size * 1.5f)
            val spacing = barWidth * 0.5f
            val maxBarHeight = size.height - 56.dp.toPx() // Leave space for labels and avg line

            // Average line
            val avgY = size.height - 20.dp.toPx() - (maxBarHeight * avgRatio)
            drawLine(
                color = avgLineColor,
                start = Offset(0f, avgY),
                end = Offset(size.width, avgY),
                strokeWidth = 1.dp.toPx(),
            )
            drawContext.canvas.nativeCanvas.drawText(
                "média ${avgFreq.toInt()}",
                8.dp.toPx(),
                avgY - 4.dp.toPx(),
                textPaint,
            )

            sortedStats.forEachIndexed { index, stat ->
                val freqRatio = animatedRatios[index].value
                val barHeight = maxBarHeight * freqRatio
                val x = index * (barWidth + spacing)
                val y = size.height - barHeight - 20.dp.toPx()

                val isAboveAvg = stat.frequency.toFloat() >= avgFreq
                val barColorForStat =
                    when {
                        stat.number in highlightMaxNumbers -> barColor
                        stat.number in highlightMinNumbers -> errorColor
                        isAboveAvg -> barColor.copy(alpha = 0.9f)
                        else -> barColor.copy(alpha = 0.4f)
                    }

                // Draw bar
                drawRect(
                    color = barColorForStat,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                )

                // Draw frequency value above highlighted bars
                if (stat.number in highlightMaxNumbers || stat.number in highlightMinNumbers) {
                    drawContext.canvas.nativeCanvas.drawText(
                        stat.frequency.toString(),
                        x + barWidth / 2,
                        y - 4.dp.toPx(),
                        highlightPaint,
                    )
                }

                // Draw number label below
                if (index % labelEvery == 0 || index == sortedStats.lastIndex) {
                    drawContext.canvas.nativeCanvas.drawText(
                        stat.number.toString(),
                        x + barWidth / 2,
                        size.height,
                        textPaint,
                    )
                }
            }
        }
    }
}
