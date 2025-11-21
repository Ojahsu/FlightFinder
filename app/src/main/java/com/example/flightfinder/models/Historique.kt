package com.example.flightfinder.models

data class Historique(
    val aircraft: String,
    val airline: String,
    val operator: String,
    val typeCode: String,
    val airlineCode: String,
    val operatorCode: String,
    val modeS: String,
    val flights: List<FlightHistorique>
)

data class FlightHistorique(
    val date: String,
    val from: String,
    val to: String,
    val flight: String,
    val flightTime: String,
    val std: String,
    val atd: String,
    val sta: String,
    val status: String
)