package com.wisecrowd.data_generator.utils

import com.wisecrowd.data_generator.data_collections.asset_class.AssetClass
import kotlin.random.Random

/**
 * The purpose of this class is to generate realistic Swedish/Nordic asset names
 * based on asset class characteristics using predefined naming components.
 *
 * Written by Claude Sonnet 4
 */
class AssetNamer(
    private val random: Random = Random.Default,
) {
    /**
     * Generates a realistic asset name based on the asset class characteristics
     *
     * @param assetClass The asset class to generate a name for
     * @return A realistic Swedish/Nordic asset name
     */
    fun generateName(assetClass: AssetClass): String {
        require(assetClass.name.isNotBlank()) { "Asset class name cannot be blank" }

        val provider = PROVIDERS.random(random)
        val region = REGIONS.random(random)

        return when (assetClass.id) {
            // Nordic stocks
            1 -> "$provider $region ${SECTORS.random(random)} ${EQUITY_FUND_TYPES.random(random)}"
            // Government bond
            2 -> "$provider $region ${BOND_FUND_TYPES.random(random)} Bond"
            // Corporate Bond
            3 -> "$provider $region Corporate ${BOND_FUND_TYPES.random(random)}"
            // Medium-Risk Fund
            4 -> "$provider $region ${MIXED_FUND_TYPES.random(random)} Fund"
            // Large-Cap Equity
            5 -> "$provider $region Large-Cap ${SECTORS.random(random)} ${EQUITY_FUND_TYPES.random(random)}"
            // Gold / Precious Metals
            6 -> "$provider $region Precious Metals ${ALTERNATIVE_FUND_TYPES.random(random)}"
            // REITs
            7 -> "$provider $region Real Estate ${ALTERNATIVE_FUND_TYPES.random(random)}"
            // Crypto
            8 -> "$provider Digital Assets Fund"
            // Unknown/fallback
            else -> "$provider $region ${assetClass.name} ${MIXED_FUND_TYPES.random(random)}"
        }
    }

    companion object {
        private val PROVIDERS = listOf("Swedbank", "SEB", "Nordea", "Handelsbanken", "Länsförsäkringar")
        private val REGIONS = listOf("Sweden", "Nordic", "Europe", "Global", "Asia", "US")
        private val SECTORS = listOf("Tech", "Health", "Finance", "Energy", "Real Estate")
        private val EQUITY_FUND_TYPES = listOf("Index", "Active", "Dividend", "Growth", "Value")
        private val BOND_FUND_TYPES = listOf("Government", "Corporate", "High-Yield", "Short-Term")
        private val MIXED_FUND_TYPES = listOf("Balanced", "Strategic", "Defensive", "Opportunity")
        private val ALTERNATIVE_FUND_TYPES = listOf("Commodity", "Real Estate", "Alternative", "Specialty")
    }
}
