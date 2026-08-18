package com.example.util

import android.content.Context

/**
 * Helper class wrapping [SpeechToText] for Android SpeechRecognizer API
 * to capture voice queries in Indian regional languages (Hindi, English, etc.).
 *
 * @param context Android Application Context.
 * @param onResult Callback invoked when speech is recognized with high confidence. Passes transcribed text string.
 * @param onError Callback invoked when an error or timeout occurs. Passes error message string.
 */
class VoiceRecognizer(
    context: Context,
    onResult: (String) -> Unit,
    onError: (String) -> Unit
) {
    private val speechToText = SpeechToText(
        context = context,
        onResult = onResult,
        onError = onError
    )

    /**
     * Checks if SpeechRecognition service is available on this Android device.
     */
    fun isAvailable(): Boolean = speechToText.isAvailable()

    /**
     * Creates system RecognizerIntent for voice input.
     */
    fun createSpeechIntent(languageCode: String = "hi-IN", prompt: String = "योजना या दस्तावेज का नाम बोलें..."): android.content.Intent {
        return speechToText.createSpeechIntent(languageCode, prompt)
    }

    /**
     * Checks if RECORD_AUDIO permission is granted.
     */
    fun hasRecordAudioPermission(): Boolean = speechToText.hasRecordAudioPermission()

    /**
     * Starts listening to user's voice input using Android SpeechRecognizer intent.
     * @param languageCode BCP-47 language tag (e.g., "hi-IN" for Hindi, "en-IN" for Indian English).
     */
    fun startListening(languageCode: String = "hi-IN") {
        speechToText.startListening(languageCode)
    }

    /**
     * Stops listening to active speech input.
     */
    fun stopListening() {
        speechToText.stopListening()
    }

    /**
     * Cancels active recognition session.
     */
    fun cancel() {
        speechToText.cancel()
    }

    /**
     * Releases speech recognition resources.
     */
    fun destroy() {
        speechToText.destroy()
    }
}
