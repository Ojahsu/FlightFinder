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
fun resizeDrawable(drawable: Drawable, width: Int, height: Int): BitmapDrawable {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return BitmapDrawable(null, Bitmap.createScaledBitmap(bitmap, width, height, true))
}

@Composable
fun Radar(
    flights: List<States>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

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

            flights.forEach { flight ->
                if (flight.hasValidPosition()) {
                    val marker = Marker(mapView).apply {
                        position = GeoPoint(flight.latitude!!, flight.longitude!!)
                        title = flight.callsign?.trim() ?: "Vol inconnu"
                        snippet = buildString {
                            append("🌍 ${flight.originCountry}\n")
                            flight.getAltitudeInFeet()?.let {
                                append("✈️ Altitude: $it ft\n")
                            }
                            flight.getVelocityInKnots()?.let {
                                append("⚡ Vitesse: $it kts\n")
                            }
                            if (flight.onGround) {
                                append("🛬 Au sol")
                            }
                        }

                        try {
                            val drawable = ContextCompat.getDrawable(mapView.context, R.drawable.ic_airplane)
                            drawable?.let {
                                // 🎯 TAILLE DE L'ICÔNE (changez ces valeurs)
                                val iconSize = 55 // Testez 20, 30, 40, 50...

                                icon = resizeDrawable(it, iconSize, iconSize)
                            }

                            // Rotation selon direction
                            flight.trueTrack?.let { track ->
                                rotation = track.toFloat()
                            }

                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
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
