package com.example.flightfinder.vues

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.flightfinder.R
import com.example.flightfinder.models.States
import com.example.flightfinder.utils.AircraftInfoWindowAdapter
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

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

// 🎯 FONCTION POUR REDIMENSIONNER L'ICÔNE
fun resizeDrawable(drawable: Drawable, width: Int, height: Int, contextAvailable: Boolean = true): BitmapDrawable {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    val scaled = Bitmap.createScaledBitmap(bitmap, width, height, true)
    return if (contextAvailable) BitmapDrawable(null, scaled) else BitmapDrawable(null, scaled)
}

@Composable
fun Radar(
    flights: List<States>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Cache des icônes redimensionnées par taille pour améliorer les performances
    val iconCache = remember { mutableMapOf<Int, BitmapDrawable>() }

    DisposableEffect(Unit) {
        Configuration.getInstance().apply {
            userAgentValue = context.packageName
            osmdroidBasePath = context.filesDir
            osmdroidTileCache = context.cacheDir
        }
        onDispose { }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(DARK_MAP_TILE_SOURCE)
                setMultiTouchControls(true)
                controller.setZoom(5.0)
                controller.setCenter(GeoPoint(48.8566, 2.3522))
                isTilesScaledToDpi = true
                minZoomLevel = 3.0
                maxZoomLevel = 18.0
            }
        },
        update = { mapView ->
            mapView.overlays.clear()

            // prepare adapter once
            val infoAdapter = AircraftInfoWindowAdapter(mapView)

            flights.forEach { flight ->
                if (flight.hasValidPosition()) {
                    val marker = Marker(mapView).apply {
                        position = GeoPoint(flight.latitude!!, flight.longitude!!)
                        title = flight.callsign?.trim() ?: "Vol inconnu"

                        // stocker l'objet lié pour l'info window
                        relatedObject = flight

                        // Rotation selon direction
                        flight.trueTrack?.let { track ->
                            rotation = track.toFloat()
                        }

                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

                        // use custom InfoWindow
                        infoWindow = infoAdapter

                        try {
                            val drawable = ContextCompat.getDrawable(mapView.context, R.drawable.ic_airplane)
                            drawable?.let { d ->
                                // calculate icon size based on current zoom
                                val zoom = mapView.zoomLevelDouble
                                val base = 40
                                val scaleFactor = (zoom / 5.0)
                                val iconSize = (base * scaleFactor).toInt().coerceIn(24, 200)

                                val cached = iconCache[iconSize] ?: run {
                                    val resized = resizeDrawable(d, iconSize, iconSize)
                                    iconCache[iconSize] = resized
                                    resized
                                }

                                icon = cached
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("Radar", "Erreur chargement icône", e)
                        }
                    }

                    mapView.overlays.add(marker)
                }
            }

            mapView.invalidate()
        }
    )
}
