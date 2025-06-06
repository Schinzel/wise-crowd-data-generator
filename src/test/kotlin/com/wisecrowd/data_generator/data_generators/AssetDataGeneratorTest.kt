package com.wisecrowd.data_generator.data_generators

import com.wisecrowd.data_generator.data_collections.asset_class.AssetClass
import com.wisecrowd.data_generator.data_collections.asset_class.AssetClassCollection
import com.wisecrowd.data_generator.data_collections.asset_class.VolatilityLevelEnum
import com.wisecrowd.data_generator.utils.AssetNamer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import kotlin.random.Random

class AssetDataGeneratorTest {

    @Test
    fun getColumnNames_defaultConfiguration_returnsCorrectColumnNames() {
        val generator = AssetDataGenerator(assetCount = 5)
        
        val columnNames = generator.getColumnNames()
        
        val expectedColumns = listOf("asset_id", "asset_class_id", "name")
        assertThat(columnNames).isEqualTo(expectedColumns)
    }

    @Test
    fun hasMoreRows_newGenerator_returnsTrue() {
        val generator = AssetDataGenerator(assetCount = 2)
        
        val hasMore = generator.hasMoreRows()
        
        assertThat(hasMore).isTrue()
    }

    @Test
    fun hasMoreRows_allAssetsGenerated_returnsFalse() {
        val generator = AssetDataGenerator(assetCount = 1)
        
        generator.getNextRow()
        val hasMore = generator.hasMoreRows()
        
        assertThat(hasMore).isFalse()
    }

    @Test
    fun getNextRow_allAssetsGenerated_throwsNoSuchElementException() {
        val generator = AssetDataGenerator(assetCount = 1)
        generator.getNextRow()
        
        assertThatThrownBy { generator.getNextRow() }
            .isInstanceOf(NoSuchElementException::class.java)
            .hasMessageContaining("No more rows available in generator")
    }

    @Test
    fun getNextRow_validGenerator_returnsCorrectDataTypes() {
        val generator = AssetDataGenerator(assetCount = 1)
        
        val row = generator.getNextRow()
        
        assertThat(row).hasSize(3)
        assertThat(row[0]).isInstanceOf(String::class.java)
        assertThat(row[1]).isInstanceOf(Integer::class.java)
        assertThat(row[2]).isInstanceOf(String::class.java)
    }

    @Test
    fun getNextRow_validGenerator_returnsValidUuidFormat() {
        val generator = AssetDataGenerator(assetCount = 1)
        
        val row = generator.getNextRow()
        val assetId = row[0] as String
        
        val uuidPattern = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"
        assertThat(assetId).matches(uuidPattern)
    }

    @Test
    fun getNextRow_customAssetClassCollection_returnsValidAssetClassIds() {
        val customCollection = AssetClassCollection()
        val expectedAssetClass1 = AssetClass(1, "Test Asset", "Description", VolatilityLevelEnum.LOW, 60)
        val expectedAssetClass2 = AssetClass(2, "Another Asset", "Description", VolatilityLevelEnum.HIGH, 40)
        customCollection.addAll(listOf(expectedAssetClass1, expectedAssetClass2))
        val generator = AssetDataGenerator(assetCount = 10, assetClassCollection = customCollection)
        
        val assetClassIds = mutableSetOf<Int>()
        repeat(10) {
            val row = generator.getNextRow()
            val assetClassId = row[1] as Int
            assetClassIds.add(assetClassId)
        }
        
        val validIds = setOf(1, 2)
        assertThat(assetClassIds).allMatch { it in validIds }
    }

    @Test
    fun getNextRow_manyGenerations_respectsPrevalenceDistribution() {
        val customCollection = AssetClassCollection()
        val highPrevalence = AssetClass(1, "High Prevalence", "Description", VolatilityLevelEnum.LOW, 80)
        val lowPrevalence = AssetClass(2, "Low Prevalence", "Description", VolatilityLevelEnum.HIGH, 20)
        customCollection.addAll(listOf(highPrevalence, lowPrevalence))
        val generator = AssetDataGenerator(assetCount = 1000, assetClassCollection = customCollection)
        
        val assetClassCounts = mutableMapOf<Int, Int>()
        repeat(1000) {
            val row = generator.getNextRow()
            val assetClassId = row[1] as Int
            assetClassCounts[assetClassId] = assetClassCounts.getOrDefault(assetClassId, 0) + 1
        }
        
        val highPrevalenceCount = assetClassCounts[1] ?: 0
        val highPrevalenceRatio = highPrevalenceCount.toDouble() / 1000
        assertThat(highPrevalenceRatio).isBetween(0.7, 0.9)
    }

    @Test
    fun getNextRow_customAssetNamer_generatesRealisticNames() {
        val fixedSeedNamer = AssetNamer(Random(42))
        val generator = AssetDataGenerator(assetCount = 1, assetNamer = fixedSeedNamer)
        
        val row = generator.getNextRow()
        val assetName = row[2] as String
        
        assertThat(assetName).isNotBlank()
        assertThat(assetName).hasSizeGreaterThan(5)
    }

    @Test
    fun constructor_zeroAssetCount_throwsIllegalArgumentException() {
        assertThatThrownBy { AssetDataGenerator(assetCount = 0) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Asset count must be positive, but was: 0")
    }

    @Test
    fun constructor_negativeAssetCount_throwsIllegalArgumentException() {
        assertThatThrownBy { AssetDataGenerator(assetCount = -5) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Asset count must be positive, but was: -5")
    }

    @Test
    fun constructor_emptyAssetClassCollection_throwsIllegalArgumentException() {
        val emptyCollection = AssetClassCollection()
        
        assertThatThrownBy { AssetDataGenerator(assetCount = 10, assetClassCollection = emptyCollection) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Asset class collection cannot be empty")
    }

    @Test
    fun getNextRow_multipleGenerations_generatesUniqueAssetIds() {
        val generator = AssetDataGenerator(assetCount = 20)
        
        val assetIds = mutableSetOf<String>()
        repeat(20) {
            val row = generator.getNextRow()
            val assetId = row[0] as String
            assetIds.add(assetId)
        }
        
        assertThat(assetIds).hasSize(20)
    }

    @Test
    fun getNextRow_defaultAssetClassCollection_returnsValidAssetClassIds() {
        val generator = AssetDataGenerator(assetCount = 50)
        
        val assetClassIds = mutableSetOf<Int>()
        repeat(50) {
            val row = generator.getNextRow()
            val assetClassId = row[1] as Int
            assetClassIds.add(assetClassId)
        }
        
        assertThat(assetClassIds).allMatch { it in 1..8 }
        assertThat(assetClassIds).hasSizeGreaterThan(1)
    }

    @Test
    fun getNextRow_specifiedAssetCount_generatesExactCount() {
        val assetCount = 15
        val generator = AssetDataGenerator(assetCount = assetCount)
        
        var generatedCount = 0
        while (generator.hasMoreRows()) {
            generator.getNextRow()
            generatedCount++
        }
        
        assertThat(generatedCount).isEqualTo(assetCount)
    }
}