package com.wisecrowd.data_generator.utils

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import kotlin.random.Random

/**
 * The purpose of this class is to test the WeightedRandomSelector implementation
 * ensuring correct weighted distribution, edge case handling, and deterministic behavior.
 *
 * Written by Claude Sonnet 4
 */
class WeightedRandomSelectorTest {

    @Test
    fun `constructor _ empty items list _ throws IllegalArgumentException`() {
        assertThatThrownBy {
            WeightedRandomSelector(emptyList<WeightedItem<String>>())
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Items list cannot be empty")
    }

    @Test
    fun `constructor _ total percent is zero _ throws IllegalArgumentException`() {
        val items = listOf(
            WeightedItem("A", 0.0),
            WeightedItem("B", 0.0)
        )
        assertThatThrownBy {
            WeightedRandomSelector(items)
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Percentages must sum to exactly 100.0, but sum was: 0.0")
    }

    @Test
    fun `getRandomItem _ single item provided _ returns only item`() {
        val items = listOf(WeightedItem("OnlyItem", 100.0))
        val selector = WeightedRandomSelector(items)

        repeat(10) {
            assertThat(selector.getRandomItem()).isEqualTo("OnlyItem")
        }
    }

    @Test
    fun `getRandomItem _ deterministic random _ distributes according to percentages`() {
        val items = listOf(
            WeightedItem("A", 50.0),
            WeightedItem("B", 30.0),
            WeightedItem("C", 20.0)
        )
        val fixedRandom = Random(42)
        val selector = WeightedRandomSelector(items, fixedRandom)

        val results = mutableMapOf<String, Int>()
        val totalSelections = 10_000
        repeat(totalSelections) {
            val item = selector.getRandomItem()
            results[item] = results.getOrDefault(item, 0) + 1
        }

        // Calculate actual percentages
        val actualPercentageA = (results["A"]!! * 100.0) / totalSelections
        val actualPercentageB = (results["B"]!! * 100.0) / totalSelections
        val actualPercentageC = (results["C"]!! * 100.0) / totalSelections

        // Assert percentages are within tolerance (±2% for large sample)
        assertThat(actualPercentageA).isBetween(48.0, 52.0)
        assertThat(actualPercentageB).isBetween(28.0, 32.0)
        assertThat(actualPercentageC).isBetween(18.0, 22.0)
    }

    @Test
    fun `getRandomItem _ percentage values _ works correctly`() {
        val items = listOf(
            WeightedItem("High", 60.0),
            WeightedItem("Low", 40.0)
        )
        val selector = WeightedRandomSelector(items, Random(123))

        val results = mutableSetOf<String>()
        repeat(100) {
            results.add(selector.getRandomItem())
        }

        assertThat(results).contains("High", "Low")
    }

    @Test
    fun `constructor _ percentages sum to less than 100 _ throws IllegalArgumentException`() {
        val items = listOf(
            WeightedItem("A", 30.0),
            WeightedItem("B", 40.0)
        )
        assertThatThrownBy {
            WeightedRandomSelector(items)
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Percentages must sum to exactly 100.0, but sum was: 70.0")
    }

    @Test
    fun `constructor _ percentages sum to more than 100 _ throws IllegalArgumentException`() {
        val items = listOf(
            WeightedItem("A", 60.0),
            WeightedItem("B", 50.0)
        )
        assertThatThrownBy {
            WeightedRandomSelector(items)
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Percentages must sum to exactly 100.0, but sum was: 110.0")
    }
}
