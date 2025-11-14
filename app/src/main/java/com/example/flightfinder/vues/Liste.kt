package com.example.flightfinder.vues

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Liste(){
//    val flights by viewModel.flightsState.collectAsState()
//
//    LazyColumn(
//        modifier = Modifier
//            .padding(innerPadding)
//            .fillMaxSize()
//    ) {
//        items(flights) { state ->
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp),
//                elevation = CardDefaults.cardElevation(4.dp)
//            ) {
//                Column(
//                    modifier = Modifier.padding(16.dp)
//                ) {
//                    Text(
//                        text = "Callsign: ${state.callsign ?: "N/A"}",
//                        style = MaterialTheme.typography.titleMedium
//                    )
//                    Text("ICAO24: ${state.icao24}")
//                    Text("Pays: ${state.originCountry}")
//                    Text("Position: ${state.latitude ?: "N/A"}, ${state.longitude ?: "N/A"}")
//                    Text("Altitude baro: ${state.baroAltitude ?: "N/A"} m")
//                    Text("Altitude geo: ${state.geoAltitude ?: "N/A"} m")
//                    Text("Vitesse: ${state.velocity ?: "N/A"} m/s")
//                    Text("Cap: ${state.trueTrack ?: "N/A"}°")
//                    Text("Taux vertical: ${state.verticalRate ?: "N/A"} m/s")
//                    Text("Au sol: ${if (state.onGround) "Oui" else "Non"}")
//                    state.squawk?.let { Text("Squawk: $it") }
//                }
//            }
//        }
//    }
}