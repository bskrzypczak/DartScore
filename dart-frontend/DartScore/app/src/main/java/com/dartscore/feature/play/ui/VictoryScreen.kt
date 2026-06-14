package com.dartscore.feature.play.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dartscore.core.designsystem.Accent
import com.dartscore.core.designsystem.BorderGray
import com.dartscore.core.designsystem.LightGrayText

@Composable
fun VictoryScreen(
    onBackToHome: () -> Unit,
    viewModel: MatchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val s = state
    var saved by remember { mutableStateOf(false) }

    if (s == null || !s.isFinished) {
        Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Brak wyniku", color = Color.White) }
        return
    }

    val winner = s.winner
    val me = s.players.firstOrNull()
    val iWon = winner?.id == me?.id

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
        Spacer(Modifier.height(24.dp))

        Text(
            text = if (iWon) "ZWYCIĘSTWO!" else "KONIEC GRY",
            color = if (iWon) Accent else Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
        Text("Wygrywa: ${winner?.name ?: "-"}", color = LightGrayText, fontSize = 16.sp)

        Spacer(Modifier.height(8.dp))

        // Klasyfikacja: najmniej pozostałych punktów = wyżej.
        s.players.sortedBy { it.remaining }.forEachIndexed { index, p ->
            val isWinner = p.id == winner?.id
            Box(
                Modifier.fillMaxWidth()
                    .border(1.dp, if (isWinner) Accent else BorderGray, RoundedCornerShape(12.dp))
                    .padding(16.dp),
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${index + 1}. ${p.name}",
                        color = if (isWinner) Accent else Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "śr. ${"%.1f".format(p.threeDartAvg)} • lotki ${p.dartsThrown} • 180: ${p.count180s}",
                        color = LightGrayText,
                        fontSize = 12.sp,
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        if (s.config.autoSave) {
            Text(if (saved) "Mecz zapisany." else "Zapisywanie...", color = LightGrayText)
        } else {
            Button(
                onClick = { viewModel.saveResult(); saved = true },
                enabled = !saved,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.Black),
            ) { Text(if (saved) "ZAPISANO" else "ZAPISZ MECZ", fontWeight = FontWeight.Bold, letterSpacing = 1.sp) }
        }

        OutlinedButton(
            onClick = { viewModel.leaveGame(); onBackToHome() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) { Text("POWRÓT DO DASHBOARD", color = LightGrayText, letterSpacing = 1.sp) }
    }
}