package com.dartscore.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// MVP: jeden ciemny motyw zgodny z designem webowym.
// Light scheme dorobimy później (Settings ma już przełącznik motywu w designie).
private val DarkColors = darkColorScheme(
    primary = Emerald600,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = Blue500,
    background = Slate900,
    onBackground = androidx.compose.ui.graphics.Color.White,
    surface = Slate800,
    onSurface = Slate300,
    surfaceVariant = Slate700,
    onSurfaceVariant = Slate400,
    error = Red600,
)

@Composable
fun DartScoreTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content,
    )
}
