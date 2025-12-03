package week11.st465546.auditorylearnerlab.speech

data class SpeechRecognitionState(
    val hasPermission: Boolean = false,
    val isListening: Boolean = false,
    val recognizedText: String = "",
    val error: String? = null
)