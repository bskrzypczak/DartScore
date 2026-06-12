package com.dartscore.feature.friends.data

import com.dartscore.feature.friends.domain.Friend
import com.dartscore.feature.friends.domain.FriendRequest
import com.dartscore.feature.friends.domain.PlayerSearchResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface FriendsRepository {
    fun observeFriends(): Flow<List<Friend>>
    fun observeIncomingRequests(): Flow<List<FriendRequest>>
    // Wyszukiwanie po nicku (prefix), żeby znaleźć kogo zaprosić.
    suspend fun searchUsers(query: String): List<PlayerSearchResult>
    suspend fun sendRequest(toUid: String)
    suspend fun acceptRequest(request: FriendRequest)
    suspend fun declineRequest(fromUid: String)
    suspend fun removeFriend(friendUid: String)
}

@Singleton
class FirestoreFriendsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : FriendsRepository {

    private fun users() = firestore.collection("users")
    private fun requireUid() = auth.currentUser?.uid ?: error("Brak zalogowanego użytkownika")
    private fun myName() = auth.currentUser?.displayName?.takeIf { it.isNotBlank() }
        ?: auth.currentUser?.email?.substringBefore("@") ?: "Gracz"

    override fun observeFriends(): Flow<List<Friend>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) { trySend(emptyList()); close(); return@callbackFlow }
        val registration = users().document(uid).collection("friends")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(); return@addSnapshotListener }
                trySend(snapshot?.documents?.mapNotNull { it.toFriend() } ?: emptyList())
            }
        awaitClose { registration.remove() }
    }

    override fun observeIncomingRequests(): Flow<List<FriendRequest>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) { trySend(emptyList()); close(); return@callbackFlow }
        val registration = users().document(uid).collection("friendRequests")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(); return@addSnapshotListener }
                trySend(snapshot?.documents?.mapNotNull { it.toFriendRequest() } ?: emptyList())
            }
        awaitClose { registration.remove() }
    }

    override suspend fun searchUsers(query: String): List<PlayerSearchResult> {
        val q = query.trim()
        if (q.isBlank()) return emptyList()
        val me = auth.currentUser?.uid
        // Prefix-search po displayName: [q, q+\uf8ff). Uwaga: rozróżnia wielkość liter.
        val snapshot = users()
            .orderBy("displayName")
            .startAt(q)
            .endAt(q + "\uf8ff")
            .limit(20)
            .get().await()
        return snapshot.documents
            .filter { it.id != me } // nie pokazuj samego siebie
            .map {
                PlayerSearchResult(
                    uid = it.id,
                    displayName = it.getString("displayName") ?: "Gracz",
                    country = it.getString("country") ?: "PL",
                )
            }
    }

    override suspend fun sendRequest(toUid: String) {
        val me = requireUid()
        if (toUid == me) error("Nie możesz dodać samego siebie")
        // Tworzymy zaproszenie U ODBIORCY. ID dokumentu = moje uid (reguła to sprawdza).
        users().document(toUid).collection("friendRequests").document(me)
            .set(
                mapOf(
                    "fromDisplayName" to myName(),
                    "sentAt" to System.currentTimeMillis(),
                )
            ).await()
    }

    override suspend fun acceptRequest(request: FriendRequest) {
        val me = requireUid()
        val from = request.fromUid
        val now = System.currentTimeMillis()
        // Wszystko atomowo w batchu: wpis po obu stronach + skasowanie zaproszenia.
        val batch = firestore.batch()
        batch.set(
            users().document(me).collection("friends").document(from),
            mapOf("displayName" to request.fromDisplayName, "addedAt" to now),
        )
        // Dodaję SIEBIE do listy wnioskodawcy (doc id = moje uid -> reguła przepuści).
        batch.set(
            users().document(from).collection("friends").document(me),
            mapOf("displayName" to myName(), "addedAt" to now),
        )
        batch.delete(users().document(me).collection("friendRequests").document(from))
        batch.commit().await()
    }

    override suspend fun declineRequest(fromUid: String) {
        val me = requireUid()
        users().document(me).collection("friendRequests").document(fromUid).delete().await()
    }

    override suspend fun removeFriend(friendUid: String) {
        val me = requireUid()
        val batch = firestore.batch()
        batch.delete(users().document(me).collection("friends").document(friendUid))
        // Usuwam też siebie z listy drugiej osoby (doc id = moje uid -> reguła przepuści).
        batch.delete(users().document(friendUid).collection("friends").document(me))
        batch.commit().await()
    }
}

private fun DocumentSnapshot.toFriend(): Friend? {
    val name = getString("displayName") ?: return null
    return Friend(uid = id, displayName = name, addedAtMillis = getLong("addedAt") ?: 0L)
}

private fun DocumentSnapshot.toFriendRequest(): FriendRequest? {
    val name = getString("fromDisplayName") ?: return null
    return FriendRequest(fromUid = id, fromDisplayName = name, sentAtMillis = getLong("sentAt") ?: 0L)
}