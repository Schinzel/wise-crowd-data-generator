package com.wisecrowd.data_generator.utils

/**
 * The purpose of this data class is to represent an item with its associated percentage
 * for use in weighted random selection algorithms.
 *
 * The percent represents the distribution percentage (e.g., 25.0 for 25%).
 *
 * Written by Claude Sonnet 4
 */
data class WeightedItem<T>(
    /** The item to be selected */
    val item: T,
    
    /** The percentage for selection (e.g., 25.0 for 25%) */
    val percent: Double
) {
    init {
        require(percent.isFinite()) { 
            "Percent must be a finite number, but was: $percent" 
        }
        require(percent >= 0.0) { 
            "Percent must be non-negative, but was: $percent" 
        }
        require(percent <= 100.0) { 
            "Percent must be at most 100.0, but was: $percent" 
        }
    }
}
