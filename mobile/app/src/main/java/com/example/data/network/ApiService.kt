package com.example.data.network

import com.example.data.network.dto.CreateJobRequest
import com.example.data.network.dto.CreateReviewRequest
import com.example.data.network.dto.CreateServiceRequestDto
import com.example.data.network.dto.CreateTradeProfileRequest
import com.example.data.network.dto.GenericMessageResponse
import com.example.data.network.dto.JobDto
import com.example.data.network.dto.LoginRequest
import com.example.data.network.dto.LoginResponse
import com.example.data.network.dto.NotificationDto
import com.example.data.network.dto.PayJobResponse
import com.example.data.network.dto.PayoutRequest
import com.example.data.network.dto.RefreshTokenRequest
import com.example.data.network.dto.RefreshTokenResponse
import com.example.data.network.dto.RegisterRequest
import com.example.data.network.dto.RegisterResponse
import com.example.data.network.dto.RespondServiceRequestDto
import com.example.data.network.dto.ReviewDto
import com.example.data.network.dto.ServiceRequestDto
import com.example.data.network.dto.TradeProfileDto
import com.example.data.network.dto.UserDto
import com.example.data.network.dto.VerifyPaymentRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ==========================================
    // AUTH & USERS (/api/users/)
    // ==========================================

    @POST("api/users/login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/users/register/")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/users/token/refresh/")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshTokenResponse>

    @GET("api/users/me/")
    suspend fun getCurrentUser(): Response<UserDto>

    @Multipart
    @POST("api/users/upload-documents/")
    suspend fun uploadDocuments(
        @Part("id_type") idType: RequestBody,
        @Part idFrontImage: MultipartBody.Part,
        @Part idBackImage: MultipartBody.Part,
        @Part idSelfieImage: MultipartBody.Part? = null
    ): Response<GenericMessageResponse>

    @GET("api/users/notifications/")
    suspend fun getNotifications(): Response<List<NotificationDto>>

    @PATCH("api/users/notifications/{id}/read/")
    suspend fun markNotificationRead(@Path("id") id: Long): Response<GenericMessageResponse>

    // ==========================================
    // JOBS MARKETPLACE (/api/jobs/)
    // ==========================================

    @GET("api/jobs/available/")
    suspend fun getAvailableJobs(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Response<List<JobDto>>

    @POST("api/jobs/create/")
    suspend fun createJob(@Body request: CreateJobRequest): Response<JobDto>

    @POST("api/jobs/{id}/accept/")
    suspend fun acceptJob(@Path("id") id: Long): Response<GenericMessageResponse>

    @GET("api/jobs/my-jobs/")
    suspend fun getClientJobs(): Response<List<JobDto>>

    @GET("api/jobs/active/")
    suspend fun getActiveJob(): Response<JobDto>

    @GET("api/jobs/worker-history/")
    suspend fun getWorkerHistory(): Response<List<JobDto>>

    @PATCH("api/jobs/{id}/worker-complete/")
    suspend fun workerCompleteJob(@Path("id") id: Long): Response<GenericMessageResponse>

    @PATCH("api/jobs/{id}/complete/")
    suspend fun completeJob(@Path("id") id: Long): Response<GenericMessageResponse>

    @PATCH("api/jobs/{id}/dispute/")
    suspend fun disputeJob(@Path("id") id: Long): Response<GenericMessageResponse>

    @POST("api/jobs/reviews/create/")
    suspend fun createReview(@Body request: CreateReviewRequest): Response<ReviewDto>

    @GET("api/jobs/reviews/worker/{worker_id}/")
    suspend fun getWorkerReviews(@Path("worker_id") workerId: Long): Response<List<ReviewDto>>

    @GET("api/jobs/reviews/my-reviews/")
    suspend fun getMyReviews(): Response<List<ReviewDto>>

    // Payment & Escrow
    @POST("api/jobs/{id}/pay/")
    suspend fun payJob(@Path("id") id: Long): Response<PayJobResponse>

    @POST("api/jobs/{id}/verify-payment/")
    suspend fun verifyPayment(
        @Path("id") id: Long,
        @Body request: VerifyPaymentRequest
    ): Response<GenericMessageResponse>

    @POST("api/jobs/payout/")
    suspend fun requestPayout(@Body request: PayoutRequest): Response<GenericMessageResponse>

    // ==========================================
    // TRADE PROFILES & REQUESTS (/api/profiles/)
    // ==========================================

    @POST("api/profiles/trade-profiles/")
    suspend fun createTradeProfile(@Body request: CreateTradeProfileRequest): Response<TradeProfileDto>

    @GET("api/profiles/trade-profiles/mine/")
    suspend fun getMyTradeProfiles(): Response<List<TradeProfileDto>>

    @DELETE("api/profiles/trade-profiles/{id}/")
    suspend fun deleteTradeProfile(@Path("id") id: Long): Response<Unit>

    @GET("api/profiles/trade-profiles/category/{category}/")
    suspend fun getTradeProfilesByCategory(@Path("category") category: String): Response<List<TradeProfileDto>>

    @GET("api/profiles/trade-profiles/detail/{id}/")
    suspend fun getTradeProfileDetail(@Path("id") id: Long): Response<TradeProfileDto>

    @POST("api/profiles/service-requests/")
    suspend fun createServiceRequest(@Body request: CreateServiceRequestDto): Response<ServiceRequestDto>

    @GET("api/profiles/service-requests/mine/")
    suspend fun getMyServiceRequests(): Response<List<ServiceRequestDto>>

    @PATCH("api/profiles/service-requests/{id}/")
    suspend fun respondToServiceRequest(
        @Path("id") id: Long,
        @Body request: RespondServiceRequestDto
    ): Response<ServiceRequestDto>

    @POST("api/profiles/service-requests/{id}/pay/")
    suspend fun payServiceRequest(@Path("id") id: Long): Response<PayJobResponse>

    @POST("api/profiles/service-requests/{id}/verify-payment/")
    suspend fun verifyServiceRequestPayment(
        @Path("id") id: Long,
        @Body request: VerifyPaymentRequest
    ): Response<GenericMessageResponse>

    @PATCH("api/profiles/service-requests/{id}/worker-complete/")
    suspend fun workerCompleteServiceRequest(@Path("id") id: Long): Response<GenericMessageResponse>

    @PATCH("api/profiles/service-requests/{id}/release-funds/")
    suspend fun releaseServiceRequestFunds(@Path("id") id: Long): Response<GenericMessageResponse>
}
