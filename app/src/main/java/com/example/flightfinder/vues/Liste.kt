package com.example.flightfinder.vues

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flightfinder.MainViewmodel
import com.example.flightfinder.R

@Composable
fun Liste(
    viewModel: MainViewmodel,
    onNavigateToRadar: () -> Unit
) {
    val flights = viewModel.flightsState.collectAsState().value
    val localFlights = viewModel.localFlights.collectAsState().value
    val userPreferences by viewModel.userPreferences.collectAsState()

    // États des filtres
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var filterOnGround by remember { mutableStateOf<Boolean?>(null) }
    var selectedCountries by remember { mutableStateOf<Set<String>>(emptySet()) }
    var selectedEmergencySquawks by remember { mutableStateOf<Set<String>>(emptySet()) }

    val keyboardController = LocalSoftwareKeyboardController.current

    val generalColor = if (userPreferences.isDarkTheme) {
        Color(0xFF2dbdb4)
    } else {
        Color(0xFFfe9d15)
    }

    val drawable = if (userPreferences.isDarkTheme) {
        R.drawable.ic_airplane_cyan
    } else {
        R.drawable.ic_airplane_orange
    }

    val backgroundColor = if (userPreferences.isDarkTheme) {
        Color(0xFF090909)
    } else {
        Color(0xFFFFFFFF)
    }

    val cardColor = if (userPreferences.isDarkTheme) {
        Color(0xFF121A26)
    } else {
        Color(0xFFF5F5F5)
    }

    val textPrimary = if (userPreferences.isDarkTheme) {
        Color(0xFFFFFFFF)
    } else {
        Color(0xFF000000)
    }

    // Squawks d'urgence avec leurs descriptions
    val emergencySquawks = listOf(
        "7500" to "Détournement",
        "7600" to "Panne radio",
        "7700" to "Urgence"
    )

    // Filtrage des vols
    val filteredFlights = flights.filter { flight ->
        val matchesSearch = searchQuery.isBlank() ||
                flight.callsign?.contains(searchQuery, ignoreCase = true) == true ||
                flight.icao24.contains(searchQuery, ignoreCase = true) ||
                flight.originCountry.contains(searchQuery, ignoreCase = true)

        val matchesGroundFilter = filterOnGround == null || flight.onGround == filterOnGround

        val matchesCountry = selectedCountries.isEmpty() || selectedCountries.contains(flight.originCountry)

        val matchesEmergency = selectedEmergencySquawks.isEmpty() ||
                (flight.squawk != null && selectedEmergencySquawks.contains(flight.squawk))

        matchesSearch && matchesGroundFilter && matchesCountry && matchesEmergency
    }

    // Liste des pays avec leur nombre de vols
    val countriesWithCount = flights
        .groupBy { it.originCountry }
        .map { (country, flights) -> country to flights.size }
        .sortedByDescending { it.second }

    // Compteur de vols en urgence
    val emergencyFlightsCount = flights.count {
        it.squawk in listOf("7500", "7600", "7700")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // ═══════════════════════════════════════════════
        // BARRE DE RECHERCHE
        // ═══════════════════════════════════════════════
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF090909))
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Rechercher (callsing...)",
                        color = Color.Gray
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Rechercher",
                        tint = generalColor
                    )
                },
                trailingIcon = {
                    Row {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Effacer",
                                    tint = generalColor
                                )
                            }
                        }
                        IconButton(onClick = { showFilters = !showFilters }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Filtres",
                                tint = if (filterOnGround != null || selectedCountries.isNotEmpty() || selectedEmergencySquawks.isNotEmpty()) {
                                    generalColor
                                } else {
                                    Color.Gray
                                }
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = generalColor,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = textPrimary,
                    unfocusedTextColor = textPrimary,
                    cursorColor = generalColor
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { keyboardController?.hide() }
                )
            )

            // ═══════════════════════════════════════════════
            // SECTION FILTRES (EXPANDABLE)
            // ═══════════════════════════════════════════════
            if (showFilters) {
                Spacer(modifier = Modifier.height(12.dp))

                // ALERTE URGENCES (si des vols en urgence existent)
                if (emergencyFlightsCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFF5252).copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, Color(0xFFFF5252)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFFF5252),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "$emergencyFlightsCount vol(s) en situation d'urgence détecté(s)",
                                fontSize = 12.sp,
                                color = Color(0xFFFF5252),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Filtre Squawk d'Urgence - SUR UNE SEULE LIGNE
                Text(
                    text = "🚨 Codes d'urgence",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    emergencySquawks.forEach { (code, description) ->
                        FilterChip(
                            selected = selectedEmergencySquawks.contains(code),
                            onClick = {
                                selectedEmergencySquawks = if (selectedEmergencySquawks.contains(code)) {
                                    selectedEmergencySquawks - code
                                } else {
                                    selectedEmergencySquawks + code
                                }
                            },
                            label = {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = code,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = description,
                                        fontSize = 9.sp,
                                        color = Color.Gray
                                    )

                                    val count = flights.count { it.squawk == code }
                                    if (count > 0) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFFF5252)
                                        ) {
                                            Text(
                                                text = count.toString(),
                                                fontSize = 9.sp,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFF5252).copy(alpha = 0.2f),
                                selectedLabelColor = Color(0xFFFF5252)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (selectedEmergencySquawks.contains(code)) Color(0xFFFF5252) else Color.Gray
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filtre Au sol / En vol
                Text(
                    text = "État du vol",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = filterOnGround == null,
                        onClick = { filterOnGround = null },
                        label = { Text("Tous") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = generalColor.copy(alpha = 0.3f),
                            selectedLabelColor = generalColor
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (filterOnGround == null) generalColor else Color.Gray
                        )
                    )

                    FilterChip(
                        selected = filterOnGround == false,
                        onClick = { filterOnGround = false },
                        label = { Text("En vol") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = generalColor.copy(alpha = 0.3f),
                            selectedLabelColor = generalColor
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (filterOnGround == false) generalColor else Color.Gray
                        )
                    )

                    FilterChip(
                        selected = filterOnGround == true,
                        onClick = { filterOnGround = true },
                        label = { Text("Au sol") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color.Gray.copy(alpha = 0.3f),
                            selectedLabelColor = Color.Gray
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (filterOnGround == true) Color.Gray else Color.Gray.copy(alpha = 0.5f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filtre Pays
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pays d'origine",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )

                    if (selectedCountries.isNotEmpty()) {
                        Text(
                            text = "${selectedCountries.size} sélectionné(s)",
                            fontSize = 11.sp,
                            color = generalColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Liste horizontale scrollable des pays
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(countriesWithCount) { (country, count) ->
                        FilterChip(
                            selected = selectedCountries.contains(country),
                            onClick = {
                                selectedCountries = if (selectedCountries.contains(country)) {
                                    selectedCountries - country
                                } else {
                                    selectedCountries + country
                                }
                            },
                            label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = country,
                                        fontSize = 12.sp
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (selectedCountries.contains(country)) {
                                            generalColor
                                        } else {
                                            Color.Gray.copy(alpha = 0.3f)
                                        }
                                    ) {
                                        Text(
                                            text = count.toString(),
                                            fontSize = 10.sp,
                                            color = if (selectedCountries.contains(country)) {
                                                Color.White
                                            } else {
                                                Color.Gray
                                            },
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = generalColor.copy(alpha = 0.3f),
                                selectedLabelColor = generalColor
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (selectedCountries.contains(country)) generalColor else Color.Gray
                            )
                        )
                    }
                }

                // Bouton réinitialiser les filtres
                if (filterOnGround != null || selectedCountries.isNotEmpty() || selectedEmergencySquawks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        onClick = {
                            filterOnGround = null
                            selectedCountries = emptySet()
                            selectedEmergencySquawks = emptySet()
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Red.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, Color.Red),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Réinitialiser les filtres",
                                fontSize = 12.sp,
                                color = Color.Red,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Compteur de résultats
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${filteredFlights.size} vol(s) trouvé(s)",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }

        // ═══════════════════════════════════════════════
        // LISTE DES VOLS FILTRÉS
        // ═══════════════════════════════════════════════
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredFlights) { state ->
                var expanded by remember { mutableStateOf(false) }

                val isEmergency = state.squawk in listOf("7500", "7600", "7700")

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .animateContentSize(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(cardColor),
                    border = BorderStroke(
                        2.dp,
                        if (isEmergency) Color(0xFFFF5252) else generalColor
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        // PREMIÈRE LIGNE - Callsign + Badge + Bouton Favori
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Image(
                                    painter = painterResource(id = drawable),
                                    contentDescription = "Aircraft",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .rotate(45f)
                                )

                                Text(
                                    text = state.callsign?.trim() ?: state.icao24,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )

                                // Badge urgence si squawk critique
                                if (isEmergency) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFFFF5252).copy(alpha = 0.2f),
                                        border = BorderStroke(1.dp, Color(0xFFFF5252))
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = null,
                                                tint = Color(0xFFFF5252),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "URGENCE",
                                                fontSize = 10.sp,
                                                color = Color(0xFFFF5252),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (state.onGround) {
                                            Color.Gray.copy(alpha = 0.2f)
                                        } else {
                                            generalColor.copy(alpha = 0.2f)
                                        },
                                        border = BorderStroke(
                                            1.dp,
                                            if (state.onGround) Color.Gray else generalColor
                                        )
                                    ) {
                                        Text(
                                            text = if (state.onGround) "Au sol" else "En vol",
                                            fontSize = 10.sp,
                                            color = if (state.onGround) Color.Gray else generalColor,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            // Vérifier si le vol est déjà dans les favoris
                            val isFavorite = localFlights.any { it.icao24 == state.icao24 }

                            IconButton(
                                onClick = {
                                    if (!isFavorite) {
                                        viewModel.insertFlightToDatabase(state)
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = if (isFavorite) "Déjà dans les favoris" else "Ajouter aux favoris",
                                    tint = if (isFavorite) Color.Red else generalColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // DEUXIÈME LIGNE - Pays + Bouton Expand
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Pays d'origine",
                                    fontSize = 11.sp,
                                    color = Color(0xFFB0BEC5),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = state.originCountry,
                                    fontSize = 14.sp,
                                    color = textPrimary
                                )
                            }

                            IconButton(
                                onClick = { expanded = !expanded },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (expanded) {
                                        Icons.Default.KeyboardArrowUp
                                    } else {
                                        Icons.Default.KeyboardArrowDown
                                    },
                                    contentDescription = if (expanded) "Réduire" else "Développer",
                                    tint = generalColor
                                )
                            }
                        }

                        // SECTION EXPANDABLE - Détails
                        if (expanded) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                DetailRow(
                                    label = "ICAO24",
                                    value = state.icao24,
                                    textColor = Color(0xFF78909C)
                                )

                                DetailRow(
                                    label = "Position",
                                    value = "${state.latitude ?: "N/A"}, ${state.longitude ?: "N/A"}",
                                    textColor = Color(0xFF78909C)
                                )

                                DetailRow(
                                    label = "Altitude baro",
                                    value = "${state.baroAltitude ?: "N/A"} m",
                                    textColor = Color(0xFF78909C)
                                )

                                DetailRow(
                                    label = "Altitude geo",
                                    value = "${state.geoAltitude ?: "N/A"} m",
                                    textColor = Color(0xFF78909C)
                                )

                                DetailRow(
                                    label = "Vitesse",
                                    value = "${state.velocity ?: "N/A"} m/s",
                                    textColor = Color(0xFF78909C)
                                )

                                DetailRow(
                                    label = "Cap",
                                    value = "${state.trueTrack ?: "N/A"}°",
                                    textColor = Color(0xFF78909C)
                                )

                                DetailRow(
                                    label = "Taux vertical",
                                    value = "${state.verticalRate ?: "N/A"} m/s",
                                    textColor = Color(0xFF78909C)
                                )

                                state.squawk?.let {
                                    DetailRow(
                                        label = "Squawk",
                                        value = it,
                                        textColor = if (isEmergency) Color(0xFFFF5252) else Color(0xFF78909C)
                                    )
                                }

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            // définir le vol sélectionné dans le ViewModel
                                            viewModel.selectFlight(state)
                                            // naviguer vers l'écran Radar
                                            onNavigateToRadar()
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    color = generalColor.copy(alpha = 0.1f),
                                    border = BorderStroke(1.dp, generalColor)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.RemoveRedEye,
                                            contentDescription = null,
                                            tint = generalColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Voir en direct",
                                            fontSize = 14.sp,
                                            color = generalColor,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    textColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = textColor.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = textColor
        )
    }
}
