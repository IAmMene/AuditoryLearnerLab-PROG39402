package week11.st465546.auditorylearnerlab.studyset

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import week11.st465546.auditorylearnerlab.model.Quiz
import week11.st465546.auditorylearnerlab.model.QuizAttempt

class QuizRepo {

    //Get Instances of Authentication and Database
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()



    fun getCurrentUser() = auth.currentUser //get the current user

    // real-time user quizzes
    fun getUserQuizzes(): Flow<List<Quiz>> = callbackFlow {
        val uid = auth.currentUser?.uid ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db.collection("quizzes")
            .whereEqualTo("ownerId", uid)
            .addSnapshotListener { snap, e ->

                val quizzes = snap?.toObjects(Quiz::class.java)?.mapIndexed { i, q ->
                    println("🔥 Parsed quiz before adding ID: $q") //debugger to ensure that the right quiz is being parsed
                    q.copy(id = snap.documents[i].id)
                } ?: emptyList()


                trySend(quizzes)
            }

        awaitClose { listener.remove() }
    }

    // save quiz
    suspend fun saveQuiz(quiz: Quiz): Result<Unit> =
        try {
            db.collection("quizzes").add(quiz).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

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