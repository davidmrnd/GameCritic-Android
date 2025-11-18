package es.ulpgc.gamecritic.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.tasks.await

class LoginRepository(private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()) {
    suspend fun login(email: String, password: String): Result<AuthResult> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}