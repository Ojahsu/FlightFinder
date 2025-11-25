package com.example.flightfinder.vues

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.flightfinder.MainViewmodel
import com.example.flightfinder.R
import com.example.flightfinder.models.States
import com.example.flightfinder.utils.AircraftInfoWindowAdapter
import kotlinx.coroutines.delay
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

// ═══════════════════════════════════════════════════
// CONFIGURATION DES TUILES DE CARTE
// ═══════════════════════════════════════════════════

val DARK_MAP_TILE_SOURCE = XYTileSource(
    "CartoDarkMatter",
    0, 19, 256, ".png",
    arrayOf(
        "https://a.basemaps.cartocdn.com/dark_all/",
        "https://b.basemaps.cartocdn.com/dark_all/",
        "https://c.basemaps.cartocdn.com/dark_all/",
        "https://d.basemaps.cartocdn.com/dark_all/"
    ),
    "© OpenStreetMap contributors © CARTO"
)

val LIGHT_MAP_TILE_SOURCE = XYTileSource(
    "CartoCDBPositron",
    0, 19, 256, ".png",
    arrayOf(
        "https://a.basemaps.cartocdn.com/light_all/",
        "https://b.basemaps.cartocdn.com/light_all/",
        "https://c.basemaps.cartocdn.com/light_all/"
    ),
    "© OpenStreetMap contributors © CARTO"
)

// ═══════════════════════════════════════════════════
// UTILITAIRES
// ═══════════════════════════════════════════════════

fun resizeDrawable(
    drawable: Drawable,
    width: Int,
    height: Int,
    contextAvailable: Boolean = true
): BitmapDrawable {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    val scaled = Bitmap.createScaledBitmap(bitmap, width, height, true)
    return BitmapDrawable(null, scaled)
}

// ═══════════════════════════════════════════════════
// COMPOSABLE PRINCIPAL RADAR
// ═══════════════════════════════════════════════════

@Composable
fun Radar(
    flights: List<States>,
    modifier: Modifier = Modifier,
    viewModel: MainViewmodel
) {
    val context = LocalContext.current
    val userPreferences by viewModel.userPreferences.collectAsState()
    val localFlights by viewModel.localFlights.collectAsState()

    val favoriteIcao24Set = remember(localFlights) {
        localFlights.mapNotNull { it.plane?.icao24 }.toSet()
    }

    // Cache des icônes pour optimisation
    val iconCache = remember { mutableMapOf<Int, BitmapDrawable>() }

    // État pour les statistiques de la carte
    var visibleFlights by remember { mutableStateOf(0) }

    // Référence à la MapView pour contrôler le zoom
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }

    var isInfoExpanded by remember { mutableStateOf(false) }

    var refreshProgress by remember { mutableStateOf(0f) }
    val refreshInterval = (userPreferences.refreshIntervalSeconds * 1000).toLong() // en ms

    val selectedFlight by viewModel.selectedFlight.collectAsState()

    LaunchedEffect(selectedFlight?.icao24) {
        val flight = selectedFlight
        if (flight?.hasValidPosition() == true && mapViewRef != null) {
            val map = mapViewRef!!
            // Attendre un court instant pour s'assurer que les overlays sont mis à jour
            kotlinx.coroutines.delay(50)
            // Chercher le marker correspondant
            val marker = map.overlays
                .filterIsInstance<org.osmdroid.views.overlay.Marker>()
                .firstOrNull { m ->
                    val related = m.relatedObject
                    if (related is States) {
                        related.icao24 == flight.icao24
                    } else {
                        val pos = m.position
                        val latMatch = pos.latitude == flight.latitude
                        val lonMatch = pos.longitude == flight.longitude
                        latMatch && lonMatch
                    }
                }

            marker?.let {
                map.controller.animateTo(it.position)
                if (map.zoomLevelDouble < 8.0) {
                    map.controller.setZoom(8.0)
                }
                it.showInfoWindow()
                map.invalidate()
            }
        }
    }

    // Animation de la progress bar
    LaunchedEffect(refreshInterval) {
        while (true) {
            val startTime = System.currentTimeMillis()
            while (true) {
                val elapsed = System.currentTimeMillis() - startTime
                refreshProgress = (elapsed.toFloat() / refreshInterval).coerceIn(0f, 1f)

                if (elapsed >= refreshInterval) {
                    refreshProgress = 0f
                    break
                }

                delay(50)
            }
        }
    }

    val generalColor = if (userPreferences.isDarkTheme) {
        Color(0xFF2dbdb4)
    } else {
        Color(0xFFfe9d15)
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

    val textSecondary = if (userPreferences.isDarkTheme) {
        Color(0xFFB0BEC5)
    } else {
        Color(0xFF78909C)
    }

    DisposableEffect(Unit) {
        Configuration.getInstance().apply {
            userAgentValue = context.packageName
            osmdroidBasePath = context.filesDir
            osmdroidTileCache = context.cacheDir
        }
        onDispose { }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setBuiltInZoomControls(false)
                    setMultiTouchControls(true)

                    // Style de carte selon le thème
                    setTileSource(
                        if (userPreferences.isDarkTheme) {
                            DARK_MAP_TILE_SOURCE
                        } else {
                            LIGHT_MAP_TILE_SOURCE
                        }
                    )

                    // Configuration de base
                    controller.setZoom(userPreferences.defaultMapZoom)
                    controller.setCenter(
                        GeoPoint(
                            userPreferences.defaultMapLatitude,
                            userPreferences.defaultMapLongitude
                        )
                    )
                    isTilesScaledToDpi = true
                    minZoomLevel = 3.0
                    maxZoomLevel = 18.0

                    // Sauvegarder la référence
                    mapViewRef = this
                }
            },
            update = { mapView ->
                // Ne changer le style que si le thème a réellement changé
                val currentTileSource = if (userPreferences.isDarkTheme) {
                    DARK_MAP_TILE_SOURCE
                } else {
                    LIGHT_MAP_TILE_SOURCE
                }

                if (mapView.tileProvider.tileSource != currentTileSource) {
                    mapView.setTileSource(currentTileSource)
                }

                // Nettoyage des overlays existants
                mapView.overlays.clear()

                // ✅ AJOUT : Overlay pour capturer les clics sur la carte
                val mapEventsReceiver = object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        // Fermer toutes les InfoWindows ouvertes
                        mapView.overlays
                            .filterIsInstance<Marker>()
                            .forEach { marker ->
                                marker.closeInfoWindow()
                            }

                        // Effacer la sélection dans le ViewModel
                        viewModel.clearSelectedFlight()

                        mapView.invalidate()
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint?): Boolean {
                        return false
                    }
                }

                val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
                mapView.overlays.add(mapEventsOverlay)

                // Adapter pour les info windows
                val infoAdapter = AircraftInfoWindowAdapter(mapView, viewModel)

                // Filtrage des vols
                val filteredFlights = if (userPreferences.hideGroundedAircraft) {
                    flights.filter { it.onGround == false }
                } else {
                    flights
                }

                // Compteur pour les statistiques
                var count = 0

                // Ajout des markers pour chaque vol
                filteredFlights.forEach { flight ->
                    if (flight.hasValidPosition()) {
                        count++

                        val isInFavoris = flight.icao24 in favoriteIcao24Set

                        val marker = Marker(mapView).apply {
                            position = GeoPoint(flight.latitude!!, flight.longitude!!)

                            title = if (userPreferences.showAircraftLabels) {
                                flight.callsign?.trim() ?: "Vol inconnu"
                            } else {
                                ""
                            }

                            relatedObject = flight

                            flight.trueTrack?.let { track ->
                                rotation = (track - 90f).toFloat()
                            }

                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            infoWindow = infoAdapter

                            try {
                                val drawableRes = when {
                                    isInFavoris -> R.drawable.ic_airplane_rouge
                                    userPreferences.isDarkTheme -> R.drawable.ic_airplane_cyan
                                    else -> R.drawable.ic_airplane_orange
                                }

                                val drawable = ContextCompat.getDrawable(mapView.context, drawableRes)

                                drawable?.let { d ->
                                    val baseSize = 75 * userPreferences.aircraftIconScale
                                    val iconSize = baseSize.toInt().coerceIn(24, 200)
                                    val cacheKey = iconSize * 10000 + drawableRes

                                    val cachedIcon = iconCache[cacheKey] ?: run {
                                        val resized = resizeDrawable(d, iconSize, iconSize)
                                        iconCache[cacheKey] = resized
                                        resized
                                    }

                                    icon = cachedIcon
                                }
                            } catch (e: Exception) {
                                Log.e("Radar", "Erreur chargement icône", e)
                            }
                        }

                        mapView.overlays.add(marker)
                    }
                }

                visibleFlights = count

                // Utiliser postInvalidate() au lieu de invalidate()
                mapView.postInvalidate()
            }
        )

        // ═══════════════════════════════════════════════
        // PANNEAU D'INFORMATIONS (Overlay en haut à gauche)
        // ═══════════════════════════════════════════════
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = cardColor.copy(alpha = 0.95f),
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    generalColor.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // EN-TÊTE AVEC BOUTON TOGGLE
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = generalColor.copy(alpha = 0.2f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Radar,
                                    contentDescription = null,
                                    tint = generalColor,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(8.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Radar en direct",
                                    fontSize = 12.sp,
                                    color = textSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Flight,
                                        contentDescription = null,
                                        tint = generalColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "$visibleFlights vols",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = { isInfoExpanded = !isInfoExpanded },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isInfoExpanded) {
                                    Icons.Default.KeyboardArrowUp
                                } else {
                                    Icons.Default.KeyboardArrowDown
                                },
                                contentDescription = if (isInfoExpanded) {
                                    "Masquer les détails"
                                } else {
                                    "Afficher les détails"
                                },
                                tint = textSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // PROGRESS BAR
                    if (isInfoExpanded) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = textSecondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "Prochain rafraîchissement",
                                        fontSize = 10.sp,
                                        color = textSecondary
                                    )
                                }
                                if (userPreferences.isAutoRefreshEnabled) {
                                    Text(
                                        text = "${((1 - refreshProgress) * userPreferences.refreshIntervalSeconds).toInt()}s",
                                        fontSize = 10.sp,
                                        color = generalColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
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
                                                imageVector = Icons.Default.Cancel,
                                                contentDescription = null,
                                                tint = Color(0xFFFF5252),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "Auto-rafraîchissement désactivé",
                                                fontSize = 10.sp,
                                                color = Color(0xFFFF5252),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            if (userPreferences.isAutoRefreshEnabled) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(textSecondary.copy(alpha = 0.2f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(refreshProgress)
                                            .fillMaxHeight()
                                            .background(generalColor)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ═══════════════════════════════════════════════
        // BOUTONS CONTRÔLE ZOOM
        // ═══════════════════════════════════════════════
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FloatingActionButton(
                onClick = {
                    mapViewRef?.let { map ->
                        map.controller.zoomOut()
                    }
                },
                containerColor = cardColor.copy(alpha = 0.95f),
                contentColor = generalColor,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Zoom arrière",
                    modifier = Modifier.size(24.dp)
                )
            }

            FloatingActionButton(
                onClick = {
                    mapViewRef?.let { map ->
                        map.controller.zoomIn()
                    }
                },
                containerColor = cardColor.copy(alpha = 0.95f),
                contentColor = generalColor,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Zoom avant",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
