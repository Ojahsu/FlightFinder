package com.example.flightfinder.repository

import android.util.Log
import com.example.flightfinder.models.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class FlightAPIRepository {
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
            val response: FlightFromAPI = client.get(url).body()
            Log.d("FlightRepository", "Récupération de ${response.states?.size ?: 0} vols")
            response.states?.filter { it.hasValidPosition() } ?: emptyList()
        } catch (e: Exception) {
            Log.e("FlightRepository", "Erreur lors de la récupération des vols", e)
            emptyList()
        }
    }

    suspend fun getPhoto(callsign: String): Photo? {
        return try {
            val cleanCallsign = callsign.trim()
            val url = "https://www.jetapi.dev/api?reg=$cleanCallsign&photos=1&only_jp=true"

            Log.d("FlightRepository", "🔍 Recherche photo pour callsign: '$cleanCallsign'")
            Log.d("FlightRepository", "📡 URL: $url")

            // ✅ Récupérer la réponse HTTP brute
            val httpResponse: HttpResponse = client.get(url)

            // ✅ Vérifier le statut HTTP
            if (!httpResponse.status.isSuccess()) {
                val errorBody = httpResponse.bodyAsText()
                Log.w("FlightRepository", "⚠️ HTTP ${httpResponse.status.value}: $errorBody")
                return null
            }

            // ✅ Parser seulement si statut 2xx
            val response: PhotoResponse = httpResponse.body()

            Log.d("FlightRepository", "✅ Réponse reçue - Reg: ${response.reg}, Images: ${response.images.size}")

            val photo = response.images.firstOrNull()

            if (photo != null) {
                Log.d("FlightRepository", "📸 Photo trouvée: ${photo.image}")
            } else {
                Log.w("FlightRepository", "⚠️ Aucune photo dans la réponse")
            }

            photo

        } catch (e: Exception) {
            Log.e("FlightRepository", "❌ Erreur photo pour '$callsign': ${e.message}", e)
            null
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
            val response: FlightFromAPI = client.get(url).body()
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
