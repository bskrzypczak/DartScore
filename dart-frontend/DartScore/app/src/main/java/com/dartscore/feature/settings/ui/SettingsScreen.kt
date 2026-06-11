package com.dartscore.feature.settings.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dartscore.core.ui.PlaceholderScreen
import com.dartscore.feature.auth.ui.AuthViewModel

@Composable
fun SettingsScreen(viewModel: AuthViewModel = hiltViewModel()) {
    PlaceholderScreen(title = "Ustawienia") {
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { viewModel.signOut() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
            ),
        ) { Text("Wyloguj się") }
    }
}
