package com.dartscore.feature.play.domain

enum class GameMode(val startScore: Int, val label: String) {
    MODE_101(101, "101"), MODE_301(301, "301"), MODE_501(501, "501")
}

// Wspólna reguła dla wejścia (in) i wyjścia (out).
enum class EntryRule(val label: String) {
    STRAIGHT("Straight"), DOUBLE("Double"), TRIPLE("Triple")
}

enum class MatchType(val label: String) {
    DUEL("Duel"), FRIENDLY("Friendly"), MULTIPLAYER("Multiplayer"), BOT("Z botem")
}

enum class BotLevel(val label: String, val accuracy: Double) {
    EASY("Łatwy", 0.30), MEDIUM("Średni", 0.55), HARD("Trudny", 0.80)
}

enum class Multiplier(val factor: Int, val label: String) {
    SINGLE(1, ""), DOUBLE(2, "D"), TRIPLE(3, "T")
}

data class Dart(val segment: Int, val multiplier: Multiplier) {
    val points: Int get() = segment * multiplier.factor
    val isDouble: Boolean get() = multiplier == Multiplier.DOUBLE
    val isTriple: Boolean get() = multiplier == Multiplier.TRIPLE
    val label: String get() = if (segment == 0) "Miss" else "${multiplier.label}$segment"
}

data class GameConfig(
    val mode: GameMode,
    val inRule: EntryRule,
    val outRule: EntryRule,
    val matchType: MatchType,
    val playerNames: List<String>,
    val botLevel: BotLevel? = null,
) {
    // Mecz typu Duel zapisuje się automatycznie; Friendly/Multiplayer ręcznie.
    val autoSave: Boolean get() = matchType == MatchType.DUEL
}

data class GamePlayer(
    val id: Int,
    val name: String,
    val isBot: Boolean = false,
    val remaining: Int,
    val opened: Boolean = false,
    val dartsThrown: Int = 0,
    val pointsScored: Int = 0,
    val count180s: Int = 0,
    val lastThrows: List<Int> = emptyList(),
) {
    val threeDartAvg: Double get() = if (dartsThrown == 0) 0.0 else pointsScored.toDouble() / dartsThrown * 3
}

sealed interface GameStatus {
    data object InProgress : GameStatus
    data class Finished(val winnerId: Int) : GameStatus
}

data class GameState(
    val config: GameConfig,
    val players: List<GamePlayer>,
    val currentPlayerIndex: Int = 0,
    val currentTurn: List<Dart> = emptyList(),
    val status: GameStatus = GameStatus.InProgress,
) {
    val currentPlayer: GamePlayer get() = players[currentPlayerIndex]
    val turnPoints: Int get() = currentTurn.sumOf { it.points }
    val provisionalRemaining: Int get() = currentPlayer.remaining - turnPoints
    val isFinished: Boolean get() = status is GameStatus.Finished
    val winner: GamePlayer? get() = (status as? GameStatus.Finished)?.let { f -> players.first { it.id == f.winnerId } }
}