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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.flightfinder.vues.Favoris
import com.example.flightfinder.vues.Liste
import com.example.flightfinder.vues.Radar
import com.example.flightfinder.vues.SettingsScreen

// ═══════════════════════════════════════════════════
// DESTINATIONS (avec Serializable pour la sauvegarde)
// ═══════════════════════════════════════════════════
class DestinationRadar : java.io.Serializable
class DestinationParametres : java.io.Serializable
class DestinationFavoris : java.io.Serializable
class DestinationListe : java.io.Serializable

// ═══════════════════════════════════════════════════
// ACTIVITÉ PRINCIPALE
// ═══════════════════════════════════════════════════
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Main()
        }
    }
}

val BackStackSaver = Saver<List<Any>, List<String>>(
    save = { backStack ->
        backStack.map { destination ->
            when (destination) {
                is DestinationRadar -> "radar"
                is DestinationParametres -> "parametres"
                is DestinationFavoris -> "favoris"
                is DestinationListe -> "liste"
                else -> "radar" // Fallback
            }
        }
    },
    restore = { savedList ->
        savedList.map { key ->
            when (key) {
                "radar" -> DestinationRadar()
                "parametres" -> DestinationParametres()
                "favoris" -> DestinationFavoris()
                "liste" -> DestinationListe()
                else -> DestinationRadar()
            }
        }
    }
)

// ═══════════════════════════════════════════════════
// ÉCRAN PRINCIPAL
// ═══════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main() {
    // ✅ SAUVEGARDE LA BACKSTACK AVEC rememberSaveable
    val backStack = rememberSaveable(saver = BackStackSaver) {
        mutableStateListOf<Any>(DestinationRadar())
    }.toMutableStateList()

    val viewModel = viewModel<MainViewmodel>()
    val userPreferences by viewModel.userPreferences.collectAsState()

    Scaffold(
        topBar = {
            val current = backStack.lastOrNull()
            val titleText = when (current) {
                is DestinationRadar -> "Radar"
                is DestinationParametres -> "Parametres"
                is DestinationFavoris -> "Favoris"
                is DestinationListe -> "Liste des vols"
                else -> "Erreur"
            }

            val colors = if (userPreferences.isDarkTheme) {
                TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF090909),
                    titleContentColor = Color(0xFF2dbdb4),
                    navigationIconContentColor = Color(0xFF2dbdb4),
                    actionIconContentColor = Color(0xFF2dbdb4)
                )
            } else {
                TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFFFFF),
                    titleContentColor = Color(0xFFfe9d15),
                    navigationIconContentColor = Color(0xFFfe9d15),
                    actionIconContentColor = Color(0xFFfe9d15)
                )
            }

            TopAppBar(
                colors = colors,
                title = { Text(titleText) },
                navigationIcon = {
                    IconButton(onClick = { backStack.add(DestinationListe()) }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Ouvrir le menu"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { backStack.add(DestinationParametres()) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Paramètres"
                        )
                    }
                }
            )
        },
        bottomBar = {
            val activeTab = when (backStack.lastOrNull()) {
                is DestinationRadar -> 0
                is DestinationFavoris -> 1
                else -> -1
            }

            val containerColor = if (userPreferences.isDarkTheme) Color(0xFF090909) else Color(0xFFFFFFFF)
            val contentColor = if (userPreferences.isDarkTheme) Color(0xFF2dbdb4) else Color(0xFFfe9d15)
            val airplaneIcon = if (userPreferences.isDarkTheme) R.drawable.ic_airplane_cyan else R.drawable.ic_airplane_orange

            BottomAppBar(
                containerColor = containerColor,
                contentColor = contentColor
            ) {
                NavigationBar(
                    containerColor = containerColor,
                    contentColor = contentColor
                ) {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = airplaneIcon),
                                contentDescription = "Radar",
                                modifier = Modifier.size(24.dp).rotate(90f)
                            )
                        },
                        selected = activeTab == 0,
                        onClick = {
                            backStack.clear()
                            backStack.add(DestinationRadar())
                            viewModel.clearSelectedFlight()
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = contentColor,
                            selectedTextColor = contentColor,
                            indicatorColor = contentColor.copy(alpha = 0.1f)
                        )
                    )

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Favoris",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        selected = activeTab == 1,
                        onClick = {
                            backStack.clear()
                            backStack.add(DestinationFavoris())
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = contentColor,
                            selectedTextColor = contentColor,
                            indicatorColor = contentColor.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        val flights by viewModel.flightsState.collectAsState()

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                            Favoris(
                                viewModel = viewModel,
                                onNavigateToRadar = { backStack.add(DestinationRadar()) }
                            )
                        }
                        is DestinationListe -> NavEntry(key) {
                            Liste(
                                viewModel = viewModel,
                                onNavigateToRadar = { backStack.add(DestinationRadar()) }
                            )
                        }
                        else -> error("Unknown key $key")
                    }
                }
            )
        }
    }
}
