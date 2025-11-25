package week11.st465546.auditorylearnerlab.model

//Model Data Class for the Question with the correct answer
data class Question(
    val text: String = "", //question
    val options: List<String> = emptyList(),
    val correctIndex: Int = -1
)