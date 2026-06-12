package com.dartscore.feature.play.data

import com.dartscore.feature.play.domain.MatchRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface MatchRepository {
    // Historia meczów zalogowanego gracza, najnowsze pierwsze.
    fun observeMyMatches(limit: Long = 20): Flow<List<MatchRecord>>
    // Zapisuje mecz I aktualizuje agregaty na profilu – atomowo (transakcja).
    suspend fun saveMatch(match: MatchRecord)
}

@Singleton
class FirestoreMatchRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : MatchRepository {

    private fun matchesOf(uid: String) =
        firestore.collection("users").document(uid).collection("matches")

    override fun observeMyMatches(limit: Long): Flow<List<MatchRecord>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val registration = matchesOf(uid)
            .orderBy("playedAt", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close()
                    return@addSnapshotListener
                }
                val matches = snapshot?.documents?.mapNotNull { it.toMatchRecord() } ?: emptyList()
                trySend(matches)
            }
        awaitClose { registration.remove() }
    }

    override suspend fun saveMatch(match: MatchRecord) {
        val uid = auth.currentUser?.uid ?: error("Brak zalogowanego użytkownika")
        val profileRef = firestore.collection("users").document(uid)
        // Pre-generujemy ID meczu, żeby móc go ustawić wewnątrz transakcji.
        val matchRef = matchesOf(uid).document()

        firestore.runTransaction { txn ->
            // 1) odczyt bieżących agregatów (transakcja gwarantuje spójność)
            val profile = txn.get(profileRef)
            val totalMatches = profile.getLong("totalMatches") ?: 0L
            val wins = profile.getLong("wins") ?: 0L
            val total180s = profile.getLong("total180s") ?: 0L
            val totalDarts = profile.getLong("totalDartsThrown") ?: 0L
            val totalPoints = profile.getLong("totalPointsScored") ?: 0L

            // 2) zapis dokumentu meczu
            txn.set(matchRef, match.toMap())

            // 3) aktualizacja agregatów na profilu (denormalizacja)
            val updated = mapOf(
                "totalMatches" to totalMatches + 1,
                "wins" to wins + if (match.won) 1 else 0,
                "total180s" to total180s + match.count180s,
                "totalDartsThrown" to totalDarts + match.dartsThrown,
                "totalPointsScored" to totalPoints + match.pointsScored,
            )
            txn.set(profileRef, updated, SetOptions.merge())
        }.await()
    }
}

private fun MatchRecord.toMap(): Map<String, Any> = mapOf(
    "mode" to mode,
    "won" to won,
    "opponentName" to opponentName,
    "legsScore" to legsScore,
    "dartsThrown" to dartsThrown,
    "pointsScored" to pointsScored,
    "count180s" to count180s,
    "playedAt" to playedAtMillis,
)

private fun DocumentSnapshot.toMatchRecord(): MatchRecord? {
    val mode = getLong("mode")?.toInt() ?: return null
    return MatchRecord(
        id = id,
        mode = mode,
        won = getBoolean("won") ?: false,
        opponentName = getString("opponentName") ?: "",
        legsScore = getString("legsScore") ?: "",
        dartsThrown = getLong("dartsThrown")?.toInt() ?: 0,
        pointsScored = getLong("pointsScored")?.toInt() ?: 0,
        count180s = getLong("count180s")?.toInt() ?: 0,
        playedAtMillis = getLong("playedAt") ?: 0L,
    )
}
