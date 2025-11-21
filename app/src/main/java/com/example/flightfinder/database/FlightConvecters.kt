package com.example.flightfinder.database

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.flightfinder.models.FlightFromBDD
import com.example.flightfinder.models.Photo
import com.squareup.moshi.Moshi

@ProvidedTypeConverter
class FlightConvecters(moshi: Moshi) {
    private val FlightFromBDDAdapter = moshi.adapter(FlightFromBDD::class.java)
    private val PhotoAdapter = moshi.adapter(Photo::class.java)

    @TypeConverter
    fun fromFlight(FlightFromBDD: FlightFromBDD): String {
        return FlightFromBDDAdapter.toJson(FlightFromBDD)
    }

    @TypeConverter
    fun toFlight(json: String): FlightFromBDD? {
        return FlightFromBDDAdapter.fromJson(json)
    }

    @TypeConverter
    fun fromPhoto(photo: Photo): String {
        return PhotoAdapter.toJson(photo)
    }

    @TypeConverter
    fun toPhoto(json: String): Photo? {
        return PhotoAdapter.fromJson(json)
    }
}