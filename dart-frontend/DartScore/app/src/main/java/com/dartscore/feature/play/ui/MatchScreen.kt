package com.dartscore.feature.play.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dartscore.feature.play.domain.Dart
import com.dartscore.feature.play.domain.GamePlayer
import com.dartscore.feature.play.domain.Multiplier

@Composable
fun MatchScreen(
    onFinish: () -> Unit,
    onQuit: () -> Unit,
    viewModel: MatchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val s = state

    LaunchedEffect(s?.isFinished) {
        if (s?.isFinished == true) onFinish()
    }
    if (s == null) {
        Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Brak aktywnej gry") }
        return
    }

    var showScanner by remember { mutableStateOf(false) }
    if (showScanner) {
        ScanScreen(
            onCancel = { showScanner = false },
            // TODO (następny krok): wyślij zdjęcie do backendu i zamień odczyt na lotki.
            onPhotoConfirmed = { showScanner = false },
        )
        return
    }

    var multiplier by remember { mutableStateOf(Multiplier.SINGLE) }
    val current = s.currentPlayer

    LazyColumn(
        Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Mecz ${s.config.mode.label}", style = MaterialTheme.typography.titleLarge)
                Row {
                    IconButton(onClick = { showScanner = true }) {
                        Icon(Icons.Outlined.Star, contentDescription = "Skanuj tarczę")
                    }
                    OutlinedButton(onClick = { viewModel.leaveGame(); onQuit() }) { Text("Wyjdź") }
                }
            }
        }

        items(s.players, key = { it.id }) { player ->
            PlayerCard(player, isCurrent = player.id == current.id)
        }

        // Podpowiedzi checkoutu (gdy <= 180).
        if (current.remaining in 2..180) {
            item {
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    if (viewModel.getCheckoutHint(current.remaining)!= null){
                        Text(
                            "Sugerowany checkout: ${viewModel.getCheckoutHint(current.remaining)}",
                            Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        item {
            // Bieżąca tura: 3 sloty.
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { i ->
                    val dart = s.currentTurn.getOrNull(i)
                    Card(Modifier.weight(1f)) {
                        Box(Modifier.fillMaxWidth().padding(vertical = 12.dp), Alignment.Center) {
                            Text(dart?.label ?: "–", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }

        if (current.isBot) {
            item { Text("Bot rzuca...", Modifier.padding(8.dp), color = MaterialTheme.colorScheme.primary) }
        } else {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Multiplier.entries.forEach { m ->
                        FilterChip(
                            selected = multiplier == m,
                            onClick = { multiplier = m },
                            label = { Text(if (m == Multiplier.SINGLE) "Single" else if (m == Multiplier.DOUBLE) "Double" else "Triple") },
                        )
                    }
                }
            }
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.fillMaxWidth().height(260.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    gridItems((1..20).toList()) { n ->
                        ScoreButton("$n") {
                            viewModel.addDart(Dart(n, multiplier)); multiplier = Multiplier.SINGLE
                        }
                    }
                    item {
                        ScoreButton("25") {
                            // Bull: tripla nie istnieje -> traktuj jako single/double
                            val m = if (multiplier == Multiplier.TRIPLE) Multiplier.DOUBLE else multiplier
                            viewModel.addDart(Dart(25, m)); multiplier = Multiplier.SINGLE
                        }
                    }
                    item { ScoreButton("Miss") { viewModel.addDart(Dart(0, Multiplier.SINGLE)) } }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { viewModel.undo() }, enabled = s.currentTurn.isNotEmpty(), modifier = Modifier.weight(1f)) {
                        Text("Cofnij")
                    }
                    Button(onClick = { viewModel.confirm() }, enabled = s.currentTurn.isNotEmpty(), modifier = Modifier.weight(1f)) {
                        Text("Zatwierdź")
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerCard(player: GamePlayer, isCurrent: Boolean) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
            else MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(player.name + if (isCurrent) " • teraz" else "", fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal)
                Text("Śr. 3D: ${"%.1f".format(player.threeDartAvg)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val last = player.lastThrows
                    repeat(3) { i -> Text(last.getOrNull(i)?.toString() ?: "–", style = MaterialTheme.typography.bodySmall) }
                }
            }
            Text("${player.remaining}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ScoreButton(label: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.height(48.dp)) { Text(label) }
}