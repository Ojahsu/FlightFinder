package com.example.flightfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightfinder.ui.theme.FlightFinderTheme
import androidx.compose.runtime.getValue
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.flightfinder.vues.Favoris
import com.example.flightfinder.vues.Liste
import com.example.flightfinder.vues.Radar
import com.example.flightfinder.vues.SettingsScreen


class DestinationRadar
class DestinationParametres
class DestinationFavoris
class DestinationListe

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
    val userPreferences by viewModel.userPreferences.collectAsState()

    Scaffold(
        topBar = {
            if (userPreferences.isDarkTheme) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF090909),
                        titleContentColor = Color(0xFF2dbdb4),
                        navigationIconContentColor = Color(0xFF2dbdb4),
                        actionIconContentColor = Color(0xFF2dbdb4)
                    ),
                    title = {
                        val current = backStack.lastOrNull()
                        val titleText = when (current) {
                            is DestinationRadar -> "Radar"
                            is DestinationParametres -> "Parametres"
                            is DestinationFavoris -> "Favoris"
                            is DestinationListe -> "Liste des vols"
                            else -> "Erreur"
                        }
                        Text(titleText)
                    },
                    navigationIcon = {
                        IconButton(onClick = {backStack.add(DestinationListe())}) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Ouvrir le menu"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {backStack.add(DestinationParametres())}) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Paramètres"
                            )
                        }
                    }
                )
            } else {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFFFFFFF),
                        titleContentColor = Color(0xFFfe9d15),
                        navigationIconContentColor = Color(0xFFfe9d15),
                        actionIconContentColor = Color(0xFFfe9d15)
                    ),
                    title = {
                        val current = backStack.lastOrNull()
                        val titleText = when (current) {
                            is DestinationRadar -> "Radar"
                            is DestinationParametres -> "Parametres"
                            is DestinationFavoris -> "Favoris"
                            else -> "Erreur"
                        }
                        Text(titleText)
                    },
                    navigationIcon = {
                        IconButton(onClick = {backStack.add(DestinationListe())}) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Ouvrir le menu"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {backStack.add(DestinationParametres())}) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Paramètres"
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (userPreferences.isDarkTheme) {
                BottomAppBar (
                    containerColor = Color(0xFF090909),
                    contentColor = Color(0xFF2dbdb4),
                ) {
                    NavigationBar (
                        containerColor = Color(0xFF090909),
                        contentColor = Color(0xFF2dbdb4),
                    ) {
                        val activeTab = when (backStack.lastOrNull()) {
                            is DestinationRadar -> 0
                            is DestinationFavoris -> 1
                            is DestinationParametres -> 2
                            else -> -1
                        }
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_airplane_cyan),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp).rotate(90f)
                                )
                            },
                            selected = activeTab == 0,
                            onClick = {
                                backStack.clear()
                                backStack.add(DestinationRadar())
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF2dbdb4),
                                selectedTextColor = Color(0xFF2dbdb4),
                                indicatorColor = Color(0xFF2dbdb4).copy(alpha = 0.1f)
                            )
                        )

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Ouvrir le menu",
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            selected = activeTab == 1,
                            onClick = {
                                backStack.clear()
                                backStack.add(DestinationFavoris())
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF2dbdb4),
                                selectedTextColor = Color(0xFF2dbdb4),
                                indicatorColor = Color(0xFF2dbdb4).copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            } else {
                BottomAppBar (
                    containerColor = Color(0xFFFFFFFF),
                    contentColor = Color(0xFFfe9d15),
                ) {
                    NavigationBar (
                        containerColor = Color(0xFFFFFFFF),
                        contentColor = Color(0xFFfe9d15),
                    ) {
                        val activeTab = when (backStack.lastOrNull()) {
                            is DestinationRadar -> 0
                            is DestinationFavoris -> 1
                            is DestinationParametres -> 2
                            else -> -1
                        }
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_airplane_orange),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp).rotate(90f)
                                )
                            },
                            selected = activeTab == 0,
                            onClick = {
                                backStack.clear()
                                backStack.add(DestinationRadar())
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFfe9d15),
                                selectedTextColor = Color(0xFFfe9d15),
                                indicatorColor = Color(0xFFfe9d15).copy(alpha = 0.1f)
                            )
                        )

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Ouvrir le menu",
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            selected = activeTab == 1,
                            onClick = {
                                backStack.clear()
                                backStack.add(DestinationFavoris())
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFfe9d15),
                                selectedTextColor = Color(0xFFfe9d15),
                                indicatorColor = Color(0xFFfe9d15).copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }

        }
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
                            Radar(flights, viewModel = viewModel)
                        }
                        is DestinationParametres -> NavEntry(key) {
                            SettingsScreen(viewModel)
                        }
                        is DestinationFavoris -> NavEntry(key) {
                            Favoris(viewModel)
                        }
                        is DestinationListe -> NavEntry(key) {
                            Liste(
                                viewModel = viewModel,
                                onNavigateToRadar = { backStack.add(DestinationRadar()) }
                            )
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