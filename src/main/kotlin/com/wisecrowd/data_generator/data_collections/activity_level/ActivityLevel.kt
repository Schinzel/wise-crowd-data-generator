package com.wisecrowd.data_generator.data_collections.activity_level

/**
 * The purpose of this data class is to represent an activity level with its
 * properties including id, name, description, and distribution percentage.
 *
 * Written by Claude Sonnet 4
 */
data class ActivityLevel(
    val id: Int,
    val name: String,
    val description: String,
    val distributionPercentage: Double
) {
    init {
        require(id > 0) { "Activity level ID must be positive, but was: $id" }
        require(name.isNotBlank()) { "Activity level name cannot be blank" }
        require(description.isNotBlank()) { "Activity level description cannot be blank" }
        require(distributionPercentage >= 0.0 && distributionPercentage <= 100.0) {
            "Distribution percentage must be between 0 and 100, but was: $distributionPercentage"
        }
    }
}