package com.wisecrowd.data_generator.data_collections.utils

/**
 * The purpose of this object is to provide utility functions for filtering collections
 * based on distribution percentages, eliminating code duplication across collection classes.
 *
 * Written by Claude
 */
object DistributionUtils {
    
    /**
     * Filters a collection of items based on their distribution percentage within a specified range.
     *
     * @param items The collection of items to filter
     * @param minPercentage The minimum percentage (inclusive), must be between 0 and 100
     * @param maxPercentage The maximum percentage (inclusive), must be between 0 and 100
     * @param distributionSelector Function that extracts the distribution percentage from each item
     * @return List of items whose distribution percentage falls within the specified range
     * @throws IllegalArgumentException if percentage parameters are invalid
     */
    fun <T> filterByDistribution(
        items: Collection<T>,
        minPercentage: Double,
        maxPercentage: Double,
        distributionSelector: (T) -> Double
    ): List<T> {
        validatePercentageRange(minPercentage, maxPercentage)
        return items.filter { distributionSelector(it) in minPercentage..maxPercentage }
    }
    
    /**
     * Validates that percentage parameters are within valid ranges and properly ordered.
     *
     * @param minPercentage The minimum percentage to validate
     * @param maxPercentage The maximum percentage to validate
     * @throws IllegalArgumentException if parameters are invalid
     */
    private fun validatePercentageRange(minPercentage: Double, maxPercentage: Double) {
        require(minPercentage >= 0.0 && minPercentage <= 100.0) {
            "Min percentage must be between 0 and 100, but was: $minPercentage"
        }
        require(maxPercentage >= 0.0 && maxPercentage <= 100.0) {
            "Max percentage must be between 0 and 100, but was: $maxPercentage"
        }
        require(minPercentage <= maxPercentage) {
            "Min percentage ($minPercentage) must be less than or equal to max percentage ($maxPercentage)"
        }
    }
}
