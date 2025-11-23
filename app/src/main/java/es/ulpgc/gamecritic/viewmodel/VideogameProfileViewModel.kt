package es.ulpgc.gamecritic.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ulpgc.gamecritic.model.Videogame
import es.ulpgc.gamecritic.repository.VideogameRepository
import kotlinx.coroutines.launch

class VideogameProfileViewModel : ViewModel() {
    private val repository = VideogameRepository()

    var videogame by mutableStateOf<Videogame?>(null)
        private set

    fun loadVideogame(id: String) {
        viewModelScope.launch {
            try {
                videogame = repository.getVideogameById(id)
            } catch (e: Exception) {
                // Aquí podrías exponer también un estado de error si lo necesitas
                videogame = null
            }
        }
    }
}
