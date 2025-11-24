package com.example.flightfinder.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FlightFromBDD(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val photo: Photo? = null,
    val icao24: String,
    val nom: String? = null,
    val paysOrigine: String? = null,
    val plane: OSNAircraft? = null,
){

}
