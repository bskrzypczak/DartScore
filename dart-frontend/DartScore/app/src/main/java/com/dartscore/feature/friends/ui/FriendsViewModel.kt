package com.dartscore.feature.friends.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dartscore.feature.friends.data.FriendsRepository
import com.dartscore.feature.friends.domain.Friend
import com.dartscore.feature.friends.domain.FriendRequest
import com.dartscore.feature.friends.domain.PlayerSearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FriendsUiState(
    val friends: List<Friend> = emptyList(),
    val requests: List<FriendRequest> = emptyList(),
    val searchResults: List<PlayerSearchResult> = emptyList(),
    val isSearching: Boolean = false,
    val message: String? = null,
)

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val repository: FriendsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeFriends().collect { list -> _uiState.update { it.copy(friends = list) } }
        }
        viewModelScope.launch {
            repository.observeIncomingRequests().collect { list -> _uiState.update { it.copy(requests = list) } }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            try {
                val results = repository.searchUsers(query)
                _uiState.update { it.copy(searchResults = results, isSearching = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSearching = false, message = e.message ?: "Błąd wyszukiwania") }
            }
        }
    }

    fun sendRequest(uid: String) = act("Wysłano zaproszenie") { repository.sendRequest(uid) }
    fun accept(request: FriendRequest) = act("Dodano znajomego") { repository.acceptRequest(request) }
    fun decline(fromUid: String) = act("Odrzucono zaproszenie") { repository.declineRequest(fromUid) }
    fun remove(friendUid: String) = act("Usunięto znajomego") { repository.removeFriend(friendUid) }

    fun clearMessage() = _uiState.update { it.copy(message = null) }

    private fun act(successMessage: String, block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
                _uiState.update { it.copy(message = successMessage) }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = e.message ?: "Coś poszło nie tak") }
            }
        }
    }
}

