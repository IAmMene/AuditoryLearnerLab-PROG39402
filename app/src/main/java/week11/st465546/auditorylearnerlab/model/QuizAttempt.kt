package week11.st465546.auditorylearnerlab.model
/**
 * Code for quiz attempt model for Progress Bar
 *
 * Author: Mariah
 * DATE updated: Dec 4, 2025
 */
data class QuizAttempt(
    val id: String = "",
    val quizId: String = "",
    val userId: String = "",
    val date: Long = System.currentTimeMillis(),
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val score: Float = 0f
)
