package com.wisecrowd.data_generator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import java.time.LocalDate

/**
 * The purpose of this class is to provide integration testing for the complete
 * WiseCrowd data generation pipeline with real file generation and validation.
 *
 * Written by Claude Sonnet 4
 */
class WiseCrowdDataOrchestratorIntegrationTest {
    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `generate _ small dataset _ creates valid files with expected structure`() {
        val config =
            DataGenerationConfig(
                startDate = LocalDate.of(2024, 1, 1),
                endDate = LocalDate.of(2024, 1, 2),
                numberOfAssets = 5,
                numberOfUsers = 10,
                outputDirectory = tempDir.toString(),
            )
        val generator = WiseCrowdDataOrchestrator(config)

        generator.generate()

        // Verify all files exist and have content
        validateFileExists(FileNameEnum.ASSET_DATA.fileName)
        validateFileExists(FileNameEnum.PRICE_SERIES.fileName)
        validateFileExists(FileNameEnum.USERS.fileName)
        validateFileExists(FileNameEnum.TRANSACTIONS.fileName)
        validateFileExists(FileNameEnum.USER_HOLDINGS.fileName)

        // Validate file content structure
        validateAssetDataContent()
        validatePriceSeriesContent()
        validateUsersContent()
        validateTransactionsContent()
        validateUserHoldingsContent()
    }

    @Test
    fun `generate _ custom date range _ creates price series for correct date range`() {
        val startDate = LocalDate.of(2024, 6, 1)
        val endDate = LocalDate.of(2024, 6, 5)
        val config =
            DataGenerationConfig(
                startDate = startDate,
                endDate = endDate,
                numberOfAssets = 3,
                numberOfUsers = 5,
                outputDirectory = tempDir.toString(),
            )
        val generator = WiseCrowdDataOrchestrator(config)

        generator.generate()

        val priceFile = File(tempDir.toFile(), FileNameEnum.PRICE_SERIES.fileName)
        val lines = priceFile.readLines()

        // Should have header + (5 days * 3 assets) = 16 lines
        val expectedRows = 5 * 3
        assertThat(lines.size).isEqualTo(expectedRows + 1) // +1 for header

        // Verify date range in content
        val dataLines = lines.drop(1) // Skip header
        val dates = dataLines.map { it.split("\t")[1] }.distinct()
        assertThat(dates).hasSize(5) // 5 unique dates
    }

    private fun validateFileExists(fileName: String) {
        val file = File(tempDir.toFile(), fileName)
        assertThat(file.exists()).isTrue()
        assertThat(file.length()).isGreaterThan(0)

        val lines = file.readLines()
        assertThat(lines).isNotEmpty()
        assertThat(lines[0]).isNotBlank() // Header should exist
    }

    private fun validateAssetDataContent() {
        val file = File(tempDir.toFile(), FileNameEnum.ASSET_DATA.fileName)
        val lines = file.readLines()

        // Validate header
        val header = lines[0].split("\t")
        assertThat(header).containsExactly("asset_id", "asset_class_id", "name")

        // Validate data rows
        val dataLines = lines.drop(1)
        assertThat(dataLines).hasSize(5) // 5 assets configured

        dataLines.forEach { line ->
            val columns = line.split("\t")
            assertThat(columns).hasSize(3)
            assertThat(columns[0]).isNotBlank() // asset_id
            assertThat(columns[1]).isNotBlank() // asset_class_id
            assertThat(columns[2]).isNotBlank() // name
        }
    }

    private fun validatePriceSeriesContent() {
        val file = File(tempDir.toFile(), FileNameEnum.PRICE_SERIES.fileName)
        val lines = file.readLines()

        // Validate header
        val header = lines[0].split("\t")
        assertThat(header).containsExactly("asset_id", "date", "price")

        // Validate data rows (2 days * 5 assets = 10 rows)
        val dataLines = lines.drop(1)
        assertThat(dataLines).hasSize(10)

        dataLines.forEach { line ->
            val columns = line.split("\t")
            assertThat(columns).hasSize(3)
            assertThat(columns[0]).isNotBlank() // asset_id
            assertThat(columns[1]).matches("\\d{4}-\\d{2}-\\d{2}") // date format
            assertThat(columns[2].toDouble()).isGreaterThan(0.0) // positive price
        }
    }

    private fun validateUsersContent() {
        val file = File(tempDir.toFile(), FileNameEnum.USERS.fileName)
        val lines = file.readLines()

        // Validate data rows
        val dataLines = lines.drop(1)
        assertThat(dataLines).hasSize(10) // 10 users configured

        dataLines.forEach { line ->
            val columns = line.split("\t")
            assertThat(columns.size).isGreaterThanOrEqualTo(7) // At least 7 columns expected
            assertThat(columns[0]).isNotBlank() // user_id
        }
    }

    private fun validateTransactionsContent() {
        val file = File(tempDir.toFile(), FileNameEnum.TRANSACTIONS.fileName)
        val lines = file.readLines()

        if (lines.size > 1) { // May have no transactions for small datasets
            val dataLines = lines.drop(1)
            dataLines.forEach { line ->
                val columns = line.split("\t")
                assertThat(columns.size).isGreaterThanOrEqualTo(6) // At least 6 columns expected
                assertThat(columns[0]).isNotBlank() // transaction_id
                assertThat(columns[1]).isNotBlank() // user_id
                assertThat(columns[2]).isNotBlank() // asset_id
            }
        }
    }

    private fun validateUserHoldingsContent() {
        val file = File(tempDir.toFile(), FileNameEnum.USER_HOLDINGS.fileName)
        val lines = file.readLines()

        if (lines.size > 1) { // May have no holdings for small datasets
            val dataLines = lines.drop(1)
            dataLines.forEach { line ->
                val columns = line.split("\t")
                assertThat(columns.size).isGreaterThanOrEqualTo(4) // At least 4 columns expected
                assertThat(columns[0]).isNotBlank() // user_id
                assertThat(columns[1]).isNotBlank() // asset_id
            }
        }
    }
}
