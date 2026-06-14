package com.dartscore.feature.training.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dartscore.feature.training.domain.TrainingModes
import com.dartscore.core.designsystem.Accent
import com.dartscore.core.designsystem.BodyText
import com.dartscore.core.designsystem.BorderGray
import com.dartscore.core.designsystem.LightGrayText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingDetailsScreen(modeId: String?, onBack: () -> Unit, onStart: (String) -> Unit) {
    val mode = TrainingModes.byId(modeId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(mode?.name ?: "Trening", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wstecz", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { innerPadding ->
        if (mode == null) {
            Box(Modifier.fillMaxSize().padding(innerPadding), Alignment.Center) {
                Text("Nie znaleziono trybu", color = Color.White)
            }
            return@Scaffold
        }

        Column(
            Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(mode.shortDescription, color = BodyText, fontSize = 16.sp)

            SectionLabel("ZASADY")
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                mode.rules.forEach { rule ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("•", color = Accent, fontWeight = FontWeight.Bold)
                        Text(rule, color = BodyText, fontSize = 14.sp)
                    }
                }
            }

            SectionLabel("TWOJE STATYSTYKI")
            // MOCK: docelowo z trainingSessions w Firestore.
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                StatBox("12", "SESJE", Modifier.weight(1f))
                StatBox("27", "NAJLEPSZY", Modifier.weight(1f))
                StatBox("18.4", "ŚREDNIA", Modifier.weight(1f))
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { onStart(mode.id) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.Black),
            ) {
                Text("ROZPOCZNIJ TRENING", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = LightGrayText,
        fontSize = 12.sp,
        letterSpacing = 1.5.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun StatBox(value: String, label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.border(1.dp, BorderGray, RoundedCornerShape(12.dp)).padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = Accent, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(label, color = LightGrayText, fontSize = 10.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}