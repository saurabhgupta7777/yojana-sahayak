package com.example.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.core.content.ContextCompat

/**
 * SpeechToText utility with permission validation, safe lifecycle, and error tolerance.
 */
class SpeechToText(
    private val context: Context,
    private val onResult: (String) -> Unit = {},
    private val onError: (String) -> Unit = {},
    private val onPermissionRequired: () -> Unit = {}
) {

    private var speechRecognizer: SpeechRecognizer? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var isCurrentlyListening = false

    /**
     * Checks if SpeechRecognizer service is available on this device.
     */
    fun isAvailable(): Boolean {
        return try {
            SpeechRecognizer.isRecognitionAvailable(context)
        } catch (e: Exception) {
            Log.e("SpeechToText", "Error checking SpeechRecognizer availability", e)
            false
        }
    }

    /**
     * Creates standard RecognizerIntent for Android Speech Recognizer.
     */
    fun createSpeechIntent(languageCode: String = "hi-IN", prompt: String = "योजना या दस्तावेज का नाम बोलें..."): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languageCode)
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languageCode)
            putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
    }

    /**
     * Checks if RECORD_AUDIO permission is granted.
     */
    fun hasRecordAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Starts listening safely on Main Thread.
     */
    fun startListening(languageCode: String = "hi-IN") {
        mainHandler.post {
            if (!hasRecordAudioPermission()) {
                onPermissionRequired()
                onError("माइक्रोफोन अनुमति आवश्यक है")
                return@post
            }

            try {
                // Cancel existing session
                cleanupRecognizer()

                if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                    onError("वॉइस सेवा डिवाइस पर अनुपलब्ध है")
                    return@post
                }

                val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
                speechRecognizer = recognizer
                isCurrentlyListening = true

                val intent = createSpeechIntent(languageCode)

                recognizer.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        Log.d("SpeechToText", "Ready for speech")
                    }

                    override fun onBeginningOfSpeech() {
                        Log.d("SpeechToText", "Beginning of speech")
                    }

                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        isCurrentlyListening = false
                    }

                    override fun onError(error: Int) {
                        isCurrentlyListening = false
                        val errorMsg = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "ऑडियो रिकॉर्डिंग में समस्या हुई"
                            SpeechRecognizer.ERROR_CLIENT -> "वॉइस इनपुट इंजन सक्रिय नहीं है"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "माइक्रोफोन अनुमति की आवश्यकता है"
                            SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "नेटवर्क कनेक्शन चेक करें"
                            SpeechRecognizer.ERROR_NO_MATCH -> "आवाज स्पष्ट नहीं सुनाई दी, पुनः बोलें"
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "वॉइस सिस्टम व्यस्त है"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "समय समाप्त, कृपया दोबारा बोलें"
                            else -> "वॉइस इनपुट त्रुटि"
                        }
                        Log.w("SpeechToText", "SpeechRecognizer error: $error ($errorMsg)")
                        mainHandler.post {
                            onError(errorMsg)
                        }
                    }

                    override fun onResults(results: Bundle?) {
                        isCurrentlyListening = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            val text = matches[0].trim()
                            Log.d("SpeechToText", "Recognized: $text")
                            mainHandler.post {
                                onResult(text)
                            }
                        } else {
                            mainHandler.post {
                                onError("कोई शब्द समझ में नहीं आया")
                            }
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            Log.d("SpeechToText", "Partial: ${matches[0]}")
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })

                recognizer.startListening(intent)

            } catch (e: Exception) {
                isCurrentlyListening = false
                Log.e("SpeechToText", "Failed to start listening", e)
                onError("वॉइस इनपुट शुरू नहीं हो सका: ${e.message}")
            }
        }
    }

    /**
     * Stops listening to active speech input.
     */
    fun stopListening() {
        mainHandler.post {
            try {
                if (isCurrentlyListening) {
                    speechRecognizer?.stopListening()
                    isCurrentlyListening = false
                }
            } catch (e: Exception) {
                Log.w("SpeechToText", "Error stopping", e)
            }
        }
    }

    /**
     * Cancels active recognition session.
     */
    fun cancel() {
        mainHandler.post {
            try {
                speechRecognizer?.cancel()
                isCurrentlyListening = false
            } catch (e: Exception) {
                Log.w("SpeechToText", "Error cancelling", e)
            }
        }
    }

    private fun cleanupRecognizer() {
        try {
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            Log.w("SpeechToText", "Error destroying speech recognizer", e)
        } finally {
            speechRecognizer = null
            isCurrentlyListening = false
        }
    }

    /**
     * Releases resources safely.
     */
    fun destroy() {
        mainHandler.post {
            cleanupRecognizer()
        }
    }
}
