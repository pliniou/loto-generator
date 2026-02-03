package com.cebolao.app.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.cebolao.R
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.domain.util.TimemaniaUtil

@Composable
fun TeamSelectionDialog(
    onDismissRequest: () -> Unit,
    onTeamSelected: (Int) -> Unit,
) {
    val spacing = LocalSpacing.current
    var searchQuery by remember { mutableStateOf("") }
    val allTeams = remember { TimemaniaUtil.teams }

    val filteredTeams =
        remember(searchQuery) {
            if (searchQuery.isBlank()) {
                allTeams
            } else {
                allTeams.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                        it.uf.contains(searchQuery, ignoreCase = true)
                }
            }
        }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f),
        ) {
            Column(
                modifier = Modifier.padding(spacing.lg),
            ) {
                // Cabeçalho
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.team_select_title),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.action_close))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Busca
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(stringResource(R.string.team_search_placeholder)) },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(spacing.sm))

                // Lista
                LazyColumn {
                    items(filteredTeams) { team ->
                        ListItem(
                            headlineContent = { Text(team.name) },
                            supportingContent = {
                                Text(stringResource(R.string.team_code, team.uf, team.id))
                            },
                            modifier =
                                Modifier.clickable {
                                    onTeamSelected(team.id)
                                    onDismissRequest()
                                },
                        )
                    }
                }
            }
        }
    }
}
