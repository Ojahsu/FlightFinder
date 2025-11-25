package com.example.flightfinder.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.flightfinder.models.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Extension pour créer le DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Repository des préférences utilisateur avec DataStore
 */
class UserPreferencesRepository(private val context: Context) {

    // Clés pour DataStore
    private object PreferencesKeys {
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val AIRCRAFT_ICON_SCALE = floatPreferencesKey("aircraft_icon_scale")
        val DEFAULT_MAP_ZOOM = doublePreferencesKey("default_map_zoom")
        val DEFAULT_MAP_LATITUDE = doublePreferencesKey("default_map_latitude")
        val DEFAULT_MAP_LONGITUDE = doublePreferencesKey("default_map_longitude")
        val SHOW_FLIGHT_TRAILS = booleanPreferencesKey("show_flight_trails")
        val REFRESH_INTERVAL_SECONDS = intPreferencesKey("refresh_interval_seconds")
        val HIDE_GROUNDED_AIRCRAFT = booleanPreferencesKey("hide_grounded_aircraft")
        val SHOW_AIRCRAFT_LABELS = booleanPreferencesKey("show_aircraft_labels")
        val IS_AUTO_REFRESH_ENABLED = booleanPreferencesKey("is_auto_refresh_enabled")
    }

    /**
     * Flow qui émet les préférences utilisateur à chaque changement
     */
    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                isDarkTheme = preferences[PreferencesKeys.IS_DARK_THEME] ?: false,
                aircraftIconScale = preferences[PreferencesKeys.AIRCRAFT_ICON_SCALE] ?: 1.0f,
                defaultMapZoom = preferences[PreferencesKeys.DEFAULT_MAP_ZOOM] ?: 5.0,
                defaultMapLatitude = preferences[PreferencesKeys.DEFAULT_MAP_LATITUDE] ?: 48.8566,
                defaultMapLongitude = preferences[PreferencesKeys.DEFAULT_MAP_LONGITUDE] ?: 2.3522,
                showFlightTrails = preferences[PreferencesKeys.SHOW_FLIGHT_TRAILS] ?: false,
                refreshIntervalSeconds = preferences[PreferencesKeys.REFRESH_INTERVAL_SECONDS] ?: 10,
                hideGroundedAircraft = preferences[PreferencesKeys.HIDE_GROUNDED_AIRCRAFT] ?: false,
                showAircraftLabels = preferences[PreferencesKeys.SHOW_AIRCRAFT_LABELS] ?: false,
                isAutoRefreshEnabled = preferences[PreferencesKeys.IS_AUTO_REFRESH_ENABLED] ?: true
            )
        }

    /**
     * Met à jour le thème
     */
    suspend fun updateTheme(isDarkTheme: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME] = isDarkTheme
        }
    }

    /**
     * Met à jour l'échelle des icônes d'avions
     */
    suspend fun updateAircraftIconScale(scale: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AIRCRAFT_ICON_SCALE] = scale.coerceIn(0.5f, 2.0f)
        }
    }

    /**
     * Met à jour le zoom par défaut de la carte
     */
    suspend fun updateDefaultMapZoom(zoom: Double) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_MAP_ZOOM] = zoom.coerceIn(3.0, 18.0)
        }
    }

    /**
     * Met à jour la position par défaut de la carte
     */
    suspend fun updateDefaultMapPosition(latitude: Double, longitude: Double) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_MAP_LATITUDE] = latitude
            preferences[PreferencesKeys.DEFAULT_MAP_LONGITUDE] = longitude
        }
    }

    /**
     * Met à jour l'affichage des trajectoires
     */
    suspend fun updateShowFlightTrails(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_FLIGHT_TRAILS] = show
        }
    }

    /**
     * Met à jour l'intervalle de rafraîchissement
     */
    suspend fun updateRefreshInterval(seconds: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REFRESH_INTERVAL_SECONDS] = seconds.coerceIn(5, 60)
        }
    }

    /**
     * Met à jour le masquage des avions au sol
     */
    suspend fun updateHideGroundedAircraft(hide: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HIDE_GROUNDED_AIRCRAFT] = hide
        }
    }

    /**
     * Met à jour l'affichage des labels
     */
    suspend fun updateShowAircraftLabels(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_AIRCRAFT_LABELS] = show
        }
    }

    /**
     * Réinitialise toutes les préférences aux valeurs par défaut
     */
    suspend fun resetToDefaults() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Met à jour l'activation du rafraîchissement automatique
     */
    suspend fun updateIsAutoRefreshEnabled(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_AUTO_REFRESH_ENABLED] = isEnabled
        }
    }
}

