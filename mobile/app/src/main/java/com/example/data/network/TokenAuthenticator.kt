package com.example.data.network

import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Transparently catches HTTP 401 Unauthorized responses across all API calls,
 * requests a fresh JWT access token using the stored refresh token, updates SessionManager,
 * and retries the original failed request with the new access token.
 */
class TokenAuthenticator(
    private val sessionManager: SessionManager
) : Authenticator {

    private val refreshClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Guard against infinite 401 retry loops
        if (responseCount(response) >= 3) {
            return null
        }

        val refreshToken = sessionManager.getRefreshToken()
        if (refreshToken.isNullOrBlank()) {
            return null
        }

        synchronized(this) {
            val currentAccessToken = sessionManager.getAccessToken()
            val failedRequestHeader = response.request.header("Authorization")
            val tokenUsed = failedRequestHeader?.removePrefix("Bearer ")?.trim()

            // If another thread already successfully refreshed the token, retry with it
            if (!currentAccessToken.isNullOrBlank() && currentAccessToken != tokenUsed) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentAccessToken")
                    .build()
            }

            // Perform synchronous refresh call
            val baseUrl = sessionManager.getBaseUrl()
            val refreshUrl = if (baseUrl.endsWith("/")) "${baseUrl}api/users/token/refresh/" else "$baseUrl/api/users/token/refresh/"

            val payload = JSONObject().apply {
                put("refresh", refreshToken)
            }.toString()

            val refreshRequest = Request.Builder()
                .url(refreshUrl)
                .post(payload.toRequestBody("application/json; charset=utf-8".toMediaType()))
                .addHeader("Accept", "application/json")
                .build()

            return try {
                val refreshResponse = refreshClient.newCall(refreshRequest).execute()
                if (refreshResponse.isSuccessful) {
                    val responseBody = refreshResponse.body?.string()
                    if (!responseBody.isNullOrBlank()) {
                        val json = JSONObject(responseBody)
                        val newAccessToken = json.optString("access")
                        val newRefreshToken = json.optString("refresh", refreshToken)
                        if (newAccessToken.isNotBlank()) {
                            sessionManager.saveTokens(newAccessToken, newRefreshToken)
                            return response.request.newBuilder()
                                .header("Authorization", "Bearer $newAccessToken")
                                .build()
                        }
                    }
                }
                null
            } catch (_: Exception) {
                null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
