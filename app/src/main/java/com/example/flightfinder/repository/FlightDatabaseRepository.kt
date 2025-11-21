package com.example.flightfinder.repository

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.flightfinder.database.AppDatabase
import com.example.flightfinder.database.FlightConvecters
import com.example.flightfinder.models.FlightFromAPI
import com.example.flightfinder.models.FlightFromBDD
import com.example.flightfinder.models.Photo
import com.example.flightfinder.models.States
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class FlightDatabaseRepository(context: Context) {

    private val flightAPIRepository = FlightAPIRepository()

    // --- Repository pour la BDD ---
    val database = Room.databaseBuilder( context, AppDatabase::class.java, "flight_database" )
        .addTypeConverter(FlightConvecters(Moshi.Builder().add(KotlinJsonAdapterFactory()).build()))
        .build()

    val dao = database.flightDao()

    // --- Méthodes pour interagir avec la BDD ---

    suspend fun insertFlight(state: States) {
        val photo = flightAPIRepository.getPhoto(state.callsign ?: "")
        Log.d("FlightDatabaseRepository", "Inserting flight with photo: $photo")
        dao.insertFlight(FlightFromBDD(0, photo,
            state.icao24 ?: "",
            state.callsign ?: "")
        )
    }

    suspend fun getAllFlights(): List<FlightFromBDD> {
        return dao.getAllFlights()
    }

    suspend fun deleteFlight(id: Int) {
        dao.deleteFlight(id)
    }

    suspend fun deleteAllFlights() {
        dao.deleteAllFlights()
    }

    suspend fun getFlightById(id: Int): FlightFromBDD? {
        return dao.getFlightById(id)
    }
}