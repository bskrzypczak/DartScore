package com.dartscore.feature.profile.domain

// Publiczny profil gracza (users/{uid}). Trzymamy SUROWE liczniki,
// a pochodne (PPD, winRate) liczymy z nich – w NoSQL nie robimy AVG po historii,
// bo to kosztowałoby odczyt wszystkich meczów przy każdym wyświetleniu.
data class PlayerProfile(
    val uid: String,
    val displayName: String,
    val country: String = "PL",
    val photoUrl: String? = null,

    // Agregaty aktualizowane przy każdym zapisanym meczu:
    val totalMatches: Int = 0,
    val wins: Int = 0,
    val total180s: Int = 0,
    val totalDartsThrown: Int = 0,
    val totalPointsScored: Int = 0,
) {
    val losses: Int get() = totalMatches - wins
    val winRate: Int get() = if (totalMatches == 0) 0 else (wins * 100) / totalMatches
    val ppd: Double get() = if (totalDartsThrown == 0) 0.0
    else totalPointsScored.toDouble() / totalDartsThrown * 3
}