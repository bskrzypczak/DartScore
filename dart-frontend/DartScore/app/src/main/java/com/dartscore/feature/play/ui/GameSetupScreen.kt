package com.dartscore.feature.play.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dartscore.core.designsystem.Accent
import com.dartscore.core.designsystem.BorderGray
import com.dartscore.core.designsystem.LightGrayText
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
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text("NOWA GRA", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)

        ChoiceRow("TRYB", MatchType.entries, matchType, { it.label }) { matchType = it }
        ChoiceRow("GRAMY DO", GameMode.entries, mode, { it.label }) { mode = it }
        ChoiceRow("WEJŚCIE (IN)", EntryRule.entries, inRule, { it.label }) { inRule = it }
        ChoiceRow("WYJŚCIE (OUT)", EntryRule.entries, outRule, { it.label }) { outRule = it }

        if (matchType == MatchType.BOT) {
            ChoiceRow("POZIOM BOTA", BotLevel.entries, botLevel, { it.label }) { botLevel = it }
        }
        if (matchType == MatchType.MULTIPLAYER) {
            SectionLabel("LICZBA GRACZY: $playerCount")
            Slider(
                value = playerCount.toFloat(),
                onValueChange = { playerCount = it.roundToInt() },
                valueRange = 3f..6f,
                steps = 2,
                colors = SliderDefaults.colors(
                    thumbColor = Accent,
                    activeTrackColor = Accent,
                    inactiveTrackColor = BorderGray,
                ),
            )
        }
        if (matchType == MatchType.DUEL || matchType == MatchType.FRIENDLY) {
            OutlinedTextField(
                value = opponent,
                onValueChange = { opponent = it },
                label = { Text("Nazwa przeciwnika") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = BorderGray,
                    focusedLabelColor = Accent,
                    unfocusedLabelColor = LightGrayText,
                    cursorColor = Accent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                ),
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
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.Black),
        ) {
            Text("ROZPOCZNIJ MECZ", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, color = LightGrayText, fontSize = 12.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun <T> ChoiceRow(label: String, options: List<T>, selected: T, labelFor: (T) -> String, onSelect: (T) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel(label)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                val isSelected = option == selected
                Text(
                    text = labelFor(option),
                    color = if (isSelected) Accent else LightGrayText,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    modifier = Modifier
                        .border(1.dp, if (isSelected) Accent else BorderGray, RoundedCornerShape(50))
                        .background(
                            if (isSelected) Accent.copy(alpha = 0.15f) else Color.Transparent,
                            RoundedCornerShape(50),
                        )
                        .clickable { onSelect(option) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                )
            }
        }
    }
}