package com.example.flightfinder.models

/**
 * Data class représentant les préférences utilisateur de l'application
 */
data class UserPreferences(
    // Thème de l'application
    val isDarkTheme: Boolean = true,

    // Taille des icônes d'avions sur la carte (facteur multiplicateur)
    val aircraftIconScale: Float = 1.0f, // 0.5 à 2.0

    // Zoom par défaut de la carte
    val defaultMapZoom: Double = 5.0,

    // Position par défaut de la carte (latitude)
    val defaultMapLatitude: Double = 48.8566, // Paris par défaut

    // Position par défaut de la carte (longitude)
    val defaultMapLongitude: Double = 2.3522, // Paris par défaut

    // Afficher les trajectoires des avions
    val showFlightTrails: Boolean = false,

    // Intervalle de rafraîchissement des données (en secondes)
    val refreshIntervalSeconds: Int = 30,

    // Afficher uniquement les avions en vol (masquer ceux au sol)
    val hideGroundedAircraft: Boolean = false,

    // Afficher les labels des avions sur la carte
    val showAircraftLabels: Boolean = false,

    // Activer le rafraîchissement automatique des données
    val isAutoRefreshEnabled: Boolean = false
)

