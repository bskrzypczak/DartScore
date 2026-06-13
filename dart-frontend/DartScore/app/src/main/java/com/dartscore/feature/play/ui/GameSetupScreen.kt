package com.dartscore.feature.play.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dartscore.feature.play.domain.BotLevel
import com.dartscore.feature.play.domain.EntryRule
import com.dartscore.feature.play.domain.GameConfig
import com.dartscore.feature.play.domain.GameMode
import com.dartscore.feature.play.domain.MatchType
import kotlin.math.roundToInt

@Composable
fun GameSetupScreen(
    onStartMatch: () -> Unit,
    viewModel: GameSetupViewModel = hiltViewModel(),
) {
    var matchType by remember { mutableStateOf(MatchType.DUEL) }
    var mode by remember { mutableStateOf(GameMode.MODE_501) }
    var inRule by remember { mutableStateOf(EntryRule.STRAIGHT) }
    var outRule by remember { mutableStateOf(EntryRule.DOUBLE) }
    var botLevel by remember { mutableStateOf(BotLevel.MEDIUM) }
    var playerCount by remember { mutableStateOf(3) }
    var opponent by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Nowa gra", style = MaterialTheme.typography.headlineMedium)

        ChoiceRow("Tryb", MatchType.entries, matchType, { it.label }) { matchType = it }
        ChoiceRow("Gramy do", GameMode.entries, mode, { it.label }) { mode = it }
        ChoiceRow("Wejście (In)", EntryRule.entries, inRule, { it.label }) { inRule = it }
        ChoiceRow("Wyjście (Out)", EntryRule.entries, outRule, { it.label }) { outRule = it }

        if (matchType == MatchType.BOT) {
            ChoiceRow("Poziom bota", BotLevel.entries, botLevel, { it.label }) { botLevel = it }
        }
        if (matchType == MatchType.MULTIPLAYER) {
            Text("Liczba graczy: $playerCount", style = MaterialTheme.typography.titleSmall)
            Slider(
                value = playerCount.toFloat(),
                onValueChange = { playerCount = it.roundToInt() },
                valueRange = 3f..6f,
                steps = 2,
            )
        }
        if (matchType == MatchType.DUEL || matchType == MatchType.FRIENDLY) {
            OutlinedTextField(
                value = opponent,
                onValueChange = { opponent = it },
                label = { Text("Nazwa przeciwnika") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Button(
            onClick = {
                val names = when (matchType) {
                    MatchType.DUEL, MatchType.FRIENDLY ->
                        listOf("Ty", opponent.ifBlank { "Przeciwnik" })
                    MatchType.MULTIPLAYER ->
                        listOf("Ty") + (2..playerCount).map { "Gracz $it" }
                    MatchType.BOT ->
                        listOf("Ty", "Bot (${botLevel.label})")
                }
                viewModel.startGame(
                    GameConfig(
                        mode = mode, inRule = inRule, outRule = outRule,
                        matchType = matchType, playerNames = names,
                        botLevel = if (matchType == MatchType.BOT) botLevel else null,
                    )
                )
                onStartMatch()
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Rozpocznij mecz") }
    }
}

@Composable
private fun <T> ChoiceRow(title: String, options: List<T>, selected: T, label: (T) -> String, onSelect: (T) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                FilterChip(
                    selected = option == selected,
                    onClick = { onSelect(option) },
                    label = { Text(label(option)) },
                )
            }
        }
    }
}