package com.dartscore.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dartscore.feature.auth.data.AuthRepository
import com.dartscore.feature.auth.domain.AuthState
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Stan ekranów logowania/rejestracji. authState (do nawigacji) jest osobno,
// bo płynie wprost z repozytorium i nie zależy od tego ViewModelu.
data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {

    val authState: StateFlow<AuthState> = repository.authState

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Podaj email i hasło")
            return
        }
        launchAuth { repository.signIn(email, password) }
    }

    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Uzupełnij wszystkie pola")
            return
        }
        launchAuth { repository.register(email, password, name) }
    }

    fun signOut() = repository.signOut()

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    // Wspólny przebieg: ładowanie -> akcja -> sukces (nawigacja zrobi listener) lub błąd.
    private fun launchAuth(action: suspend () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                action()
                _uiState.value = AuthUiState()
            } catch (e: Exception) {
                _uiState.value = AuthUiState(errorMessage = e.toFriendlyMessage())
            }
        }
    }
}

// Surowe wyjątki Firebase -> czytelne komunikaty PL.
private fun Throwable.toFriendlyMessage(): String = when (this) {
    is FirebaseAuthInvalidCredentialsException -> "Nieprawidłowy email lub hasło"
    is FirebaseAuthInvalidUserException -> "Nie znaleziono konta dla tego adresu"
    is FirebaseAuthUserCollisionException -> "Konto z tym adresem już istnieje"
    is FirebaseAuthWeakPasswordException -> "Hasło jest za słabe (min. 6 znaków)"
    is FirebaseNetworkException -> "Brak połączenia z internetem"
    else -> message ?: "Coś poszło nie tak. Spróbuj ponownie."
}