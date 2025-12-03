package week11.st465546.auditorylearnerlab.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/*
       Name: Mariah Falzon
       Date: Dec 3 2025

       Description: manager to handle text-to-speech

        Citation: https://medium.com/@vedantkingh/unlocking-the-power-of-android-tts-a-guide-to-using-different-languages-and-voices-fe4e6ead2368
        https://developer.android.com/reference/android/speech/tts/TextToSpeech

 */


class TTSManager(context: Context) {
    private var tts: TextToSpeech? = null //initialize TTS
    private var isInitialized = false
    // A list of listeners to be notified when TTS initialization is complete
    private val initializationListeners = mutableListOf<(Boolean) -> Unit>()

    init {
        //start TTS Initialization
        tts = TextToSpeech(context) { status ->
            isInitialized = if (status == TextToSpeech.SUCCESS) { //if successful the isInitialized is true
                // Set language to default locale
                tts?.language = Locale.getDefault()
                // Set speech rate and pitch
                tts?.setSpeechRate(0.9f)
                tts?.setPitch(1.0f)

                // Notify all listeners
                initializationListeners.forEach { it(true) }
                initializationListeners.clear()
                true
            } else {
                initializationListeners.forEach { it(false) }
                initializationListeners.clear()
                false
            }
        }
    }

    suspend fun speak(text: String, utteranceId: String = "default") {
        if (!isInitialized) {
            waitForInitialization() //wait for initiliaztion to complete before performing speech
        }
//perform speech
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

//    fun speakNonBlocking(text: String, utteranceId: String = "default") {
//        if (isInitialized) {
//            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
//        }
//    }

    //adjust speaking speed
    fun setSpeechRate(rate: Float) {
        tts?.setSpeechRate(rate)
    }
//adjust voice pitch
    fun setPitch(pitch: Float) {
        tts?.setPitch(pitch)
    }

    //stop ongoing speech
    fun stop() {
        tts?.stop()
    }

    //shut down TTS
    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }

    //coroutine for speech initialization
    private suspend fun waitForInitialization() = suspendCoroutine<Unit> { continuation ->
        if (isInitialized) {
            continuation.resume(Unit)
        } else {
            initializationListeners.add { success ->
                if (success) {
                    continuation.resume(Unit)
                } else {
                    continuation.resume(Unit) // Resume anyway, TTS will fail later
                }
            }
        }
    }

    fun getSpeechFlow() = callbackFlow {
        val listener = object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                trySend(TTSEvent.Started(utteranceId))
            }

            override fun onDone(utteranceId: String?) {
                trySend(TTSEvent.Finished(utteranceId))
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                trySend(TTSEvent.Error(utteranceId))
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                trySend(TTSEvent.Error(utteranceId))
            }
        }

        tts?.setOnUtteranceProgressListener(listener)

        awaitClose {
            tts?.setOnUtteranceProgressListener(null)
        }
    }
}
