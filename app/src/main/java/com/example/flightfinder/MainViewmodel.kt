package com.example.flightfinder

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightfinder.models.FlightFromBDD
import com.example.flightfinder.models.States
import com.example.flightfinder.models.UserPreferences
import com.example.flightfinder.repository.FlightAPIRepository
import com.example.flightfinder.repository.FlightDatabaseRepository
import com.example.flightfinder.repository.OSNAircraftRepository
import com.example.flightfinder.repository.UserPreferencesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewmodel(application: Application) : AndroidViewModel(application) {

    // ═══════════════════════════════════════════════════════════
    // 🔧 PROPERTIES
    // ═══════════════════════════════════════════════════════════

    private val context: Context
        get() = getApplication<Application>().applicationContext

    private val flightAPIRepository = FlightAPIRepository()
    private val flightDatabaseRepository = FlightDatabaseRepository(context)
    private val onsAircraftRepository = OSNAircraftRepository()
    val userPreferencesRepository = UserPreferencesRepository(context)

    // ═══════════════════════════════════════════════════════════
    // 📊 STATE FLOWS
    // ═══════════════════════════════════════════════════════════

    private val _flightsState = MutableStateFlow<List<States>>(emptyList())
    val flightsState: StateFlow<List<States>> = _flightsState.asStateFlow()

    val localFlights = MutableStateFlow<List<FlightFromBDD>>(emptyList())

    private val _selectedFlight = MutableStateFlow<States?>(null)
    val selectedFlight: StateFlow<States?> = _selectedFlight.asStateFlow()

    val userPreferences: StateFlow<UserPreferences> = userPreferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    // ═══════════════════════════════════════════════════════════
    // ⏱️ AUTO-REFRESH
    // ═══════════════════════════════════════════════════════════

    private var refreshJob: Job? = null

    private val _isRefreshing = MutableStateFlow(userPreferences.value.isAutoRefreshEnabled)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // ═══════════════════════════════════════════════════════════
    // 🚀 INITIALIZATION
    // ═══════════════════════════════════════════════════════════

    init {
        getFlights()
        getAllLocalFlights()
        startAutoRefresh()
    }

    // ═══════════════════════════════════════════════════════════
    // 🔄 AUTO-REFRESH LOGIC
    // ═══════════════════════════════════════════════════════════

    /**
     * Démarre le rafraîchissement automatique basé sur les préférences
     */
    private fun startAutoRefresh() {
        viewModelScope.launch {
            userPreferences.collect { prefs ->
                refreshJob?.cancel()
                refreshJob = launch {
                    while (true) {
                        delay(userPreferences.value.refreshIntervalSeconds * 1000L) // Convertir secondes en ms
                        Log.d(TAG, "Auto-refresh: ${userPreferences.value.refreshIntervalSeconds}s")
                        getFlights()
                    }
                }
            }
        }
    }

    /**
     * Rafraîchissement manuel (pour le pull-to-refresh)
     */
    fun refreshFlights() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                getFlights()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    // ═══════════════════════════════════════════════════════════
    // ✈️ FLIGHTS API
    // ═══════════════════════════════════════════════════════════

    fun getFlights() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Début de la récupération des vols")
                val states = flightAPIRepository.getFlights()
                _flightsState.value = states
                Log.d(TAG, "Vols récupérés: ${states.size}")
            } catch (e: Exception) {
                Log.e(TAG, "Erreur lors de la récupération des vols", e)
                _flightsState.value = emptyList()
            }
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 💾 DATABASE OPERATIONS
    // ═══════════════════════════════════════════════════════════

    fun insertFlightToDatabase(state: States) {
        viewModelScope.launch {
            if (flightDatabaseRepository.getFlightByICAO(state.icao24) == null) {
                val aircraft = onsAircraftRepository.getAircraftByICAO(state.icao24)
                Log.d(TAG, "Avion envoyé: ${aircraft?.registration}")
                flightDatabaseRepository.insertFlight(state, aircraft)
                getAllLocalFlights()
            }
        }
    }

    fun getAllLocalFlights() {
        viewModelScope.launch {
            val flights = flightDatabaseRepository.getAllFlights()
            localFlights.value = flights
        }
    }

    fun clearDatabase() {
        viewModelScope.launch {
            flightDatabaseRepository.deleteAllFlights()
            localFlights.value = emptyList()
        }
    }

    fun deleteFlight(id: Int) {
        viewModelScope.launch {
            flightDatabaseRepository.deleteFlight(id)
            getAllLocalFlights()
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 🎯 FLIGHT SELECTION
    // ═══════════════════════════════════════════════════════════

    fun selectFlightByIcao(icao24: String) {
        val flight = _flightsState.value.find { it.icao24 == icao24 }
        _selectedFlight.value = flight
    }

    fun clearSelectedFlight() {
        _selectedFlight.value = null
    }

    // ═══════════════════════════════════════════════════════════
    // 🧹 LIFECYCLE
    // ═══════════════════════════════════════════════════════════

    override fun onCleared() {
        super.onCleared()
        refreshJob?.cancel()
        Log.d(TAG, "ViewModel cleared - refresh job cancelled")
    }

    // ═══════════════════════════════════════════════════════════
    // 🏷️ CONSTANTS
    // ═══════════════════════════════════════════════════════════

    companion object {
        private const val TAG = "MainViewModel"
    }
}
