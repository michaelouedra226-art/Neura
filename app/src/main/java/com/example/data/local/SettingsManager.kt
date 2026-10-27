package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("neura_settings_prefs", Context.MODE_PRIVATE)

    companion object {
        // Main Conversation Engine
        private const val KEY_ENDPOINT = "ai_endpoint"
        private const val KEY_API_KEY = "ai_api_key"
        private const val KEY_PROVIDER_NAME = "ai_provider_name"
        private const val KEY_TEXT_MODEL = "ai_text_model"
        private const val KEY_VISION_MODEL = "ai_vision_model"

        // Dedicated Agnes AI Image Engine
        private const val KEY_AGNES_ENDPOINT = "agnes_endpoint"
        private const val KEY_AGNES_API_KEY = "agnes_api_key"
        private const val KEY_AGNES_MODEL = "agnes_model"
        private const val KEY_AGNES_PING_MS = "agnes_ping_ms"
        private const val KEY_AGNES_PING_STATUS = "agnes_ping_status"

        // User Profile
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_FOCUS = "user_focus"
        private const val KEY_LEARNING_STYLE = "learning_style"
        private const val KEY_ONBOARDING_DONE = "onboarding_done"
        private const val KEY_LAST_PING_MS = "last_ping_ms"
        private const val KEY_LAST_PING_STATUS = "last_ping_status"
    }

    // Main API (Conversation & Text)
    var endpoint: String
        get() = prefs.getString(KEY_ENDPOINT, "https://generativelanguage.googleapis.com") ?: "https://generativelanguage.googleapis.com"
        set(value) = prefs.edit().putString(KEY_ENDPOINT, value).apply()

    var apiKey: String
        get() = prefs.getString(KEY_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_API_KEY, value).apply()

    var providerName: String
        get() = prefs.getString(KEY_PROVIDER_NAME, "Gemini Flash") ?: "Gemini Flash"
        set(value) = prefs.edit().putString(KEY_PROVIDER_NAME, value).apply()

    var textModel: String
        get() = prefs.getString(KEY_TEXT_MODEL, "gemini-2.5-flash") ?: "gemini-2.5-flash"
        set(value) = prefs.edit().putString(KEY_TEXT_MODEL, value).apply()

    var visionModel: String
        get() = prefs.getString(KEY_VISION_MODEL, "gemini-2.5-flash-vision") ?: "gemini-2.5-flash-vision"
        set(value) = prefs.edit().putString(KEY_VISION_MODEL, value).apply()

    var imageModel: String
        get() = agnesModel
        set(value) { agnesModel = value }

    // Dedicated Agnes AI (Image Generation Engine)
    var agnesEndpoint: String
        get() = prefs.getString(KEY_AGNES_ENDPOINT, "https://apihub.agnes-ai.com/v1") ?: "https://apihub.agnes-ai.com/v1"
        set(value) = prefs.edit().putString(KEY_AGNES_ENDPOINT, value).apply()

    var agnesApiKey: String
        get() = prefs.getString(KEY_AGNES_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_AGNES_API_KEY, value).apply()

    var agnesModel: String
        get() = prefs.getString(KEY_AGNES_MODEL, "dall-e-3") ?: "dall-e-3"
        set(value) = prefs.edit().putString(KEY_AGNES_MODEL, value).apply()

    var agnesPingMs: Long
        get() = prefs.getLong(KEY_AGNES_PING_MS, 280L)
        set(value) = prefs.edit().putLong(KEY_AGNES_PING_MS, value).apply()

    var agnesPingStatus: String
        get() = prefs.getString(KEY_AGNES_PING_STATUS, "Prêt pour génération") ?: "Prêt pour génération"
        set(value) = prefs.edit().putString(KEY_AGNES_PING_STATUS, value).apply()

    // User Profile
    var userName: String
        get() = prefs.getString(KEY_USER_NAME, "Michael") ?: "Michael"
        set(value) = prefs.edit().putString(KEY_USER_NAME, value).apply()

    var userFocus: String
        get() = prefs.getString(KEY_USER_FOCUS, "Études & Programmation") ?: "Études & Programmation"
        set(value) = prefs.edit().putString(KEY_USER_FOCUS, value).apply()

    var learningStyle: String
        get() = prefs.getString(KEY_LEARNING_STYLE, "Pédagogique avec exemples concrets") ?: "Pédagogique avec exemples concrets"
        set(value) = prefs.edit().putString(KEY_LEARNING_STYLE, value).apply()

    var isOnboardingDone: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_DONE, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_DONE, value).apply()

    var lastPingMs: Long
        get() = prefs.getLong(KEY_LAST_PING_MS, 320L)
        set(value) = prefs.edit().putLong(KEY_LAST_PING_MS, value).apply()

    var lastPingStatus: String
        get() = prefs.getString(KEY_LAST_PING_STATUS, "Connecté") ?: "Connecté"
        set(value) = prefs.edit().putString(KEY_LAST_PING_STATUS, value).apply()

    fun getMaskedApiKey(): String {
        val key = apiKey
        if (key.length <= 6) return if (key.isEmpty()) "Non configurée" else "••••••"
        val prefix = key.take(4)
        val suffix = key.takeLast(2)
        return "$prefix••••••••$suffix"
    }

    fun getMaskedAgnesApiKey(): String {
        val key = agnesApiKey
        if (key.length <= 6) return if (key.isEmpty()) "Non configurée" else "••••••"
        val prefix = key.take(4)
        val suffix = key.takeLast(2)
        return "$prefix••••••••$suffix"
    }
}
