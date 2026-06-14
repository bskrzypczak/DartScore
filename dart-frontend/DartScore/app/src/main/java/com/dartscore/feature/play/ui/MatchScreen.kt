package com.dartscore.feature.play.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dartscore.R
import com.dartscore.core.designsystem.Accent
import com.dartscore.core.designsystem.BorderGray
import com.dartscore.core.designsystem.LightGrayText
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

    LaunchedEffect(s?.isFinished) { if (s?.isFinished == true) onFinish() }
    if (s == null) {
        Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Brak aktywnej gry", color = Color.White) }
        return
    }

    var showScanner by remember { mutableStateOf(false) }
    if (showScanner) {
        ScanScreen(
            onCancel = { showScanner = false },
            onResult = { result -> viewModel.applyScan(result.darts); showScanner = false },
        )
        return
    }

    var multiplier by remember { mutableStateOf(Multiplier.SINGLE) }
    val current = s.currentPlayer

    Column(Modifier.fillMaxSize().padding(12.dp)) {

        // --- nagłówek ---
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Mecz ${s.config.mode.label}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showScanner = true }) {
                    Icon(painterResource(R.drawable.ico_camera), contentDescription = "Skanuj tarczę", tint = Accent)
                }
                IconButton(onClick = { viewModel.leaveGame(); onQuit() }){
                    Icon(painterResource(R.drawable.ico_logout), contentDescription = "Wyjdz", tint = Accent)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // --- lista graczy (przewijalna, zajmuje resztę miejsca) ---
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(s.players, key = { it.id }) { player ->
                PlayerCard(player, isCurrent = player.id == current.id)
            }
            if (current.remaining in 2..180) {
                if (viewModel.getCheckoutHint(current.remaining) != null) {
                    item {
                        Box(
                            Modifier.fillMaxWidth()
                                .border(1.dp, BorderGray, RoundedCornerShape(12.dp)).padding(12.dp)
                        ) {
                            Text(
                                "Sugerowany checkout: ${viewModel.getCheckoutHint(current.remaining)}",
                                color = LightGrayText,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // --- panel wprowadzania (przyklejony do dołu) ---
        // bieżąca tura
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(3) { i ->
                val dart = s.currentTurn.getOrNull(i)
                Box(
                    Modifier.weight(1f).border(1.dp, BorderGray, RoundedCornerShape(10.dp)).padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(dart?.label ?: "–", color = if (dart != null) Accent else LightGrayText, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        if (current.isBot) {
            Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
                Text("Bot rzuca...", color = Accent, fontWeight = FontWeight.Bold)
            }
        } else {
            // mnożnik
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MultiplierChip("Single", multiplier == Multiplier.SINGLE) { multiplier = Multiplier.SINGLE }
                MultiplierChip("Double", multiplier == Multiplier.DOUBLE) { multiplier = Multiplier.DOUBLE }
                MultiplierChip("Triple", multiplier == Multiplier.TRIPLE) { multiplier = Multiplier.TRIPLE }
            }

            Spacer(Modifier.height(8.dp))

            // klawiatura 1-20
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                (1..20).toList().chunked(5).forEach { rowNums ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        rowNums.forEach { n ->
                            ScoreKey("$n", Modifier.weight(1f)) {
                                viewModel.addDart(Dart(n, multiplier)); multiplier = Multiplier.SINGLE
                            }
                        }
                    }
                }
                // ostatni rząd: 25 (zablokowane przy Triple) + Miss + wyrównanie
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ScoreKey("25", Modifier.weight(1f), enabled = multiplier != Multiplier.TRIPLE) {
                        viewModel.addDart(Dart(25, multiplier)); multiplier = Multiplier.SINGLE
                    }
                    ScoreKey("Miss", Modifier.weight(1f)) {
                        viewModel.addDart(Dart(0, Multiplier.SINGLE))
                    }
                    Spacer(Modifier.weight(3f))
                }
            }

            Spacer(Modifier.height(8.dp))

            // Cofnij / Zatwierdź
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { viewModel.undo() }, enabled = s.currentTurn.isNotEmpty(), modifier = Modifier.weight(1f)) {
                    Text("Cofnij", color = LightGrayText)
                }
                Button(
                    onClick = { viewModel.confirm() },
                    enabled = s.currentTurn.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.Black),
                ) { Text("Zatwierdź", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun PlayerCard(player: GamePlayer, isCurrent: Boolean) {
    Box(
        Modifier
            .fillMaxWidth()
            .border(1.dp, if (isCurrent) Accent else BorderGray, RoundedCornerShape(12.dp))
            .background(if (isCurrent) Accent.copy(alpha = 0.12f) else Color.Transparent, RoundedCornerShape(12.dp))
            .padding(12.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(player.name, color = Color.White, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal)
                    if (isCurrent) Text("• teraz", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Text("Śr. 3D: ${"%.1f".format(player.threeDartAvg)}", color = LightGrayText, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(3) { i -> Text(player.lastThrows.getOrNull(i)?.toString() ?: "–", color = LightGrayText, fontSize = 12.sp) }
                }
            }
            Text("${player.remaining}", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MultiplierChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        color = if (selected) Accent else LightGrayText,
        fontSize = 13.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        modifier = Modifier
            .border(1.dp, if (selected) Accent else BorderGray, RoundedCornerShape(50))
            .background(if (selected) Accent.copy(alpha = 0.15f) else Color.Transparent, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    )
}

@Composable
private fun ScoreKey(label: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(48.dp)
            .border(1.dp, if (enabled) BorderGray else BorderGray.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = if (enabled) Color.White else LightGrayText.copy(alpha = 0.4f), fontWeight = FontWeight.Medium)
    }
}