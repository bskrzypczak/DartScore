package com.dartscore.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.ui.graphics.vector.ImageVector

// Wszystkie trasy w jednym miejscu, żeby nie rozsiewać "magic stringów".
object Routes {
    // ekrany grafu Auth
    const val LOGIN = "login"
    const val REGISTER = "register"

    // zakładki paska (graf Main)
    const val HOME = "home"
    const val FRIENDS = "friends"
    const val PLAY = "play"          // start zakładki "Graj" = konfiguracja gry
    const val TRAINING = "training"
    const val SETTINGS = "settings"

    // flow meczu (osiągany z zakładki "Graj")
    const val MATCH = "match"
    const val VICTORY = "victory"
}

// Definicja jednej zakładki dolnego paska.
enum class TopLevelTab(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    HOME(Routes.HOME, "Home", Icons.Outlined.Home),
    FRIENDS(Routes.FRIENDS, "Znajomi", Icons.Outlined.Refresh),
    PLAY(Routes.PLAY, "Graj", Icons.Outlined.Warning),
    TRAINING(Routes.TRAINING, "Trening", Icons.Outlined.Info),
    SETTINGS(Routes.SETTINGS, "Ustawienia", Icons.Outlined.Settings),
}
