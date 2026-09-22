package com.groupeight.safezonesa.data

import android.content.Context
import android.content.SharedPreferences

/** Keys for the switches on the Settings screen. */
object PrefKeys {
    const val SHAKE_SOS = "pref_shake_sos"
    const val VOLUME_SOS = "pref_volume_sos"
    const val ALERT_NOTIFICATIONS = "pref_alert_notifications"
    const val ANONYMOUS_DEFAULT = "pref_anonymous_default"
}

/**
 * Holds the JWT bearer token returned by POST /api/auth/login (Section 5.2/5.3, NFR2),
 * the logged-in user's details, and the Settings switches.
 */
object SessionManager {
    private const val PREFS_NAME = "safezone_session"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_DISPLAY_NAME = "display_name"
    private const val KEY_EMAIL = "email"
    private const val KEY_ONBOARDING_SEEN = "onboarding_seen"

    private fun prefs(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveSession(context: Context, token: String, userId: String, displayName: String = "", email: String = "") {
        prefs(context).edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER_ID, userId)
            .putString(KEY_DISPLAY_NAME, displayName)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun getToken(context: Context): String? = prefs(context).getString(KEY_TOKEN, null)
    fun getUserId(context: Context): String? = prefs(context).getString(KEY_USER_ID, null)
    fun getDisplayName(context: Context): String? = prefs(context).getString(KEY_DISPLAY_NAME, null)
    fun getEmail(context: Context): String? = prefs(context).getString(KEY_EMAIL, null)

    fun isLoggedIn(context: Context): Boolean = getToken(context) != null

    // Onboarding shown once, on the very first launch, then never again — separate from
    // login state, so a user who logs out later isn't shown the onboarding slides again.
    fun hasSeenOnboarding(context: Context): Boolean = prefs(context).getBoolean(KEY_ONBOARDING_SEEN, false)

    fun setOnboardingSeen(context: Context) {
        prefs(context).edit().putBoolean(KEY_ONBOARDING_SEEN, true).apply()
    }

    fun getPref(context: Context, key: String, default: Boolean = true): Boolean =
        prefs(context).getBoolean(key, default)

    fun setPref(context: Context, key: String, value: Boolean) {
        prefs(context).edit().putBoolean(key, value).apply()
    }

    /** Removes only the login details, so settings and the onboarding flag survive. */
    fun logout(context: Context) {
        prefs(context).edit()
            .remove(KEY_TOKEN).remove(KEY_USER_ID).remove(KEY_DISPLAY_NAME).remove(KEY_EMAIL)
            .apply()
    }

    fun clear(context: Context) {
        prefs(context).edit().clear().apply()
    }
}