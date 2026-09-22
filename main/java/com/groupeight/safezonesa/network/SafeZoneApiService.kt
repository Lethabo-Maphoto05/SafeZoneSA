package com.groupeight.safezonesa.network

import retrofit2.Response
import retrofit2.http.*

/**
 * Mirrors the key endpoints listed in Section 5.3 of the Planning & Design document.
 * All calls carry a JWT bearer token (obtained via /api/auth/login) in the Authorization
 * header, added centrally by the OkHttp interceptor in ApiClient.
 */
interface SafeZoneApiService {

    // --- Auth (FR1) ---
    @POST("api/auth/register")
    suspend fun register(@Body body: Map<String, String>): Response<Unit>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(@Body body: Map<String, String>): Response<Unit>

    @POST("api/auth/login")
    suspend fun login(@Body body: Map<String, String>): Response<Map<String, String>>

    // --- Incidents / Community Feed (FR2, FR15) ---
    @GET("api/incidents")
    suspend fun getIncidents(
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null
    ): Response<List<IncidentDto>>

    @POST("api/incidents")
    suspend fun postIncident(@Body body: CreateIncidentDto): Response<Unit>

    @GET("api/incidents/{id}")
    suspend fun getIncident(@Path("id") id: String): Response<IncidentDto>

    @PATCH("api/incidents/{id}/confirm")
    suspend fun confirmIncident(@Path("id") id: String): Response<IncidentDto>

    // --- SOS (FR3) ---
    @POST("api/sos")
    suspend fun raiseSos(@Body body: SosRequestDto): Response<Unit>

    // --- Walk With Me (FR4) ---
    @POST("api/walks/start")
    suspend fun startWalk(@Body body: Map<String, Any>): Response<Map<String, Any>>

    @PATCH("api/walks/{id}/location")
    suspend fun updateWalkLocation(@Path("id") id: String, @Body body: Map<String, Double>): Response<Unit>

    @POST("api/walks/{id}/complete")
    suspend fun completeWalk(@Path("id") id: String): Response<Unit>

    // --- Map pins (FR7, FR8) ---
    @GET("api/map/pins")
    suspend fun getMapPins(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radiusKm: Double,
        @Query("range") range: String
    ): Response<List<Map<String, Any>>>

    // --- City problem reports (FR9) ---
    @POST("api/reports/city-problem")
    suspend fun postCityProblem(@Body body: Map<String, Any>): Response<Map<String, Any>>

    @GET("api/reports/city-problem/{id}")
    suspend fun getCityProblem(@Path("id") id: String): Response<Map<String, Any>>

    // --- Suburb Safety Score (FR14) ---
    @GET("api/neighbourhoods/{id}/safety-score")
    suspend fun getSafetyScore(@Path("id") id: String): Response<Map<String, Any>>

    // --- Emergency contacts (FR16) ---
    @POST("api/users/{id}/emergency-contacts")
    suspend fun addEmergencyContact(@Path("id") userId: String, @Body body: Map<String, Any>): Response<Unit>
}