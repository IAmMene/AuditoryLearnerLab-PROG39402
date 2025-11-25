package week11.st465546.auditorylearnerlab.studyset

import week11.st465546.auditorylearnerlab.model.Question

data class QuizUiState(
    val title: String = "",
    val questions: List<Question> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null

)
