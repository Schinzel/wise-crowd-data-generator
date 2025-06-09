package com.wisecrowd.data_generator.utils

import com.wisecrowd.data_generator.data_collections.asset_class.AssetClass
import com.wisecrowd.data_generator.data_collections.asset_class.VolatilityLevelEnum
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.random.Random

class AssetNamerTest {
    @Test
    fun generateName_nordicStocksAssetClass_returnsEquityStyleName() {
        val assetClass = createAssetClass(id = 1, name = "Nordic stocks")
        val assetNamer = AssetNamer(Random(42))

        val result = assetNamer.generateName(assetClass)

        assertThat(result).contains("Handelsbanken")
        assertThat(result).contains("Sweden")
        assertThat(result).contains("Health")
        assertThat(result).contains("Dividend")
    }

    @Test
    fun generateName_governmentBondAssetClass_returnsBondStyleName() {
        val assetClass = createAssetClass(id = 2, name = "Government bond")
        val assetNamer = AssetNamer(Random(42))

        val result = assetNamer.generateName(assetClass)

        assertThat(result).contains("Handelsbanken")
        assertThat(result).contains("Sweden")
        assertThat(result).contains("Short-Term")
        assertThat(result).contains("Bond")
    }

    @Test
    fun generateName_corporateBondAssetClass_returnsCorporateBondStyleName() {
        val assetClass = createAssetClass(id = 3, name = "Corporate Bond")
        val assetNamer = AssetNamer(Random(42))

        val result = assetNamer.generateName(assetClass)

        assertThat(result).contains("Handelsbanken")
        assertThat(result).contains("Sweden")
        assertThat(result).contains("Corporate")
        assertThat(result).contains("Short-Term")
    }

    @Test
    fun generateName_mediumRiskFundAssetClass_returnsMixedFundStyleName() {
        val assetClass = createAssetClass(id = 4, name = "Medium-Risk Fund")
        val assetNamer = AssetNamer(Random(42))

        val result = assetNamer.generateName(assetClass)

        assertThat(result).contains("Handelsbanken")
        assertThat(result).contains("Sweden")
        assertThat(result).contains("Opportunity")
        assertThat(result).contains("Fund")
    }

    @Test
    fun generateName_cryptoAssetClass_returnsCryptoStyleName() {
        val assetClass = createAssetClass(id = 8, name = "Crypto")
        val assetNamer = AssetNamer(Random(42))

        val result = assetNamer.generateName(assetClass)

        assertThat(result).contains("Handelsbanken")
        assertThat(result).contains("Digital Assets Fund")
    }

    @Test
    fun generateName_allAssetClassTypes_returnsValidNames() {
        val assetNamer = AssetNamer(Random(100))
        val allAssetTypes =
            listOf(
                1 to "Nordic stocks",
                2 to "Government bond",
                3 to "Corporate Bond",
                4 to "Medium-Risk Fund",
                5 to "Large-Cap Equity",
                6 to "Gold / Precious Metals",
                7 to "REITs",
                8 to "Crypto",
                99 to "Unknown Asset",
            )

        allAssetTypes.forEach { (id, name) ->
            val assetClass = createAssetClass(id = id, name = name)
            val result = assetNamer.generateName(assetClass)
            assertThat(result).isNotBlank()
            assertThat(result.length).isGreaterThan(5)
        }
    }

    @Test
    fun generateName_differentRandomSeeds_generatesDifferentNames() {
        val assetClass = createAssetClass(id = 1, name = "Nordic stocks")
        val assetNamer1 = AssetNamer(Random(42))
        val assetNamer2 = AssetNamer(Random(43))

        val result1 = assetNamer1.generateName(assetClass)
        val result2 = assetNamer2.generateName(assetClass)

        assertThat(result1).isNotEqualTo(result2)
    }

    @Test
    fun generateName_sameRandomSeed_generatesSameName() {
        val assetClass = createAssetClass(id = 1, name = "Nordic stocks")
        val assetNamer1 = AssetNamer(Random(42))
        val assetNamer2 = AssetNamer(Random(42))

        val result1 = assetNamer1.generateName(assetClass)
        val result2 = assetNamer2.generateName(assetClass)

        assertThat(result1).isEqualTo(result2)
    }

    private fun createAssetClass(
        id: Int = 1,
        name: String = "Test Asset",
        description: String = "Test description",
        volatilityLevel: VolatilityLevelEnum = VolatilityLevelEnum.MEDIUM,
        prevalencePercentage: Int = 10,
    ): AssetClass =
        AssetClass(
            id = id,
            name = name,
            description = description,
            volatilityLevel = volatilityLevel,
            prevalencePercentage = prevalencePercentage,
        )
}
