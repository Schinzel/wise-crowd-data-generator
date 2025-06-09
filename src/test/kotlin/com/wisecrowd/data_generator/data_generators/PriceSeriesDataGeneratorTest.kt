package com.wisecrowd.data_generator.data_generators

import com.wisecrowd.data_generator.data_collections.asset_class.AssetClassCollection
import com.wisecrowd.data_generator.data_collections.market_trend.MarketTrendCollection
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random

class PriceSeriesDataGeneratorTest {
    private val testAssetIds =
        listOf(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            UUID.fromString("00000000-0000-0000-0000-000000000002"),
            UUID.fromString("00000000-0000-0000-0000-000000000003"),
        )
    private val testStartDate = LocalDate.of(2023, 1, 1)
    private val testEndDate = LocalDate.of(2023, 1, 3)
    private val fixedRandom = Random(42)

    @Nested
    inner class Constructor {
        @Test
        fun `valid parameters _ creates generator successfully`() {
            val generator =
                PriceSeriesDataGenerator(
                    assetIds = testAssetIds,
                    startDate = testStartDate,
                    endDate = testEndDate,
                    random = fixedRandom,
                )

            assertThat(generator).isInstanceOf(IDataGenerator::class.java)
        }

        @Test
        fun `empty asset ids list _ throws illegal argument exception`() {
            assertThatThrownBy {
                PriceSeriesDataGenerator(
                    assetIds = emptyList(),
                    startDate = testStartDate,
                    endDate = testEndDate,
                )
            }.isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `end date before start date _ throws illegal argument exception`() {
            assertThatThrownBy {
                PriceSeriesDataGenerator(
                    assetIds = testAssetIds,
                    startDate = testEndDate,
                    endDate = testStartDate,
                )
            }.isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `zero initial price _ throws illegal argument exception`() {
            assertThatThrownBy {
                PriceSeriesDataGenerator(
                    assetIds = testAssetIds,
                    startDate = testStartDate,
                    endDate = testEndDate,
                    initialPrice = 0.0,
                )
            }.isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `negative initial price _ throws illegal argument exception`() {
            assertThatThrownBy {
                PriceSeriesDataGenerator(
                    assetIds = testAssetIds,
                    startDate = testStartDate,
                    endDate = testEndDate,
                    initialPrice = -10.0,
                )
            }.isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    @Nested
    inner class GetColumnNames {
        @Test
        fun `default state _ returns correct column names`() {
            val generator =
                PriceSeriesDataGenerator(
                    assetIds = testAssetIds,
                    startDate = testStartDate,
                    endDate = testEndDate,
                    random = fixedRandom,
                )

            val columnNames = generator.getColumnNames()

            assertThat(columnNames).containsExactly("asset_id", "date", "price")
        }
    }

    @Nested
    inner class HasMoreRows {
        @Test
        fun `initial state _ returns true`() {
            val generator =
                PriceSeriesDataGenerator(
                    assetIds = testAssetIds,
                    startDate = testStartDate,
                    endDate = testEndDate,
                    random = fixedRandom,
                )

            val hasMoreRows = generator.hasMoreRows()

            assertThat(hasMoreRows).isTrue()
        }

        @Test
        fun `after generating all rows _ returns false`() {
            val singleAssetId = UUID.fromString("00000000-0000-0000-0000-000000000001")
            val generator =
                PriceSeriesDataGenerator(
                    assetIds = listOf(singleAssetId),
                    startDate = testStartDate,
                    endDate = testStartDate,
                    random = fixedRandom,
                )

            generator.getNextRow()
            val hasMoreRows = generator.hasMoreRows()

            assertThat(hasMoreRows).isFalse()
        }
    }

    @Nested
    inner class GetNextRow {
        @Test
        fun `valid state _ returns row with correct structure`() {
            val generator =
                PriceSeriesDataGenerator(
                    assetIds = testAssetIds,
                    startDate = testStartDate,
                    endDate = testEndDate,
                    random = fixedRandom,
                )

            val row = generator.getNextRow()

            assertThat(row).hasSize(3)
        }

        @Test
        fun `valid state _ returns asset id as uuid`() {
            val generator =
                PriceSeriesDataGenerator(
                    assetIds = testAssetIds,
                    startDate = testStartDate,
                    endDate = testEndDate,
                    random = fixedRandom,
                )

            val row = generator.getNextRow()
            val assetId = row[0]

            assertThat(assetId).isInstanceOf(UUID::class.java)
        }

        @Test
        fun `valid state _ returns date as local date`() {
            val generator =
                PriceSeriesDataGenerator(
                    assetIds = testAssetIds,
                    startDate = testStartDate,
                    endDate = testEndDate,
                    random = fixedRandom,
                )

            val row = generator.getNextRow()
            val date = row[1]

            assertThat(date).isInstanceOf(LocalDate::class.java)
        }

        @Test
        fun `valid state _ returns price as double`() {
            val generator =
                PriceSeriesDataGenerator(
                    assetIds = testAssetIds,
                    startDate = testStartDate,
                    endDate = testEndDate,
                    random = fixedRandom,
                )

            val row = generator.getNextRow()
            val price = row[2]

            assertThat(price).isInstanceOf(Number::class.java)
        }

        @Test
        fun `valid state _ returns positive price`() {
            val generator =
                PriceSeriesDataGenerator(
                    assetIds = testAssetIds,
                    startDate = testStartDate,
                    endDate = testEndDate,
                    random = fixedRandom,
                )

            val row = generator.getNextRow()
            val price = row[2] as Double

            assertThat(price).isPositive()
        }

        @Test
        fun `first row _ returns first asset for start date`() {
            val generator =
                PriceSeriesDataGenerator(
                    assetIds = testAssetIds,
                    startDate = testStartDate,
                    endDate = testEndDate,
                    random = fixedRandom,
                )

            val row = generator.getNextRow()
            val assetId = row[0]
            val date = row[1]

            assertThat(assetId).isEqualTo(testAssetIds[0])
            assertThat(date).isEqualTo(testStartDate)
        }

        @Test
        fun `three asset ids with three dates _ generates nine rows total`() {
            val generator =
                PriceSeriesDataGenerator(
                    assetIds = testAssetIds,
                    startDate = testStartDate,
                    endDate = testEndDate,
                    random = fixedRandom,
                )

            var rowCount = 0
            while (generator.hasMoreRows()) {
                generator.getNextRow()
                rowCount++
            }

            assertThat(rowCount).isEqualTo(9)
        }

        @Test
        fun `multiple calls _ generates all assets for each date in sequence`() {
            val generator =
                PriceSeriesDataGenerator(
                    assetIds = testAssetIds,
                    startDate = testStartDate,
                    endDate = testEndDate,
                    random = fixedRandom,
                )

            val firstThreeRows =
                listOf(
                    generator.getNextRow(),
                    generator.getNextRow(),
                    generator.getNextRow(),
                )

            val firstRowAssetId = firstThreeRows[0][0]
            val firstRowDate = firstThreeRows[0][1]
            val secondRowAssetId = firstThreeRows[1][0]
            val secondRowDate = firstThreeRows[1][1]
            val thirdRowAssetId = firstThreeRows[2][0]
            val thirdRowDate = firstThreeRows[2][1]

            assertThat(firstRowAssetId).isEqualTo(testAssetIds[0])
            assertThat(firstRowDate).isEqualTo(testStartDate)
            assertThat(secondRowAssetId).isEqualTo(testAssetIds[1])
            assertThat(secondRowDate).isEqualTo(testStartDate)
            assertThat(thirdRowAssetId).isEqualTo(testAssetIds[2])
            assertThat(thirdRowDate).isEqualTo(testStartDate)
        }

        @Test
        fun `no more rows available _ throws no such element exception`() {
            val singleAssetId = UUID.fromString("00000000-0000-0000-0000-000000000001")
            val generator =
                PriceSeriesDataGenerator(
                    assetIds = listOf(singleAssetId),
                    startDate = testStartDate,
                    endDate = testStartDate,
                    random = fixedRandom,
                )

            generator.getNextRow()

            assertThatThrownBy { generator.getNextRow() }
                .isInstanceOf(NoSuchElementException::class.java)
        }

        @Test
        fun `single asset single date _ generates one row`() {
            val singleAssetId = UUID.fromString("00000000-0000-0000-0000-000000000001")
            val generator =
                PriceSeriesDataGenerator(
                    assetIds = listOf(singleAssetId),
                    startDate = testStartDate,
                    endDate = testStartDate,
                    random = fixedRandom,
                )

            val row = generator.getNextRow()
            val assetId = row[0]
            val date = row[1]

            assertThat(assetId).isEqualTo(singleAssetId)
            assertThat(date).isEqualTo(testStartDate)
        }

        @Test
        fun `long date range _ generates correct number of rows`() {
            val singleAssetId = UUID.fromString("00000000-0000-0000-0000-000000000001")
            val longEndDate = testStartDate.plusDays(30)
            val generator =
                PriceSeriesDataGenerator(
                    assetIds = listOf(singleAssetId),
                    startDate = testStartDate,
                    endDate = longEndDate,
                    random = fixedRandom,
                )

            var rowCount = 0
            while (generator.hasMoreRows()) {
                generator.getNextRow()
                rowCount++
            }

            assertThat(rowCount).isEqualTo(31)
        }

        @Test
        fun `custom collections provided _ generates data without errors`() {
            val assetClassCollection = AssetClassCollection.createDefaultCollection()
            val marketTrendCollection = MarketTrendCollection.createDefaultCollection()
            val generator =
                PriceSeriesDataGenerator(
                    assetIds = testAssetIds,
                    startDate = testStartDate,
                    endDate = testEndDate,
                    assetClassCollection = assetClassCollection,
                    marketTrendCollection = marketTrendCollection,
                    random = fixedRandom,
                )

            val row = generator.getNextRow()

            assertThat(row).hasSize(3)
        }

        @Test
        fun `custom initial price _ influences generated price range`() {
            val singleAssetId = UUID.fromString("00000000-0000-0000-0000-000000000001")
            val initialPrice = 150.0
            val generator =
                PriceSeriesDataGenerator(
                    assetIds = listOf(singleAssetId),
                    startDate = testStartDate,
                    endDate = testStartDate,
                    initialPrice = initialPrice,
                    random = fixedRandom,
                )

            val row = generator.getNextRow()
            val generatedPrice = row[2] as Double

            assertThat(generatedPrice).isBetween(initialPrice * 0.5, initialPrice * 2.0)
        }
    }
}
