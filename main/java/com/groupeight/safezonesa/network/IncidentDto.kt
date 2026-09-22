package com.groupeight.safezonesa.network

import com.groupeight.safezonesa.model.Incident
import com.groupeight.safezonesa.model.IncidentStatus
import com.groupeight.safezonesa.model.Severity
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * Shape returned by GET /api/incidents and GET /api/incidents/{id} — this mirrors the raw
 * Incident entity from the API (SafeZoneSA.Api/Models/Incident.cs), not the app's domain
 * model below. Two things don't line up between them, both handled in [toDomain]:
 *
 * 1. ASP.NET Core serialises Severity/Status as plain strings like "High" or "Unverified"
 *    (they're just `string` properties server-side, not real enums), while the app's
 *    [Severity]/[IncidentStatus] are Kotlin enum constants like HIGH/UNVERIFIED. Gson
 *    matches enum JSON values by exact constant name, so deserializing "High" straight into
 *    Severity would crash — toDomain() upper-cases and falls back to a safe default instead.
 * 2. The API entity has no reporterDisplayName (no join to the Users table yet) and does
 *    not persist photoUrls at all (CreateIncidentRequest accepts them but the model never
 *    stores them) — both are TODOs on the API side, not bugs here. They're treated as
 *    absent rather than pretended-present.
 */
data class IncidentDto(
    val incidentId: String,
    val reporterId: String?,
    val category: String?,
    val title: String?,
    val description: String?,
    val latitude: Double,
    val longitude: Double,
    val severity: String?,
    val status: String?,
    val confirmationCount: Int,
    val isAnonymous: Boolean,
    val createdAt: String?
)

private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

fun IncidentDto.toDomain(): Incident {
    val severity = Severity.entries.firstOrNull { it.name.equals(severity?.trim(), ignoreCase = true) }
        ?: Severity.MEDIUM
    val status = IncidentStatus.entries.firstOrNull { it.name.equals(status?.trim(), ignoreCase = true) }
        ?: IncidentStatus.UNVERIFIED

    return Incident(
        incidentId = incidentId,
        category = category ?: "Other",
        title = title ?: "",
        description = description ?: "",
        latitude = latitude,
        longitude = longitude,
        severity = severity,
        status = status,
        confirmationCount = confirmationCount,
        confirmationsRequired = 10, // Incident.ConfirmationsRequired on the API side is a
                                    // C# const, so it is never actually included in the JSON —
                                    // it has to be hard-coded here to match.
        isAnonymous = isAnonymous,
        // Neither of these exists in the API response yet — see the class doc above.
        reporterDisplayName = if (isAnonymous) null else "Community Member",
        photoUrls = emptyList(),
        createdAt = createdAt ?: "",
        timeAgo = createdAt?.let { relativeTimeFrom(it) } ?: ""
    )
}

private fun relativeTimeFrom(iso: String): String {
    val parsedMillis = runCatching { apiDateFormat.parse(iso.removeSuffix("Z"))?.time }.getOrNull()
        ?: return ""
    val diffMs = System.currentTimeMillis() - parsedMillis
    val minutes = diffMs / 60_000
    return when {
        minutes < 1 -> "just now"
        minutes < 60 -> "${minutes}m ago"
        minutes < 24 * 60 -> "${minutes / 60}h ago"
        else -> "${minutes / (24 * 60)}d ago"
    }
}
