package com.wisecrowd.data_generator.data_generators.transaction_data_generator

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

class AssetPriceCollectionTest {

    @Nested
    inner class GetPriceOnDate {
        
        @Test
        fun getPriceOnDate_existingAssetAndDate_returnsCorrectPrice() {
            val assetId = UUID.randomUUID()
            val date = LocalDate.of(2024, 1, 1)
            val expectedPrice = 100.0
            val priceData = listOf(PriceData(assetId, date, expectedPrice))
            val collection = AssetPriceCollection(priceData)
            
            val actualPrice = collection.getPriceOnDate(assetId, date)
            
            assertThat(actualPrice).isEqualTo(expectedPrice)
        }

        @Test
        fun getPriceOnDate_nonExistentAsset_returnsNull() {
            val assetId = UUID.randomUUID()
            val nonExistentAssetId = UUID.randomUUID()
            val date = LocalDate.of(2024, 1, 1)
            val priceData = listOf(PriceData(assetId, date, 100.0))
            val collection = AssetPriceCollection(priceData)
            
            val price = collection.getPriceOnDate(nonExistentAssetId, date)
            
            assertThat(price).isNull()
        }

        @Test
        fun getPriceOnDate_nonExistentDate_returnsNull() {
            val assetId = UUID.randomUUID()
            val existingDate = LocalDate.of(2024, 1, 1)
            val nonExistentDate = LocalDate.of(2024, 1, 2)
            val priceData = listOf(PriceData(assetId, existingDate, 100.0))
            val collection = AssetPriceCollection(priceData)
            
            val price = collection.getPriceOnDate(assetId, nonExistentDate)
            
            assertThat(price).isNull()
        }
    }

    @Nested
    inner class GetAssetsWithPriceOnDate {
        
        @Test
        fun getAssetsWithPriceOnDate_existingDate_returnsCorrectAssets() {
            val assetId1 = UUID.randomUUID()
            val assetId2 = UUID.randomUUID()
            val assetId3 = UUID.randomUUID()
            val date = LocalDate.of(2024, 1, 1)
            val differentDate = LocalDate.of(2024, 1, 2)
            
            val priceData = listOf(
                PriceData(assetId1, date, 100.0),
                PriceData(assetId2, date, 200.0),
                PriceData(assetId3, differentDate, 300.0)
            )
            val collection = AssetPriceCollection(priceData)
            
            val assetsWithPrice = collection.getAssetsWithPriceOnDate(date)
            
            assertThat(assetsWithPrice).containsExactlyInAnyOrder(assetId1, assetId2)
        }

        @Test
        fun getAssetsWithPriceOnDate_noAssetsOnDate_returnsEmptyList() {
            val assetId = UUID.randomUUID()
            val existingDate = LocalDate.of(2024, 1, 1)
            val nonExistentDate = LocalDate.of(2024, 1, 2)
            val priceData = listOf(PriceData(assetId, existingDate, 100.0))
            val collection = AssetPriceCollection(priceData)
            
            val assetsWithPrice = collection.getAssetsWithPriceOnDate(nonExistentDate)
            
            assertThat(assetsWithPrice).isEmpty()
        }
    }

    @Nested
    inner class GetLatestPriceDate {
        
        @Test
        fun getLatestPriceDate_multipleDates_returnsLatestDate() {
            val assetId = UUID.randomUUID()
            val earlierDate = LocalDate.of(2024, 1, 1)
            val laterDate = LocalDate.of(2024, 1, 2)
            val latestDate = LocalDate.of(2024, 1, 3)
            
            val priceData = listOf(
                PriceData(assetId, earlierDate, 100.0),
                PriceData(assetId, latestDate, 300.0),
                PriceData(assetId, laterDate, 200.0)
            )
            val collection = AssetPriceCollection(priceData)
            
            val actualLatestDate = collection.getLatestPriceDate()
            
            assertThat(actualLatestDate).isEqualTo(latestDate)
        }

        @Test
        fun getLatestPriceDate_emptyCollection_throwsIllegalStateException() {
            val emptyPriceData = emptyList<PriceData>()
            val collection = AssetPriceCollection(emptyPriceData)
            
            assertThatThrownBy {
                collection.getLatestPriceDate()
            }.isInstanceOf(IllegalStateException::class.java)
                .hasMessageContaining("No price data available")
        }
    }

    @Nested
    inner class GetAllAssetIds {
        
        @Test
        fun getAllAssetIds_multipleAssets_returnsAllUniqueAssetIds() {
            val assetId1 = UUID.randomUUID()
            val assetId2 = UUID.randomUUID()
            val date = LocalDate.of(2024, 1, 1)
            
            val priceData = listOf(
                PriceData(assetId1, date, 100.0),
                PriceData(assetId2, date, 200.0),
                PriceData(assetId1, date.plusDays(1), 101.0)  // Duplicate asset
            )
            val collection = AssetPriceCollection(priceData)
            
            val allAssetIds = collection.getAllAssetIds()
            
            assertThat(allAssetIds).containsExactlyInAnyOrder(assetId1, assetId2)
        }

        @Test
        fun getAllAssetIds_emptyCollection_returnsEmptyList() {
            val emptyPriceData = emptyList<PriceData>()
            val collection = AssetPriceCollection(emptyPriceData)
            
            val allAssetIds = collection.getAllAssetIds()
            
            assertThat(allAssetIds).isEmpty()
        }
    }
}