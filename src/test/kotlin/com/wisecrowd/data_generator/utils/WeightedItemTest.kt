package com.wisecrowd.data_generator.utils

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

/**
 * The purpose of this class is to test the WeightedItem data class
 * ensuring proper validation and construction.
 *
 * Written by Claude Sonnet 4
 */
class WeightedItemTest {

    @Test
    fun `constructor _ positive percent _ creates valid WeightedItem`() {
        val item = "test"
        val percent = 25.0
        val weightedItem = WeightedItem(item, percent)

        assertThat(weightedItem.item).isEqualTo(item)
        assertThat(weightedItem.percent).isEqualTo(percent)
    }

    @Test
    fun `constructor _ zero percent _ creates valid WeightedItem`() {
        val item = "test"
        val percent = 0.0
        val weightedItem = WeightedItem(item, percent)

        assertThat(weightedItem.item).isEqualTo(item)
        assertThat(weightedItem.percent).isEqualTo(percent)
    }

    @Test
    fun `constructor _ negative percent _ throws IllegalArgumentException`() {
        assertThatThrownBy {
            WeightedItem("test", -1.0)
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Percent must be non-negative, but was: -1.0")
    }

    @Test
    fun `constructor _ infinite percent _ throws IllegalArgumentException`() {
        assertThatThrownBy {
            WeightedItem("test", Double.POSITIVE_INFINITY)
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Percent must be a finite number, but was: Infinity")
    }

    @Test
    fun `constructor _ NaN percent _ throws IllegalArgumentException`() {
        assertThatThrownBy {
            WeightedItem("test", Double.NaN)
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Percent must be a finite number, but was: NaN")
    }

    @Test
    fun `constructor _ percent over 100 _ throws IllegalArgumentException`() {
        assertThatThrownBy {
            WeightedItem("test", 101.0)
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Percent must be at most 100.0, but was: 101.0")
    }

    @Test
    fun `constructor _ 100 percent _ creates valid WeightedItem`() {
        val item = "test"
        val percent = 100.0
        val weightedItem = WeightedItem(item, percent)

        assertThat(weightedItem.item).isEqualTo(item)
        assertThat(weightedItem.percent).isEqualTo(percent)
    }
}
