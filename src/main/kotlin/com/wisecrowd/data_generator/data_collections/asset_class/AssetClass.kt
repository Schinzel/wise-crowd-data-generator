package com.wisecrowd.data_generator.data_collections.asset_class

/**
 * The purpose of this class is to represent a single asset class with
 * its properties including ID, name, description, volatility level and prevalence.
 *
 * Written by Claude 3.7 Sonnet
 */
data class AssetClass(
    /** Unique identifier for the asset class */
    val id: Int,
    
    /** Name of the asset class */
    val name: String,
    
    /** Description of the asset class */
    val description: String,
    
    /** Volatility level (Low, Medium, High, Very High) */
    val volatilityLevel: VolatilityLevelEnum,
    
    /** Prevalence percentage in the market (0-100) */
    val prevalencePercentage: Int
) {
    init {
        require(id > 0) { "Asset class ID must be positive, but was: $id" }
        require(name.isNotBlank()) { "Asset class name cannot be blank" }
        require(description.isNotBlank()) { "Asset class description cannot be blank" }
        require(prevalencePercentage in 0..100) {
            "Prevalence percentage must be between 0 and 100, but was: $prevalencePercentage"
        }
    }
}