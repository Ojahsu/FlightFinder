package com.example.flightfinder.repository

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.flightfinder.database.AppDatabase
import com.example.flightfinder.database.FlightConvecters
import com.example.flightfinder.models.FlightFromBDD
import com.example.flightfinder.models.OSNAircraft
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

    suspend fun insertFlight(state: States, aircraft: OSNAircraft?) {
        Log.d("FlightDatabaseRepository", "Avion reçu: $aircraft")
        val photo = flightAPIRepository.getPhoto(aircraft?.registration ?: "")
        Log.d("FlightDatabaseRepository", "Inserting flight with photo: $photo")
        // On inclut l'objet 'aircraft' dans le champ 'plane' pour qu'il soit persisté via le converter
        dao.insertFlight(
            FlightFromBDD(
                id = 0,
                photo = photo,
                icao24 = state.icao24 ?: "",
                nom = state.callsign ?: "",
                plane = aircraft
            )
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

    suspend fun getFlightByICAO(icao: String): FlightFromBDD? {
        val flight = dao.getFlightByICAO(icao)
        return flight
    }
}