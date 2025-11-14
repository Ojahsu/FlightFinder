package com.example.flightfinder.models

import android.util.Log
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable
data class Flight(
    @SerialName("time")
    val time: Long? = null,

    @SerialName("states")
    @Serializable(with = StatesListSerializer::class)
    val states: List<States>? = null
)

@Serializable
data class States(
    val icao24: String,
    val callsign: String? = null,
    val originCountry: String,
    val timePosition: Long? = null,
    val lastContact: Long,
    val longitude: Double? = null,
    val latitude: Double? = null,
    val baroAltitude: Double? = null,
    val onGround: Boolean,
    val velocity: Double? = null,
    val trueTrack: Double? = null,
    val verticalRate: Double? = null,
    val sensors: List<Int>? = null,
    val geoAltitude: Double? = null,
    val squawk: String? = null,
    val spi: Boolean,
    val positionSource: Int
) {
    fun hasValidPosition(): Boolean = latitude != null && longitude != null
    fun getAltitudeInFeet(): Int? = baroAltitude?.times(3.28084)?.toInt()
    fun getVelocityInKnots(): Int? = velocity?.times(1.94384)?.toInt()
}

object StatesListSerializer : KSerializer<List<States>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("StatesList")

    override fun deserialize(decoder: Decoder): List<States> {
        val jsonDecoder = decoder as JsonDecoder
        val element = jsonDecoder.decodeJsonElement()

        // Vérifier si c'est un JsonArray
        if (element !is JsonArray) {
            Log.w("StatesSerializer", "Expected JsonArray but got: ${element::class.simpleName}")
            return emptyList()
        }

        val jsonArray = element

        return jsonArray.mapNotNull { arrayElement ->
            // Vérifier si l'élément du tableau est null
            if (arrayElement is JsonNull) {
                Log.w("StatesSerializer", "Skipping null element in array")
                return@mapNotNull null
            }

            // Vérifier si l'élément est un tableau
            if (arrayElement !is JsonArray) {
                Log.w("StatesSerializer", "Skipping non-array element: ${arrayElement::class.simpleName}")
                return@mapNotNull null
            }

            try {
                val array = arrayElement

                // Vérifier que le tableau a suffisamment d'éléments
                if (array.size < 17) {
                    Log.w("StatesSerializer", "Array too short: ${array.size} elements")
                    return@mapNotNull null
                }

                States(
                    icao24 = array.getStringOrNull(0) ?: run {
                        Log.w("StatesSerializer", "Missing icao24")
                        return@mapNotNull null
                    },
                    callsign = array.getStringOrNull(1)?.trim()?.takeIf { it.isNotBlank() },
                    originCountry = array.getStringOrNull(2) ?: run {
                        Log.w("StatesSerializer", "Missing originCountry")
                        return@mapNotNull null
                    },
                    timePosition = array.getLongOrNull(3),
                    lastContact = array.getLongOrNull(4) ?: run {
                        Log.w("StatesSerializer", "Missing lastContact")
                        return@mapNotNull null
                    },
                    longitude = array.getDoubleOrNull(5),
                    latitude = array.getDoubleOrNull(6),
                    baroAltitude = array.getDoubleOrNull(7),
                    onGround = array.getBooleanOrNull(8) ?: false,
                    velocity = array.getDoubleOrNull(9),
                    trueTrack = array.getDoubleOrNull(10),
                    verticalRate = array.getDoubleOrNull(11),
                    sensors = try {
                        array.getOrNull(12)?.let { sensorsElement ->
                            if (sensorsElement is JsonArray) {
                                sensorsElement.mapNotNull { it.jsonPrimitive.intOrNull }
                            } else null
                        }
                    } catch (e: Exception) {
                        null
                    },
                    geoAltitude = array.getDoubleOrNull(13),
                    squawk = array.getStringOrNull(14),
                    spi = array.getBooleanOrNull(15) ?: false,
                    positionSource = array.getIntOrNull(16) ?: 0
                )
            } catch (e: Exception) {
                Log.e("StatesSerializer", "Error parsing state: ${e.message}", e)
                null
            }
        }
    }

    override fun serialize(encoder: Encoder, value: List<States>) {
        throw NotImplementedError("Serialization not supported")
    }

    // Fonctions utilitaires pour extraire les valeurs en toute sécurité
    private fun JsonArray.getStringOrNull(index: Int): String? {
        val element = getOrNull(index) ?: return null
        if (element is JsonNull) return null
        return try {
            element.jsonPrimitive.contentOrNull?.takeIf { it != "null" && it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }

    private fun JsonArray.getIntOrNull(index: Int): Int? {
        val element = getOrNull(index) ?: return null
        if (element is JsonNull) return null
        return try {
            element.jsonPrimitive.intOrNull
        } catch (e: Exception) {
            null
        }
    }

    private fun JsonArray.getLongOrNull(index: Int): Long? {
        val element = getOrNull(index) ?: return null
        if (element is JsonNull) return null
        return try {
            element.jsonPrimitive.longOrNull
        } catch (e: Exception) {
            null
        }
    }

    private fun JsonArray.getDoubleOrNull(index: Int): Double? {
        val element = getOrNull(index) ?: return null
        if (element is JsonNull) return null
        return try {
            element.jsonPrimitive.doubleOrNull
        } catch (e: Exception) {
            null
        }
    }

    private fun JsonArray.getBooleanOrNull(index: Int): Boolean? {
        val element = getOrNull(index) ?: return null
        if (element is JsonNull) return null
        return try {
            element.jsonPrimitive.booleanOrNull
        } catch (e: Exception) {
            null
        }
    }
}
