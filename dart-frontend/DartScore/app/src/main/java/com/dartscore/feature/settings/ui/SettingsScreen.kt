package com.dartscore.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dartscore.core.designsystem.Background
import com.dartscore.feature.auth.ui.AuthViewModel
import com.dartscore.core.designsystem.Accent
import com.dartscore.core.designsystem.BorderGray
import com.dartscore.core.designsystem.LightGrayText

@Composable
fun SettingsScreen(viewModel: AuthViewModel = hiltViewModel()) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))

        ProfileHeader(name = profile?.displayName ?: "Gracz")

        Spacer(Modifier.height(48.dp))

        StatsSection(
            ppd = profile?.ppd?.let { "%.1f".format(it) } ?: "-",
            winRate = profile?.let { "${it.winRate}%" } ?: "-",
            count180 = profile?.total180s?.toString() ?: "-",
        )

        Spacer(Modifier.height(48.dp))

        MenuSection(viewModel)
    }
}

@Composable
fun ProfileHeader(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(120.dp)) {
            Box(
                modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color.Gray),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(64.dp))
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Background)
                    .padding(2.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(1.dp, BorderGray, CircleShape)
                        .clickable { /* TODO: Edycja zdjęcia */ },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edytuj zdjęcie", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = name,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = MaterialTheme.typography.headlineLarge.fontFamily,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "DARTSCORE PLAYER",
            color = LightGrayText,
            fontSize = 12.sp,
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun StatsSection(ppd: String, winRate: String, count180: String) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "STATYSTYKI",
            color = LightGrayText,
            fontSize = 12.sp,
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatBox(value = ppd, label = "PPD", modifier = Modifier.weight(1f))
            StatBox(value = winRate, label = "WIN %", modifier = Modifier.weight(1f))
            StatBox(value = count180, label = "180s", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StatBox(value: String, label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = Accent, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                color = LightGrayText,
                fontSize = 10.sp,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun MenuSection(viewModel: AuthViewModel = hiltViewModel()) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MenuListItem(text = "MOJE KONTO")
        MenuListItem(text = "INFORMACJE")
        MenuListItem(text = "USTAWIENIA")

        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = BorderGray, thickness = 1.dp)
        Spacer(Modifier.height(16.dp))

        MenuListItem(
            text = "WYLOGUJ",
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            onClick = { viewModel.signOut() },
        )
    }
}

@Composable
fun MenuListItem(
    text: String,
    icon: ImageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = text, color = Color.White, fontSize = 14.sp, letterSpacing = 1.sp)
        Icon(imageVector = icon, contentDescription = null, tint = LightGrayText, modifier = Modifier.size(24.dp))
    }
}