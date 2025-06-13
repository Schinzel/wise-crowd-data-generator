package com.wisecrowd.data_generator

import com.wisecrowd.data_generator.output_directory.CustomOutputDirectory
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate

/**
 * The purpose of this class is to provide comprehensive integration testing
 * for the complete WiseCrowd data generation pipeline, including data validation,
 * consistency checks, and error scenarios.
 *
 * Written by Claude Sonnet 4
 */
class ComprehensiveIntegrationTest {
    @TempDir
    lateinit var tempDir: Path

    @Nested
    inner class SmallDatasetScenarios {
        @Test
        fun `generate _ small dataset _ produces consistent and valid data`() {
            val config =
                DataGenerationConfig(
                    startDate = LocalDate.of(2024, 1, 1),
                    endDate = LocalDate.of(2024, 1, 7),
                    numberOfAssets = 10,
                    numberOfUsers = 50,
                    outputDirectory = CustomOutputDirectory(tempDir.toString()),
                )
            val orchestrator = WiseCrowdDataOrchestrator(config)

            orchestrator.generate()

            val files = getGeneratedFiles()

            // Validate all files exist and have correct format
            validateAllFileFormats(files)

            // Validate data consistency across files
            DataValidationUtilities.validateAssetIdConsistency(files.assetData, files.priceSeries)
            DataValidationUtilities.validateUserIdConsistency(files.users, files.transactions, files.userHoldings)
            DataValidationUtilities.validatePriceSeriesDateRange(files.priceSeries, config.startDate, config.endDate)
            DataValidationUtilities.validateHoldingsTransactionConsistency(files.transactions, files.userHoldings)
            DataValidationUtilities.validateNoOrphanedReferences(
                files.assetData,
                files.users,
                files.transactions,
                files.userHoldings,
            )

            // Validate data ranges
            DataValidationUtilities.validateDataRanges(
                files.assetData,
                files.users,
                config.numberOfAssets,
                config.numberOfUsers,
            )
        }

        @Test
        fun `generate _ minimal dataset _ handles edge case successfully`() {
            val config =
                DataGenerationConfig(
                    startDate = LocalDate.of(2024, 6, 1),
                    endDate = LocalDate.of(2024, 6, 1), // Single day
                    numberOfAssets = 1,
                    numberOfUsers = 1,
                    outputDirectory = CustomOutputDirectory(tempDir.toString()),
                )
            val orchestrator = WiseCrowdDataOrchestrator(config)

            orchestrator.generate()

            val files = getGeneratedFiles()
            validateAllFileFormats(files)

            // Verify minimal data
            val assetLines = files.assetData.readLines().drop(1)
            assertThat(assetLines).hasSize(1)

            val userLines = files.users.readLines().drop(1)
            assertThat(userLines).hasSize(1)

            val priceLines = files.priceSeries.readLines().drop(1)
            assertThat(priceLines).hasSize(1) // 1 asset * 1 day
        }

        @Test
        fun `generate _ medium dataset _ processes efficiently`() {
            val config =
                DataGenerationConfig(
                    startDate = LocalDate.of(2024, 1, 1),
                    endDate = LocalDate.of(2024, 1, 31),
                    numberOfAssets = 50,
                    numberOfUsers = 500,
                    outputDirectory = CustomOutputDirectory(tempDir.toString()),
                )
            val orchestrator = WiseCrowdDataOrchestrator(config)

            val startTime = System.currentTimeMillis()
            orchestrator.generate()
            val endTime = System.currentTimeMillis()

            // Should complete within reasonable time (less than 10 seconds)
            val executionTime = endTime - startTime
            assertThat(executionTime).isLessThan(10_000)

            val files = getGeneratedFiles()
            DataValidationUtilities.validateDataRanges(
                files.assetData,
                files.users,
                config.numberOfAssets,
                config.numberOfUsers,
            )

            // Verify expected price series volume (31 days * 50 assets = 1,550 rows)
            val priceLines = files.priceSeries.readLines().drop(1)
            assertThat(priceLines).hasSize(31 * 50)
        }
    }

    @Nested
    inner class CustomConfigurationScenarios {
        @Test
        fun `generate _ custom date range _ validates correct time coverage`() {
            val startDate = LocalDate.of(2023, 12, 25)
            val endDate = LocalDate.of(2024, 1, 5)
            val config =
                DataGenerationConfig(
                    startDate = startDate,
                    endDate = endDate,
                    numberOfAssets = 5,
                    numberOfUsers = 20,
                    outputDirectory = CustomOutputDirectory(tempDir.toString()),
                )
            val orchestrator = WiseCrowdDataOrchestrator(config)

            orchestrator.generate()

            val files = getGeneratedFiles()
            DataValidationUtilities.validatePriceSeriesDateRange(files.priceSeries, startDate, endDate)

            // Verify exactly 12 days of data (Dec 25-31 + Jan 1-5)
            val priceLines = files.priceSeries.readLines().drop(1)
            assertThat(priceLines).hasSize(12 * 5) // 12 days * 5 assets
        }

        @Test
        fun `generate _ weekend date range _ includes all calendar days`() {
            val startDate = LocalDate.of(2024, 6, 8) // Saturday
            val endDate = LocalDate.of(2024, 6, 9) // Sunday
            val config =
                DataGenerationConfig(
                    startDate = startDate,
                    endDate = endDate,
                    numberOfAssets = 3,
                    numberOfUsers = 10,
                    outputDirectory = CustomOutputDirectory(tempDir.toString()),
                )
            val orchestrator = WiseCrowdDataOrchestrator(config)

            orchestrator.generate()

            val files = getGeneratedFiles()
            val priceLines = files.priceSeries.readLines().drop(1)
            assertThat(priceLines).hasSize(2 * 3) // 2 days * 3 assets (includes weekends)
        }

        @Test
        fun `generate _ large asset count _ produces correct volume`() {
            val config =
                DataGenerationConfig(
                    startDate = LocalDate.of(2024, 6, 1),
                    endDate = LocalDate.of(2024, 6, 3),
                    numberOfAssets = 200,
                    numberOfUsers = 100,
                    outputDirectory = CustomOutputDirectory(tempDir.toString()),
                )
            val orchestrator = WiseCrowdDataOrchestrator(config)

            orchestrator.generate()

            val files = getGeneratedFiles()
            val assetLines = files.assetData.readLines().drop(1)
            assertThat(assetLines).hasSize(200)

            val priceLines = files.priceSeries.readLines().drop(1)
            assertThat(priceLines).hasSize(3 * 200) // 3 days * 200 assets
        }
    }

    @Nested
    inner class ErrorScenarios {
        @Test
        fun `generate _ read_only_directory _ handles gracefully`() {
            val readOnlyDir = tempDir.resolve("readonly")
            Files.createDirectory(readOnlyDir)
            readOnlyDir.toFile().setReadOnly()

            val config =
                DataGenerationConfig(
                    startDate = LocalDate.of(2024, 1, 1),
                    endDate = LocalDate.of(2024, 1, 2),
                    numberOfAssets = 5,
                    numberOfUsers = 10,
                    outputDirectory = CustomOutputDirectory(readOnlyDir.toString()),
                )
            val orchestrator = WiseCrowdDataOrchestrator(config)

            // Should handle permission errors gracefully
            assertThatThrownBy {
                orchestrator.generate()
            }.isInstanceOf(Exception::class.java)

            // Cleanup
            readOnlyDir.toFile().setWritable(true)
        }
    }

    @Nested
    inner class DataConsistencyValidation {
        @Test
        fun `generate _ verify cross_file_references _ maintains referential integrity`() {
            val config =
                DataGenerationConfig(
                    startDate = LocalDate.of(2024, 3, 1),
                    endDate = LocalDate.of(2024, 3, 5),
                    numberOfAssets = 15,
                    numberOfUsers = 75,
                    outputDirectory = CustomOutputDirectory(tempDir.toString()),
                )
            val orchestrator = WiseCrowdDataOrchestrator(config)

            orchestrator.generate()

            val files = getGeneratedFiles()

            // Comprehensive cross-file validation
            DataValidationUtilities.validateAssetIdConsistency(files.assetData, files.priceSeries)
            DataValidationUtilities.validateUserIdConsistency(files.users, files.transactions, files.userHoldings)
            DataValidationUtilities.validateNoOrphanedReferences(
                files.assetData,
                files.users,
                files.transactions,
                files.userHoldings,
            )
        }

        @Test
        fun `generate _ verify transaction_holdings_balance _ maintains financial consistency`() {
            val config =
                DataGenerationConfig(
                    startDate = LocalDate.of(2024, 4, 1),
                    endDate = LocalDate.of(2024, 4, 10),
                    numberOfAssets = 8,
                    numberOfUsers = 25,
                    outputDirectory = CustomOutputDirectory(tempDir.toString()),
                )
            val orchestrator = WiseCrowdDataOrchestrator(config)

            orchestrator.generate()

            val files = getGeneratedFiles()
            DataValidationUtilities.validateHoldingsTransactionConsistency(files.transactions, files.userHoldings)
        }

        @Test
        fun `generate _ verify complete_date_coverage _ ensures no missing dates`() {
            val startDate = LocalDate.of(2024, 2, 28)
            val endDate = LocalDate.of(2024, 3, 2) // Leap year boundary
            val config =
                DataGenerationConfig(
                    startDate = startDate,
                    endDate = endDate,
                    numberOfAssets = 6,
                    numberOfUsers = 30,
                    outputDirectory = CustomOutputDirectory(tempDir.toString()),
                )
            val orchestrator = WiseCrowdDataOrchestrator(config)

            orchestrator.generate()

            val files = getGeneratedFiles()
            DataValidationUtilities.validatePriceSeriesDateRange(files.priceSeries, startDate, endDate)
        }
    }

    // Helper methods
    private data class GeneratedFiles(
        val assetData: File,
        val priceSeries: File,
        val users: File,
        val transactions: File,
        val userHoldings: File,
    )

    private fun getGeneratedFiles(): GeneratedFiles =
        GeneratedFiles(
            assetData = File(tempDir.toFile(), FileNameEnum.ASSET_DATA.fileName),
            priceSeries = File(tempDir.toFile(), FileNameEnum.PRICE_SERIES.fileName),
            users = File(tempDir.toFile(), FileNameEnum.USERS.fileName),
            transactions = File(tempDir.toFile(), FileNameEnum.TRANSACTIONS.fileName),
            userHoldings = File(tempDir.toFile(), FileNameEnum.USER_HOLDINGS.fileName),
        )

    private fun validateAllFileFormats(files: GeneratedFiles) {
        DataValidationUtilities.validateFileFormat(
            files.assetData,
            listOf("asset_id", "asset_class_id", "name"),
        )
        DataValidationUtilities.validateFileFormat(
            files.priceSeries,
            listOf("asset_id", "date", "price"),
        )
        DataValidationUtilities.validateFileFormat(
            files.users,
            listOf(
                "user_id",
                "investor_profile_id",
                "activity_level_id",
                "country_id",
                "join_date",
                "departure_date",
                "customer_status",
            ),
        )
        DataValidationUtilities.validateFileFormat(
            files.transactions,
            listOf("transaction_id", "user_id", "asset_id", "transaction_type", "amount", "currency_id"),
        )
        DataValidationUtilities.validateFileFormat(
            files.userHoldings,
            listOf("user_id", "asset_id", "amount", "currency_id"),
        )
    }
}
