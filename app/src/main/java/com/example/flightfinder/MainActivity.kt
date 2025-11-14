package com.example.flightfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightfinder.ui.theme.FlightFinderTheme
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items


class DestinationRadar
class DestinationParamettres
class DestinationFavoris

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Main()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main() {
    val backStack = remember { mutableStateListOf<Any>(DestinationRadar()) }
    val viewModel = viewModel<MainViewmodel>()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    val current = backStack.lastOrNull()
                    val titleText = when (current) {
                        is DestinationRadar -> "Radar"
                        is DestinationParamettres -> "Paramettres"
                        is DestinationFavoris -> "Favoris"
                        else -> "Erreur"
                    }
                    Text(titleText)
                },
                navigationIcon = {
                    IconButton(onClick = {}) {

                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Ouvrir le menu"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Ouvrir le menu"
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        val flights by viewModel.flightsState.collectAsState()

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            items(flights) { state ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Callsign: ${state.callsign ?: "N/A"}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("ICAO24: ${state.icao24}")
                        Text("Pays: ${state.originCountry}")
                        Text("Position: ${state.latitude ?: "N/A"}, ${state.longitude ?: "N/A"}")
                        Text("Altitude baro: ${state.baroAltitude ?: "N/A"} m")
                        Text("Altitude geo: ${state.geoAltitude ?: "N/A"} m")
                        Text("Vitesse: ${state.velocity ?: "N/A"} m/s")
                        Text("Cap: ${state.trueTrack ?: "N/A"}°")
                        Text("Taux vertical: ${state.verticalRate ?: "N/A"} m/s")
                        Text("Au sol: ${if (state.onGround) "Oui" else "Non"}")
                        state.squawk?.let { Text("Squawk: $it") }
                    }
                }
            }
        }

    }
}



















@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FlightFinderTheme {
        Greeting("Android")
    }
}