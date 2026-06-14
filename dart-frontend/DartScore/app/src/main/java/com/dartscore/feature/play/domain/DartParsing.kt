package com.dartscore.feature.play.domain

// Zamienia wynik z serwera ("T20", "D16", "7", "25", "50", "0") na model Dart.
// "50" = bullseye (środek) = 25 x2; "25" = pojedynczy bull; "0" = pudło.
fun parseDartScore(raw: String): Dart? {
    val s = raw.trim().uppercase()
    return when {
        s == "0" || s == "MISS" -> Dart(0, Multiplier.SINGLE)
        s == "50" -> Dart(25, Multiplier.DOUBLE)
        s == "25" -> Dart(25, Multiplier.SINGLE)
        s.startsWith("T") -> s.drop(1).toIntOrNull()?.let { Dart(it, Multiplier.TRIPLE) }
        s.startsWith("D") -> s.drop(1).toIntOrNull()?.let { Dart(it, Multiplier.DOUBLE) }
        else -> s.toIntOrNull()?.let { Dart(it, Multiplier.SINGLE) }
    }
}

