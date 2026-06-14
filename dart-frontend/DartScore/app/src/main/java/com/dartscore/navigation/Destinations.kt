package com.dartscore.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.dartscore.R

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
    const val TRAINING_GAME = "training_game"
}

enum class TopLevelTab(
    val route: String,
    val label: String,
    val icon: Int,
) {
    HOME(Routes.HOME, "Home", R.drawable.ico_home),
    FRIENDS(Routes.FRIENDS, "Znajomi", R.drawable.ico_friends),
    PLAY(Routes.PLAY, "Graj", R.drawable.ico_scoreboard),
    TRAINING(Routes.TRAINING, "Trening", R.drawable.ico_training),
    SETTINGS(Routes.SETTINGS, "Ustawienia", R.drawable.ico_settings),
}
