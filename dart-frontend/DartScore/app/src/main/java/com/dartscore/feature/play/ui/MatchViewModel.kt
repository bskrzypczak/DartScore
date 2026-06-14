package com.dartscore.feature.play.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dartscore.feature.play.data.MatchRepository
import com.dartscore.feature.play.domain.BotLevel
import com.dartscore.feature.play.domain.CheckoutProvider
import com.dartscore.feature.play.domain.Dart
import com.dartscore.feature.play.domain.EntryRule
import com.dartscore.feature.play.domain.GameSession
import com.dartscore.feature.play.domain.GameState
import com.dartscore.feature.play.domain.GameStatus
import com.dartscore.feature.play.domain.Multiplier
import com.dartscore.feature.play.domain.MatchRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val session: GameSession,
    private val matchRepository: MatchRepository,
) : ViewModel() {

    val state: StateFlow<GameState?> = session.state

    fun addDart(dart: Dart) {
        val s = session.state.value ?: return
        if (s.currentPlayer.isBot) return // wejścia gracza-bota nie wprowadza człowiek
        session.addDart(dart)
    }

    fun undo() = session.undo()

    fun confirm() {
        session.confirm()
        maybePlayBot()
    }

    private fun maybePlayBot() {
        viewModelScope.launch {
            var cur = session.state.value
            while (cur != null && !cur.isFinished && cur.currentPlayer.isBot) {
                delay(700)
                playBotTurn(cur.config.botLevel ?: BotLevel.MEDIUM)
                cur = session.state.value
            }
        }
    }

    private fun playBotTurn(level: BotLevel) {
        repeat(3) {
            val s = session.state.value ?: return
            if (s.isFinished || s.currentTurn.size >= 3) return
            session.addDart(botDart(s, level))
        }
        session.confirm()
    }

    // Prosty bot: próbuje checkoutu gdy blisko, inaczej celuje w T20 wg trafności.
    private fun botDart(s: GameState, level: BotLevel): Dart {
        val rem = s.provisionalRemaining
        val out = s.config.outRule
        if (out == EntryRule.DOUBLE && rem in 2..40 && rem % 2 == 0) return Dart(rem / 2, Multiplier.DOUBLE)
        if (out == EntryRule.STRAIGHT && rem in 1..20) return Dart(rem, Multiplier.SINGLE)
        return if (Random.nextDouble() < level.accuracy) Dart(20, Multiplier.TRIPLE)
        else Dart((1..20).random(), Multiplier.SINGLE)
    }

    // Zapis z perspektywy właściciela konta (gracz o indeksie 0).
    fun saveResult() {
        val s = session.state.value ?: return
        val me = s.players.firstOrNull() ?: return
        val won = (s.status as? GameStatus.Finished)?.winnerId == me.id
        viewModelScope.launch {
            runCatching {
                matchRepository.saveMatch(
                    MatchRecord(
                        mode = s.config.mode.startScore,
                        won = won,
                        opponentName = s.players.getOrNull(1)?.name ?: "Przeciwnik",
                        legsScore = if (won) "1-0" else "0-1",
                        dartsThrown = me.dartsThrown,
                        pointsScored = me.pointsScored,
                        count180s = me.count180s,
                    )
                )
            }
        }
    }

    fun leaveGame() = session.reset()

    fun getCheckoutHint(remainingScore: Int): String? {
        if (remainingScore > 170) return null

        return CheckoutProvider.getHint(remainingScore)
    }
}
