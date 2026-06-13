package com.dartscore.feature.play.ui

import androidx.lifecycle.ViewModel
import com.dartscore.feature.play.domain.GameConfig
import com.dartscore.feature.play.domain.GameSession
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameSetupViewModel @Inject constructor(
    private val session: GameSession,
) : ViewModel() {
    fun startGame(config: GameConfig) = session.start(config)
}