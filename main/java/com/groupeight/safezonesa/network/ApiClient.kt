package com.groupeight.safezonesa.network

import android.content.Context
import com.groupeight.safezonesa.BuildConfig
import com.groupeight.safezonesa.data.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Builds the Retrofit client that talks to the ASP.NET Core REST API (Section 5).
 * Every request carries the JWT bearer token from [SessionManager] in the Authorization
 * header, once a token exists (obtained via /api/auth/login after OTP verification —
 * Section 5.2, NFR2). The base URL comes from BuildConfig.API_BASE_URL
 * (app/build.gradle.kts), pointed at the Azure App Service deployment (Section 5.5).
 */
object ApiClient {

    private var cachedService: SafeZoneApiService? = null

    fun getService(context: Context): SafeZoneApiService {
        return cachedService ?: build(context).also { cachedService = it }
    }

    private fun build(context: Context): SafeZoneApiService {
        val authInterceptor = Interceptor { chain ->
            val token = SessionManager.getToken(context)
            val request = if (token != null) {
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                chain.request()
            }
            chain.proceed(request)
        }

        val logging = HttpLoggingInterceptor().apply {
            // BODY is convenient during Part 2 integration testing; drop to NONE for release
            // builds so request/response bodies (which may include location data) aren't logged.
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            // OkHttp's defaults are 10s each way, which is tight enough that a cold LocalDB
            // start (EnsureCreated() building the whole schema on the API's first request
            // after (re)starting it) can trip a "Read timed out" even though the server is
            // working fine and would have answered a few seconds later. 30s gives it room
            // without masking a genuinely dead connection for very long.
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SafeZoneApiService::class.java)
    }
}
