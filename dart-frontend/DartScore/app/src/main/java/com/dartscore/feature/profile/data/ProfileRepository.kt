package com.dartscore.feature.profile.data

import com.dartscore.feature.profile.domain.PlayerProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface ProfileRepository {
    // Obserwuje profil zalogowanego usera na żywo (null = brak / wylogowany).
    fun observeMyProfile(): Flow<PlayerProfile?>
    // Tworzy profil, jeśli jeszcze nie istnieje (idempotentne). Woła się po logowaniu.
    suspend fun ensureProfile()
    // Jednorazowy odczyt cudzego profilu (np. znajomego w rankingu).
    suspend fun getProfile(uid: String): PlayerProfile?
}

@Singleton
class FirestoreProfileRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : ProfileRepository {

    private fun users() = firestore.collection("users")

    override fun observeMyProfile(): Flow<PlayerProfile?> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(null)
            close()
            return@callbackFlow
        }
        // Listener emituje od razu bieżący stan i potem każdą zmianę.
        val registration = users().document(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close()
                return@addSnapshotListener
            }
            trySend(snapshot?.takeIf { it.exists() }?.toPlayerProfile())
        }
        awaitClose { registration.remove() }
    }

    override suspend fun ensureProfile() {
        val user = auth.currentUser ?: return
        val docRef = users().document(user.uid)
        val snapshot = docRef.get().await()
        if (snapshot.exists()) return // profil już jest – nic nie nadpisujemy

        val name = user.displayName?.takeIf { it.isNotBlank() }
            ?: user.email?.substringBefore("@")
            ?: "Gracz"
        val initial = mapOf(
            "displayName" to name,
            "country" to "PL",
            "photoUrl" to user.photoUrl?.toString(),
            "totalMatches" to 0L,
            "wins" to 0L,
            "total180s" to 0L,
            "totalDartsThrown" to 0L,
            "totalPointsScored" to 0L,
            "createdAt" to FieldValue.serverTimestamp(),
        )
        docRef.set(initial, SetOptions.merge()).await()
    }

    override suspend fun getProfile(uid: String): PlayerProfile? {
        val snapshot = users().document(uid).get().await()
        return snapshot.takeIf { it.exists() }?.toPlayerProfile()
    }
}

// Mapowanie dokumentu Firestore -> model domenowy (jawne, bez refleksji).
private fun DocumentSnapshot.toPlayerProfile(): PlayerProfile = PlayerProfile(
    uid = id,
    displayName = getString("displayName") ?: "Gracz",
    country = getString("country") ?: "PL",
    photoUrl = getString("photoUrl"),
    totalMatches = getLong("totalMatches")?.toInt() ?: 0,
    wins = getLong("wins")?.toInt() ?: 0,
    total180s = getLong("total180s")?.toInt() ?: 0,
    totalDartsThrown = getLong("totalDartsThrown")?.toInt() ?: 0,
    totalPointsScored = getLong("totalPointsScored")?.toInt() ?: 0,
)