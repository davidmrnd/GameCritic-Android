package es.ulpgc.gamecritic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ulpgc.gamecritic.model.Comment
import es.ulpgc.gamecritic.repository.CommentRepository
import es.ulpgc.gamecritic.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AddCommentUiState(
    val videogameId: String = "",
    val rating: Double = 0.0,
    val content: String = "",
    val ratingError: String? = null,
    val contentError: String? = null,
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val isEditMode: Boolean = false,
    val existingCommentId: String? = null,
    val isLoadingInitial: Boolean = false,
    val loadError: String? = null
)

class AddCommentViewModel : ViewModel() {
    private val commentRepository = CommentRepository()
    private val userRepository = UserRepository()

    private val _uiState = MutableStateFlow(AddCommentUiState())
    val uiState: StateFlow<AddCommentUiState> = _uiState

    fun setVideogameId(id: String) {
        _uiState.value = _uiState.value.copy(videogameId = id)
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingInitial = true, loadError = null)
            try {
                val userId = userRepository.getCurrentUserId()
                if (userId != null) {
                    val comment: Comment? = commentRepository.getUserCommentForVideogame(userId, id)
                    if (comment != null) {
                        _uiState.value = _uiState.value.copy(
                            rating = comment.rating,
                            content = comment.content,
                            isEditMode = true,
                            existingCommentId = comment.id,
                            isLoadingInitial = false
                        )
                        return@launch
                    }
                }
                _uiState.value = _uiState.value.copy(
                    isEditMode = false,
                    existingCommentId = null,
                    isLoadingInitial = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingInitial = false,
                    loadError = e.message
                )
            }
        }
    }

    fun onRatingChange(rating: Double) {
        _uiState.value = _uiState.value.copy(rating = rating, ratingError = null)
    }

    fun onContentChange(content: String) {
        _uiState.value = _uiState.value.copy(content = content, contentError = null)
    }

    fun submit(onSuccess: () -> Unit) {
        val current = _uiState.value
        var ratingError: String? = null
        var contentError: String? = null

        if (current.rating !in 1.0..5.0) {
            ratingError = "Selecciona una puntuación entre 1 y 5 estrellas"
        }
        if (current.content.isBlank() || current.content.length < 10) {
            contentError = "El comentario debe tener al menos 10 caracteres"
        }

        if (ratingError != null || contentError != null) {
            _uiState.value = current.copy(
                ratingError = ratingError,
                contentError = contentError
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, saveError = null)
            try {
                val userId = userRepository.getCurrentUserId() ?: run {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        saveError = "Debes iniciar sesión para comentar"
                    )
                    return@launch
                }

                if (current.isEditMode && current.existingCommentId != null) {
                    commentRepository.updateComment(
                        commentId = current.existingCommentId,
                        rating = current.rating,
                        content = current.content.trim()
                    )
                } else {
                    commentRepository.addComment(
                        userId = userId,
                        videogameId = current.videogameId,
                        rating = current.rating,
                        content = current.content.trim()
                    )
                }

                _uiState.value = AddCommentUiState(videogameId = current.videogameId)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveError = e.message ?: "Error al guardar el comentario"
                )
            }
        }
    }

    fun deleteComment(onSuccess: () -> Unit) {
        val current = _uiState.value
        val commentId = current.existingCommentId ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, saveError = null)
            try {
                commentRepository.deleteComment(commentId)
                _uiState.value = AddCommentUiState(videogameId = current.videogameId)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveError = e.message ?: "Error al eliminar el comentario"
                )
            }
        }
    }
}
