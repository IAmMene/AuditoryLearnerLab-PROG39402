package week11.st465546.auditorylearnerlab.studyset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import week11.st465546.auditorylearnerlab.model.Question
import week11.st465546.auditorylearnerlab.model.Quiz
import week11.st465546.auditorylearnerlab.model.QuizAttempt

class HomeViewModel( private val repo: QuizRepo = QuizRepo() ): ViewModel() {

    /**
     *
     * State Flow for UI State, and Quizzes
     */
    private val _ui = MutableStateFlow(QuizUiState())
    val ui: StateFlow<QuizUiState> = _ui

    private val _quizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val quizzes: StateFlow<List<Quiz>> = _quizzes

    /**
     * Initializer of the Coroutine Flow
     */
    init {
        viewModelScope.launch {
            println("🔥 Current UID = ${repo.getCurrentUser()?.uid}") //debugger of the current user id
            repo.getUserQuizzes().collect { //show all the quizzes of the user
                _quizzes.value = it
                println("🔥 QUIZ SNAPSHOT:")
                it.forEach { q -> println(q) }
            }
        }
    }

    /**
     * Below Code is for the Screens of Creating a Quiz or Taking a Quiz
     */

    fun updateTitle(v: String) {
        _ui.value = _ui.value.copy(title = v)
    }

    fun addQuestion(q: Question) {
        val updated = _ui.value.questions.toMutableList()
        updated.add(q)
        _ui.value = _ui.value.copy(questions = updated)
    }

    fun saveQuiz(onSaved: () -> Unit) {
        val user = repo.getCurrentUser() ?: return //get the current user
        val state = _ui.value //get the current state

        //Required Information for saving a quiz on create quiz
        if (state.title.isBlank() || state.questions.isEmpty()) {
            _ui.value = state.copy(error = "Title & questions required")
            return
        }

        _ui.value = state.copy(loading = true)

        viewModelScope.launch {
            val quiz = Quiz(
                ownerId = user.uid,
                title = state.title,
                questions = state.questions
            )
            println("🔥 SAVING QUIZ = $quiz") //debugger to print if the quiz is saved with ID
            val result = repo.saveQuiz(quiz)
            _ui.value = _ui.value.copy(loading = false)

            if (result.isSuccess) onSaved()
            else _ui.value = _ui.value.copy(error = result.exceptionOrNull()?.message)
        }
    }

    fun deleteQuiz(quizId: String) {
        viewModelScope.launch {
            val result = repo.deleteQuiz(quizId)
            if (result.isFailure) {
                _ui.value = _ui.value.copy(
                    error = result.exceptionOrNull()?.message
                )
            }
            // No need to update _quizzes manually — snapshot listener auto-updates
        }
    }

  //Save Quiz Attempt after taking a quiz
    fun saveQuizAttempt(quizId: String, correctAnswers: Int, totalQuestions: Int) {
        val user = repo.getCurrentUser() ?: return
        val score = if (totalQuestions > 0) correctAnswers.toFloat() / totalQuestions.toFloat() else 0f

        viewModelScope.launch {
            val attempt = QuizAttempt(
                quizId = quizId,
                userId = user.uid,
                totalQuestions = totalQuestions,
                correctAnswers = correctAnswers,
                score = score
            )

            val result = repo.saveQuizAttempt(attempt)
            if (result.isFailure) {
                println("🔥 Failed to save quiz attempt: ${result.exceptionOrNull()?.message}")
            } else {
                println("🔥 Quiz attempt saved successfully!")
            }
        }
    }
}
