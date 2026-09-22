package com.groupeight.safezonesa.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.groupeight.safezonesa.model.Incident
import com.groupeight.safezonesa.network.ApiClient
import com.groupeight.safezonesa.network.CreateIncidentDto
import com.groupeight.safezonesa.network.apiErrorMessage
import com.groupeight.safezonesa.network.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class IncidentsUiState(
    val isLoading: Boolean = false,
    val incidents: List<Incident> = emptyList(),
    val errorMessage: String? = null
)

/**
 * FR2 — Community Feed, backed by GET/POST /api/incidents and PATCH
 * /api/incidents/{id}/confirm (Section 5.3).
 */
class IncidentsViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(IncidentsUiState())
    val uiState: StateFlow<IncidentsUiState> = _uiState

    init {
        loadIncidents()
    }

    fun loadIncidents() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = ApiClient.getService(getApplication()).getIncidents()
                if (response.isSuccessful) {
                    val incidents = response.body().orEmpty().map { it.toDomain() }
                    _uiState.value = IncidentsUiState(incidents = incidents)
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Couldn't load the feed (${response.code()}).")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Couldn't reach the SafeZone SA server: ${e.localizedMessage ?: "unknown error"}"
                    )
                }
            }
        }
    }

    /** FR2 — posts a new incident. onResult receives null on success, or an error message. */
    @SuppressLint("MissingPermission")
    fun postIncident(
        category: String,
        title: String,
        description: String,
        anonymous: Boolean,
        onResult: (String?) -> Unit
    ) {
        val context = getApplication<Application>()
        viewModelScope.launch {
            try {
                var lat = -26.1420   // fallback (Soweto) if location is unavailable
                var lng = 27.8470
                val hasLocation = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                if (hasLocation) {
                    LocationServices.getFusedLocationProviderClient(context).lastLocation.await()?.let {
                        lat = it.latitude
                        lng = it.longitude
                    }
                }

                val response = ApiClient.getService(context).postIncident(
                    CreateIncidentDto(category, title, description, lat, lng, anonymous = anonymous)
                )
                when {
                    response.isSuccessful -> { loadIncidents(); onResult(null) }
                    response.code() == 401 -> onResult("Your session has expired. Please log out and log in again.")
                    else -> onResult(response.apiErrorMessage("Couldn't post (${response.code()})."))
                }
            } catch (e: Exception) {
                onResult("Couldn't reach the server: ${e.localizedMessage ?: "unknown error"}")
            }
        }
    }

    /** NFR8 — the ten-confirmation verification rule, enforced server-side. */
    fun confirmIncident(incidentId: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.getService(getApplication()).confirmIncident(incidentId)
                if (response.isSuccessful) {
                    val updated = response.body()?.toDomain() ?: return@launch
                    _uiState.update { state ->
                        state.copy(incidents = state.incidents.map { if (it.incidentId == updated.incidentId) updated else it })
                    }
                }
            } catch (_: Exception) {
                // Swallowed: a failed confirm tap shouldn't interrupt the feed with an error banner.
            }
        }
    }
}
