package es.ulpgc.gamecritic.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ulpgc.gamecritic.model.Comment
import es.ulpgc.gamecritic.model.Videogame
import es.ulpgc.gamecritic.repository.CommentRepository
import es.ulpgc.gamecritic.repository.VideogameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideogameProfileViewModel : ViewModel() {
    private val repository = VideogameRepository()
    private val commentRepository = CommentRepository()

    var videogame by mutableStateOf<Videogame?>(null)
        private set

    var comments by mutableStateOf<List<Comment>>(emptyList())
        private set

    var isLoadingComments by mutableStateOf(false)
        private set

    var commentsError by mutableStateOf<String?>(null)
        private set

    fun loadVideogame(id: String) {
        viewModelScope.launch {
            try {
                videogame = repository.getVideogameById(id)
            } catch (e: Exception) {
                videogame = null
            }
        }
    }

    fun loadComments(videogameId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoadingComments = true
            commentsError = null
            try {
                val data = commentRepository.getCommentsForVideogame(videogameId)
                comments = data
            } catch (e: Exception) {
                comments = emptyList()
                commentsError = e.message
            } finally {
                isLoadingComments = false
            }
        }
    }
}
