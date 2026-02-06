package com.cebolao.app.feature.checker.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cebolao.app.theme.LocalSpacing
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
    availableContests: List<Contest>, // Should be filtered by query if VM does it, or we filter here
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val lotteryColor = LotteryColors.getColor(selectedType)

    // Filter locally if VM doesn't provide pre-filtered list (assuming passed list is all cached contests)
    // Actually VM has filtered cached contests? No, VM has `cachedContests` which is ALL.
    // Let's filter locally for responsiveness or assume VM handles it. 
    // Plan said VM handles filtering logic, but for simple dropdown search, local filter on a list of a few thousand items is fast enough.
    
    val filteredContests = remember(availableContests, searchQuery) {
        if (searchQuery.isBlank()) {
            availableContests
        } else {
            availableContests.filter {
                it.id.toString().contains(searchQuery) || it.drawDate.contains(searchQuery)
            }
        }
    }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = if (!isExpanded && currentContest != null) "Concurso ${currentContest.id} (${currentContest.drawDate})" else searchQuery,
            onValueChange = { 
                onQueryChange(it)
                if (!isExpanded) onExpandedChange(true) 
            },
            readOnly = false, // Allow typing to search
            label = { Text("Concurso") },
            placeholder = { Text("Buscar por número...") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = lotteryColor,
                focusedLabelColor = lotteryColor,
                focusedTrailingIconColor = lotteryColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryEditable, true)
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.heightIn(max = 250.dp)
        ) {
            if (filteredContests.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Nenhum concurso encontrado") },
                    onClick = { /* No-op */ }
                )
            } else {
                filteredContests.forEach { contest ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Concurso ${contest.id} - ${contest.drawDate}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = {
                            onContestSelected(contest)
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}
