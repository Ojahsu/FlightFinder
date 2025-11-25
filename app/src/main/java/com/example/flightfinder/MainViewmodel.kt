package com.example.flightfinder

import android.app.Application
import android.content.Context
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

    private val context: Context
        get() = getApplication<Application>().applicationContext
    private val flightAPIRepository = FlightAPIRepository()
    private val flightDatabaseRepository = FlightDatabaseRepository(context)
    private val onsAircraftRepository = OSNAircraftRepository()
    val userPreferencesRepository = UserPreferencesRepository(context)
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

    private var refreshJob: Job? = null

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        getFlights()
        getAllLocalFlights()
        observeAutoRefreshPreference()
    }

    private fun observeAutoRefreshPreference() {
        viewModelScope.launch {
            userPreferences.collect { prefs ->
                if (prefs.isAutoRefreshEnabled) {
                    startAutoRefresh(prefs.refreshIntervalSeconds)
                } else {
                    stopAutoRefresh()
                }
            }
        }
    }

    private fun startAutoRefresh(intervalSeconds: Int) {
        refreshJob?.cancel()

        refreshJob = viewModelScope.launch {
            while (true) {
                delay(intervalSeconds * 1000L)
                getFlights()
            }
        }
    }

    private fun stopAutoRefresh() {
        refreshJob?.cancel()
        refreshJob = null
    }

    fun refreshFlights() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                getFlights()
            } finally {
                delay(500)
                _isRefreshing.value = false
            }
        }
    }

    fun getFlights() {
        viewModelScope.launch {
            try {
                val states = flightAPIRepository.getFlights()
                _flightsState.value = states
            } catch (e: Exception) {
                _flightsState.value = emptyList()
            }
        }
    }

    fun insertFlightToDatabase(state: States) {
        viewModelScope.launch {
            if (flightDatabaseRepository.getFlightByICAO(state.icao24) == null) {
                val aircraft = onsAircraftRepository.getAircraftByICAO(state.icao24)
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

    fun selectFlightByIcao(icao24: String) {
        val flight = _flightsState.value.find { it.icao24 == icao24 }
        _selectedFlight.value = flight
    }

    fun clearSelectedFlight() {
        _selectedFlight.value = null
    }

    override fun onCleared() {
        super.onCleared()
        stopAutoRefresh()
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}
