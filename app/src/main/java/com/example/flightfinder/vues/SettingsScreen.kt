package com.example.flightfinder.vues

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flightfinder.MainViewmodel
import com.example.flightfinder.repository.UserPreferencesRepository
import com.example.flightfinder.models.UserPreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewmodel) {
    val context = LocalContext.current
    val preferencesRepository = remember { UserPreferencesRepository(context) }
    val preferences by preferencesRepository.userPreferencesFlow.collectAsState(initial = UserPreferences())
    val scope = rememberCoroutineScope()

    val generalColor = if (preferences.isDarkTheme) {
        Color(0xFF2dbdb4)
    } else {
        Color(0xFFfe9d15)
    }

    val backgroundColor = if (preferences.isDarkTheme) {
        Color(0xFF090909)
    } else {
        Color(0xFFFFFFFF)
    }

    val cardColor = if (preferences.isDarkTheme) {
        Color(0xFF121A26)
    } else {
        Color(0xFFF5F5F5)
    }

    val textPrimary = if (preferences.isDarkTheme) {
        Color(0xFFFFFFFF)
    } else {
        Color(0xFF000000)
    }

    val textSecondary = if (preferences.isDarkTheme) {
        Color(0xFFB0BEC5)
    } else {
        Color(0xFF78909C)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ═══════════════════════════════════════════════
        // HEADER
        // ═══════════════════════════════════════════════
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = generalColor.copy(alpha = 0.2f)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = generalColor,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(12.dp)
                )
            }
            Column {
                Text(
                    text = "Paramètres",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Text(
                    text = "Personnalisez votre expérience",
                    fontSize = 14.sp,
                    color = textSecondary
                )
            }
        }

        SettingsSection(
            title = "Apparence",
            icon = Icons.Default.Palette,
            iconColor = generalColor,
            cardColor = cardColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        ) {
            ModernSettingsSwitch(
                title = "Thème sombre",
                subtitle = "Activer le mode sombre",
                icon = if (preferences.isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                checked = preferences.isDarkTheme,
                onCheckedChange = {
                    scope.launch {
                        preferencesRepository.updateTheme(it)
                    }
                },
                generalColor = generalColor,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                isDarkTheme = preferences.isDarkTheme
            )
        }

        SettingsSection(
            title = "Carte",
            icon = Icons.Default.Map,
            iconColor = generalColor,
            cardColor = cardColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        ) {
            ModernSettingsSlider(
                title = "Taille des icônes d'avions",
                subtitle = "Ajuster la taille des avions sur la carte",
                icon = Icons.Default.AirplanemodeActive,
                value = preferences.aircraftIconScale,
                valueRange = 0.5f..2.0f,
                steps = 14,
                onValueChange = {
                    scope.launch {
                        preferencesRepository.updateAircraftIconScale(it)
                    }
                },
                valueLabel = { "${(it * 100).toInt()}%" },
                generalColor = generalColor,
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            ModernSettingsSwitch(
                title = "Masquer les avions au sol",
                subtitle = "N'afficher que les avions en vol",
                icon = Icons.Default.Flight,
                checked = preferences.hideGroundedAircraft,
                onCheckedChange = {
                    scope.launch {
                        preferencesRepository.updateHideGroundedAircraft(it)
                    }
                },
                generalColor = generalColor,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                isDarkTheme = preferences.isDarkTheme
            )

            Spacer(modifier = Modifier.height(12.dp))

            ModernSettingsSwitch(
                title = "Afficher les labels",
                subtitle = "Montrer les indicatifs d'appel sur la carte",
                icon = Icons.Default.Label,
                checked = preferences.showAircraftLabels,
                onCheckedChange = {
                    scope.launch {
                        preferencesRepository.updateShowAircraftLabels(it)
                    }
                },
                generalColor = generalColor,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                enabled = false,
                isDarkTheme = preferences.isDarkTheme
            )

            Spacer(modifier = Modifier.height(12.dp))

            ModernSettingsSwitch(
                title = "Afficher les trajectoires",
                subtitle = "Tracer le chemin des avions (expérimental)",
                icon = Icons.Default.Timeline,
                checked = preferences.showFlightTrails,
                onCheckedChange = {
                    scope.launch {
                        preferencesRepository.updateShowFlightTrails(it)
                    }
                },
                generalColor = generalColor,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                enabled = false,
                isDarkTheme = preferences.isDarkTheme
            )
        }

        SettingsSection(
            title = "Rafraîchissement",
            icon = Icons.Default.Sync,
            iconColor = generalColor,
            cardColor = cardColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        ) {
            ModernSettingsSlider(
                title = "Intervalle de mise à jour",
                subtitle = "Fréquence de rafraîchissement des données",
                icon = Icons.Default.Update,
                value = preferences.refreshIntervalSeconds.toFloat(),
                valueRange = 5f..60f,
                steps = 10,
                onValueChange = {
                    scope.launch {
                        preferencesRepository.updateRefreshInterval(it.toInt())
                    }
                },
                valueLabel = { "${it.toInt()}s" },
                generalColor = generalColor,
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        DangerButton(
            text = "Réinitialiser les paramètres",
            icon = Icons.Default.RestartAlt,
            onClick = {
                scope.launch {
                    preferencesRepository.resetToDefaults()
                }
            },
            cardColor = cardColor,
            generalColor = generalColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )

        DangerButton(
            text = "Vider la base de données",
            icon = Icons.Default.DeleteForever,
            onClick = {
                viewModel.clearDatabase()
            },
            cardColor = cardColor,
            generalColor = generalColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )

        // Footer
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "FlightFinder v1.0 • Tous les paramètres sont sauvegardés automatiquement",
            fontSize = 11.sp,
            color = textSecondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    cardColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Header de section
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
        }

        // Card conteneur
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                content = content
            )
        }
    }
}

@Composable
fun ModernSettingsSwitch(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    generalColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    enabled: Boolean = true,
    isDarkTheme: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = generalColor.copy(alpha = 0.15f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = generalColor,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(8.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = textSecondary
                )
            }
        }

        if (isDarkTheme) {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = generalColor,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray.copy(alpha = 0.5f),
                    uncheckedBorderColor = Color.LightGray,
                    disabledCheckedThumbColor = Color.LightGray.copy(alpha = 0.1f),
                    disabledUncheckedThumbColor = Color.LightGray.copy(alpha = 0.1f),
                    disabledCheckedTrackColor = Color.LightGray.copy(alpha = 0.1f),
                    disabledUncheckedTrackColor = Color.LightGray.copy(alpha = 0.1f),
                    disabledCheckedBorderColor = Color.LightGray.copy(alpha = 0.1f),
                    disabledUncheckedBorderColor = Color.LightGray.copy(alpha = 0.1f)
                ),
                enabled = enabled
            )
        } else {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = generalColor,
                    uncheckedThumbColor = Color.Gray.copy(alpha = 0.5f),
                    uncheckedTrackColor = Color.LightGray.copy(alpha = 0.3f),
                    uncheckedBorderColor = Color.Gray.copy(alpha = 0.5f),
                    disabledCheckedThumbColor = Color.LightGray,
                    disabledUncheckedThumbColor = Color.LightGray,
                    disabledCheckedBorderColor = Color.LightGray,
                    disabledCheckedTrackColor = Color.Gray.copy(alpha = 0.1f),
                    disabledUncheckedTrackColor = Color.Gray.copy(alpha = 0.1f),
                    disabledUncheckedBorderColor = Color.Gray.copy(alpha = 0.1f)
                ),
                enabled = enabled
            )
        }
    }
}

@Composable
fun ModernSettingsSlider(
    title: String,
    subtitle: String,
    icon: ImageVector,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit,
    valueLabel: (Float) -> String,
    generalColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = generalColor.copy(alpha = 0.15f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = generalColor,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(8.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = textPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = textSecondary
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = generalColor.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, generalColor)
            ) {
                Text(
                    text = valueLabel(value),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = generalColor,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = generalColor,
                activeTrackColor = generalColor,
                inactiveTrackColor = generalColor.copy(alpha = 0.3f),
                activeTickColor = Color.White,
                inactiveTickColor = generalColor.copy(alpha = 0.3f)
            ),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
fun DangerButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    cardColor: Color,
    generalColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    var showDialog by remember { mutableStateOf(false) }

    Surface(
        onClick = { showDialog = true },
        shape = RoundedCornerShape(12.dp),
        color = cardColor,
        border = BorderStroke(2.dp, Color(0xFFFF5252)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFFF5252).copy(alpha = 0.15f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFFF5252),
                    modifier = Modifier
                        .size(40.dp)
                        .padding(8.dp)
                )
            }

            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFFF5252)
            )
        }
    }

    // Dialog de confirmation
    if (showDialog) {
        AlertDialog(
            containerColor = cardColor,
            titleContentColor = textPrimary,
            onDismissRequest = { showDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF5252),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Confirmation",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Êtes-vous sûr de vouloir effectuer cette action ? Cette opération est irréversible.",
                    color = textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClick()
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5252)
                    )
                ) {
                    Text("Confirmer")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (textPrimary == Color.Black) Color.Gray else Color.LightGray
                    )
                ) {
                    Text("Annuler")
                }
            }
        )
    }
}
