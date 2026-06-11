package com.dartscore.feature.auth.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dartscore.core.ui.PlaceholderScreen

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    PlaceholderScreen(title = "Login") {
        Spacer(Modifier.height(24.dp))
        // W MVP logujemy danymi demo – chodzi o przełączenie AuthState -> Main.
        Button(
            onClick = { viewModel.signIn("demo@dartscore.app", "demo") },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Zaloguj się (demo)") }

        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = { viewModel.continueAsGuest() },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Kontynuuj jako gość") }

        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onNavigateToRegister) {
            Text("Nie masz konta? Zarejestruj się")
        }
    }
}
