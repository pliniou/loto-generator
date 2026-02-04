package com.cebolao.app.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cebolao.R
import com.cebolao.app.theme.LocalSpacing

@Composable
fun TeamSelectionDialog(
    onDismissRequest: () -> Unit,
    onTeamSelected: (Int) -> Unit,
) {
    val spacing = LocalSpacing.current
    var searchQuery by remember { mutableStateOf("") }
    val teams = remember { com.cebolao.domain.util.TimemaniaUtil.teams }
    val filteredTeams =
        remember(searchQuery) {
            if (searchQuery.isEmpty()) {
                teams
            } else {
                teams.filter { it.name.contains(searchQuery, ignoreCase = true) }
            }
        }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        title = { Text(stringResource(R.string.team_select_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text(stringResource(R.string.team_search_placeholder)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(spacing.md))
                LazyColumn(
                    modifier = Modifier.height(400.dp),
                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    items(items = filteredTeams, key = { it.id }) { team ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onTeamSelected(team.id)
                                    onDismissRequest()
                                }
                                .padding(vertical = spacing.sm),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        ) {
                            Text(
                                text = team.name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = spacing.md),
                            )
                        }
                    }
                }
            }
        }
    )
}
