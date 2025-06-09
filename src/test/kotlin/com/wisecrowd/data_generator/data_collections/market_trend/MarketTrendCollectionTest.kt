package com.wisecrowd.data_generator.data_collections.market_trend

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

class MarketTrendCollectionTest {
    private lateinit var collection: MarketTrendCollection

    @BeforeEach
    fun setUp() {
        collection = MarketTrendCollection()
    }

    @Nested
    inner class BasicOperations {
        @Test
        fun `empty collection _ returns isEmpty true and size 0`() {
            // Assert
            assertThat(collection.isEmpty()).isTrue()
            assertThat(collection.size()).isEqualTo(0)
        }

        @Test
        fun `add one trend _ increases size and is not empty`() {
            // Arrange
            val trend =
                MarketTrend(
                    startDateString = "2020-01-01",
                    endDateString = "2020-12-31",
                    trendType = "Bull",
                    strength = 1.5,
                    description = "Test trend",
                )

            // Act
            collection.add(trend)

            // Assert
            assertThat(collection.isEmpty()).isFalse()
            assertThat(collection.size()).isEqualTo(1)
            assertThat(collection.getAll()).containsExactly(trend)
        }

        @Test
        fun `addAll with multiple trends _ adds all trends`() {
            // Arrange
            val trends =
                listOf(
                    MarketTrend(
                        startDateString = "2020-01-01",
                        endDateString = "2020-06-30",
                        trendType = "Bull",
                        strength = 1.5,
                        description = "First half",
                    ),
                    MarketTrend(
                        startDateString = "2020-07-01",
                        endDateString = "2020-12-31",
                        trendType = "Bear",
                        strength = -1.0,
                        description = "Second half",
                    ),
                )

            // Act
            collection.addAll(trends)

            // Assert
            assertThat(collection.size()).isEqualTo(2)
            assertThat(collection.getAll()).containsExactlyElementsOf(trends)
        }
    }

    @Nested
    inner class Filtering {
        private lateinit var trend1: MarketTrend
        private lateinit var trend2: MarketTrend
        private lateinit var trend3: MarketTrend

        @BeforeEach
        fun setUpTrends() {
            // First quarter
            trend1 =
                MarketTrend(
                    startDateString = "2020-01-01",
                    endDateString = "2020-03-31",
                    trendType = "Bull",
                    strength = 1.5,
                    description = "Q1",
                )

            // Second quarter
            trend2 =
                MarketTrend(
                    startDateString = "2020-04-01",
                    endDateString = "2020-06-30",
                    trendType = "Bear",
                    strength = -1.0,
                    description = "Q2",
                )

            // Whole year
            trend3 =
                MarketTrend(
                    startDateString = "2020-01-01",
                    endDateString = "2020-12-31",
                    trendType = "Sideways",
                    strength = 0.2,
                    description = "Full year",
                )

            collection.addAll(listOf(trend1, trend2, trend3))
        }

        @Test
        fun `getTrendsOnDate with date in Q1 _ returns Q1 and full year trends`() {
            // Arrange
            val date = LocalDate.of(2020, 2, 15)

            // Act
            val trends = collection.getTrendsOnDate(date)

            // Assert
            assertThat(trends).containsExactlyInAnyOrder(trend1, trend3)
        }

        @Test
        fun `getTrendsOnDate with date in Q2 _ returns Q2 and full year trends`() {
            // Arrange
            val date = LocalDate.of(2020, 5, 15)

            // Act
            val trends = collection.getTrendsOnDate(date)

            // Assert
            assertThat(trends).containsExactlyInAnyOrder(trend2, trend3)
        }

        @Test
        fun `getTrendsOnDate with date outside all ranges _ returns empty list`() {
            // Arrange
            val date = LocalDate.of(2021, 1, 1)

            // Act
            val trends = collection.getTrendsOnDate(date)

            // Assert
            assertThat(trends).isEmpty()
        }

        @Test
        fun `getTrendsInRange spanning Q1 and Q2 _ returns all trends`() {
            // Arrange
            val startDate = LocalDate.of(2020, 3, 15)
            val endDate = LocalDate.of(2020, 4, 15)

            // Act
            val trends = collection.getTrendsInRange(startDate, endDate)

            // Assert
            assertThat(trends).containsExactlyInAnyOrder(trend1, trend2, trend3)
        }

        @Test
        fun `getTrendsInRange within Q1 _ returns Q1 and full year trends`() {
            // Arrange
            val startDate = LocalDate.of(2020, 2, 1)
            val endDate = LocalDate.of(2020, 2, 28)

            // Act
            val trends = collection.getTrendsInRange(startDate, endDate)

            // Assert
            assertThat(trends).containsExactlyInAnyOrder(trend1, trend3)
        }

        @Test
        fun `getTrendsInRange with start date after end date _ throws IllegalArgumentException`() {
            // Arrange
            val startDate = LocalDate.of(2020, 5, 1)
            val endDate = LocalDate.of(2020, 4, 1)

            // Act & Assert
            assertThatThrownBy {
                collection.getTrendsInRange(startDate, endDate)
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("End date cannot be before start date")
        }
    }

    @Nested
    inner class DefaultCollection {
        @Test
        fun `createDefaultCollection _ creates collection with expected trends`() {
            // Act
            val defaultCollection = MarketTrendCollection.createDefaultCollection()

            // Assert
            assertThat(defaultCollection.isEmpty()).isFalse()
            // We expect 21 trends as per the design document
            assertThat(defaultCollection.size()).isEqualTo(21)

            // Check a few specific dates to verify correct trends
            val financialCrisisDate = LocalDate.of(2008, 9, 15)
            val financialCrisisTrends = defaultCollection.getTrendsOnDate(financialCrisisDate)
            assertThat(financialCrisisTrends).hasSize(1)
            assertThat(financialCrisisTrends[0].trendType).isEqualTo("Bear")
            assertThat(financialCrisisTrends[0].strength).isEqualTo(-2.3)

            val covidCrashDate = LocalDate.of(2020, 3, 15)
            val covidCrashTrends = defaultCollection.getTrendsOnDate(covidCrashDate)
            assertThat(covidCrashTrends).hasSize(1)
            assertThat(covidCrashTrends[0].trendType).isEqualTo("Crash")
            assertThat(covidCrashTrends[0].description).contains("COVID-19")
        }
    }
}
