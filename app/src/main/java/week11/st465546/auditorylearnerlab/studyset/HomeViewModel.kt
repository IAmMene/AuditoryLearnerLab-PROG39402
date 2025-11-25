package week11.st465546.auditorylearnerlab.studyset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import week11.st465546.auditorylearnerlab.model.Question
import week11.st465546.auditorylearnerlab.model.Quiz

class HomeViewModel( private val repo: QuizRepo = QuizRepo() ): ViewModel() {

    private val _ui = MutableStateFlow(QuizUiState())
    val ui: StateFlow<QuizUiState> = _ui

    private val _quizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val quizzes: StateFlow<List<Quiz>> = _quizzes

    init {
        viewModelScope.launch {
            println("🔥 Current UID = ${repo.getCurrentUser()?.uid}")
            repo.getUserQuizzes().collect {
                _quizzes.value = it
                println("🔥 QUIZ SNAPSHOT:")
                it.forEach { q -> println(q) }
            }
        }
    }

    fun updateTitle(v: String) {
        _ui.value = _ui.value.copy(title = v)
    }

    fun addQuestion(q: Question) {
        val updated = _ui.value.questions.toMutableList()
        updated.add(q)
        _ui.value = _ui.value.copy(questions = updated)
    }

    fun saveQuiz(onSaved: () -> Unit) {
        val user = repo.getCurrentUser() ?: return
        val state = _ui.value

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
            println("🔥 SAVING QUIZ = $quiz")
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


}