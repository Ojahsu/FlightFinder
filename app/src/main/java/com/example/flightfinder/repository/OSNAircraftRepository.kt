package com.example.flightfinder.repository

import android.util.Log
import com.example.flightfinder.models.FlightFromAPI
import com.example.flightfinder.models.OSNAircraft
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

class OSNAircraftRepository {

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

//    suspend fun getAircraftByICAO(icao: String): OSNAircraft {
//        return try {
//            val url = "https://opensky-network.org/api/metadata/aircraft/icao/$icao"
//            val response: OSNAircraft = client.get(url).body()
//            response ?: OSNAircraft()
//        } catch (e: Exception) {
//            null
//        }
//    }
}
