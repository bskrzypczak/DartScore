package com.dartscore.feature.play.domain

// Pojedynczy mecz w historii gracza (users/{uid}/matches/{id}).
// To rekord WYNIKU meczu, nie stan rozgrywki – zapisujemy go po zakończeniu.
data class MatchRecord(
    val id: String = "",
    val mode: Int,                 // 501 / 301 / 101
    val won: Boolean,
    val opponentName: String,
    val legsScore: String,         // np. "3-2"
    val dartsThrown: Int,
    val pointsScored: Int,
    val count180s: Int = 0,
    val playedAtMillis: Long = System.currentTimeMillis(),
) {
    val ppd: Double get() = if (dartsThrown == 0) 0.0
    else pointsScored.toDouble() / dartsThrown * 3
}