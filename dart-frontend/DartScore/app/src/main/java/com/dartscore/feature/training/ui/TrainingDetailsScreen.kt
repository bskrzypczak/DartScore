package com.dartscore.feature.training.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dartscore.feature.training.domain.TrainingModes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingDetailsScreen(modeId: String?, onBack: () -> Unit) {
    val mode = TrainingModes.byId(modeId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(mode?.name ?: "Trening") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wstecz")
                    }
                },
            )
        },
    ) { innerPadding ->
        if (mode == null) {
            Box(Modifier.fillMaxSize().padding(innerPadding), Alignment.Center) {
                Text("Nie znaleziono trybu")
            }
            return@Scaffold
        }

        Column(
            Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(mode.shortDescription, style = MaterialTheme.typography.bodyLarge)

            Text("Zasady", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            mode.rules.forEach { rule ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("•")
                    Text(rule, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Text("Twoje statystyki", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            // MOCK: docelowo z trainingSessions w Firestore.
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Sesje", "12", Modifier.weight(1f))
                StatCard("Najlepszy", "27", Modifier.weight(1f))
                StatCard("Średnia", "18.4", Modifier.weight(1f))
            }
            Text(
                "Ostatnia sesja: 3 dni temu (dane przykładowe)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}