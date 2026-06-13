package com.dartscore.feature.play.domain

// Wynik odczytu tarczy – to, co UI pokazuje do potwierdzenia.
data class ScanResult(
    val darts: List<String>,
    val total: Int,
    val calibrationOk: Boolean,
    val mockMode: Boolean,
)