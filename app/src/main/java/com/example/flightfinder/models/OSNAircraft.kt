package com.example.flightfinder.models

import kotlinx.serialization.Serializable

@Serializable
data class OSNAircraft(
    val acars: Boolean? = null,
    val adsb: Boolean? = null,
    val categoryDescription: String? = null,
    val country: String? = null,
    val engines: String? = null,
    val icao24: String? = null,
    val icaoAircraftClass: String? = null,
    val lineNumber: String? = null,
    val manufacturerIcao: String? = null,
    val manufacturerName: String? = null,
    val model: String? = null,
    val modes: Boolean? = null,
    val notes: String? = null,
    val operator: String? = null,
    val operatorCallsign: String? = null,
    val operatorIata: String? = null,
    val operatorIcao: String? = null,
    val owner: String? = null,
    val registration: String? = null,
    val selCal: String? = null,
    val serialNumber: String? = null,
    val status: String? = null,
    val timestamp: Long? = null,
    val typecode: String? = null,
    val vdl: Boolean? = null
)
