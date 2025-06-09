package com.wisecrowd.data_generator.data_generators

import com.wisecrowd.data_generator.data_collections.asset_class.AssetClassCollection
import com.wisecrowd.data_generator.utils.AssetNamer
import com.wisecrowd.data_generator.utils.WeightedItem
import com.wisecrowd.data_generator.utils.WeightedRandomSelector
import java.util.UUID

/**
 * The purpose of this class is to generate realistic asset data distributed
 * according to asset class prevalence percentages using AssetClassCollection and AssetNamer.
 *
 * This generator implements IDataGenerator to produce asset_data.txt file contents with
 * assets distributed based on their prevalence percentages from the design specification.
 * Each generated asset includes a unique UUID, asset class ID, and realistic Swedish/Nordic name.
 *
 * Written by Claude Sonnet 4
 */
class AssetDataGenerator(
    private val assetCount: Int,
    private val assetClassCollection: AssetClassCollection = AssetClassCollection.createDefaultCollection(),
    private val assetNamer: AssetNamer = AssetNamer(),
) : IDataGenerator {
    private var currentIndex = 0
    private val assetClassSelector: WeightedRandomSelector<Int>

    init {
        require(assetCount > 0) { "Asset count must be positive, but was: $assetCount" }
        require(!assetClassCollection.isEmpty()) { "Asset class collection cannot be empty" }

        // Create weighted selector using asset class prevalence percentages
        val weightedItems =
            assetClassCollection.getAll().map { assetClass ->
                WeightedItem(assetClass.id, assetClass.prevalencePercentage.toDouble())
            }

        assetClassSelector = WeightedRandomSelector(weightedItems)
    }

    override fun getColumnNames(): List<String> = listOf("asset_id", "asset_class_id", "name")

    override fun hasMoreRows(): Boolean = currentIndex < assetCount

    override fun getNextRow(): List<Any> {
        if (!hasMoreRows()) {
            throw NoSuchElementException("No more rows available in generator")
        }

        currentIndex++

        val assetClassId = assetClassSelector.getRandomItem()
        val assetClass = assetClassCollection.getById(assetClassId)
        val assetName = assetNamer.generateName(assetClass)

        return listOf(
            UUID.randomUUID(), // asset_id (UUID)
            assetClassId, // asset_class_id (Int)
            assetName, // name (String)
        )
    }
}
