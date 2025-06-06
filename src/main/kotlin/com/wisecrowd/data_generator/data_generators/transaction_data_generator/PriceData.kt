package com.wisecrowd.data_generator.data_generators.transaction_data_generator

import java.time.LocalDate
import java.util.*

/**
 * The purpose of this data class is to represent a single price data point
 * for an asset on a specific date with type safety and clear property names.
 *
 * Written by Claude Sonnet 4
 */
data class PriceData(
    /** The unique identifier for the asset */
    val assetId: UUID,
    
    /** The date for this price data point */
    val date: LocalDate,
    
    /** The price of the asset on this date */
    val price: Double
) {
    init {
        require(price > 0.0) { "Price must be positive, but was: $price" }
    }
}