package com.example.data.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JobDto(
    @Json(name = "id") val id: Long,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "service_type") val serviceType: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "address") val address: String,
    @Json(name = "budget") val budget: String,
    @Json(name = "is_negotiable") val isNegotiable: Boolean = false,
    @Json(name = "urgency_level") val urgencyLevel: String = "Standard",
    @Json(name = "status") val status: String = "pending",
    @Json(name = "escrow_status") val escrowStatus: String = "none",
    @Json(name = "client") val client: Long? = null,
    @Json(name = "client_username") val clientUsername: String? = null,
    @Json(name = "client_phone") val clientPhone: String? = null,
    @Json(name = "worker") val worker: Long? = null,
    @Json(name = "worker_username") val workerUsername: String? = null,
    @Json(name = "worker_phone") val workerPhone: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "distance_km") val distanceKm: Double? = null,
    @Json(name = "razorpay_order_id") val razorpayOrderId: String? = null,
    @Json(name = "razorpay_payment_id") val razorpayPaymentId: String? = null
)

@JsonClass(generateAdapter = true)
data class CreateJobRequest(
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "service_type") val serviceType: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "address") val address: String,
    @Json(name = "budget") val budget: Double,
    @Json(name = "is_negotiable") val isNegotiable: Boolean,
    @Json(name = "urgency_level") val urgencyLevel: String
)

@JsonClass(generateAdapter = true)
data class CreateReviewRequest(
    @Json(name = "job") val job: Long,
    @Json(name = "rating") val rating: Int,
    @Json(name = "comment") val comment: String
)

@JsonClass(generateAdapter = true)
data class ReviewDto(
    @Json(name = "id") val id: Long,
    @Json(name = "job") val job: Long,
    @Json(name = "reviewer") val reviewer: Long? = null,
    @Json(name = "reviewer_username") val reviewerUsername: String? = null,
    @Json(name = "target") val target: Long? = null,
    @Json(name = "target_username") val targetUsername: String? = null,
    @Json(name = "rating") val rating: Int,
    @Json(name = "comment") val comment: String,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class PayJobResponse(
    @Json(name = "message") val message: String? = null,
    @Json(name = "razorpay_order_id") val razorpayOrderId: String? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "currency") val currency: String? = "INR",
    @Json(name = "key_id") val keyId: String? = null
)

@JsonClass(generateAdapter = true)
data class VerifyPaymentRequest(
    @Json(name = "razorpay_order_id") val razorpayOrderId: String,
    @Json(name = "razorpay_payment_id") val razorpayPaymentId: String,
    @Json(name = "razorpay_signature") val razorpaySignature: String
)

@JsonClass(generateAdapter = true)
data class PayoutRequest(
    @Json(name = "bank_name") val bankName: String,
    @Json(name = "bank_account_number") val bankAccountNumber: String,
    @Json(name = "bank_ifsc") val bankIfsc: String
)
