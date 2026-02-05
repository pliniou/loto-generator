package com.cebolao.app.feature.statistics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.domain.model.DistributionStats

@Composable
fun DistributionPanel(
    stats: DistributionStats,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    Column(modifier = modifier) {
        // Didactics
        Text(
            text = "Distribuição por Dezenas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(spacing.sm))

        val maxDecadeCount = stats.decadeDistribution.values.maxOrNull() ?: 1

        stats.decadeDistribution.forEach { (range, count) ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = range,
                    modifier = Modifier.width(60.dp),
                    style = MaterialTheme.typography.labelMedium,
                )
                LinearProgressIndicator(
                    progress = { count / maxDecadeCount.toFloat() },
                    modifier = Modifier.weight(1f).height(8.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Spacer(modifier = Modifier.width(spacing.xs))
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.lg))

        Text(
            text = "Quadrantes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(spacing.sm))

        if (stats.quadrantDistribution.size >= 4) {
            val q = stats.quadrantDistribution
            val total = q.sum().toFloat().coerceAtLeast(1f)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                // Visual 2x2 Grid Representation
                Card(
                    modifier = Modifier.weight(1f).aspectRatio(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Column(modifier = Modifier.fillMaxHeight()) {
                        Row(modifier = Modifier.weight(1f)) {
                            QuadrantCell(count = q[0], total = total, modifier = Modifier.weight(1f)) // TopLeft
                            QuadrantCell(count = q[1], total = total, modifier = Modifier.weight(1f)) // TopRight
                        }
                        Row(modifier = Modifier.weight(1f)) {
                            QuadrantCell(count = q[2], total = total, modifier = Modifier.weight(1f)) // BottomLeft
                            QuadrantCell(count = q[3], total = total, modifier = Modifier.weight(1f)) // BottomRight
                        }
                    }
                }

                // Legend
                Column(modifier = Modifier.weight(1f)) {
                    Text("1º: ${q[0]} (${(q[0] / total * 100).toInt()}%)", style = MaterialTheme.typography.bodySmall)
                    Text("2º: ${q[1]} (${(q[1] / total * 100).toInt()}%)", style = MaterialTheme.typography.bodySmall)
                    Text("3º: ${q[2]} (${(q[2] / total * 100).toInt()}%)", style = MaterialTheme.typography.bodySmall)
                    Text("4º: ${q[3]} (${(q[3] / total * 100).toInt()}%)", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun QuadrantCell(
    count: Int,
    total: Float,
    modifier: Modifier = Modifier,
) {
    val alpha = (count / total).coerceIn(0.1f, 1f)
    Box(
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
                .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = count.toString(),
            color = if (alpha > 0.5f) Color.White else Color.Black,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
