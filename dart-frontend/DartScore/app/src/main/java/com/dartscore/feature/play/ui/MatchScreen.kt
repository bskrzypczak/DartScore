package com.dartscore.feature.play.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dartscore.core.ui.PlaceholderScreen

@Composable
fun MatchScreen(
    onFinish: () -> Unit,
    onQuit: () -> Unit,
) {
    PlaceholderScreen(title = "Mecz") {
        Spacer(Modifier.height(24.dp))
        Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) {
            Text("Zakończ mecz (zwycięstwo)")
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onQuit, modifier = Modifier.fillMaxWidth()) {
            Text("Wyjdź z meczu")
        }
    }
}
