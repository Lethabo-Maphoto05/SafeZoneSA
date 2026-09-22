package com.groupeight.safezonesa.model

/**
 * Data entities mirror Section 7 of the Planning & Design document (Users, Incidents,
 * EmergencyContacts, SafeWalks, EvidenceMedia) and the Azure SQL schema they map to.
 * These are the client-side (Kotlin) representations returned by the REST API.
 */

enum class UserRole { RESIDENT, VERIFIED_WATCH_MEMBER, AUTHORISED_RESPONDER, ADMINISTRATOR }

data class SafeZoneUser(
    val userId: String,
    val displayName: String,
    val mobileNumber: String,
    val email: String,
    val role: UserRole = UserRole.RESIDENT,
    val neighbourhoodId: String = "",
    val guardianLevel: String = "Community Guardian",
    val pointsEarned: Int = 0,
    val reportsVerified: Int = 0,
    val badges: List<String> = emptyList()
)

enum class IncidentStatus { UNVERIFIED, VERIFIED, RESOLVED, REJECTED }
enum class Severity { LOW, MEDIUM, HIGH, CRITICAL }

data class Incident(
    val incidentId: String,
    val category: String,          // e.g. Robbery, Accident, Missing, Fire, Scam (FR2)
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val severity: Severity,
    val status: IncidentStatus,
    val confirmationCount: Int,
    val confirmationsRequired: Int = 10, // FR2 / NFR8: ten-confirmation verification rule
    val isAnonymous: Boolean,
    val reporterDisplayName: String?,
    val photoUrls: List<String> = emptyList(),
    val createdAt: String,
    val timeAgo: String = ""
)

// FR8 — Beware Reports (hazards, kept visually distinct from crime pins)
data class BewareReport(
    val id: String,
    val category: String, // Dangerous Road, Suspicious Vehicle, Unsafe Taxi, Broken Streetlight, Pothole, Flood, Hijacking Hotspot
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

// FR11 — Missing Persons
data class MissingPerson(
    val id: String,
    val name: String,
    val photoUrl: String? = null,
    val lastSeenLocation: String,
    val lastSeenTime: String,
    val description: String,
    val contactNumber: String
)

// FR4 — Walk With Me
enum class WalkStatus { IN_PROGRESS, COMPLETED, ESCALATED }

data class SafeWalk(
    val walkId: String,
    val destination: String,
    val guardianName: String,
    val guardianNumber: String,
    val startTime: String,
    val expectedArrival: String,
    val status: WalkStatus
)

// FR16 emergency contact used for SOS escalation and Trusted Guardian
data class EmergencyContact(
    val contactId: String,
    val name: String,
    val number: String,
    val relationship: String,
    val priority: Int
)

// FR18 — Emergency Numbers & Local Authorities Directory
data class DirectoryEntry(
    val name: String,
    val number: String
)

// FR17 — Volunteer Responder network
data class VolunteerResponder(
    val id: String,
    val name: String,
    val type: String, // CPF Patroller, First Aid Volunteer, Security Company
    val distanceAway: String
)

// Section 4.6 — Suburb Safety Score / AI Crime Prediction risk levels
enum class RiskLevel { LOW, MEDIUM, HIGH }

data class NeighbourhoodRisk(
    val neighbourhoodName: String,
    val riskLevel: RiskLevel,
    val trendDescription: String
)

// FR12 — categorised push notifications
enum class NotificationCategory { ALERT, MESSAGE, SYSTEM }

data class AppNotification(
    val id: String,
    val category: NotificationCategory,
    val title: String,
    val body: String,
    val timeAgo: String
)

// FR10 — community chat message
data class ChatMessage(
    val id: String,
    val senderName: String,
    val body: String,
    val timeAgo: String,
    val isOwn: Boolean = false
)
