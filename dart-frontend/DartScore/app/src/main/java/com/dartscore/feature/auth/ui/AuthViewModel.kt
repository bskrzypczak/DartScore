package com.dartscore.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dartscore.feature.auth.data.AuthRepository
import com.dartscore.feature.auth.domain.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Cienki dyrygent: wystawia authState i deleguje akcje do repozytorium.
// Wszystkie instancje współdzielą jeden singletonowy AuthRepository,
// więc stan jest spójny niezależnie od tego, który ekran wywoła akcję.
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {

    val authState: StateFlow<AuthState> = repository.authState

    fun signIn(email: String, password: String) = viewModelScope.launch {
        repository.signIn(email, password)
    }

    fun register(email: String, password: String, name: String) = viewModelScope.launch {
        repository.register(email, password, name)
    }

    fun continueAsGuest() = repository.continueAsGuest()

    fun signOut() = repository.signOut()
}
