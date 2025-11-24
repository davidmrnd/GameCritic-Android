package es.ulpgc.gamecritic.repository

import com.google.firebase.firestore.FirebaseFirestore
import es.ulpgc.gamecritic.model.Videogame
import kotlinx.coroutines.tasks.await

class VideogameRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getVideogamesByCategory(category: String): List<Videogame> {
        val snapshot = firestore.collection("videogames").get().await()
        return snapshot.documents.mapNotNull { doc ->
            val categories = doc.get("category") as? List<String> ?: emptyList()
            if (categories.contains(category)) {
                Videogame(
                    id = doc.getString("id") ?: doc.id,
                    title = doc.getString("title") ?: "",
                    subtitle = doc.getString("subtitle") ?: "",
                    description = doc.getString("description") ?: "",
                    category = categories,
                    imageCarousel = doc.getString("imagecarousel") ?: "",
                    imageProfile = doc.getString("imageprofile") ?: ""
                )
            } else null
        }
    }

    suspend fun getVideogameById(id: String): Videogame? {
        val doc = firestore
            .collection("videogames")
            .document(id)
            .get()
            .await()

        if (!doc.exists()) return null

        val categories = doc.get("category") as? List<String> ?: emptyList()

        return Videogame(
            id = doc.getString("id") ?: doc.id,
            title = doc.getString("title") ?: "",
            subtitle = doc.getString("subtitle") ?: "",
            description = doc.getString("description") ?: "",
            category = categories,
            imageCarousel = doc.getString("imagecarousel") ?: "",
            imageProfile = doc.getString("imageprofile") ?: ""
        )
    }

    suspend fun searchVideogamesByQuery(query: String, limit: Long = 50): List<Videogame> {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isEmpty()) return emptyList()

        val snapshot = firestore
            .collection("videogames")
            .limit(limit)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val categories = doc.get("category") as? List<String> ?: emptyList()
            val title = doc.getString("title") ?: ""
            val subtitle = doc.getString("subtitle") ?: ""

            val normalizedTitle = title.lowercase()
            val normalizedSubtitle = subtitle.lowercase()
            val normalizedSearch = normalizedQuery.lowercase()

            if (normalizedTitle.contains(normalizedSearch) || normalizedSubtitle.contains(normalizedSearch)) {
                Videogame(
                    id = doc.getString("id") ?: doc.id,
                    title = title,
                    subtitle = subtitle,
                    description = doc.getString("description") ?: "",
                    category = categories,
                    imageCarousel = doc.getString("imagecarousel") ?: "",
                    imageProfile = doc.getString("imageprofile") ?: ""
                )
            } else {
                null
            }
        }
    }
}
