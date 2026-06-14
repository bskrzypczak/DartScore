package com.dartscore.feature.training.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dartscore.core.designsystem.Accent
import com.dartscore.core.designsystem.BorderGray
import com.dartscore.core.designsystem.LightGrayText
import com.dartscore.feature.training.domain.TrainingMode
import com.dartscore.feature.training.domain.TrainingModes


@Composable
fun TrainingScreen(onModeClick: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            text = "TRYBY TRENINGOWE",
            color = LightGrayText,
            fontSize = 24.sp,
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(TrainingModes.all, key = { it.id }) { mode ->
                TrainingRow(mode = mode, onClick = { onModeClick(mode.id) })
            }
        }
    }
}

@Composable
private fun TrainingRow(mode: TrainingMode, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = mode.icon,
            contentDescription = null,
            tint = Accent,
            modifier = Modifier.size(26.dp),
        )
        Text(
            text = mode.name.uppercase(),
            color = Color.White,
            fontSize = 14.sp,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Otwórz",
            tint = LightGrayText,
        )
    }
}