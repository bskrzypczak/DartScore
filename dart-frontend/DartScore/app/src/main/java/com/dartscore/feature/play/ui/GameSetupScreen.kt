package com.dartscore.feature.play.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dartscore.core.ui.PlaceholderScreen

@Composable
fun GameSetupScreen(onStartMatch: () -> Unit) {
    PlaceholderScreen(title = "Graj – konfiguracja gry") {
        Spacer(Modifier.height(24.dp))
        Button(onClick = onStartMatch, modifier = Modifier.fillMaxWidth()) {
            Text("Rozpocznij mecz")
        }
    }
}
