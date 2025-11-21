package com.example.flightfinder.vues

import androidx.compose.foundation.clickable
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
import coil3.compose.AsyncImage
import com.example.flightfinder.MainViewmodel

@Composable
fun Favoris(viewModel: MainViewmodel){
    val flights = viewModel.localFlights.collectAsState().value

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(flights) { plane ->
            Card(
                modifier = Modifier
                    .clickable(
                        onClick = { viewModel.clearDatabase() }
                    )
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    AsyncImage(
                        model = "${plane.photo?.link}",
                        contentDescription = "Bannière de l'évènement",
                    )
                    Text(
                        text = "Callsign: ${plane.nom ?: "N/A"}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("ICAO24: ${plane.icao24}")
                }
            }
        }
    }
}