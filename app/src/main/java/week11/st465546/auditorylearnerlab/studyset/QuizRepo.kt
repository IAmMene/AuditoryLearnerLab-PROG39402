package week11.st465546.auditorylearnerlab.studyset

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import week11.st465546.auditorylearnerlab.model.Quiz

class QuizRepo {

    //Get Instances of Authentication and Database
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()



    fun getCurrentUser() = auth.currentUser

    // real-time user quizzes
    fun getUserQuizzes(): Flow<List<Quiz>> = callbackFlow {
        val uid = auth.currentUser?.uid ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db.collection("quizzes")
            .whereEqualTo("ownerId", uid)
            .addSnapshotListener { snap, _ ->
                val quizzes = snap?.toObjects(Quiz::class.java)?.mapIndexed { i, q ->
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
}