package com.dartscore.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dartscore.feature.play.data.MatchRepository
import com.dartscore.feature.play.domain.MatchRecord
import com.dartscore.feature.profile.data.ProfileRepository
import com.dartscore.feature.profile.domain.PlayerProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val profile: PlayerProfile? = null,
    val matches: List<MatchRecord> = emptyList(),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    profileRepository: ProfileRepository,
    private val matchRepository: MatchRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        combine(
            profileRepository.observeMyProfile(),
            matchRepository.observeMyMatches(20),
        ) { profile, matches -> HomeUiState(profile, matches) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun addMatch(record: MatchRecord) {
        viewModelScope.launch {
            runCatching { matchRepository.saveMatch(record) }
        }
    }
}
