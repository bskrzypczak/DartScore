package com.dartscore.feature.play.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dartscore.feature.play.data.DartboardScanRepository
import com.dartscore.feature.play.domain.ScanResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

sealed interface ScanUiState {
    data object Idle : ScanUiState
    data object Loading : ScanUiState
    data class Success(val result: ScanResult) : ScanUiState
    data class Error(val message: String) : ScanUiState
}

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val repository: DartboardScanRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val state: StateFlow<ScanUiState> = _state.asStateFlow()

    fun scan(image: File) {
        viewModelScope.launch {
            _state.value = ScanUiState.Loading
            _state.value = try {
                ScanUiState.Success(repository.scan(image))
            } catch (e: Exception) {
                ScanUiState.Error(e.message ?: "Nie udało się połączyć z serwerem")
            }
        }
    }

    fun reset() { _state.value = ScanUiState.Idle }
}