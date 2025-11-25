package week11.st465546.auditorylearnerlab.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/*
AuditoryLearnerLab
Group 5
Date: November 25 2025


Responsible for handling all interactions with Firebase Authentication
 */
class Repository(
    //get instance of Firebase Authorization
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    //get current user
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