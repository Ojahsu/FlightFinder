package com.example.flightfinder.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Photo(
    @SerialName("Image") val image: String,
    @SerialName("Link") val link: String,
    @SerialName("Thumbnail") val thumbnail: String,
    @SerialName("DateTaken") val dateTaken: String,
    @SerialName("DateUploaded") val dateUploaded: String,
    @SerialName("Location") val location: String,
    @SerialName("Photographer") val photographer: String,
    @SerialName("Aircraft") val aircraft: String,
    @SerialName("Serial") val serial: String,
    @SerialName("Airline") val airline: String,

    val offlineImage: String? = null
)

@Serializable
data class PhotoResponse(
    @SerialName("Reg") val reg: String,
    @SerialName("Images") val images: List<Photo>
)
