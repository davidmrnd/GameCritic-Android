package es.ulpgc.gamecritic.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

        val userCache = mutableMapOf<String, DocumentSnapshot?>()
        val gameCache = mutableMapOf<String, DocumentSnapshot?>()

        snapshot.documents.map { doc ->
            async { mapComment(doc, userCache, gameCache) }
        }.awaitAll()
    }

    suspend fun getCommentsForUser(userId: String): List<Comment> = coroutineScope {
        val snapshot = firestore.collection("comments")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()

        val userCache = mutableMapOf<String, DocumentSnapshot?>()
        val gameCache = mutableMapOf<String, DocumentSnapshot?>()

        snapshot.documents.map { doc ->
            async { mapComment(doc, userCache, gameCache) }
        }.awaitAll()
    }

    suspend fun addComment(
        userId: String,
        videogameId: String,
        rating: Double,
        content: String
    ) {
        val createdAt = Instant.now().toString()
        val data = hashMapOf(
            "userId" to userId,
            "videogameId" to videogameId,
            "rating" to rating,
            "content" to content,
            "createdAt" to createdAt
        )

        firestore.collection("comments")
            .add(data)
            .await()
    }

    suspend fun getUserCommentForVideogame(userId: String, videogameId: String): Comment? {
        val snapshot = firestore.collection("comments")
            .whereEqualTo("userId", userId)
            .whereEqualTo("videogameId", videogameId)
            .limit(1)
            .get()
            .await()

        val doc = snapshot.documents.firstOrNull() ?: return null
        val userCache = mutableMapOf<String, DocumentSnapshot?>()
        val gameCache = mutableMapOf<String, DocumentSnapshot?>()
        return mapComment(doc, userCache, gameCache)
    }

    suspend fun updateComment(commentId: String, rating: Double, content: String) {
        val updates = mapOf(
            "rating" to rating,
            "content" to content
        )
        firestore.collection("comments").document(commentId).update(updates).await()
    }

    suspend fun deleteComment(commentId: String) {
        firestore.collection("comments").document(commentId).delete().await()
    }

    private suspend fun mapComment(
        doc: DocumentSnapshot,
        userCache: MutableMap<String, DocumentSnapshot?>,
        gameCache: MutableMap<String, DocumentSnapshot?>
    ): Comment {
        val userId = doc.getString("userId") ?: ""
        val videogameId = doc.getString("videogameId") ?: ""
        val createdAtRaw = doc.getString("createdAt") ?: ""

        val userDoc = fetchUser(userId, userCache)
        val gameDoc = fetchGame(videogameId, gameCache)

        return Comment(
            id = doc.id,
            videogameId = videogameId,
            videogameTitle = gameDoc?.getString("title") ?: "",
            videogameImage = gameDoc?.getString("imageprofile") ?: "",
            userId = userId,
            username = userDoc?.getString("username") ?: "Usuario desconocido",
            userProfileIcon = userDoc?.getString("profileicon") ?: "",
            content = doc.getString("content") ?: "",
            rating = doc.getDouble("rating") ?: 0.0,
            createdAt = createdAtRaw,
            createdAtFormatted = formatTimestamp(createdAtRaw)
        )
    }

    private suspend fun fetchUser(
        userId: String,
        cache: MutableMap<String, DocumentSnapshot?>
    ): DocumentSnapshot? {
        if (userId.isBlank()) return null
        if (cache.containsKey(userId)) return cache[userId]
        val snapshot = firestore.collection("users").document(userId).get().await()
        cache[userId] = snapshot
        return snapshot
    }

    private suspend fun fetchGame(
        videogameId: String,
        cache: MutableMap<String, DocumentSnapshot?>
    ): DocumentSnapshot? {
        if (videogameId.isBlank()) return null
        if (cache.containsKey(videogameId)) return cache[videogameId]
        val snapshot = firestore.collection("videogames").document(videogameId).get().await()
        cache[videogameId] = snapshot
        return snapshot
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
