package com.dartscore.feature.friends.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FriendsScreen(viewModel: FriendsViewModel = hiltViewModel()) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    var query by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text("Znajomi", style = MaterialTheme.typography.headlineMedium)
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Szukaj gracza po nicku") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            Button(
                onClick = { viewModel.search(query) },
                enabled = !ui.isSearching,
                modifier = Modifier.fillMaxWidth(),
            ) { Text(if (ui.isSearching) "Szukam..." else "Szukaj") }
        }
        ui.message?.let { msg ->
            item { Text(msg, color = MaterialTheme.colorScheme.primary) }
        }

        if (ui.searchResults.isNotEmpty()) {
            item { SectionTitle("Wyniki wyszukiwania") }
            items(ui.searchResults, key = { "search_${it.uid}" }) { result ->
                PersonRow(
                    name = result.displayName,
                    actionLabel = "Zaproś",
                    onAction = { viewModel.sendRequest(result.uid) },
                )
            }
        }

        if (ui.requests.isNotEmpty()) {
            item { SectionTitle("Zaproszenia (${ui.requests.size})") }
            items(ui.requests, key = { "req_${it.fromUid}" }) { request ->
                Card(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(request.fromDisplayName)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Button(onClick = { viewModel.accept(request) }) { Text("Akceptuj") }
                            TextButton(onClick = { viewModel.decline(request.fromUid) }) { Text("Odrzuć") }
                        }
                    }
                }
            }
        }

        item { SectionTitle("Moi znajomi (${ui.friends.size})") }
        if (ui.friends.isEmpty()) {
            item { Text("Nie masz jeszcze znajomych.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        items(ui.friends, key = { "friend_${it.uid}" }) { friend ->
            PersonRow(
                name = friend.displayName,
                actionLabel = "Usuń",
                onAction = { viewModel.remove(friend.uid) },
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
}

@Composable
private fun PersonRow(name: String, actionLabel: String, onAction: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(name)
            TextButton(onClick = onAction) { Text(actionLabel) }
        }
    }
}