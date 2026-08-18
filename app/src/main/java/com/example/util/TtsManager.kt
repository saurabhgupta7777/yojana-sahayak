package com.example.data.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import java.util.Locale

/**
 * Manages Text-To-Speech (TTS) audio narration for scheme details and voice assistant responses.
 * Tuned with a speech rate of 0.88x for optimal clarity for elderly and rural citizen users.
 *
 * @param context Android Application Context.
 */
class TtsManager(context: Context) : TextToSpeech.OnInitListener {

    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())
    private var tts: TextToSpeech? = TextToSpeech(appContext, this)
    private var isInitialized = false
    private var pendingTextToSpeak: String? = null

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            var result = tts?.setLanguage(Locale("hi", "IN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                result = tts?.setLanguage(Locale("en", "IN"))
            }
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                result = tts?.setLanguage(Locale.US)
            }
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.getDefault())
            }

            // Tailored speaking rate 0.88x for elderly and rural users
            tts?.setSpeechRate(0.88f)
            tts?.setPitch(1.0f)
            isInitialized = true

            pendingTextToSpeak?.let { text ->
                val toSpeak = text
                pendingTextToSpeak = null
                speakAloud(toSpeak)
            }
        } else {
            Log.e("TtsManager", "TTS Initialization failed status: $status")
        }
    }

    /**
     * Dynamically updates the TextToSpeech engine locale matching the user's active language.
     * @param languageStr Display language name (e.g., "Hindi", "Bengali", "English", "Telugu").
     */
    fun updateLanguage(languageStr: String) {
        if (!isInitialized || tts == null) return
        val lang = languageStr.lowercase()
        val locale = when {
            lang.contains("english") -> Locale("en", "IN")
            lang.contains("bengali") || lang.contains("বাংলা") -> Locale("bn", "IN")
            lang.contains("telugu") || lang.contains("తెలుగు") -> Locale("te", "IN")
            lang.contains("marathi") || lang.contains("मराठी") -> Locale("mr", "IN")
            lang.contains("tamil") || lang.contains("தமிழ்") -> Locale("ta", "IN")
            lang.contains("gujarati") || lang.contains("ગુજરાતી") -> Locale("gu", "IN")
            lang.contains("kannada") || lang.contains("<ctrl42>कन्नड") -> Locale("kn", "IN")
            lang.contains("malayalam") || lang.contains("മലയാളം") -> Locale("ml", "IN")
            lang.contains("punjabi") || lang.contains("ਪੰਜਾਬੀ") -> Locale("pa", "IN")
            lang.contains("or") || lang.contains("ଓଡ଼ିଆ") -> Locale("or", "IN")
            lang.contains("urdu") || lang.contains("اردو") -> Locale("ur", "IN")
            else -> Locale("hi", "IN")
        }
        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.setLanguage(Locale("hi", "IN"))
        }
    }

    /**
     * Reads out the provided text string aloud via TTS.
     * Cleans markdown formatting characters (*, •) prior to synthesis.
     * @param text String text to be spoken.
     */
    fun speakAloud(text: String) {
        if (text.isBlank()) return

        if (isInitialized && tts != null) {
            mainHandler.post {
                try {
                    tts?.stop()
                    val cleanText = text.replace("*", "")
                        .replace("#", "")
                        .replace("•", "")
                        .replace("-", " ")
                        .trim()
                        .take(500)

                    val speakResult = tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "YS_SPEECH_ID")
                    if (speakResult == TextToSpeech.SUCCESS) {
                        Toast.makeText(appContext, "🔊 बोलकर सुनाया जा रहा है...", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.w("TtsManager", "TTS speak returned code $speakResult")
                        Toast.makeText(appContext, "🔊 $cleanText", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("TtsManager", "Error speaking text", e)
                }
            }
        } else {
            pendingTextToSpeak = text
        }
    }

    fun stop() {
        mainHandler.post {
            try {
                tts?.stop()
            } catch (_: Exception) {}
        }
    }

    fun shutdown() {
        mainHandler.post {
            try {
                tts?.stop()
                tts?.shutdown()
                tts = null
            } catch (_: Exception) {}
        }
    }
}

