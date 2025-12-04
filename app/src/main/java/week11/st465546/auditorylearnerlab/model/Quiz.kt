package week11.st465546.auditorylearnerlab.model

data class Quiz(
    var id: String = "",
    var ownerId: String = "",
    var title: String = "",
    var questions: List<Question> = emptyList(),
    var latestScore: Float? = null,
    var bestScore: Float? = null
)
