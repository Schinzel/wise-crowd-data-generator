package com.wisecrowd.data_generator.data_generators.transaction_data_generator

import java.time.LocalDate
import java.util.*

/**
 * The purpose of this class is to provide efficient access to price data
 * organized by asset with methods for date-based lookups and filtering.
 *
 * This class replaces complex Map<UUID, List<Triple<...>>> structures with
 * a clean, readable interface for price data operations.
 *
 * Written by Claude Sonnet 4
 */
class AssetPriceCollection(priceData: List<PriceData>) {
    
    // Group and sort price data by asset and date for efficient access
    private val pricesByAsset: Map<UUID, List<PriceData>> = priceData.groupBy { it.assetId }
        .mapValues { (_, prices) ->
            prices.sortedBy { it.date }
        }

    /**
     * Gets the price for a specific asset on a specific date
     * 
     * @param assetId The asset to look up
     * @param date The date to get the price for
     * @return The price if available, null otherwise
     */
    fun getPriceOnDate(assetId: UUID, date: LocalDate): Double? {
        val assetPrices = pricesByAsset[assetId] ?: return null
        return assetPrices.find { it.date == date }?.price
    }
    
    /**
     * Gets all assets that have price data available on the specified date
     * 
     * @param date The date to check for price availability
     * @return List of asset IDs that have prices on this date
     */
    fun getAssetsWithPriceOnDate(date: LocalDate): List<UUID> {
        return pricesByAsset.keys.filter { assetId ->
            getPriceOnDate(assetId, date) != null
        }
    }
    
    /**
     * Gets the latest date from all price data across all assets
     * 
     * @return The most recent date with price data
     */
    fun getLatestPriceDate(): LocalDate {
        return pricesByAsset.values.flatten()
            .maxByOrNull { it.date }?.date 
            ?: throw IllegalStateException("No price data available")
    }
    
    /**
     * Gets all unique asset IDs in this collection
     * 
     * @return List of all asset IDs that have price data
     */
    fun getAllAssetIds(): List<UUID> {
        return pricesByAsset.keys.toList()
    }
}