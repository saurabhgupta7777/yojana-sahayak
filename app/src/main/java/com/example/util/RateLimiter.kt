package com.example.util

import android.os.SystemClock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Rate Limiter and Abuse Prevention utility to prevent rapid-fire automated/bot API requests
 * and protect against cost overruns or server denial-of-service.
 */
object RateLimiter {

    private const val MAX_REQUESTS_PER_MINUTE = 10
    private const val ONE_MINUTE_MS = 60_000L
    private const val BOT_MIN_INTERVAL_MS = 800L // Requests faster than 800ms flag rapid bot behavior

    private val requestTimestamps = ArrayDeque<Long>()
    private var lastRequestTime = 0L

    /**
     * Evaluates whether an API call is permitted under rate limit and bot detection rules.
     * @return Pair<Boolean, String?> - (IsAllowed, ErrorMessageIfRejected)
     */
    @Synchronized
    fun isRequestAllowed(actionKey: String = "default"): Pair<Boolean, String?> {
        val now = SystemClock.elapsedRealtime()

        // 1. Rapid bot request detection
        if (lastRequestTime > 0 && (now - lastRequestTime) < BOT_MIN_INTERVAL_MS) {
            return Pair(
                false,
                "⚠️ बहुत तेज़ अनुरोध! kripya 1-2 second ruk kar dubara prayas karein. (Rapid requests detected)."
            )
        }

        // 2. Clear timestamps older than 1 minute
        while (requestTimestamps.isNotEmpty() && (now - requestTimestamps.first()) > ONE_MINUTE_MS) {
            requestTimestamps.removeFirst()
        }

        // 3. Rate limit check
        if (requestTimestamps.size >= MAX_REQUESTS_PER_MINUTE) {
            return Pair(
                false,
                "⏳ Anurodh sima samapt! Kripya 1 minute baad dubara try karein. (Rate limit reached: max $MAX_REQUESTS_PER_MINUTE requests per minute)."
            )
        }

        // Permit request
        requestTimestamps.addLast(now)
        lastRequestTime = now
        return Pair(true, null)
    }

    /**
     * Resets rate limiter state (useful for tests or explicit resets).
     */
    @Synchronized
    fun reset() {
        requestTimestamps.clear()
        lastRequestTime = 0L
    }
}
