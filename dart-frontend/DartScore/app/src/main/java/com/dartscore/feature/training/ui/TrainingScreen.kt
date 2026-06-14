package com.dartscore.feature.training.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.List // Lub Icons.Default.BarChart zależnie od gustu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

// Zwykła klasa danych na potrzeby mockowania
data class TrainingMode(val name: String, val description: String)

@Composable
fun TrainingScreen() {
    // Mockowana lista trybów
    val mockModes = listOf(
        TrainingMode("Around the Clock", "Rzucaj po kolei we wszystkie wartości od 1 do 20."),
        TrainingMode("Bob's 27", "Zacznij z 27 punktami i trafiaj duble. Nie trafisz - tracisz."),
        TrainingMode("Trening checkoutów", "Ćwicz kończenie legów z wybranych wartości.")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp), // Usunięty verticalScroll!
    ) {
        Text(
            text = "Tryby treningowe",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        HorizontalDivider()

        // Mock wyszukiwarki
        OutlinedTextField(
            value = "",
            onValueChange = {}, // Puste, bo to tylko mock UI
            placeholder = { Text("Szukaj trybu...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Ikona wyszukiwania")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Lazy Column z listą kart
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp) // Odstępy między kartami
        ) {
            items(mockModes) { mode ->
                TrainingCard(
                    name = mode.name,
                    description = mode.description
                )
            }
        }
    }
}

@Composable
fun TrainingCard(name: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lewa strona - Teksty (zajmuje całe dostępne miejsce dzięki weight(1f))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis // Dodaje "..." jeśli tekst jest za długi
                )
            }

            // Prawa strona - Ikony akcji
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* TODO: Akcja do statystyk */ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = "Statystyki",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = { /* TODO: Akcja do gry */ }) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Graj",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
