package com.groupeight.safezonesa.network

/**
 * Typed request bodies. Using data classes instead of Map<String, Any> avoids the Kotlin
 * "? extends Object" wildcard that Retrofit rejects in @Body parameters.
 * Property names must match the API records, case-insensitively.
 */
data class SosRequestDto(
    val userId: String,
    val category: String,
    val latitude: Double,
    val longitude: Double
)

data class CreateIncidentDto(
    val type: String,
    val title: String,
    val description: String?,
    val latitude: Double,
    val longitude: Double,
    val photoUrls: List<String> = emptyList(),
    val anonymous: Boolean = false,
    val severity: String = "Medium"
)