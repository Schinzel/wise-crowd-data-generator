package com.wisecrowd.data_generator.data_collections.currency

/**
 * The purpose of this data class is to represent a currency with its
 * properties including id, code, name, distribution percentage, and conversion rate to SEK.
 *
 * Written by Claude Sonnet 4
 */
data class Currency(
    /** Unique identifier for the currency */
    val id: Int,
    /** ISO currency code (e.g., SEK, EUR, USD) */
    val code: String,
    /** Full name of the currency */
    val name: String,
    /** Distribution percentage in the market (0-100) */
    val distributionPercentage: Double,
    /** Conversion rate to SEK */
    val conversionToSek: Double,
) {
    init {
        require(id > 0) { "Currency ID must be positive, but was: $id" }
        require(code.isNotBlank()) { "Currency code cannot be blank" }
        require(name.isNotBlank()) { "Currency name cannot be blank" }
        require(distributionPercentage >= 0.0 && distributionPercentage <= 100.0) {
            "Distribution percentage must be between 0 and 100, but was: $distributionPercentage"
        }
        require(conversionToSek > 0.0) {
            "Conversion to SEK must be positive, but was: $conversionToSek"
        }
    }
}
