package es.ulpgc.gamecritic.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import es.ulpgc.gamecritic.data.local.AppDatabase
import es.ulpgc.gamecritic.model.RecentSearch
import es.ulpgc.gamecritic.model.User
import es.ulpgc.gamecritic.model.Videogame
import es.ulpgc.gamecritic.repository.RecentSearchRepository
import es.ulpgc.gamecritic.repository.UserRepository
import es.ulpgc.gamecritic.repository.VideogameRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class SearchTab { VIDEOGAMES, USERS }

data class SearchState(
    val query: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val videogames: List<Videogame> = emptyList(),
    val users: List<User> = emptyList(),
    val activeTab: SearchTab = SearchTab.VIDEOGAMES,
    val recentSearches: List<RecentSearch> = emptyList()
)

class SearchViewModel(
    private val videogameRepository: VideogameRepository = VideogameRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val recentSearchRepository: RecentSearchRepository
) : ViewModel() {

    var uiState by mutableStateOf(SearchState())
        private set

    private var searchJob: Job? = null

    init {
        observeRecentSearches()
    }

    private fun observeRecentSearches() {
        viewModelScope.launch {
            recentSearchRepository.observeRecentSearches(limit = 10).collectLatest { list ->
                uiState = uiState.copy(recentSearches = list)
            }
        }
    }

    fun onQueryChange(newQuery: String) {
        uiState = uiState.copy(query = newQuery)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)
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

    fun onVideogameResultClick(videogame: Videogame) {
        viewModelScope.launch {
            recentSearchRepository.saveVideogameSearch(videogame.id, videogame.title, videogame.imageProfile)
        }
    }

    fun onUserResultClick(user: User) {
        viewModelScope.launch {
            recentSearchRepository.saveUserSearch(user.id, user.username, user.profileIcon)
        }
    }

    fun onRecentSearchDeleteClicked(recentSearch: RecentSearch) {
        viewModelScope.launch {
            recentSearchRepository.deleteSearch(recentSearch)
        }
    }

    fun onClearAllRecentSearches() {
        viewModelScope.launch {
            recentSearchRepository.clearAll()
        }
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

class SearchViewModelFactory(
    private val context: android.content.Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            val db = AppDatabase.getInstance(context)
            val recentRepo = RecentSearchRepository(db.recentSearchDao())
            return SearchViewModel(
                videogameRepository = VideogameRepository(),
                userRepository = UserRepository(),
                recentSearchRepository = recentRepo
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
