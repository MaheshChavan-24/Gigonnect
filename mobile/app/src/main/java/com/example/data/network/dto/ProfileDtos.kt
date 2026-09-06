package com.example.data.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TradeProfileDto(
    @Json(name = "id") val id: Long,
    @Json(name = "worker") val worker: Long? = null,
    @Json(name = "worker_username") val workerUsername: String? = null,
    @Json(name = "display_name") val displayName: String,
    @Json(name = "trade_category") val tradeCategory: String,
    @Json(name = "skills") val skills: String,
    @Json(name = "experience_description") val experienceDescription: String,
    @Json(name = "years_of_experience") val yearsOfExperience: Int,
    @Json(name = "availability") val availability: String,
    @Json(name = "tools_equipment") val toolsEquipment: String? = "",
    @Json(name = "languages") val languages: String? = "",
    @Json(name = "is_active") val isActive: Boolean = true,
    @Json(name = "average_rating") val averageRating: Double? = 0.0,
    @Json(name = "review_count") val reviewCount: Int? = 0
)

@JsonClass(generateAdapter = true)
data class CreateTradeProfileRequest(
    @Json(name = "display_name") val displayName: String,
    @Json(name = "trade_category") val tradeCategory: String,
    @Json(name = "skills") val skills: String,
    @Json(name = "experience_description") val experienceDescription: String,
    @Json(name = "years_of_experience") val yearsOfExperience: Int,
    @Json(name = "availability") val availability: String,
    @Json(name = "tools_equipment") val toolsEquipment: String = "",
    @Json(name = "languages") val languages: String = ""
)

@JsonClass(generateAdapter = true)
data class ServiceRequestDto(
    @Json(name = "id") val id: Long,
    @Json(name = "client") val client: Long? = null,
    @Json(name = "client_username") val clientUsername: String? = null,
    @Json(name = "worker") val worker: Long? = null,
    @Json(name = "worker_username") val workerUsername: String? = null,
    @Json(name = "trade_profile") val tradeProfile: Long? = null,
    @Json(name = "trade_category") val tradeCategory: String? = null,
    @Json(name = "worker_display_name") val workerDisplayName: String? = null,
    @Json(name = "description") val description: String,
    @Json(name = "preferred_date") val preferredDate: String,
    @Json(name = "preferred_time_slot") val preferredTimeSlot: String,
    @Json(name = "status") val status: String = "pending",
    @Json(name = "worker_notes") val workerNotes: String? = null,
    @Json(name = "budget") val budget: String? = "0.00",
    @Json(name = "escrow_status") val escrowStatus: String = "none",
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class CreateServiceRequestDto(
    @Json(name = "trade_profile_id") val tradeProfileId: Long,
    @Json(name = "description") val description: String,
    @Json(name = "preferred_date") val preferredDate: String,
    @Json(name = "preferred_time_slot") val preferredTimeSlot: String,
    @Json(name = "budget") val budget: Double
)

@JsonClass(generateAdapter = true)
data class RespondServiceRequestDto(
    @Json(name = "status") val status: String, // "accepted" or "rejected"
    @Json(name = "worker_notes") val workerNotes: String? = null
)
