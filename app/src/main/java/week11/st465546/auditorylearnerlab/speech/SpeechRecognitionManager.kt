package week11.st465546.auditorylearnerlab.speech

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale

class SpeechRecognitionManager(
    private val context: Context
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private val _state = MutableStateFlow(SpeechRecognitionState())
    val state: StateFlow<SpeechRecognitionState> = _state

    init {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(createRecognitionListener())
            }
        } else {
            _state.value = _state.value.copy(
                error = "Speech recognition is not available on this device"
            )
        }
    }

    fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun startListening() {
        if (!checkPermission()) {
            _state.value = _state.value.copy(
                error = "Microphone permission required",
                isListening = false
            )
            return
        }

        if (speechRecognizer == null) {
            _state.value = _state.value.copy(
                error = "Speech recognizer not available",
                isListening = false
            )
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        try {
            speechRecognizer?.startListening(intent)
            _state.value = _state.value.copy(
                isListening = true,
                error = null,
                recognizedText = ""
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                error = "Failed to start listening: ${e.message}",
                isListening = false
            )
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _state.value = _state.value.copy(isListening = false)
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _state.value = _state.value.copy(error = null)
            }

            override fun onBeginningOfSpeech() {
                // Speech has started
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Volume level changed
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // Buffer received
            }

            override fun onEndOfSpeech() {
                _state.value = _state.value.copy(isListening = false)
            }

            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                    else -> "Unknown error: $error"
                }
                _state.value = _state.value.copy(
                    error = errorMessage,
                    isListening = false
                )
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""

                _state.value = _state.value.copy(
                    recognizedText = text,
                    isListening = false,
                    error = null
                )
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""

                _state.value = _state.value.copy(
                    recognizedText = text
                )
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // Handle events if needed
            }
        }
    }
}