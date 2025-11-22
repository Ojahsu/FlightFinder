package com.example.flightfinder.utils

/**
 * Utilitaire pour convertir un code pays ISO (ex: "FR", "US") en emoji de drapeau
 */
object CountryFlagEmoji {

    /**
     * Convertit un code pays ISO 3166-1 alpha-2 en emoji de drapeau
     * Exemples:
     * - "FR" → 🇫🇷
     * - "US" → 🇺🇸
     * - "JP" → 🇯🇵
     */
    fun getFlag(countryCode: String): String {
        if (countryCode.length != 2) return "🌍" // Globe par défaut si code invalide

        val upperCode = countryCode.uppercase()

        // Convertir chaque lettre en Regional Indicator Symbol
        // A = U+1F1E6, B = U+1F1E7, etc.
        val firstChar = Character.codePointAt(upperCode, 0)
        val secondChar = Character.codePointAt(upperCode, 1)

        // Si ce n'est pas une lettre A-Z, retourner le globe
        if (firstChar !in 'A'.code..'Z'.code || secondChar !in 'A'.code..'Z'.code) {
            return "🌍"
        }

        // Offset pour Regional Indicator Symbols: U+1F1E6 (🇦) - 'A' = 0x1F1A5
        val offset = 0x1F1E6 - 'A'.code

        val flag1 = Character.toChars(firstChar + offset)
        val flag2 = Character.toChars(secondChar + offset)

        return String(flag1) + String(flag2)
    }

    /**
     * Extrait le code pays depuis originCountry (ex: "France" → "FR")
     * Note: OpenSky API retourne parfois le nom complet du pays
     */
    fun getCountryCode(originCountry: String?): String {
        if (originCountry.isNullOrBlank()) return "UN" // United Nations flag

        // Map des noms de pays vers codes ISO (ajoutez-en selon vos besoins)
        val countryMap = mapOf(
            "France" to "FR",
            "United States" to "US",
            "Germany" to "DE",
            "United Kingdom" to "GB",
            "Spain" to "ES",
            "Italy" to "IT",
            "Canada" to "CA",
            "Japan" to "JP",
            "China" to "CN",
            "Russia" to "RU",
            "Brazil" to "BR",
            "Australia" to "AU",
            "India" to "IN",
            "Mexico" to "MX",
            "Netherlands" to "NL",
            "Switzerland" to "CH",
            "Belgium" to "BE",
            "Austria" to "AT",
            "Sweden" to "SE",
            "Norway" to "NO",
            "Denmark" to "DK",
            "Poland" to "PL",
            "Turkey" to "TR",
            "South Korea" to "KR",
            "Singapore" to "SG",
            "United Arab Emirates" to "AE",
            "Qatar" to "QA",
            "Saudi Arabia" to "SA",
            "Thailand" to "TH",
            "Indonesia" to "ID",
            "Malaysia" to "MY",
            "Philippines" to "PH",
            "Vietnam" to "VN",
            "Argentina" to "AR",
            "Chile" to "CL",
            "Colombia" to "CO",
            "Peru" to "PE",
            "South Africa" to "ZA",
            "Egypt" to "EG",
            "Morocco" to "MA",
            "Israel" to "IL",
            "Greece" to "GR",
            "Portugal" to "PT",
            "Finland" to "FI",
            "Ireland" to "IE",
            "Czech Republic" to "CZ",
            "Hungary" to "HU",
            "Romania" to "RO",
            "New Zealand" to "NZ",
            "Luxembourg" to "LU",
            "Iceland" to "IS",
            "Tunisia" to "TN",
            "Estonia" to "EE",
            "Malta" to "MT",
            "Kingdom of the Netherlands" to "NL"
        )

        // Si c'est déjà un code à 2 lettres, le retourner
        if (originCountry.length == 2) {
            return originCountry.uppercase()
        }

        // Chercher dans la map
        return countryMap[originCountry] ?: "UN"
    }
}

