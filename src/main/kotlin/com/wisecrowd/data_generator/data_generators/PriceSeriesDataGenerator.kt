package com.wisecrowd.data_generator.data_generators

import com.wisecrowd.data_generator.data_collections.asset_class.AssetClass
import com.wisecrowd.data_generator.data_collections.asset_class.AssetClassCollection
import com.wisecrowd.data_generator.data_collections.asset_class.VolatilityLevelEnum
import com.wisecrowd.data_generator.data_collections.market_trend.MarketTrendCollection
import java.time.LocalDate
import java.util.UUID
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * The purpose of this class is to generate realistic price series data for assets
 * based on market trends and asset volatility levels.
 *
 * This generator creates historical price movements influenced by market trend strength,
 * asset-specific volatility, and realistic price patterns following geometric Brownian motion.
 * Each generated row contains asset_id, date, and price for creating price_series.txt output.
 *
 * The price generation uses:
 * - Market trend strength to influence daily price drift
 * - Asset class volatility to determine price movement variance
 * - Geometric Brownian motion for realistic financial modeling
 * - Box-Muller transformation for normal distribution random shocks
 *
 * Written by Claude Sonnet 4
 */
class PriceSeriesDataGenerator(
    private val assetIds: List<UUID>,
    private val startDate: LocalDate,
    private val endDate: LocalDate,
    private val assetClassCollection: AssetClassCollection = AssetClassCollection.createDefaultCollection(),
    private val marketTrendCollection: MarketTrendCollection = MarketTrendCollection.createDefaultCollection(),
    private val initialPrice: Double = 100.0,
    private val random: Random = Random.Default
) : IDataGenerator {

    // Current position in the iteration: date first, then asset within that date
    private var currentDate = startDate
    private var currentAssetIndex = 0
    
    // Track current price for each asset to ensure continuity
    private val assetPrices = mutableMapOf<UUID, Double>()
    
    // Total number of rows to generate (dates × assets)
    private val totalRows: Int

    init {
        // Validate input parameters to ensure valid data generation
        require(assetIds.isNotEmpty()) { "Asset IDs list cannot be empty" }
        require(endDate >= startDate) { "End date ($endDate) cannot be before start date ($startDate)" }
        require(initialPrice > 0) { "Initial price must be positive, but was: $initialPrice" }

        // Calculate total rows that will be generated
        val dayCount = startDate.datesUntil(endDate.plusDays(1)).count().toInt()
        totalRows = dayCount * assetIds.size

        // Initialize all assets with the same starting price
        assetIds.forEach { assetId ->
            assetPrices[assetId] = initialPrice
        }
    }

    /**
     * Returns the column structure for price series data
     * Matches the design specification for price_series.txt output
     */
    override fun getColumnNames(): List<String> {
        return listOf("asset_id", "date", "price")
    }

    /**
     * Checks if more price data rows are available for generation
     * Continues until all dates have prices for all assets
     */
    override fun hasMoreRows(): Boolean {
        return currentDate <= endDate
    }

    /**
     * Generates the next price data row using market trends and volatility
     * Returns asset_id, date, and calculated price based on financial modeling
     */
    override fun getNextRow(): List<Any> {
        // Ensure we haven't exceeded available data
        if (!hasMoreRows()) {
            throw NoSuchElementException("No more rows available in generator")
        }

        // Get current asset and generate its price for the current date
        val assetId = assetIds[currentAssetIndex]
        val currentPrice = generateNextPrice(assetId, currentDate)
        
        // Store the new price for price continuity
        assetPrices[assetId] = currentPrice

        // Create the row data matching column structure
        val result = listOf(
            // asset_id (UUID)
            assetId,
            // date (LocalDate)
            currentDate,
            // price (Double)
            currentPrice
        )

        // Move to next asset for this date
        currentAssetIndex++
        
        // If we've completed all assets for this date, move to next date
        if (currentAssetIndex >= assetIds.size) {
            currentAssetIndex = 0
            currentDate = currentDate.plusDays(1)
        }

        return result
    }

    /**
     * Generates the next price for an asset using geometric Brownian motion
     * Incorporates market trend strength and asset-specific volatility
     */
    private fun generateNextPrice(assetId: UUID, date: LocalDate): Double {
        // Get the current price for this asset (or initial price if first day)
        val currentPrice = assetPrices[assetId] ?: initialPrice
        
        // Determine asset class to get volatility characteristics
        val assetClass = getAssetClassForAsset(assetId)
        
        // Find active market trend for this date to influence price direction
        val marketTrend = marketTrendCollection.getTrendsOnDate(date).firstOrNull()

        // Convert asset class volatility to daily volatility for modeling
        val baseVolatility = getVolatilityForAssetClass(assetClass.volatilityLevel)
        
        // Convert market trend strength to daily drift component
        // Market trend strength affects the expected return direction
        val trendDrift = marketTrend?.let { 
            // Convert to daily drift (252 trading days/year)
            (it.strength / 100.0) / 252.0
        } ?: 0.0

        // Generate random shock using normal distribution for price randomness
        // Daily time step
        val randomShock = random.nextGaussian() * sqrt(1.0 / 252.0)
        
        // Calculate log return using drift + volatility * random shock
        val logReturn = trendDrift + baseVolatility * randomShock
        
        // Apply geometric Brownian motion: S(t+1) = S(t) * exp(logReturn)
        return currentPrice * exp(logReturn)
    }

    /**
     * Maps an asset ID to its corresponding asset class
     * Uses asset index to distribute assets across available asset classes
     */
    private fun getAssetClassForAsset(assetId: UUID): AssetClass {
        // Since we don't have direct asset->assetClass mapping, distribute evenly
        val assetIndex = assetIds.indexOf(assetId)
        val assetClassCount = assetClassCollection.size()
        
        // Cycle through asset classes based on asset position
        val assetClassIndex = (assetIndex % assetClassCount) + 1
        
        return assetClassCollection.getById(assetClassIndex)
    }

    /**
     * Converts volatility level enum to annual volatility percentage
     * Based on typical financial market volatility ranges
     */
    private fun getVolatilityForAssetClass(volatilityLevel: VolatilityLevelEnum): Double {
        return when (volatilityLevel) {
            // 10% annual volatility (bonds)
            VolatilityLevelEnum.LOW -> 0.10
            // 20% annual volatility (balanced funds)
            VolatilityLevelEnum.MEDIUM -> 0.20
            // 35% annual volatility (stocks)
            VolatilityLevelEnum.HIGH -> 0.35
            // 50% annual volatility (crypto, gold)
            VolatilityLevelEnum.VERY_HIGH -> 0.50
        }
    }

    /**
     * Generates normally distributed random numbers using Box-Muller transformation
     * Provides realistic random shocks for financial price modeling
     */
    private fun Random.nextGaussian(): Double {
        // Box-Muller transformation to convert uniform to normal distribution
        var u1: Double
        var u2: Double
        
        // Ensure u1 is not zero to avoid log(0)
        do {
            u1 = nextDouble()
            u2 = nextDouble()
        } while (u1 <= Double.MIN_VALUE)
        
        // Apply Box-Muller formula for normal distribution
        return sqrt(-2.0 * ln(u1)) * kotlin.math.cos(2.0 * kotlin.math.PI * u2)
    }
}