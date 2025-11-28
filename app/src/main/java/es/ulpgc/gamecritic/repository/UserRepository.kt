package es.ulpgc.gamecritic.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import es.ulpgc.gamecritic.model.User
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun getCurrentUserEmail(): String? = auth.currentUser?.email

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
            following = (doc.get("following") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            followers = (doc.get("followers") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
        )
    }

    suspend fun updateUserProfile(uid: String, name: String, username: String, description: String) {
        val updates = mapOf(
            "name" to name,
            "username" to username,
            "description" to description
        )
        firestore.collection("users").document(uid).update(updates).await()
    }

    suspend fun updateUserProfileImage(uid: String, base64Image: String?) {
        val updates = if (base64Image != null) {
            mapOf("profileicon" to base64Image)
        } else {
            mapOf("profileicon" to "")
        }
        firestore.collection("users").document(uid).update(updates).await()
    }

    // Método optimizado para actualizar perfil e imagen en una sola llamada
    suspend fun updateUserProfileWithImage(
        uid: String,
        name: String,
        username: String,
        description: String,
        base64Image: String?
    ) {
        val updates = mutableMapOf<String, Any>(
            "name" to name,
            "username" to username,
            "description" to description
        )
        if (base64Image != null) {
            updates["profileicon"] = base64Image
        }
        firestore.collection("users").document(uid).update(updates).await()
    }

    suspend fun searchUsersByQuery(query: String, limit: Long = 50): List<User> {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isEmpty()) return emptyList()

        val snapshot = firestore
            .collection("users")
            .limit(limit)
            .get()
            .await()

        val normalizedSearch = normalizedQuery.lowercase()

        return snapshot.documents.mapNotNull { doc ->
            val name = doc.getString("name") ?: ""
            val username = doc.getString("username") ?: ""

            if (name.lowercase().contains(normalizedSearch) || username.lowercase().contains(normalizedSearch)) {
                User(
                    id = doc.getString("id") ?: doc.id,
                    name = name.ifBlank { "Sin nombre" },
                    username = username.ifBlank { "Sin usuario" },
                    email = doc.getString("email") ?: "Sin email",
                    profileIcon = doc.getString("profileicon") ?: "",
                    description = doc.getString("description") ?: "",
                    following = (doc.get("following") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                    followers = (doc.get("followers") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                )
            } else {
                null
            }
        }
    }

    // Añadido: seguir a un usuario (actualiza followers y following en una transacción)
    suspend fun followUser(currentUid: String, targetUid: String) {
        if (currentUid == targetUid) return
        firestore.runTransaction { transaction ->
            val targetRef = firestore.collection("users").document(targetUid)
            val currentRef = firestore.collection("users").document(currentUid)
            // Usar arrayUnion para añadir si no existe
            transaction.update(targetRef, "followers", FieldValue.arrayUnion(currentUid))
            transaction.update(currentRef, "following", FieldValue.arrayUnion(targetUid))
        }.await()
    }

    // Añadido: dejar de seguir a un usuario
    suspend fun unfollowUser(currentUid: String, targetUid: String) {
        if (currentUid == targetUid) return
        firestore.runTransaction { transaction ->
            val targetRef = firestore.collection("users").document(targetUid)
            val currentRef = firestore.collection("users").document(currentUid)
            transaction.update(targetRef, "followers", FieldValue.arrayRemove(currentUid))
            transaction.update(currentRef, "following", FieldValue.arrayRemove(targetUid))
        }.await()
    }

    // Añadido: comprobar si currentUid sigue a targetUid
    suspend fun isFollowing(currentUid: String, targetUid: String): Boolean {
        if (currentUid == targetUid) return false
        val doc = firestore.collection("users").document(currentUid).get().await()
        if (!doc.exists()) return false
        val following = (doc.get("following") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
        return following.contains(targetUid)
    }
}
