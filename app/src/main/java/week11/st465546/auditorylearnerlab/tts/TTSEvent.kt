package week11.st465546.auditorylearnerlab.tts

//list of events
sealed class TTSEvent {
    data class Started(val utteranceId: String?) : TTSEvent() //event for starting of speech
    data class Finished(val utteranceId: String?) : TTSEvent() //event for finishing speech
    data class Error(val utteranceId: String?) : TTSEvent() //event for error in speech

}
