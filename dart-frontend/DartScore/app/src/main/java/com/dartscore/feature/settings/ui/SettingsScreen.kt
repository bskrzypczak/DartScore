package com.dartscore.feature.settings.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dartscore.feature.auth.ui.AuthViewModel
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dartscore.core.designsystem.Background

val LightGrayText = Color(0xFFA0AAB0)
val BorderGray = Color(0xFF404A50)

@Composable
fun SettingsScreen(viewModel: AuthViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp)) // Miejsce na status bar

        // 1. Sekcja Profilu (Zdjęcie + Imię)
        ProfileHeader()

        Spacer(modifier = Modifier.height(48.dp))

        // 2. Sekcja Umiejętności (Skills)
        SkillsSection()

        Spacer(modifier = Modifier.height(48.dp))

        // 3. Sekcja Menu
        MenuSection(viewModel)
    }
}

@Composable
fun ProfileHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Zdjęcie z nakładką edycji
        Box(
            modifier = Modifier.size(120.dp)
        ) {
            // Zaślepka na zdjęcie (w prawdziwej apce użyj np. Coil i AsyncImage)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }

            // Przycisk edycji (ołówek)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Background) // Tło dopasowane do tła ekranu, żeby wyciąć obwódkę
                    .padding(2.dp) // Grubość "wycięcia"
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(1.dp, BorderGray, CircleShape)
                        .background(Color.Transparent)
                        .clickable { /* TODO: Edycja zdjęcia */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edytuj zdjęcie",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Imię i nazwisko
        Text(
            text = "Eleanor Rigby",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = MaterialTheme.typography.headlineLarge.fontFamily // Tu możesz podpiąć font szeryfowy (serif)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Status
        Text(
            text = "MEMBER SINCE 2025",
            color = LightGrayText,
            fontSize = 12.sp,
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SkillsSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Nagłówek sekcji z ikoną
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { /* TODO: Edycja skilli */ }
        ) {
            Text(
                text = "SKILLS",
                color = LightGrayText,
                fontSize = 12.sp,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edytuj umiejętności",
                tint = LightGrayText,
                modifier = Modifier.size(14.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pigułki z umiejętnościami
        // Używamy Row (lub FlowRow jeśli jest ich dużo i mają przechodzić do nowej linii)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SkillPill(text = "PHOTOGRAPHY", modifier = Modifier.weight(1f))
            SkillPill(text = "WRITING", modifier = Modifier.weight(1f))
            SkillPill(text = "GRAPHIC DESIGN", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun SkillPill(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .border(1.dp, BorderGray, RoundedCornerShape(50))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = LightGrayText,
            fontSize = 10.sp,
            letterSpacing = 0.5.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun MenuSection(viewModel: AuthViewModel = hiltViewModel()) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MenuListItem(text = "PORTFOLIO")
        MenuListItem(text = "INFORMATIONS")
        MenuListItem(text = "SETTINGS")
        MenuListItem(text = "MY ACCOUNT")

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = BorderGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        MenuListItem(
            text = "LOGOUT",
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            onClick = { viewModel.signOut() }
        )
    }
}

@Composable
fun MenuListItem(
    text: String,
    icon: ImageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            letterSpacing = 1.sp
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = LightGrayText,
            modifier = Modifier.size(24.dp)
        )
    }
}