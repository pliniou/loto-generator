package com.cebolao.app.feature.checker.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cebolao.app.theme.LotteryColors
import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.LotteryType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckerContestSelector(
    currentContest: Contest?,
    selectedType: LotteryType,
    isExpanded: Boolean,
    searchQuery: String,
    onExpandedChange: (Boolean) -> Unit,
    onQueryChange: (String) -> Unit,
    onContestSelected: (Contest) -> Unit,
    availableContests: List<Contest>,
    modifier: Modifier = Modifier,
) {
    val lotteryColor = LotteryColors.getColor(selectedType)
    val filteredContests = remember(availableContests, searchQuery) {
        if (searchQuery.isBlank()) {
            availableContests
        } else {
            availableContests.filter { it.id.toString().contains(searchQuery) || it.drawDate.contains(searchQuery) }
        }
    }
    val fieldValue =
        if (!isExpanded && currentContest != null) {
            "Concurso ${currentContest.id} (${currentContest.drawDate})"
        } else {
            searchQuery
        }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = fieldValue,
            onValueChange = {
                onQueryChange(it)
                if (!isExpanded) onExpandedChange(true)
            },
            readOnly = false,
            label = { Text("Concurso") },
            placeholder = { Text("Buscar por número...") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = lotteryColor,
                focusedLabelColor = lotteryColor,
                focusedTrailingIconColor = lotteryColor,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = true),
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.heightIn(max = 250.dp),
        ) {
            if (filteredContests.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Nenhum concurso encontrado") },
                    onClick = {},
                )
            } else {
                filteredContests.forEach { contest ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Concurso ${contest.id} - ${contest.drawDate}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        },
                        onClick = { onContestSelected(contest) },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}
