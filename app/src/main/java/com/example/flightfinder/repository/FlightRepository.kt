package com.example.flightfinder.repository

import android.util.Log
import com.example.flightfinder.models.Flight
import com.example.flightfinder.models.States
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class FlightRepository {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                coerceInputValues = true
            })
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("HTTP Client", message)
                }
            }
            level = LogLevel.ALL
        }
    }

    suspend fun getFlights(): List<States> {
        return try {
            val url = "https://opensky-network.org/api/states/all"
            val response: Flight = client.get(url).body()
            Log.d("FlightRepository", "Récupération de ${response.states?.size ?: 0} vols")
            response.states?.filter { it.hasValidPosition() } ?: emptyList()
        } catch (e: Exception) {
            Log.e("FlightRepository", "Erreur lors de la récupération des vols", e)
            emptyList()
        }
    }

    suspend fun getFlightsInBoundingBox(
        lamin: Double,
        lomin: Double,
        lamax: Double,
        lomax: Double
    ): List<States> {
        return try {
            val url = "https://opensky-network.org/api/states/all?lamin=$lamin&lomin=$lomin&lamax=$lamax&lomax=$lomax"
            val response: Flight = client.get(url).body()
            Log.d("FlightRepository", "Récupération de ${response.states?.size ?: 0} vols dans la zone")
            response.states?.filter { it.hasValidPosition() } ?: emptyList()
        } catch (e: Exception) {
            Log.e("FlightRepository", "Erreur lors de la récupération des vols", e)
            emptyList()
        }
    }

    fun close() {
        client.close()
    }
}
