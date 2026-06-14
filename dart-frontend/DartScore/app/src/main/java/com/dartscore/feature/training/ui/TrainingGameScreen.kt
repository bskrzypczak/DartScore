package com.dartscore.feature.training.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dartscore.feature.training.domain.TrainingModes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingGameScreen(modeId: String?, onExit: () -> Unit) {
    val mode = TrainingModes.byId(modeId)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(mode?.name ?: "Trening") },
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wyjdź")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).padding(24.dp), Alignment.Center) {
            when (modeId) {
                "clock" -> AroundTheClockGame()
                "bob27" -> Bob27Game()
                "checkout" -> CheckoutGame()
                else -> Text("Nieznany tryb")
            }
        }
    }
}

@Composable
private fun BigValue(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
    }
}

// --- Around the Clock: trafiaj 1..20 po kolei ---
@Composable
private fun AroundTheClockGame() {
    var target by remember { mutableIntStateOf(1) }
    var darts by remember { mutableIntStateOf(0) }
    val finished = target > 20

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        if (!finished) {
            BigValue("Traf w", target.toString())
            Text("Rzutki: $darts", style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { target++; darts++ }, modifier = Modifier.weight(1f)) { Text("Trafione") }
                OutlinedButton(onClick = { darts++ }, modifier = Modifier.weight(1f)) { Text("Pudło") }
            }
        } else {
            BigValue("Ukończono!", "$darts rzutek")
            Button(onClick = { target = 1; darts = 0 }) { Text("Zagraj ponownie") }
        }
    }
}

// --- Bob's 27: rzucaj w duble D1..D20, podaj liczbę trafień (0-3) ---
@Composable
private fun Bob27Game() {
    var target by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(27) }
    val lost = score <= 0
    val done = target > 20 || lost

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        if (!done) {
            BigValue("Rzucasz w", "D$target")
            Text("Wynik: $score", style = MaterialTheme.typography.titleMedium)
            Text("Ile z 3 lotek trafiło?", style = MaterialTheme.typography.bodyMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (0..3).forEach { hits ->
                    Button(
                        onClick = {
                            score += if (hits == 0) -(2 * target) else hits * 2 * target
                            target++
                        },
                        modifier = Modifier.weight(1f),
                    ) { Text("$hits") }
                }
            }
        } else {
            BigValue(if (lost) "Przegrana" else "Koniec gry", "$score pkt")
            Button(onClick = { target = 1; score = 27 }) { Text("Jeszcze raz") }
        }
    }
}

// --- Trening checkoutów: zamknij kolejne wyniki, licz skuteczność ---
private val CHECKOUT_TARGETS = listOf(40, 60, 80, 100, 121, 36, 50, 24)

@Composable
private fun CheckoutGame() {
    var index by remember { mutableIntStateOf(0) }
    var success by remember { mutableIntStateOf(0) }
    val total = CHECKOUT_TARGETS.size
    val finished = index >= total

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        if (!finished) {
            BigValue("Zamknij", CHECKOUT_TARGETS[index].toString())
            Text("Próba ${index + 1} / $total  •  trafione: $success", style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { success++; index++ }, modifier = Modifier.weight(1f)) { Text("Zamknięte") }
                OutlinedButton(onClick = { index++ }, modifier = Modifier.weight(1f)) { Text("Nie udało się") }
            }
        } else {
            BigValue("Skuteczność", "${success * 100 / total}%")
            Text("$success / $total zamkniętych", style = MaterialTheme.typography.titleMedium)
            Button(onClick = { index = 0; success = 0 }) { Text("Jeszcze raz") }
        }
    }
}
