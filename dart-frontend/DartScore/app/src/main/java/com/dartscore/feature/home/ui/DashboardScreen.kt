package com.dartscore.feature.home.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dartscore.core.designsystem.Accent
import com.dartscore.core.designsystem.BorderGray
import com.dartscore.core.designsystem.LightGrayText
import com.dartscore.core.designsystem.LossColor
import com.dartscore.core.designsystem.WinColor
import com.dartscore.feature.play.domain.MatchRecord


@Composable
fun DashboardScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Text(
                "Cześć, ${ui.profile?.displayName ?: "..."}",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                StatBox(ui.profile?.ppd?.let { "%.1f".format(it) } ?: "-", "PPD", Modifier.weight(1f))
                StatBox(ui.profile?.let { "${it.winRate}%" } ?: "-", "WIN %", Modifier.weight(1f))
                StatBox(ui.profile?.total180s?.toString() ?: "-", "180s", Modifier.weight(1f))
            }
        }
        item {
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.Black),
            ) { Text("DODAJ MECZ", fontWeight = FontWeight.Bold, letterSpacing = 1.sp) }
        }
        item { SectionLabel("OSTATNIE MECZE (${ui.matches.size})") }
        if (ui.matches.isEmpty()) {
            item { Text("Brak meczów. Dodaj pierwszy!", color = LightGrayText) }
        }
        items(ui.matches, key = { it.id }) { match -> MatchRow(match) }
    }

    if (showAddDialog) {
        AddMatchDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { record -> viewModel.addMatch(record); showAddDialog = false },
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, color = LightGrayText, fontSize = 12.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun StatBox(value: String, label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.border(1.dp, BorderGray, RoundedCornerShape(12.dp)).padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = Accent, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(label, color = LightGrayText, fontSize = 10.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}

@Composable
private fun MatchRow(match: MatchRecord) {
    Box(
        Modifier.fillMaxWidth().border(1.dp, BorderGray, RoundedCornerShape(12.dp)).padding(16.dp),
    ) {
        Column {
            Text(
                if (match.won) "Wygrana z ${match.opponentName}" else "Porażka z ${match.opponentName}",
                color = if (match.won) WinColor else LossColor,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "${match.mode} • ${match.legsScore} • PPD ${"%.1f".format(match.ppd)}",
                color = LightGrayText,
                fontSize = 13.sp,
            )
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
            Button(
                onClick = {
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
                },
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.Black),
            ) { Text("Zapisz") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Anuluj", color = LightGrayText) } },
    )
}