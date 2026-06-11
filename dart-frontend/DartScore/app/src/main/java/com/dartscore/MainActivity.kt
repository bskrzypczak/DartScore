package com.dartscore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dartscore.core.designsystem.DartScoreTheme
import com.dartscore.navigation.RootNavHost
import dagger.hilt.android.AndroidEntryPoint

// Jedyna Activity w aplikacji. Hostuje root NavHost wewnątrz motywu Compose.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DartScoreTheme {
                RootNavHost()
            }
        }
    }
}
