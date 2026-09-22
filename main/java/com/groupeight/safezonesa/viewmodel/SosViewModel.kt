package com.groupeight.safezonesa.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.groupeight.safezonesa.data.SessionManager
import com.groupeight.safezonesa.network.ApiClient
import com.groupeight.safezonesa.network.SosRequestDto
import com.groupeight.safezonesa.network.apiErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class SosState {
    object Idle : SosState()
    object Sending : SosState()
    object Sent : SosState()
    data class Failed(val message: String) : SosState()
}

/**
 * FR3 — Emergency SOS Button. On activation, sends the user's verified identity, live GPS
 * coordinates, chosen category, and stored emergency-contact list to POST /api/sos
 * (Section 5.3); the backend then notifies trusted contacts, nearby verified watch
 * members, and (if subscribed) an authorised responder (Section 6).
 */
class SosViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<SosState>(SosState.Idle)
    val state: StateFlow<SosState> = _state

    @SuppressLint("MissingPermission")
    fun sendSos(category: String) {
        val context = getApplication<Application>()
        val hasLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        _state.value = SosState.Sending
        viewModelScope.launch {
            try {
                val (lat, lng) = if (hasLocationPermission) {
                    val location = LocationServices.getFusedLocationProviderClient(context).lastLocation.await()
                    (location?.latitude ?: 0.0) to (location?.longitude ?: 0.0)
                } else {
                    0.0 to 0.0 // TODO: prompt for ACCESS_FINE_LOCATION before allowing SOS in production
                }

                val userId = SessionManager.getUserId(context) ?: "unknown"
                val response = ApiClient.getService(context).raiseSos(
                    SosRequestDto(userId, category, lat, lng)
                )

                _state.value = if (response.isSuccessful) {
                    SosState.Sent
                } else {
                    SosState.Failed(response.apiErrorMessage("SOS could not be confirmed by the server (${response.code()}). Your emergency contacts have NOT been notified automatically — call them directly."))
                }
            } catch (e: Exception) {
                _state.value = SosState.Failed("Couldn't reach the SafeZone SA server — call your emergency contacts or SAPS (10111) directly.")
            }
        }
    }

    fun reset() {
        _state.value = SosState.Idle
    }
}