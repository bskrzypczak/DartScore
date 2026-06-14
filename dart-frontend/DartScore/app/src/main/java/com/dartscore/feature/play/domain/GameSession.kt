package com.dartscore.feature.play.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// Singletonowy uchwyt na bieżącą rozgrywkę – dzięki niemu ekrany Match i Victory
// (płaskie trasy) widzą ten sam stan. To uproszczenie zamiast grafu-scope'owanego VM.
@Singleton
class GameSession @Inject constructor() {
    private val _state = MutableStateFlow<GameState?>(null)
    val state: StateFlow<GameState?> = _state.asStateFlow()

    fun start(config: GameConfig) { _state.value = X01Engine.newGame(config) }
    fun addDart(dart: Dart) { _state.value = _state.value?.let { X01Engine.addDart(it, dart) } }
    fun undo() { _state.value = _state.value?.let { X01Engine.undoDart(it) } }
    fun confirm() { _state.value = _state.value?.let { X01Engine.confirmTurn(it) } }
    fun loadTurn(darts: List<Dart>) { _state.value = _state.value?.let { X01Engine.setTurn(it, darts) } }
    fun reset() { _state.value = null }
}