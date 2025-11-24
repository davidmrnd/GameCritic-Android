package es.ulpgc.gamecritic.repository

import com.google.firebase.firestore.FirebaseFirestore
import es.ulpgc.gamecritic.model.Comment
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class CommentRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getCommentsForVideogame(videogameId: String): List<Comment> = coroutineScope {
        val snapshot = firestore.collection("comments")
            .whereEqualTo("videogameId", videogameId)
            .orderBy("createdAt")
            .get()
            .await()

        snapshot.documents.map { doc ->
            async {
                val userId = doc.getString("userId").orEmpty()
                val userDoc = if (userId.isNotBlank()) {
                    firestore.collection("users").document(userId).get().await()
                } else null

                val createdAtRaw = doc.getString("createdAt") ?: ""

                Comment(
                    id = doc.id,
                    videogameId = doc.getString("videogameId") ?: "",
                    userId = userId,
                    username = userDoc?.getString("username") ?: "Usuario desconocido",
                    userProfileIcon = userDoc?.getString("profileicon") ?: "",
                    content = doc.getString("content") ?: "",
                    rating = doc.getDouble("rating") ?: 0.0,
                    createdAt = createdAtRaw,
                    createdAtFormatted = formatTimestamp(createdAtRaw)
                )
            }
        }.awaitAll()
    }

    private fun formatTimestamp(raw: String): String {
        if (raw.isBlank()) return ""
        return try {
            DISPLAY_FORMATTER.format(Instant.parse(raw))
        } catch (_: Exception) {
            raw
        }
    }

    private companion object {
        val DISPLAY_FORMATTER: DateTimeFormatter = DateTimeFormatter
            .ofPattern("dd MMM yyyy • HH:mm")
            .withLocale(Locale("es", "ES"))
            .withZone(ZoneId.systemDefault())
    }
}
