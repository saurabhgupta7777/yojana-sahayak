package com.example.util

import java.util.regex.Pattern

/**
 * Security utility for validating and sanitizing user inputs (text, names, numbers, voice transcripts, and base64 images)
 * to guard against injection attacks, prompt abuse, and payload overflow.
 */
object InputSanitizer {

    private const val DEFAULT_TEXT_MAX_LENGTH = 1000
    private const val MAX_IMAGE_BASE64_LENGTH = 7 * 1024 * 1024 // ~5MB decoded limit

    /**
     * Sanitizes general text input (queries, notes, search queries, voice-to-text transcripts).
     * Strips dangerous control characters and caps string length.
     */
    fun sanitizeText(input: String?, maxLength: Int = DEFAULT_TEXT_MAX_LENGTH): String {
        if (input.isNullOrBlank()) return ""
        
        // Remove non-printable control characters except newline and tab
        val cleaned = input.replace(Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]"), "")
            .trim()

        return if (cleaned.length > maxLength) {
            cleaned.substring(0, maxLength)
        } else {
            cleaned
        }
    }

    /**
     * Sanitizes applicant names and labels.
     */
    fun sanitizeName(name: String?, maxLength: Int = 100): String {
        val sanitized = sanitizeText(name, maxLength)
        // Allow Unicode letters (including Hindi/Indian scripts), numbers, spaces, dots, hyphens
        return sanitized.replace(Regex("[^\\p{L}\\p{N}\\s.\\-]"), "")
    }

    /**
     * Sanitizes and validates integer values within min and max limits.
     */
    fun validateInt(value: Int, min: Int, max: Int, default: Int): Int {
        return when {
            value < min -> default
            value > max -> max
            else -> value
        }
    }

    /**
     * Sanitizes and validates Long income values.
     */
    fun validateIncome(income: Long, maxIncome: Long = 100_000_000L): Long {
        return when {
            income < 0 -> 0L
            income > maxIncome -> maxIncome
            else -> income
        }
    }

    /**
     * Validates Base64 encoded image string size and structure.
     * Returns true if safe and valid, false if suspicious or oversized.
     */
    fun isBase64ImageValid(base64Str: String?): Boolean {
        if (base64Str.isNullOrBlank()) return true // Optional image
        if (base64Str.length > MAX_IMAGE_BASE64_LENGTH) return false

        // Check valid Base64 character set
        val base64Pattern = Pattern.compile("^[A-Za-z0-9+/=]+$")
        val cleanBase64 = base64Str.replace("\n", "").replace("\r", "").trim()
        return base64Pattern.matcher(cleanBase64).matches()
    }
}
