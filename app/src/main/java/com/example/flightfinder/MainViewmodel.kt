package com.example.flightfinder

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.flightfinder.database.AppDatabase
import com.example.flightfinder.database.FlightConvecters
import com.example.flightfinder.models.FlightFromBDD
import com.example.flightfinder.models.States
import com.example.flightfinder.repository.FlightAPIRepository
import com.example.flightfinder.repository.FlightDatabaseRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewmodel(application: Application) : AndroidViewModel(application) {

    private val context: Context
        get() = getApplication<Application>().applicationContext

    private val flightAPIRepository = FlightAPIRepository()
    private val flightDatabaseRepository = FlightDatabaseRepository(context)

    private val _flightsState = MutableStateFlow<List<States>>(emptyList())
    val flightsState: StateFlow<List<States>> = _flightsState.asStateFlow()

    val localFlights = MutableStateFlow<List<FlightFromBDD>>(emptyList())

    init {
        getFlights()
        getAllLocalFlights()
    }

    fun getFlights() {
        viewModelScope.launch {
            try {
                Log.d("MainViewModel", "Début de la récupération des vols")
                val states = flightAPIRepository.getFlights()
                _flightsState.value = states
                Log.d("MainViewModel", "Vols récupérés: ${states.size}")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Erreur lors de la récupération des vols", e)
                _flightsState.value = emptyList()
            }
        }
    }

    fun insertFlightToDatabase(state: States) {
        viewModelScope.launch {
            flightDatabaseRepository.insertFlight(state)
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

//    override fun onCleared() {
//        super.onCleared()
//        flightAPIRepository.close()
//    }

    // --- Partie BDD ---


}
