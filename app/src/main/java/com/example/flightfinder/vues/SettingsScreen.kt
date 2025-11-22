package com.example.flightfinder.vues

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.flightfinder.repository.UserPreferencesRepository
import com.example.flightfinder.models.UserPreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val preferencesRepository = remember { UserPreferencesRepository(context) }
    val preferences by preferencesRepository.userPreferencesFlow.collectAsState(initial = UserPreferences())
    val scope = rememberCoroutineScope()

    Scaffold(

    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section Apparence
            SettingsSectionHeader("🎨 Apparence")

            SettingsSwitch(
                title = "Thème sombre",
                subtitle = "Activer le mode sombre",
                checked = preferences.isDarkTheme,
                onCheckedChange = {
                    scope.launch {
                        preferencesRepository.updateTheme(it)
                    }
                }
            )

            HorizontalDivider()

            // Section Carte
            SettingsSectionHeader("🗺️ Carte")

            SettingsSlider(
                title = "Taille des icônes d'avions",
                subtitle = "Ajuster la taille des avions sur la carte",
                value = preferences.aircraftIconScale,
                valueRange = 0.5f..2.0f,
                steps = 14, // 0.5, 0.6, 0.7, ... 2.0
                onValueChange = {
                    scope.launch {
                        preferencesRepository.updateAircraftIconScale(it)
                    }
                },
                valueLabel = { "${(it * 100).toInt()}%" }
            )

            SettingsSwitch(
                title = "Afficher les labels",
                subtitle = "Montrer les indicatifs d'appel sur la carte",
                checked = preferences.showAircraftLabels,
                onCheckedChange = {
                    scope.launch {
                        preferencesRepository.updateShowAircraftLabels(it)
                    }
                }
            )

            SettingsSwitch(
                title = "Masquer les avions au sol",
                subtitle = "N'afficher que les avions en vol",
                checked = preferences.hideGroundedAircraft,
                onCheckedChange = {
                    scope.launch {
                        preferencesRepository.updateHideGroundedAircraft(it)
                    }
                }
            )

            SettingsSwitch(
                title = "Afficher les trajectoires",
                subtitle = "Tracer le chemin des avions (expérimental)",
                checked = preferences.showFlightTrails,
                onCheckedChange = {
                    scope.launch {
                        preferencesRepository.updateShowFlightTrails(it)
                    }
                }
            )

            HorizontalDivider()

            // Section Données
            SettingsSectionHeader("🔄 Rafraîchissement")

            SettingsSlider(
                title = "Intervalle de mise à jour",
                subtitle = "Fréquence de rafraîchissement des données",
                value = preferences.refreshIntervalSeconds.toFloat(),
                valueRange = 5f..60f,
                steps = 10,
                onValueChange = {
                    scope.launch {
                        preferencesRepository.updateRefreshInterval(it.toInt())
                    }
                },
                valueLabel = { "${it.toInt()}s" }
            )

            HorizontalDivider()

            // Bouton Reset
            Button(
                onClick = {
                    scope.launch {
                        preferencesRepository.resetToDefaults()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Réinitialiser aux valeurs par défaut")
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingsSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingsSlider(
    title: String,
    subtitle: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit,
    valueLabel: (Float) -> String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = valueLabel(value),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps
        )
    }
}

