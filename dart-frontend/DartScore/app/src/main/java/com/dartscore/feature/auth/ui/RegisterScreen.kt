package com.dartscore.feature.auth.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dartscore.core.ui.PlaceholderScreen

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    PlaceholderScreen(title = "Register") {
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { viewModel.register("demo@dartscore.app", "demo", "Nowy Gracz") },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Zarejestruj się (demo)") }

        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onBack) { Text("Wróć do logowania") }
    }
}
