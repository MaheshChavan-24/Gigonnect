package com.example.data.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "username") val username: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id") val id: Long,
    @Json(name = "username") val username: String,
    @Json(name = "first_name") val firstName: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "phone_number") val phoneNumber: String? = null,
    @Json(name = "is_client") val isClient: Boolean = false,
    @Json(name = "is_worker") val isWorker: Boolean = false,
    @Json(name = "verification_status") val verificationStatus: String = "unsubmitted",
    @Json(name = "rejection_reason") val rejectionReason: String? = null,
    @Json(name = "wallet_balance") val walletBalance: String? = "0.00",
    @Json(name = "bank_name") val bankName: String? = null,
    @Json(name = "bank_account_number") val bankAccountNumber: String? = null,
    @Json(name = "bank_ifsc") val bankIfsc: String? = null
)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "access") val access: String,
    @Json(name = "refresh") val refresh: String,
    @Json(name = "user") val user: UserDto? = null
)

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    @Json(name = "username") val username: String,
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "phone_number") val phoneNumber: String,
    @Json(name = "is_client") val isClient: Boolean,
    @Json(name = "is_worker") val isWorker: Boolean
)

@JsonClass(generateAdapter = true)
data class RegisterResponse(
    @Json(name = "user") val user: UserDto,
    @Json(name = "message") val message: String
)

@JsonClass(generateAdapter = true)
data class RefreshTokenRequest(
    @Json(name = "refresh") val refresh: String
)

@JsonClass(generateAdapter = true)
data class RefreshTokenResponse(
    @Json(name = "access") val access: String
)

@JsonClass(generateAdapter = true)
data class GenericMessageResponse(
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null,
    @Json(name = "status") val status: String? = null
)
