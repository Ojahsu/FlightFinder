package com.example.flightfinder.vues

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flightfinder.MainViewmodel
import com.example.flightfinder.models.States
import com.example.flightfinder.utils.CountryFlagEmoji

@Composable
fun AircraftInfoPopup(flight: States, viewModel: MainViewmodel) {

    val flights = viewModel.localFlights.collectAsState().value

    Column(
        modifier = Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Card principale avec les infos
        Card(
            modifier = Modifier
                .wrapContentSize()
                .padding(4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    // Informations du vol
                    Column(
                        modifier = Modifier.wrapContentWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = flight.callsign?.trim() ?: "Vol inconnu",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Text(
                            text = "${CountryFlagEmoji.getFlag(CountryFlagEmoji.getCountryCode(flight.originCountry))} ${flight.originCountry}",
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )

                        flight.getAltitudeInFeet()?.let { altitude ->
                            Text(
                                text = "Alt: $altitude ft",
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                        }

                        flight.getVelocityInKnots()?.let { speed ->
                            Text(
                                text = "Vitesse: $speed kts",
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                        }

                        Text(
                            text = if (flight.onGround) "Au sol" else "En vol",
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
                    }

                    val isFavorite = flights.any { it.icao24 == flight.icao24 }

                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.Favorite,
                        contentDescription = if (isFavorite) "Déjà dans les favoris" else "Ajouter aux favoris",
                        modifier = Modifier
                            .size(32.dp)
                            .padding(start = 6.dp)
                            .clickable(
                                onClick = {
                                    if (!isFavorite) {
                                        viewModel.insertFlightToDatabase(flight)
                                    }
                                }
                            ),
                        tint = if (isFavorite) Color.Red else Color.Gray
                    )
                }
            }
        }

        // Triangle pointant vers l'avion (pointe en bas)
        Canvas(
            modifier = Modifier
                .size(width = 24.dp, height = 12.dp)
                .offset(y = (-4).dp) // Coller au bord de la Card
        ) {
            val path = Path().apply {
                // Point du haut gauche
                moveTo(0f, 0f)
                // Point du haut droit
                lineTo(size.width, 0f)
                // Point du bas (centre) - la pointe
                lineTo(size.width / 2f, size.height)
                // Fermer le triangle
                close()
            }
            drawPath(
                path = path,
                color = Color.White
            )
        }
    }
}
