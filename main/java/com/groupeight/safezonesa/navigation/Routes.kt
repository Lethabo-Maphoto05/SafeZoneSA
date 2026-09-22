package com.groupeight.safezonesa.navigation

/** Screen routes — see Section 4.7 Screen Navigation Map. */
object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val OTP_VERIFY = "otp_verify/{phone}"
    fun otpVerify(phone: String) = "otp_verify/$phone"

    const val HOME = "home"
    const val FEED = "feed"
    const val CRIME_MAP = "crime_map"
    const val SOS = "sos"
    const val CHAT = "chat"
    const val PROFILE = "profile"

    const val WALK_WITH_ME = "walk_with_me"
    const val SAFE_ROUTE = "safe_route"
    const val BEWARE = "beware"
    const val MISSING_PERSONS = "missing_persons"
    const val EMERGENCY_DIRECTORY = "emergency_directory"
    const val NOTIFICATIONS = "notifications"
    const val USER_REPUTATION = "user_reputation"
    const val AI_CRIME_PREDICTION = "ai_crime_prediction"
    const val VOLUNTEER_RESPONDERS = "volunteer_responders"
    const val SETTINGS = "settings"
}