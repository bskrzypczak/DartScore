package com.dartscore.feature.friends.domain

// Znajomy na mojej liście (users/{me}/friends/{uid}).
data class Friend(
    val uid: String,
    val displayName: String,
    val addedAtMillis: Long = 0L,
)

// Oczekujące zaproszenie DO mnie (users/{me}/friendRequests/{fromUid}).
data class FriendRequest(
    val fromUid: String,
    val fromDisplayName: String,
    val sentAtMillis: Long = 0L,
)

// Wynik wyszukiwania gracza po nicku (kogo można zaprosić).
data class PlayerSearchResult(
    val uid: String,
    val displayName: String,
    val country: String = "PL",
)