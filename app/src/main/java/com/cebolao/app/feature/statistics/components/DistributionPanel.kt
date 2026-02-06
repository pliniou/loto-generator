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
import androidx.compose.ui.text.style.TextAlign
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
        Text(
            text = "Distribuição por Dezenas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(spacing.sm))
        Text(
            text = "Faixas adaptadas ao limite da modalidade (ex.: Lotofácil termina em 25, logo o último grupo é 20-25).",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(spacing.xs))

        val maxDecadeCount = stats.decadeDistribution.values.maxOrNull() ?: 1
        val topRange = stats.decadeDistribution.maxByOrNull { it.value }
        val totalDecades = stats.decadeDistribution.values.sum().coerceAtLeast(1)

        topRange?.let {
            Text(
                text = "Faixa mais frequente: ${it.key} (${it.value} ocorrências)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(spacing.xs))
        }

        stats.decadeDistribution.forEach { (range, count) ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = range,
                    modifier = Modifier.width(72.dp),
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
                    text = "${count} (${(count * 100f / totalDecades).toInt()}%)",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.width(64.dp),
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

@Composable
fun QuadrantsPanel(
    quadrants: List<Int>,
    modifier: Modifier = Modifier,
) {
    if (quadrants.size < 4) return
    val spacing = LocalSpacing.current
    val total = quadrants.sum().toFloat().coerceAtLeast(1f)
    val maxIndex = quadrants.indices.maxByOrNull { quadrants[it] } ?: 0
    val labels = listOf("1º", "2º", "3º", "4º")

    Column(modifier = modifier) {
        Text(
            text = "Quadrantes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(spacing.sm))
        Text(
            text = "Dividimos o volante em 2 metades (superior/inferior) e 2 colunas (dezenas terminadas em 1-5 na esquerda, 6-0 na direita). Em Lotofácil isso equivale a um grid 5x5.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "Exemplo de mapeamento: Q1 = topo+coluna esquerda (1–5 / 11–15 / ...), Q2 = topo+direita, Q3 = base+esquerda, Q4 = base+direita.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(spacing.sm))
        Text(
            text = "Quadrante mais cheio: ${labels[maxIndex]} (${quadrants[maxIndex]} dezenas)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(spacing.xs))

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
                        QuadrantCell(count = quadrants[0], total = total, modifier = Modifier.weight(1f), isMax = maxIndex == 0) // TopLeft
                        QuadrantCell(count = quadrants[1], total = total, modifier = Modifier.weight(1f), isMax = maxIndex == 1) // TopRight
                    }
                    Row(modifier = Modifier.weight(1f)) {
                        QuadrantCell(count = quadrants[2], total = total, modifier = Modifier.weight(1f), isMax = maxIndex == 2) // BottomLeft
                        QuadrantCell(count = quadrants[3], total = total, modifier = Modifier.weight(1f), isMax = maxIndex == 3) // BottomRight
                    }
                }
            }

            // Legend
            Column(modifier = Modifier.weight(1f)) {
                Text("1º: ${quadrants[0]} (${(quadrants[0] / total * 100).toInt()}%)", style = MaterialTheme.typography.bodySmall)
                Text("2º: ${quadrants[1]} (${(quadrants[1] / total * 100).toInt()}%)", style = MaterialTheme.typography.bodySmall)
                Text("3º: ${quadrants[2]} (${(quadrants[2] / total * 100).toInt()}%)", style = MaterialTheme.typography.bodySmall)
                Text("4º: ${quadrants[3]} (${(quadrants[3] / total * 100).toInt()}%)", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun QuadrantCell(
    count: Int,
    total: Float,
    isMax: Boolean,
    modifier: Modifier = Modifier,
) {
    val alpha = (count / total).coerceIn(0.1f, 1f)
    val baseColor = if (isMax) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
    Box(
        modifier =
            modifier
                .background(baseColor.copy(alpha = alpha))
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
