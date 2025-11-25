package com.example.flightfinder.utils

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.example.flightfinder.MainViewmodel
import com.example.flightfinder.models.States
import com.example.flightfinder.vues.AircraftInfoPopup
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

/**
 * Adaptateur qui fait le pont entre osmdroid (View Android classique)
 * et notre Composable AircraftInfoPopup
 */
class AircraftInfoWindowAdapter(mapView: MapView, val viewModel: MainViewmodel) : InfoWindow(createComposeView(mapView), mapView) {

    companion object {
        private fun createComposeView(mapView: MapView): ComposeView {
            return ComposeView(mapView.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }
    }

    override fun onOpen(item: Any?) {
        val marker = item as? Marker ?: return
        val flight = marker.relatedObject as? States ?: return

        val composeView = mView as? ComposeView ?: return

        composeView.setContent {
            AircraftInfoPopup(flight = flight, viewModel = viewModel)
        }
    }

    override fun onClose() {
        // nothing special
    }
}