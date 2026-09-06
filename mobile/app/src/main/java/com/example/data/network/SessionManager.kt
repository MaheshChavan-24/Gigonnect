package com.example.data.network

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.UserRole

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "sahaya_session_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_ACTIVE_ROLE = "active_role"
        private const val KEY_BASE_URL = "base_url"

        // Default Render live backend URL
        const val DEFAULT_BASE_URL = "https://asep-1-2-bq8h.onrender.com/"
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    fun saveUserSession(userId: Long, username: String, email: String?, role: UserRole) {
        prefs.edit()
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_USERNAME, username)
            .putString(KEY_EMAIL, email)
            .putString(KEY_ACTIVE_ROLE, role.name)
            .apply()
    }

    fun getUserId(): Long = prefs.getLong(KEY_USER_ID, 1L)

    fun getUsername(): String = prefs.getString(KEY_USERNAME, "User") ?: "User"

    fun getActiveRole(): UserRole {
        val roleStr = prefs.getString(KEY_ACTIVE_ROLE, UserRole.CLIENT.name)
        return runCatching { UserRole.valueOf(roleStr!!) }.getOrDefault(UserRole.CLIENT)
    }

    fun setActiveRole(role: UserRole) {
        prefs.edit().putString(KEY_ACTIVE_ROLE, role.name).apply()
    }

    fun isLoggedIn(): Boolean = !getAccessToken().isNullOrBlank()

    fun clearSession() {
        prefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_USER_ID)
            .remove(KEY_USERNAME)
            .remove(KEY_EMAIL)
            .apply()
    }

    fun getBaseUrl(): String = prefs.getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL

    fun saveBaseUrl(url: String) {
        val formatted = if (url.endsWith("/")) url else "$url/"
        prefs.edit().putString(KEY_BASE_URL, formatted).apply()
    }
}
