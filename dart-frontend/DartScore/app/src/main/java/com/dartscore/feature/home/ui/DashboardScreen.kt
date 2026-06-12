package com.dartscore.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dartscore.feature.play.domain.MatchRecord

@Composable
fun DashboardScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            val p = ui.profile
            Text("Cześć, ${p?.displayName ?: "..."}", style = MaterialTheme.typography.headlineMedium)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("PPD", ui.profile?.ppd?.let { String.format("%.1f", it) } ?: "-", Modifier.weight(1f))
                StatCard("Win %", ui.profile?.let { "${it.winRate}%" } ?: "-", Modifier.weight(1f))
                StatCard("180s", ui.profile?.total180s?.toString() ?: "-", Modifier.weight(1f))
            }
        }
        item {
            Button(onClick = { showAddDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Dodaj mecz")
            }
        }
        item {
            Text("Ostatnie mecze (${ui.matches.size})", style = MaterialTheme.typography.titleMedium)
        }
        if (ui.matches.isEmpty()) {
            item { Text("Brak meczów. Dodaj pierwszy!", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        items(ui.matches, key = { it.id }) { match ->
            Card(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            if (match.won) "Wygrana z ${match.opponentName}" else "Porażka z ${match.opponentName}",
                            color = if (match.won) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        )
                        Text(
                            "${match.mode} • ${match.legsScore} • PPD ${String.format("%.1f", match.ppd)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddMatchDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { record ->
                viewModel.addMatch(record)
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun AddMatchDialog(onDismiss: () -> Unit, onConfirm: (MatchRecord) -> Unit) {
    var opponent by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf("501") }
    var won by remember { mutableStateOf(true) }
    var legs by remember { mutableStateOf("3-0") }
    var darts by remember { mutableStateOf("45") }
    var points by remember { mutableStateOf("480") }
    var oneEighties by remember { mutableStateOf("0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dodaj mecz") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(opponent, { opponent = it }, label = { Text("Przeciwnik") }, singleLine = true)
                OutlinedTextField(mode, { mode = it }, label = { Text("Tryb (501/301/101)") }, singleLine = true)
                OutlinedTextField(legs, { legs = it }, label = { Text("Wynik (np. 3-1)") }, singleLine = true)
                OutlinedTextField(darts, { darts = it }, label = { Text("Rzucone lotki") }, singleLine = true)
                OutlinedTextField(points, { points = it }, label = { Text("Zdobyte punkty") }, singleLine = true)
                OutlinedTextField(oneEighties, { oneEighties = it }, label = { Text("Liczba 180") }, singleLine = true)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Wygrana?")
                    Switch(checked = won, onCheckedChange = { won = it })
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(
                    MatchRecord(
                        mode = mode.toIntOrNull() ?: 501,
                        won = won,
                        opponentName = opponent.ifBlank { "Przeciwnik" },
                        legsScore = legs,
                        dartsThrown = darts.toIntOrNull() ?: 0,
                        pointsScored = points.toIntOrNull() ?: 0,
                        count180s = oneEighties.toIntOrNull() ?: 0,
                    )
                )
            }) { Text("Zapisz") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Anuluj") } },
    )
}