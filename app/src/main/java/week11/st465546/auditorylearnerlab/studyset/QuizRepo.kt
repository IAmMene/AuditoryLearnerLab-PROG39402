package week11.st465546.auditorylearnerlab.studyset

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import week11.st465546.auditorylearnerlab.model.Quiz
import week11.st465546.auditorylearnerlab.model.QuizAttempt

/**
 * Repository class responsible for data operations related to Quizzes.
 *
 * This class acts as the Single Source of Truth for quiz data, mediating between
 * the ViewModel and the Firebase Firestore backend.
 * It handles authentication checks and ensures users only access their own data.
 */
class QuizRepo {
    //Get Instances of Authentication and Database
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Returns the firebase user signed in or null if not logged in.
     */
    fun getCurrentUser() = auth.currentUser

    /**
     * Retrieves a real-time flow of quizzes owned by the current user
     *
     * Uses callbackflow to attach a Firestore SnapshotListener.
     * This ensures that any changes to DB are instantly reflected in UI.
     */
    fun getUserQuizzes(): Flow<List<Quiz>> = callbackFlow {
        val uid = auth.currentUser?.uid ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        // Listen for real-time updates in the 'quizzes' collection
        val listener = db.collection("quizzes")
            .whereEqualTo("ownerId", uid)
            .addSnapshotListener { snap, e ->
                // Map documents to Quiz objects and inject the Document ID
                val quizzes = snap?.toObjects(Quiz::class.java)?.mapIndexed { i, q ->
                    println("🔥 Parsed quiz before adding ID: $q") //debugger to ensure that the right quiz is being parsed
                    q.copy(id = snap.documents[i].id)
                } ?: emptyList()
                trySend(quizzes)
            }
        // This makes sure that the listener is removed when the Flow is cancelled
        awaitClose { listener.remove() }
    }

    /**
     * New quiz persists to Cloud Firestore
     *
     * Passes a quiz as a parameter so that the quiz object is saved.
     * Returns a successful result if the save is successful.
     * Returns a failure with an exception if not.
     */
    suspend fun saveQuiz(quiz: Quiz): Result<Unit> =
        try {
            db.collection("quizzes").add(quiz).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * Updates an existing quiz document in Cloud Firestore.
     * Uses FirebaseFirestore.set to overwrite the document identified by quiz ID.
     */
    suspend fun updateQuiz(quiz: Quiz): Result<Unit> =
        try {
            // Uses .set() to overwrite the existing document with new data
            db.collection("quizzes").document(quiz.id).set(quiz).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * Deletes a specific quiz document from Cloud Firestore.
     * Utilizes quizId as a unique document ID of the quiz to remove it.
     */
    suspend fun deleteQuiz(quizId: String): Result<Unit> =
        try {
            db.collection("quizzes").document(quizId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * Save a quiz attempt to Firestore
     * Used for the Progress Bar Logic
     */
    suspend fun saveQuizAttempt(attempt: QuizAttempt): Result<Unit> =
        try {
            println("🔥 Starting to save quiz attempt for quiz: ${attempt.quizId}")

            //Save the attempt
            db.collection("quiz_attempts").add(attempt).await()
            println("🔥 Quiz attempt saved to quiz_attempts collection")

            // Update the quiz with the latest score
            val quizRef = db.collection("quizzes").document(attempt.quizId)

            // Get current quiz data first to check best score
            val quizDoc = quizRef.get().await()
            if (quizDoc.exists()) {
                println("🔥 Found quiz document: ${quizDoc.data}")

                // Get current best score if it exists
                val currentBestScore = quizDoc.getDouble("bestScore")?.toFloat()
                println("🔥 Current best score: $currentBestScore")

                val newBestScore = if (currentBestScore != null) {
                    maxOf(currentBestScore, attempt.score)
                } else {
                    attempt.score
                }
                println("🔥 New best score: $newBestScore")

                // Use update() with specific fields only
                quizRef.update(
                    mapOf(
                        "latestScore" to attempt.score,
                        "bestScore" to newBestScore
                    )
                ).await()

                println("🔥 Quiz scores updated successfully")
            } else {
                println("🔥 ERROR: Quiz document not found: ${attempt.quizId}")
            }

            Result.success(Unit)
        } catch (e: Exception) {
            println("🔥 ERROR saving quiz attempt: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }

    //Get Quiz Attempts for Specific Quiz
//    suspend fun getQuizAttempts(quizId: String): List<QuizAttempt> =
//        try {
//            val snapshot = db.collection("quiz_attempts")
//                .whereEqualTo("quizId", quizId)
//                .whereEqualTo("userId", auth.currentUser?.uid ?: "")
//                .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
//                .get()
//                .await()
//
//            snapshot.documents.map { doc ->
//                doc.toObject(QuizAttempt::class.java)?.copy(id = doc.id) ?: QuizAttempt()
//            }
//        } catch (e: Exception) {
//            emptyList()
//        }

}