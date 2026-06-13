package com.dartscore.feature.play.domain

// Czysty silnik X01 – bez Androida, w pełni testowalny.
object X01Engine {

    fun newGame(config: GameConfig): GameState {
        val botIndex = if (config.matchType == MatchType.BOT) config.playerNames.lastIndex else -1
        val players = config.playerNames.mapIndexed { index, name ->
            GamePlayer(
                id = index,
                name = name,
                isBot = index == botIndex,
                remaining = config.mode.startScore,
                opened = config.inRule == EntryRule.STRAIGHT, // double/triple-in: trzeba "otworzyć"
            )
        }
        return GameState(config = config, players = players)
    }

    fun addDart(state: GameState, dart: Dart): GameState =
        if (state.isFinished || state.currentTurn.size >= 3) state
        else state.copy(currentTurn = state.currentTurn + dart)

    fun undoDart(state: GameState): GameState =
        if (state.currentTurn.isEmpty()) state
        else state.copy(currentTurn = state.currentTurn.dropLast(1))

    fun confirmTurn(state: GameState): GameState {
        if (state.isFinished || state.currentTurn.isEmpty()) return state
        val p = state.currentPlayer
        val result = resolveTurn(p.remaining, p.opened, state.currentTurn, state.config)
        val turnSum = state.currentTurn.sumOf { it.points }

        val updated = p.copy(
            remaining = result.remaining,
            opened = result.opened,
            dartsThrown = p.dartsThrown + state.currentTurn.size,
            pointsScored = p.pointsScored + result.scored,
            count180s = p.count180s + if (!result.busted && turnSum == 180) 1 else 0,
            lastThrows = state.currentTurn.map { it.points },
        )
        val players = state.players.toMutableList().also { it[state.currentPlayerIndex] = updated }

        if (result.won) {
            return state.copy(players = players, currentTurn = emptyList(), status = GameStatus.Finished(p.id))
        }
        val next = (state.currentPlayerIndex + 1) % players.size
        return state.copy(players = players, currentPlayerIndex = next, currentTurn = emptyList())
    }

    private data class TurnResult(
        val remaining: Int, val opened: Boolean, val won: Boolean, val busted: Boolean, val scored: Int,
    )

    private fun resolveTurn(start: Int, opened0: Boolean, darts: List<Dart>, config: GameConfig): TurnResult {
        var rem = start
        var opened = opened0
        var scored = 0
        // Bust = pełny powrót do stanu sprzed tury.
        val bust = TurnResult(start, opened0, won = false, busted = true, scored = 0)

        for (dart in darts) {
            if (!opened) {
                val opens = when (config.inRule) {
                    EntryRule.STRAIGHT -> true
                    EntryRule.DOUBLE -> dart.isDouble
                    EntryRule.TRIPLE -> dart.isTriple
                }
                if (!opens) continue // lotka przed "otwarciem" = 0 punktów
                opened = true
            }
            val after = rem - dart.points
            when {
                after < 0 -> return bust
                after == 0 -> {
                    val outOk = when (config.outRule) {
                        EntryRule.STRAIGHT -> true
                        EntryRule.DOUBLE -> dart.isDouble
                        EntryRule.TRIPLE -> dart.isTriple
                    }
                    return if (outOk) TurnResult(0, opened, won = true, busted = false, scored = scored + dart.points)
                    else bust // trafiony zero bez właściwego wyjścia = bust
                }
                else -> {
                    // Zostawienie wyniku, z którego nie da się wyjść regułą out = bust.
                    val unfinishable = when (config.outRule) {
                        EntryRule.DOUBLE -> after == 1
                        EntryRule.TRIPLE -> after in 1..2
                        EntryRule.STRAIGHT -> false
                    }
                    if (unfinishable) return bust
                    rem = after
                    scored += dart.points
                }
            }
        }
        return TurnResult(rem, opened, won = false, busted = false, scored = scored)
    }
}