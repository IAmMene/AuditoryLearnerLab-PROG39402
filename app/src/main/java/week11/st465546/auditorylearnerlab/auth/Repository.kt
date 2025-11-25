package week11.st465546.auditorylearnerlab.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class Repository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    fun currentUser() = auth.currentUser

    suspend fun login(email: String, password: String): Result<Unit> =
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun register(email: String, password: String): Result<Unit> =
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun sendPasswordReset(email: String): Result<Unit> =
        try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    fun logout() {
        auth.signOut()
    }
}