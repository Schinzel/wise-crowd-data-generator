package com.wisecrowd.data_generator.utils

import kotlin.random.Random

/**
 * The purpose of this class is to provide weighted random selection functionality
 * for distributing items based on their assigned percentages.
 *
 * This implementation eliminates code duplication across all collections requiring
 * randomized distribution and provides consistent selection logic throughout the system.
 *
 * @param items List of weighted items with percentages that must sum to exactly 100.0
 * @param random Random instance for generating selection values (injectable for deterministic testing)
 *
 * Written by Claude Sonnet 4
 */
class WeightedRandomSelector<T>(
    private val items: List<WeightedItem<T>>,
    private val random: Random = Random.Default,
) : IWeightedRandomSelector<T> {
    /** Precomputed cumulative percentages for efficient binary search selection */
    private val cumulativePercentages: List<Double>

    init {
        require(items.isNotEmpty()) { "Items list cannot be empty" }

        // Build cumulative percentage array for binary search
        // Example: [25.0, 30.0, 45.0] becomes [25.0, 55.0, 100.0]
        var runningTotal = 0.0
        cumulativePercentages =
            items.map { weightedItem ->
                runningTotal += weightedItem.percent
                runningTotal
            }

        // Validate that percentages sum to exactly 100%
        val totalPercentage = cumulativePercentages.last()
        require(totalPercentage == 100.0) {
            "Percentages must sum to exactly 100.0, but sum was: $totalPercentage"
        }
    }

    /**
     * Returns a randomly selected item based on percentage distribution.
     *
     * Algorithm:
     * 1. Generate random value between 0.0 and 100.0
     * 2. Use binary search to find first cumulative percentage >= random value
     * 3. Return corresponding item
     *
     * Time complexity: O(log n) due to binary search
     *
     * @return A randomly selected item from the weighted collection
     */
    override fun getRandomItem(): T {
        // Generate random percentage between 0.0 and 100.0
        val randomPercentage = random.nextDouble() * 100.0

        // Find first cumulative percentage >= randomPercentage using binary search
        // Returns negative insertion point if exact match not found
        val searchIndex = cumulativePercentages.binarySearch { it.compareTo(randomPercentage) }

        // Convert search result to actual array index
        // If exact match: use that index
        // If no exact match: use insertion point (converted from negative value)
        val selectedIndex = if (searchIndex >= 0) searchIndex else (-searchIndex - 1)

        return items[selectedIndex].item
    }
}
