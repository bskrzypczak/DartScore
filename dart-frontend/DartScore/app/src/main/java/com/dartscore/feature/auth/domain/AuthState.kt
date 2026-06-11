package com.dartscore.feature.auth.domain

// Stan uwierzytelnienia modelujemy jawnie, NIE surowym User?.
// Gość to pełnoprawny stan (różny od LoggedOut), dlatego null nie wystarcza.
sealed interface AuthState {
    data object Loading : AuthState        // sprawdzamy sesję przy starcie -> ekran ładowania
    data object LoggedOut : AuthState       // -> graf Auth (logowanie/rejestracja)
    data object Guest : AuthState           // -> graf Main, ograniczony widok
    data class LoggedIn(val user: User) : AuthState // -> graf Main, pełny widok
}

data class User(
    val id: String,
    val email: String,
    val displayName: String,
)
