package week11.st465546.auditorylearnerlab.studyset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import week11.st465546.auditorylearnerlab.model.Question
import week11.st465546.auditorylearnerlab.model.Quiz

/**
 * ViewModel responsible for managing the UI state of the Home Screen and Quiz Creation/Editing flows.
 *
 * It holds the list of quizzes observed from the QuizRepo and manages the transient state
 * of the quiz currently being created or edited.
 */
class HomeViewModel( private val repo: QuizRepo = QuizRepo() ): ViewModel() {
    // Represents the state of the form (Title, Questions) during creation/editing
    private val _ui = MutableStateFlow(QuizUiState())
    val ui: StateFlow<QuizUiState> = _ui

    // Represents the list of quizzes from Firestore.
    private val _quizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val quizzes: StateFlow<List<Quiz>> = _quizzes

    init {
        // Initialize the real-time stream of user quizzes
        viewModelScope.launch {
            println("🔥 Current UID = ${repo.getCurrentUser()?.uid}")
            repo.getUserQuizzes().collect {
                _quizzes.value = it
                println("🔥 QUIZ SNAPSHOT:")
                it.forEach { q -> println(q) }
            }
        }
    }

    // Updates the temporary title state as the user type.
    fun updateTitle(v: String) {
        _ui.value = _ui.value.copy(title = v)
    }

    // Add a new question to the temporary state list
    fun addQuestion(q: Question) {
        val updated = _ui.value.questions.toMutableList()
        updated.add(q)
        _ui.value = _ui.value.copy(questions = updated)
    }

    /**
     * Removes a question from the temporary state list by index.
     * Allows users to delete specific questions while editing a quiz.
     */
    fun removeQuestion(index: Int) {
        val updated = _ui.value.questions.toMutableList()
        if (index in updated.indices) {
            updated.removeAt(index)
            _ui.value = _ui.value.copy(questions = updated)
        }
    }

    // Helper to clear the form for a new quiz
    fun resetUiState() {
        _ui.value = QuizUiState()
    }

    /**
     * Pre-fills the UI state with data from an existing quiz.
     * Used when the user selects "Edit" on a quiz card.
     */
    fun loadQuizForEdit(quiz: Quiz) {
        _ui.value = QuizUiState(
            id = quiz.id,
            title = quiz.title,
            questions = quiz.questions
        )
    }

    /**
     * Persists the current state to the database.
     *
     * Logic:
     * 1. Validates input (title and questions must exist).
     * 2. Checks if QuizUIState.id is present.
     * - If present: Calls repo.updateQuiz (Edit mode).
     * - If empty: Calls repo.saveQuiz (Create mode).
     */
    fun saveQuiz(onSaved: () -> Unit) {
        val user = repo.getCurrentUser() ?: return
        val state = _ui.value

        if (state.title.isBlank() || state.questions.isEmpty()) {
            _ui.value = state.copy(error = "Title & questions required")
            return
        }
        _ui.value = state.copy(loading = true)
        //Updated viewModelScope to take into consideration updateQuiz and newQuiz
        viewModelScope.launch {
            // If ID exists, we are updating if ID is empty we are creating
            val result = if (state.id.isNotBlank()) {
                val updatedQuiz = Quiz(
                    id = state.id,
                    ownerId = user.uid,
                    title = state.title,
                    questions = state.questions
                )
                repo.updateQuiz(updatedQuiz)
            } else {
                val newQuiz = Quiz(
                    ownerId = user.uid,
                    title = state.title,
                    questions = state.questions
                )
                repo.saveQuiz(newQuiz)
            }

            _ui.value = _ui.value.copy(loading = false)

            if (result.isSuccess) {
                resetUiState() // Clear state after success
                onSaved()
            } else {
                _ui.value = _ui.value.copy(error = result.exceptionOrNull()?.message)
            }
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
        }
    }
}