package com.example.flightfinder

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightfinder.models.States
import com.example.flightfinder.repository.FlightRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewmodel : ViewModel() {
    private val flightRepository = FlightRepository()

    private val _flightsState = MutableStateFlow<List<States>>(emptyList())
    val flightsState: StateFlow<List<States>> = _flightsState.asStateFlow()

    init {
        getFlights()
    }

    fun getFlights() {
        viewModelScope.launch {
            try {
                Log.d("MainViewModel", "Début de la récupération des vols")
                val flights = flightRepository.getFlights()
                _flightsState.value = flights
                Log.d("MainViewModel", "Vols récupérés: ${flights.size}")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Erreur lors de la récupération des vols", e)
                _flightsState.value = emptyList()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        flightRepository.close()
    }
}
