package com.dartscore.feature.play.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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

@Composable
fun VictoryScreen(
    onBackToHome: () -> Unit,
    viewModel: MatchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val s = state
    var saved by remember { mutableStateOf(false) }

    if (s == null || !s.isFinished) {
        Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Brak wyniku") }
        return
    }

    val winner = s.winner
    val me = s.players.firstOrNull()
    val iWon = winner?.id == me?.id

    // Duel zapisuje się automatycznie raz po wejściu na ekran.
    LaunchedEffect(Unit) {
        if (s.config.autoSave && !saved) {
            viewModel.saveResult()
            saved = true
        }
    }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            if (iWon) "Zwycięstwo!" else "Koniec gry",
            style = MaterialTheme.typography.headlineLarge,
            color = if (iWon) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
        )
        Text("Wygrywa: ${winner?.name ?: "-"}", style = MaterialTheme.typography.titleLarge)

        // Klasyfikacja: najmniej pozostałych punktów = wyżej.
        s.players.sortedBy { it.remaining }.forEachIndexed { index, p ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${index + 1}. ${p.name}", fontWeight = FontWeight.Bold)
                    Text("śr. ${"%.1f".format(p.threeDartAvg)} • lotki ${p.dartsThrown} • 180: ${p.count180s}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (s.config.autoSave) {
            Text(
                if (saved) "Mecz zapisany." else "Zapisywanie...",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Button(
                onClick = { viewModel.saveResult(); saved = true },
                enabled = !saved,
                modifier = Modifier.fillMaxWidth(),
            ) { Text(if (saved) "Zapisano" else "Zapisz mecz") }
        }

        OutlinedButton(
            onClick = { viewModel.leaveGame(); onBackToHome() },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Powrót do Dashboard") }
    }
}