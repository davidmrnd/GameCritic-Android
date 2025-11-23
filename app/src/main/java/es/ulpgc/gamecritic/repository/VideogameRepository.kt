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
}

