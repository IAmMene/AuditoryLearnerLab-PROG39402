package week11.st465546.auditorylearnerlab.model

data class Quiz(
    val id: String = "",
    val ownerId: String = "",
    val title: String = "",
    val questions: List<Question> = emptyList()
)
