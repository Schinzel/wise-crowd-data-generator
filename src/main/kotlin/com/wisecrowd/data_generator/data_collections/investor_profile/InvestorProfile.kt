package com.wisecrowd.data_generator.data_collections.investor_profile

/**
 * The purpose of this data class is to represent an investor profile with its
 * properties including id, name, description, and distribution percentage.
 *
 * Written by Claude Sonnet 4
 */
data class InvestorProfile(
    /** Unique identifier for the investor profile */
    val id: Int,
    
    /** Name of the investor profile */
    val name: String,
    
    /** Description of the investment strategy */
    val description: String,
    
    /** Distribution percentage in the market (0-100) */
    val distributionPercentage: Double
) {
    init {
        require(id > 0) { "Investor profile ID must be positive, but was: $id" }
        require(name.isNotBlank()) { "Investor profile name cannot be blank" }
        require(description.isNotBlank()) { "Investor profile description cannot be blank" }
        require(distributionPercentage >= 0.0 && distributionPercentage <= 100.0) {
            "Distribution percentage must be between 0 and 100, but was: $distributionPercentage"
        }
    }
}