package com.groupeight.safezonesa.data

import com.groupeight.safezonesa.model.*

/**
 * Sample/mock data so every screen renders meaningfully without the live ASP.NET Core API
 * (Section 5) being deployed yet. Swap this out for [SafeZoneApiService] (Retrofit, see
 * network/ package) once the backend endpoints listed in Section 5.3 are live.
 */
object MockRepository {

    val currentUser = SafeZoneUser(
        userId = "u-001",
        displayName = "Hopewell N.",
        mobileNumber = "+27 82 000 0000",
        email = "hopewell@example.com",
        role = UserRole.VERIFIED_WATCH_MEMBER,
        neighbourhoodId = "n-soweto",
        pointsEarned = 2450,
        reportsVerified = 128,
        badges = listOf("Top Reporter", "Verified", "Helper")
    )

    val incidents = listOf(
        Incident(
            incidentId = "INC-82941",
            category = "Robbery",
            title = "Armed robbery reported near Protea Glen Mall",
            description = "Please be aware and stay safe.",
            latitude = -26.1420, longitude = 27.8436,
            severity = Severity.HIGH,
            status = IncidentStatus.UNVERIFIED,
            confirmationCount = 4,
            isAnonymous = false,
            reporterDisplayName = "Thabo Molosana",
            timeAgo = "10m ago",
            createdAt = "2026-08-19T21:14:00Z"
        ),
        Incident(
            incidentId = "INC-82940",
            category = "Suspicious Activity",
            title = "Suspicious vehicle parked for a long time",
            description = "White bakkie circling the block, passed three times in 10 minutes.",
            latitude = -26.1450, longitude = 27.8500,
            severity = Severity.MEDIUM,
            status = IncidentStatus.VERIFIED,
            confirmationCount = 10,
            isAnonymous = false,
            reporterDisplayName = "Lerato Kgatyage",
            timeAgo = "1h ago",
            createdAt = "2026-08-19T20:00:00Z"
        )
    )

    val bewareReports = listOf(
        BewareReport("bw-1", "Dangerous Road"),
        BewareReport("bw-2", "Suspicious Vehicle"),
        BewareReport("bw-3", "Unsafe Taxi"),
        BewareReport("bw-4", "Broken Streetlight"),
        BewareReport("bw-5", "Pothole"),
        BewareReport("bw-6", "Flood"),
        BewareReport("bw-7", "Hijacking Hotspot")
    )

    val missingPersons = listOf(
        MissingPerson(
            id = "mp-1",
            name = "Amogelang Kgomo",
            lastSeenLocation = "Protea Glen Ext 12",
            lastSeenTime = "14 May 2026, 6:20 PM",
            description = "Wearing blue jacket, black pants",
            contactNumber = "082 123 4567"
        ),
        MissingPerson(
            id = "mp-2",
            name = "Sipheshle Dlamini",
            lastSeenLocation = "Jabulani",
            lastSeenTime = "14 May 2026, 5:00 PM",
            description = "Wearing white t-shirt, jeans",
            contactNumber = "071 987 6543"
        )
    )

    val activeWalk = SafeWalk(
        walkId = "w-1",
        destination = "Home",
        guardianName = "Sipho Dlamini",
        guardianNumber = "072 123 4567",
        startTime = "9:00 PM",
        expectedArrival = "9:45 PM",
        status = WalkStatus.IN_PROGRESS
    )

    val emergencyContacts = listOf(
        EmergencyContact("c-1", "Nomsa N.", "082 111 2222", "Mother", 1),
        EmergencyContact("c-2", "Sipho Dlamini", "072 123 4567", "Guardian", 2)
    )

    val directory = listOf(
        DirectoryEntry("SAPS Emergency", "10111"),
        DirectoryEntry("Ambulance", "10177"),
        DirectoryEntry("Fire Department", "10177"),
        DirectoryEntry("Campus Security", "011 123 4567"),
        DirectoryEntry("Community Security", "082 456 7890"),
        DirectoryEntry("Traffic Department", "0861 400 800")
    )

    val volunteerResponders = listOf(
        VolunteerResponder("v-1", "CPF Patroller", "Protea Glen", "3 min away"),
        VolunteerResponder("v-2", "First Aid Volunteer", "Jabulani", "5 min away"),
        VolunteerResponder("v-3", "Soweto Security", "Security Company", "7 min away")
    )

    val neighbourhoodRisks = listOf(
        NeighbourhoodRisk("Protea Glen Ext 11", RiskLevel.HIGH, "Risk increasing by 68%"),
        NeighbourhoodRisk("Jabulani", RiskLevel.MEDIUM, "Risk increasing by 35%"),
        NeighbourhoodRisk("Dobsonville", RiskLevel.LOW, "Risk stable")
    )

    val notifications = listOf(
        AppNotification("no-1", NotificationCategory.ALERT, "Burglary reported nearby", "Protea Glen Ext 11", "2m ago"),
        AppNotification("no-2", NotificationCategory.ALERT, "Road blocked", "Kliprivier Main Rd", "16m ago"),
        AppNotification("no-3", NotificationCategory.ALERT, "Fire reported", "Meadowlands", "45m ago"),
        AppNotification("no-4", NotificationCategory.ALERT, "Missing child", "Jabulani area", "1h ago"),
        AppNotification("no-5", NotificationCategory.SYSTEM, "System update", "App maintenance at 2AM", "2h ago")
    )

    val chatMessages = listOf(
        ChatMessage("m-1", "Thabo M.", "Anyone seen what happened near Maponya Mall?", "5m ago"),
        ChatMessage("m-2", "Nomsa D.", "Yes, there was a hijacking around 7pm.", "5m ago"),
        ChatMessage("m-3", "Admin", "Please stay safe and avoid that area.", "2m ago"),
        ChatMessage("m-4", "You", "Thanks for the update!", "just now", isOwn = true)
    )

    val emergencyCategories = listOf("Medical", "Fire", "Crime", "Accident", "Personal Danger", "Flood", "Other")
    val feedFilters = listOf("All", "Robbery", "Accident", "Missing", "Fire", "Scam")
}
