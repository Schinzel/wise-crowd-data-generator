package com.wisecrowd.data_generator.data_collections.customer_country

/**
 * The purpose of this data class is to represent a customer country with its
 * properties including id, name, country code, description, and distribution percentage.
 *
 * Written by Claude Sonnet 4
 */
data class CustomerCountry(
    /** Unique identifier for the customer country */
    val id: Int,
    
    /** Name of the country */
    val name: String,
    
    /** ISO country code (e.g., SE, NO, DK) */
    val countryCode: String,
    
    /** Description of the customer base in this country */
    val description: String,
    
    /** Distribution percentage of customers in this country (0-100) */
    val distributionPercentage: Double
) {
    init {
        require(id > 0) { "Customer country ID must be positive, but was: $id" }
        require(name.isNotBlank()) { "Customer country name cannot be blank" }
        require(countryCode.isNotBlank()) { "Country code cannot be blank" }
        require(description.isNotBlank()) { "Description cannot be blank" }
        require(distributionPercentage >= 0.0 && distributionPercentage <= 100.0) {
            "Distribution percentage must be between 0 and 100, but was: $distributionPercentage"
        }
    }
}