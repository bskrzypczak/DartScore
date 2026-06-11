package com.dartscore.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dartscore.feature.auth.domain.AuthState
import com.dartscore.feature.auth.ui.AuthViewModel
import com.dartscore.feature.auth.ui.LoginScreen
import com.dartscore.feature.auth.ui.RegisterScreen

// Najwyższy poziom nawigacji. Stan auth decyduje, który graf jest pokazany.
// Zmiana AuthState (np. po zalogowaniu lub wylogowaniu) przerysowuje cały podgraf,
// więc nie musimy ręcznie wołać navigate() między Auth a Main.
@Composable
fun RootNavHost() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when (authState) {
            AuthState.Loading -> LoadingScreen()
            AuthState.LoggedOut -> AuthNavHost()
            // Gość i zalogowany trafiają do tego samego grafu Main.
            AuthState.Guest, is AuthState.LoggedIn -> MainScaffold()
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

// Mały, oddzielny NavHost tylko dla ekranów logowania/rejestracji.
@Composable
private fun AuthNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(onNavigateToRegister = { navController.navigate(Routes.REGISTER) })
        }
        composable(Routes.REGISTER) {
            RegisterScreen(onBack = { navController.popBackStack() })
        }
    }
}
