package es.ulpgc.gamecritic.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ulpgc.gamecritic.model.Videogame
import es.ulpgc.gamecritic.repository.VideogameRepository
import kotlinx.coroutines.launch

class VideogameCarouselViewModel : ViewModel() {
    private val repository = VideogameRepository()
    val videogames = mutableStateListOf<Videogame>()

    fun loadVideogamesByCategory(category: String) {
        viewModelScope.launch {
            val result = repository.getVideogamesByCategory(category)
            videogames.clear()
            videogames.addAll(result)
        }
    }
}

