package com.dartscore.feature.auth.data

import com.dartscore.feature.auth.domain.AuthState
import com.dartscore.feature.auth.domain.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// Prawdziwa implementacja AuthRepository oparta o Firebase Auth.
// Reszta aplikacji nie wie, że to Firebase – widzi tylko interfejs i AuthState.
@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // Firebase wywołuje listener od razu z bieżącym stanem,
        // a potem przy każdej zmianie (login / logout / odświeżenie tokenu).
        // Dzięki temu nasz Flow jest zawsze zsynchronizowany z sesją Firebase.
        auth.addAuthStateListener { firebaseAuth ->
            _authState.value = firebaseAuth.currentUser.toAuthState()
        }
    }

    override suspend fun signIn(email: String, password: String) {
        // Po sukcesie listener powyżej sam ustawi AuthState.LoggedIn.
        auth.signInWithEmailAndPassword(email.trim(), password).await()
    }

    override suspend fun register(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email.trim(), password).await()
        // Firebase nie zapisuje wyświetlanej nazwy automatycznie – ustawiamy ją osobno.
        auth.currentUser?.updateProfile(
            UserProfileChangeRequest.Builder().setDisplayName(name.trim()).build()
        )?.await()
        // Listener nie reaguje na zmianę profilu, więc po ustawieniu nazwy
        // odświeżamy usera i emitujemy stan ręcznie (żeby UI od razu znało displayName).
        auth.currentUser?.let { user ->
            user.reload().await()
            _authState.value = user.toAuthState()
        }
    }

    override fun continueAsGuest() {
        // Gość = logowanie anonimowe. Wyłączone w MVP (email + hasło).
        // Aby włączyć: w konsoli Firebase aktywuj "Anonymous", a tutaj wywołaj
        // auth.signInAnonymously() – listener zmapuje isAnonymous na AuthState.Guest.
        throw UnsupportedOperationException("Tryb gościa nie jest włączony w tej wersji")
    }

    override fun signOut() {
        auth.signOut() // listener ustawi AuthState.LoggedOut
    }

    private fun FirebaseUser?.toAuthState(): AuthState = when {
        this == null -> AuthState.LoggedOut
        isAnonymous -> AuthState.Guest
        else -> AuthState.LoggedIn(
            User(
                id = uid,
                email = email.orEmpty(),
                displayName = displayName ?: email.orEmpty(),
            )
        )
    }
}