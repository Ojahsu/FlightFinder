# FlightFinder

FlightFinder est une application Android écrite en Kotlin qui affiche les vols en temps réel de façon similaire à Flightradar. L'objectif est de visualiser les positions d'avions, d'afficher les informations de vol et d'offrir des outils d'exploration pour les utilisateurs intéressés par l'aviation.

## Fonctionnalités principales (Avion)
- Carte interactive affichant la position des avions en temps réel.
- Détail d'un vol : compagnie, numéro de vol, origine/destination, altitude, vitesse, image de l'appareil si disponible.
- Recherche par numéro de vol, compagnie ou aéroport.
- Filtrage par altitude, vitesse, type d'appareil.

## Fonctionnalités secondaire (Météo)
- Couches météo sur la carte : radar précipitations animé, nuages par niveau d'altitude, vent (barbes/flux) et couverture de foudre.
- METAR / TAF : récupération et décodage pour les aéroports (affichage lisible, horodatage, tendance).
- Conditions locales instantanées : température, pression, visibilité, humidité, point de rosée, plafond nuageux.
- Intégration au vol : affichage météo le long d'un plan de vol (route-based forecast) et suggestions d'altitude/itinéraire pour éviter zones sévères.


- ### Idées suplémentaire:

    - Filtrage et seuils : permettre de filtrer avions ou zones selon conditions (ex. vent>X, pluie>Y, plafond<Z).
    - Sources recommandées : OpenWeatherMap, Open-Meteo, NOAA, MeteoFrance (selon licence), API METAR/TAF (AVWX), données de foudre/radar via fournisseurs spécialisés.
    - UX / contrôle : bascule des couches, réglage d'opacité, slider temporel, rafraîchissement configurable, clic sur carte pour afficher détail météo pointuel.
    - Performance & robustesse : utilisation de tuiles/raster pour radar, cache local pour limiter appels, gestion des erreurs et fallback si source indisponible.
    - Tests et qualité : parsers METAR/TAF testés, validation des prévisions, indicateurs de fraîcheur des données.

## Installation
1. Ouvrir le projet dans Android Studio.
2. Configurer les clés API (ex. Google Maps / Mapbox / fournisseur de données ADS‑B).
3. Build & Run sur un émulateur ou un appareil réel.

## Architecture suggérée
- Kotlin + Coroutines pour les appels réseau.
- MVVM : ViewModel + LiveData / StateFlow.
- Retrofit pour l'API des vols, Room pour le cache local.
- Map SDK : Google Maps / Mapbox / OpenStreetMap.

## APIs & données
- Utiliser une API ADS‑B publique ou un fournisseur (ex. OpenSky Network, ADS‑B Exchange) pour les positions.
- Mettre en place un cache local pour limiter les appels réseau.

## Confidentialité & limites
- Expliquer les données collectées (aucune info personnelle si non nécessaire).
- Indiquer les limites d'exactitude des données ADS‑B.

## Contribution
- Ouvrir une issue pour proposer une fonctionnalité.
- PRs bienvenues : tests unitaires et documentation attendus.

## Licence
Indiquer la licence du projet (ex. MIT).