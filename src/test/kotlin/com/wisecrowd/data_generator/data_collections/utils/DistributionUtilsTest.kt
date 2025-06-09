package com.wisecrowd.data_generator.data_collections.utils

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class DistributionUtilsTest {
    data class TestItem(
        val name: String,
        val distributionPercentage: Double,
    )

    @Test
    fun filterByDistribution_validRangeWithMatchingItems_returnsMatchingItems() {
        val items =
            listOf(
                TestItem("Low", 10.0),
                TestItem("Medium", 25.0),
                TestItem("High", 50.0),
            )

        val result =
            DistributionUtils.filterByDistribution(
                items,
                20.0,
                30.0,
            ) { it.distributionPercentage }

        assertThat(result).hasSize(1)
        assertThat(result[0].name).isEqualTo("Medium")
    }

    @Test
    fun filterByDistribution_minPercentageNegative_throwsIllegalArgumentException() {
        val items = listOf(TestItem("Test", 50.0))

        assertThatThrownBy {
            DistributionUtils.filterByDistribution(
                items,
                -1.0,
                50.0,
            ) { it.distributionPercentage }
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun filterByDistribution_emptyCollection_returnsEmptyList() {
        val emptyItems = emptyList<TestItem>()

        val result =
            DistributionUtils.filterByDistribution(
                emptyItems,
                0.0,
                100.0,
            ) { it.distributionPercentage }

        assertThat(result).isEmpty()
    }
}
