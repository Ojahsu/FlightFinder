package com.example.flightfinder.vues

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.flightfinder.MainViewmodel
import com.example.flightfinder.R

@Composable
fun Favoris(
    viewModel: MainViewmodel,
    onNavigateToRadar: () -> Unit
){
    val flights = viewModel.localFlights.collectAsState().value
    val userPreferences by viewModel.userPreferences.collectAsState()

    var generalColor = Color(0xFFfe9d15)
    if (userPreferences.isDarkTheme) {
        generalColor = Color(0xFF2dbdb4)
    }

    var drawable = R.drawable.ic_airplane_cyan
    if (!userPreferences.isDarkTheme) {
        drawable = R.drawable.ic_airplane_orange
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(if (userPreferences.isDarkTheme) Color(0xFF090909) else Color(0xFFFFFFFF))
    ) {
        items(flights) { aircraft ->
            // état local d'expansion pour chaque élément
            var expanded by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    if (userPreferences.isDarkTheme) Color(0xFF121A26) else Color(0xFFF5F5F5),
                ),
                border = BorderStroke(2.dp, generalColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp, 0.dp, 10.dp, 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Icône avion
                            Image(
                                painter = painterResource(id = drawable),
                                contentDescription = "Aircraft",
                                modifier = Modifier
                                    .size(24.dp)
                                    .rotate(45f)
                            )
                            // Callsign
                            Text(
                                text = aircraft.plane?.registration ?: "N/A",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (userPreferences.isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF000000)
                            )

                            if (aircraft.plane?.country != null) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = generalColor.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, generalColor)
                                ) {
                                    Text(
                                        text = aircraft.plane.country,
                                        fontSize = 10.sp,
                                        color = generalColor,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        IconButton(onClick = { expanded = !expanded }) {
                            if (expanded) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Ferrmer l'onglet",
                                    tint = generalColor
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Ouvrir l'onglet",
                                    tint = generalColor
                                )
                            }
                        }
                    }

                    if ((aircraft.plane?.manufacturerName != null && aircraft.plane?.manufacturerName != "") ||
                        (aircraft.plane?.model != null && aircraft.plane?.model != "" )){
                        Log.d("Favoris", "Manufacturer: ${aircraft.plane?.manufacturerName}, Model: ${aircraft.plane?.model}")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                            ) {
                                if (aircraft.plane?.manufacturerName != null) {
                                    Text(
                                        text = "" + aircraft.plane?.manufacturerName,
                                        fontSize = 14.sp,
                                        color = Color(0xFFB0BEC5),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                if (aircraft.plane?.model != null) {
                                    Text(
                                        text = "" + aircraft.plane?.model,
                                        fontSize = 13.sp,
                                        color = Color(0xFF78909C)
                                    )
                                }
                            }
                        }
                    }

                    if (expanded) {
                        Spacer(modifier = Modifier.padding(4.dp))
                        // Affiche la photo si elle existe
                        AsyncImage(
                            model = aircraft.photo?.image,
                            contentDescription = "Photo de l'avion",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .border(
                                    width = 1.dp,
                                    color = generalColor,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clip(RoundedCornerShape(12.dp))
                            ,
                            contentScale = ContentScale.Crop
                        )

                        if(viewModel.flightsState.value.any { it.icao24 == aircraft.icao24 })
                        {
                            Spacer(modifier = Modifier.padding(4.dp))

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // définir le vol sélectionné dans le ViewModel
                                        viewModel.selectFlightByIcao(aircraft.icao24)
                                        // naviguer vers l'écran Radar
                                        onNavigateToRadar()
                                    },
                                shape = RoundedCornerShape(8.dp),
                                color = generalColor.copy(alpha = 0.1f),
                                border = BorderStroke(1.dp, generalColor)
                            ) {
                                Row(
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 10.dp
                                    ),
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

                        Spacer(modifier = Modifier.padding(4.dp))

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.deleteFlight(aircraft.id)
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Red.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color.Red)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Supprimer des favoris",
                                    fontSize = 14.sp,
                                    color = Color.Red,
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