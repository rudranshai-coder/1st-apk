package com.example.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * State representation of voice dictation session
 */
sealed class SpeechUiState {
    object Idle : SpeechUiState()
    data class Ready(val message: String = "Ready to listen...") : SpeechUiState()
    data class Listening(val rmsDb: Float = 0f, val partialText: String = "") : SpeechUiState()
    data class Processing(val recognizedText: String) : SpeechUiState()
    data class Success(val finalText: String) : SpeechUiState()
    data class Error(val message: String, val canRetry: Boolean = true) : SpeechUiState()
}

/**
 * Manages voice dictation and speech-to-text for hands-free BCA study queries.
 */
class SpeechRecognitionService(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null

    private val _uiState = MutableStateFlow<SpeechUiState>(SpeechUiState.Idle)
    val uiState: StateFlow<SpeechUiState> = _uiState.asStateFlow()

    private val _transcribedText = MutableStateFlow("")
    val transcribedText: StateFlow<String> = _transcribedText.asStateFlow()

    private val _rmsLevel = MutableStateFlow(0f)
    val rmsLevel: StateFlow<Float> = _rmsLevel.asStateFlow()

    /**
     * Checks if speech recognition service is available on this Android device
     */
    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    /**
     * Creates an Intent for fallback speech recognition activity if needed
     */
    fun createRecognizerIntent(promptMessage: String = "Ask Nesty your BCA question..."): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, promptMessage)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
    }

    /**
     * Starts listening to user voice input via SpeechRecognizer
     */
    fun startListening(
        subjectContext: String? = null,
        onComplete: ((String) -> Unit)? = null
    ) {
        stopListening()

        if (!isAvailable()) {
            _uiState.value = SpeechUiState.Error(
                message = "On-device speech recognition is not available. Using fallback intent.",
                canRetry = true
            )
            return
        }

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _uiState.value = SpeechUiState.Listening(
                            rmsDb = 0f,
                            partialText = _transcribedText.value
                        )
                    }

                    override fun onBeginningOfSpeech() {
                        _uiState.value = SpeechUiState.Listening(
                            rmsDb = 1f,
                            partialText = _transcribedText.value
                        )
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        _rmsLevel.value = rmsdB.coerceAtLeast(0f)
                        val currentState = _uiState.value
                        if (currentState is SpeechUiState.Listening) {
                            _uiState.value = currentState.copy(rmsDb = rmsdB.coerceAtLeast(0f))
                        }
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _uiState.value = SpeechUiState.Processing(
                            recognizedText = _transcribedText.value
                        )
                    }

                    override fun onError(error: Int) {
                        val errorMessage = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Please check your mic."
                            SpeechRecognizer.ERROR_CLIENT -> "Speech recognition client error."
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required."
                            SpeechRecognizer.ERROR_NETWORK -> "Network error during speech recognition."
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout. Please check your connection."
                            SpeechRecognizer.ERROR_NO_MATCH -> "Couldn't understand speech. Please try speaking clearly."
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech service is busy. Please try again."
                            SpeechRecognizer.ERROR_SERVER -> "Server error. Please retry in a moment."
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected. Tap mic to speak."
                            else -> "Voice recognition issue (Code $error). Tap to try again."
                        }
                        _uiState.value = SpeechUiState.Error(errorMessage)
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val bestMatch = matches?.firstOrNull()?.trim() ?: ""

                        if (bestMatch.isNotEmpty()) {
                            _transcribedText.value = bestMatch
                            _uiState.value = SpeechUiState.Success(bestMatch)
                            onComplete?.invoke(bestMatch)
                        } else {
                            _uiState.value = SpeechUiState.Error("No words detected. Tap mic to retry.")
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val partial = matches?.firstOrNull()?.trim() ?: ""
                        if (partial.isNotEmpty()) {
                            _transcribedText.value = partial
                            _uiState.value = SpeechUiState.Listening(
                                rmsDb = _rmsLevel.value,
                                partialText = partial
                            )
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = createRecognizerIntent(
                promptMessage = if (!subjectContext.isNullOrBlank()) {
                    "Ask Nesty about $subjectContext..."
                } else {
                    "Ask Nesty your BCA question..."
                }
            )

            speechRecognizer?.startListening(intent)
            _uiState.value = SpeechUiState.Ready("Listening for your BCA question...")
        } catch (e: Exception) {
            _uiState.value = SpeechUiState.Error(
                message = "Failed to initialize microphone: ${e.localizedMessage ?: "Unknown error"}"
            )
        }
    }

    /**
     * Stops the speech recognizer
     */
    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (_: Exception) {
        } finally {
            speechRecognizer = null
            if (_uiState.value is SpeechUiState.Listening || _uiState.value is SpeechUiState.Ready) {
                _uiState.value = SpeechUiState.Idle
            }
        }
    }

    /**
     * Clears transcribed text buffer
     */
    fun clearTranscribedText() {
        _transcribedText.value = ""
        _uiState.value = SpeechUiState.Idle
    }

    /**
     * Sets manually transcribed text (e.g. from Fallback ActivityResult)
     */
    fun setTranscribedText(text: String) {
        _transcribedText.value = text
        _uiState.value = SpeechUiState.Success(text)
    }

    /**
     * Cleans up all resources
     */
    fun destroy() {
        stopListening()
    }
}
