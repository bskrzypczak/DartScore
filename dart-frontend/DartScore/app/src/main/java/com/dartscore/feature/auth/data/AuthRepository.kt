package com.dartscore.feature.auth.data

import com.dartscore.feature.auth.domain.AuthState
import com.dartscore.feature.auth.domain.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// Kontrakt warstwy danych. UI i ViewModel widzą tylko ten interfejs,
// nigdy konkretnej implementacji (Firebase ukryty za tą granicą).
interface AuthRepository {
    val authState: StateFlow<AuthState>
    suspend fun signIn(email: String, password: String)
    suspend fun register(email: String, password: String, name: String)
    fun continueAsGuest()
    fun signOut()
}

// Atrapa w pamięci – pozwala uruchomić aplikację bez konfiguracji Firebase.
// Domyślnie wstrzykiwana przez Hilt (patrz di/RepositoryModule).
// Docelowo podmieniamy binding na FirebaseAuthRepository.
@Singleton
class FakeAuthRepository @Inject constructor() : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    override suspend fun signIn(email: String, password: String) {
        _authState.value = AuthState.LoggedIn(
            User(id = "demo", email = email, displayName = "Jan Kowalski")
        )
    }

    override suspend fun register(email: String, password: String, name: String) {
        _authState.value = AuthState.LoggedIn(
            User(id = "demo", email = email, displayName = name)
        )
    }

    override fun continueAsGuest() {
        _authState.value = AuthState.Guest
    }

    override fun signOut() {
        _authState.value = AuthState.LoggedOut
    }
}
