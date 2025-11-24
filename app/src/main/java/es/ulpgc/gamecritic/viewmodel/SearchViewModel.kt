package es.ulpgc.gamecritic.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ulpgc.gamecritic.model.User
import es.ulpgc.gamecritic.model.Videogame
import es.ulpgc.gamecritic.repository.UserRepository
import es.ulpgc.gamecritic.repository.VideogameRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class SearchTab { VIDEOGAMES, USERS }

data class SearchState(
    val query: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val videogames: List<Videogame> = emptyList(),
    val users: List<User> = emptyList(),
    val activeTab: SearchTab = SearchTab.VIDEOGAMES
)

class SearchViewModel(
    private val videogameRepository: VideogameRepository = VideogameRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    var uiState by mutableStateOf(SearchState())
        private set

    private var searchJob: Job? = null

    fun onQueryChange(newQuery: String) {
        uiState = uiState.copy(query = newQuery)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400) // 400 ms de debounce
            triggerSearchIfNeeded()
        }
    }

    fun onSearchTriggered() {
        searchJob?.cancel()
        triggerSearchIfNeeded()
    }

    fun onTabSelected(tab: SearchTab) {
        uiState = uiState.copy(activeTab = tab)
    }

    private fun triggerSearchIfNeeded() {
        val currentQuery = uiState.query.trim()
        if (currentQuery.isEmpty()) {
            uiState = uiState.copy(
                isLoading = false,
                error = null,
                videogames = emptyList(),
                users = emptyList()
            )
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val videogames = videogameRepository.searchVideogamesByQuery(currentQuery)
                val users = userRepository.searchUsersByQuery(currentQuery)
                uiState = uiState.copy(
                    isLoading = false,
                    videogames = videogames,
                    users = users
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message ?: "Error al buscar. Inténtalo de nuevo."
                )
            }
        }
    }
}
