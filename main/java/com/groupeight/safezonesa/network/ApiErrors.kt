package com.groupeight.safezonesa.network

import com.google.gson.JsonParser
import retrofit2.Response

/**
 * Every controller in SafeZoneSA.Api returns `{ "message": "..." }` on a 4xx/5xx response
 * (see AuthController, IncidentsController, SosController). Retrofit only auto-deserializes
 * the success body, so callers were falling back to just the numeric status code and hiding
 * the actual reason — this pulls that message out of response.errorBody() so the UI can show
 * something the user (or you, debugging) can act on, e.g. "Password must be at least 8
 * characters." instead of "Registration failed (400)."
 */
fun <T> Response<T>.apiErrorMessage(fallback: String): String {
    val raw = errorBody()?.string()
    if (raw.isNullOrBlank()) return fallback
    return runCatching {
        JsonParser.parseString(raw).asJsonObject.get("message")?.asString
    }.getOrNull() ?: fallback
}
