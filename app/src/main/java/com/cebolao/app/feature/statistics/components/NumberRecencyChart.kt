package com.cebolao.app.feature.statistics.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import com.cebolao.domain.model.NumberStat

@Composable
fun NumberRecencyChart(
    stats: List<NumberStat>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.tertiary,
    highlightColor: Color = MaterialTheme.colorScheme.error,
    highlightedNumbers: Set<Int> = emptySet(),
    topLabeledNumbers: Set<Int> = emptySet(),
) {
    if (stats.isEmpty()) return

    val density = LocalDensity.current
    val onSurfaceVariantArgb = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val labelSmallTextSizePx = with(density) { MaterialTheme.typography.labelSmall.fontSize.toPx() }
    val textPaint =
        remember(onSurfaceVariantArgb, labelSmallTextSizePx) {
            Paint().apply {
                color = onSurfaceVariantArgb
                textSize = labelSmallTextSizePx
                textAlign = Paint.Align.CENTER
            }
        }

    val maxDelay = remember(stats) { (stats.maxOfOrNull { it.delay }?.toFloat() ?: 1f).coerceAtLeast(1f) }
    val sortedStats = remember(stats) { stats.sortedBy { it.number } }
    var selected by remember(stats) { mutableStateOf<NumberStat?>(null) }
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
                targetValue = (stat.delay / maxDelay).coerceIn(0f, 1f),
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
                label = "delay_${stat.number}",
            )
        }

    Box(modifier = modifier) {
        Canvas(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .height(220.dp)
                    .pointerInput(sortedStats) {
                        detectTapGestures { offset ->
                            val totalBars = sortedStats.size
                            val barWidth = size.width / (totalBars * 1.3f)
                            val spacing = barWidth * 0.3f
                            val group = barWidth + spacing
                            val index = (offset.x / group).toInt().coerceIn(0, sortedStats.lastIndex)
                            selected = sortedStats.getOrNull(index)
                        }
                    },
        ) {
            val barWidth = size.width / (sortedStats.size * 1.3f)
            val spacing = barWidth * 0.3f
            val maxBarHeight = size.height - 42.dp.toPx()

            sortedStats.forEachIndexed { index, stat ->
                val delayRatio = animatedRatios[index].value
                val barHeight = maxBarHeight * delayRatio
                val x = index * (barWidth + spacing)
                val y = size.height - barHeight - 20.dp.toPx()

                val isHighlighted = stat.number in highlightedNumbers

                drawRect(
                    color = if (isHighlighted) highlightColor else barColor.copy(alpha = 0.75f),
                    topLeft = Offset(x, y),
                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                )

                if (stat.number in topLabeledNumbers) {
                    drawContext.canvas.nativeCanvas.drawText(
                        stat.number.toString(),
                        x + barWidth / 2,
                        y - 6.dp.toPx(),
                        textPaint,
                    )
                }

                // Label number every 5 entries to avoid clutter
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

        AnimatedVisibility(visible = selected != null) {
            selected?.let { stat ->
                val delayLabel =
                    if (stat.delay < 0) {
                        "Ainda não saiu neste recorte"
                    } else {
                        "Não aparece há ${stat.delay} concursos"
                    }
                Text(
                    text = "Dezena ${stat.number} • $delayLabel",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier =
                        Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        }
    }
}
