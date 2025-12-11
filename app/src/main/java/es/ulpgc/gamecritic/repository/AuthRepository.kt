package es.ulpgc.gamecritic.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.ulpgc.gamecritic.model.User
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(auth.currentUser?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, username: String, email: String, password: String): Result<String> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val uid = auth.currentUser?.uid ?: throw Exception("No se pudo obtener el UID")
            val user = User(
                id = uid,
                name = name,
                username = username,
                email = email,
                profileIcon = "https://cdn-icons-png.flaticon.com/512/3870/3870822.png",
                description = "",
                following = emptyList(),
                followers = emptyList()
            )
            firestore.collection("users").document(uid).set(
                mapOf(
                    "id" to user.id,
                    "name" to user.name,
                    "username" to user.username,
                    "email" to user.email,
                    "profileicon" to user.profileIcon,
                    "description" to user.description,
                    "following" to user.following,
                    "followers" to user.followers
                )
            ).await()
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
