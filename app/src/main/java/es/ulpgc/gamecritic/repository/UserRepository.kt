package es.ulpgc.gamecritic.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.ulpgc.gamecritic.model.User
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun getCurrentUserEmail(): String? = auth.currentUser?.email

    suspend fun getUserProfile(uid: String): User? {
        val doc = firestore.collection("users").document(uid).get().await()
        if (!doc.exists()) return null
        return User(
            id = doc.getString("id") ?: uid,
            name = doc.getString("name") ?: "Sin nombre",
            username = doc.getString("username") ?: "Sin usuario",
            email = doc.getString("email") ?: "Sin email",
            profileIcon = doc.getString("profileicon") ?: "",
            description = doc.getString("description") ?: "",
            following = (doc.get("following") as? List<String>) ?: emptyList(),
            followers = (doc.get("followers") as? List<String>) ?: emptyList()
        )
    }
}
