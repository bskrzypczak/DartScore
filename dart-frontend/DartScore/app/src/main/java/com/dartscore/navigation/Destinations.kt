package com.dartscore.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.ui.graphics.vector.ImageVector

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val FRIENDS = "friends"
    const val PLAY = "play"
    const val TRAINING = "training"
    const val SETTINGS = "settings"
    const val MATCH = "match"
    const val VICTORY = "victory"
    const val TRAINING_DETAILS = "training_details"
}

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
