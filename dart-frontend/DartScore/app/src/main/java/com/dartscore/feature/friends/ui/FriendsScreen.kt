package com.dartscore.feature.friends.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dartscore.core.designsystem.Accent
import com.dartscore.core.designsystem.BorderGray
import com.dartscore.core.designsystem.LightGrayText

@Composable
fun FriendsScreen(viewModel: FriendsViewModel = hiltViewModel()) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    var query by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { Text("Znajomi", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold) }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Szukaj gracza po nicku") },
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
        item {
            Button(
                onClick = { viewModel.search(query) },
                enabled = !ui.isSearching,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.Black),
            ) { Text(if (ui.isSearching) "SZUKAM..." else "SZUKAJ", fontWeight = FontWeight.Bold, letterSpacing = 1.sp) }
        }
        ui.message?.let { msg -> item { Text(msg, color = Accent) } }

        if (ui.searchResults.isNotEmpty()) {
            item { SectionLabel("WYNIKI WYSZUKIWANIA") }
            items(ui.searchResults, key = { "search_${it.uid}" }) { result ->
                PersonRow(name = result.displayName, primaryLabel = "Zaproś", onPrimary = { viewModel.sendRequest(result.uid) })
            }
        }

        if (ui.requests.isNotEmpty()) {
            item { SectionLabel("ZAPROSZENIA (${ui.requests.size})") }
            items(ui.requests, key = { "req_${it.fromUid}" }) { request ->
                PersonRow(
                    name = request.fromDisplayName,
                    primaryLabel = "Akceptuj",
                    onPrimary = { viewModel.accept(request) },
                    secondaryLabel = "Odrzuć",
                    onSecondary = { viewModel.decline(request.fromUid) },
                )
            }
        }

        item { SectionLabel("MOI ZNAJOMI (${ui.friends.size})") }
        if (ui.friends.isEmpty()) {
            item { Text("Nie masz jeszcze znajomych.", color = LightGrayText) }
        }
        items(ui.friends, key = { "friend_${it.uid}" }) { friend ->
            PersonRow(name = friend.displayName, secondaryLabel = "Usuń", onSecondary = { viewModel.remove(friend.uid) })
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        color = LightGrayText,
        fontSize = 12.sp,
        letterSpacing = 1.5.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp),
    )
}

@Composable
private fun PersonRow(
    name: String,
    primaryLabel: String? = null,
    onPrimary: () -> Unit = {},
    secondaryLabel: String? = null,
    onSecondary: () -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth().border(1.dp, BorderGray, RoundedCornerShape(12.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(name, color = Color.White, modifier = Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            if (primaryLabel != null) {
                Button(
                    onClick = onPrimary,
                    colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.Black),
                ) { Text(primaryLabel) }
            }
            if (secondaryLabel != null) {
                TextButton(onClick = onSecondary) { Text(secondaryLabel, color = LightGrayText) }
            }
        }
    }
}