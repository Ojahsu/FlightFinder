package com.example.flightfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.flightfinder.vues.Radar


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
                    containerColor = Color(0xFF090909),
//                    titleContentColor = Color(0xFF2dbdb4),
                    titleContentColor = Color(0xFFfe9d15),
                    navigationIconContentColor = Color(0xFFfe9d15),
                    actionIconContentColor = Color(0xFFfe9d15)
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

        Column(
            modifier = Modifier.padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = { key ->
                    when (key) {
                        is DestinationRadar -> NavEntry(key) {
                            Radar(flights)
                        }
                        is DestinationParamettres -> NavEntry(key) {
                            Radar(flights)
                        }
                        is DestinationFavoris -> NavEntry(key) {
                            Radar(flights)
                        }
                        else -> {
                            error("Unknown key $key")
                        }
                    }
                }
            )
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