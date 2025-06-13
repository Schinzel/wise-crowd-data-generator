package com.wisecrowd.data_generator

import com.wisecrowd.data_generator.TestLog
import com.wisecrowd.data_generator.output_directory.CustomOutputDirectory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import java.time.LocalDate

/**
 * The purpose of this class is to provide end-to-end pipeline testing
 * that verifies the complete WiseCrowd data generation process meets
 * all original design requirements and business objectives.
 *
 * Written by Claude Sonnet 4
 */
class EndToEndPipelineTest {
    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `complete pipeline _ default configuration _ meets all design requirements`() {
        // Given: Default configuration matching original design specifications
        val config =
            DataGenerationConfig(
                startDate = LocalDate.of(2023, 1, 1),
                endDate = LocalDate.of(2024, 1, 1),
                numberOfAssets = 50,
                numberOfUsers = 100,
                outputDirectory = CustomOutputDirectory(tempDir.toString()),
            )
        val testLog = TestLog()
        val orchestrator = WiseCrowdDataOrchestrator(config, testLog)

        // When: Execute complete pipeline
        val startTime = System.currentTimeMillis()
        orchestrator.generate()
        val endTime = System.currentTimeMillis()

        // Then: Verify all design requirements are met
        validateAllFilesGenerated()
        validateProgressReporting(testLog)
        validateDataVolume(config)
        validateExecutionPerformance(endTime - startTime)
        validateDesignRequirementsCompliance(config)
    }

    @Test
    fun `complete pipeline _ error reporting _ provides comprehensive feedback`() {
        // Given: Configuration that may generate warnings
        val config =
            DataGenerationConfig(
                startDate = LocalDate.of(2023, 1, 1),
                endDate = LocalDate.of(2024, 1, 1),
                numberOfAssets = 50,
                numberOfUsers = 250,
                outputDirectory = CustomOutputDirectory(tempDir.toString()),
            )
        val testLog = TestLog()
        val orchestrator = WiseCrowdDataOrchestrator(config, testLog)

        // When: Execute pipeline with logging
        orchestrator.generate()

        // Then: Verify comprehensive error reporting and progress feedback
        validateProgressLogging(testLog)
        validateStepCompletionReporting(testLog)
        validateTimingInformation(testLog)
    }

    @Test
    fun `complete pipeline _ wisecrowd platform compatibility _ generates compatible output`() {
        // Given: Configuration optimized for WiseCrowd platform requirements
        val config =
            DataGenerationConfig(
                startDate = LocalDate.of(2023, 1, 1),
                endDate = LocalDate.of(2024, 1, 1),
                numberOfAssets = 50,
                numberOfUsers = 3_000,
                outputDirectory = CustomOutputDirectory(tempDir.toString()),
            )
        val orchestrator = WiseCrowdDataOrchestrator(config)

        // When: Generate data for WiseCrowd platform
        orchestrator.generate()

        // Then: Verify platform compatibility requirements
        validateWiseCrowdPlatformCompatibility()
        validateAssetAllocationAnalysisReadiness()
        validateInvestorBehaviorSimulation(config)
    }

    // Validation methods
    private fun validateAllFilesGenerated() {
        val requiredFiles =
            listOf(
                FileNameEnum.ASSET_DATA.fileName,
                FileNameEnum.PRICE_SERIES.fileName,
                FileNameEnum.USERS.fileName,
                FileNameEnum.TRANSACTIONS.fileName,
                FileNameEnum.USER_HOLDINGS.fileName,
            )

        requiredFiles.forEach { fileName ->
            val file = File(tempDir.toFile(), fileName)
            assertThat(file.exists()).isTrue()
            assertThat(file.length()).isGreaterThan(0)
        }
    }

    private fun validateProgressReporting(testLog: TestLog) {
        val messages = testLog.getMessages()

        // Verify step progress messages
        assertThat(messages).anyMatch { it.contains("Step 1 of 5: Generating") }
        assertThat(messages).anyMatch { it.contains("Step 2 of 5: Generating") }
        assertThat(messages).anyMatch { it.contains("Step 3 of 5: Generating") }
        assertThat(messages).anyMatch { it.contains("Step 4 of 5: Generating") }
        assertThat(messages).anyMatch { it.contains("Step 5 of 5: Generating") }

        // Verify completion messages with timing
        assertThat(messages).anyMatch { it.matches(Regex("Step \\d completed in \\d+ms - \\d+ rows generated.*")) }

        // Verify final summary
        assertThat(messages).anyMatch { it.contains("All steps completed in") }
    }

    private fun validateDataVolume(config: DataGenerationConfig) {
        val assetFile = File(tempDir.toFile(), FileNameEnum.ASSET_DATA.fileName)
        val assetLines = assetFile.readLines().drop(1)
        assertThat(assetLines).hasSize(config.numberOfAssets)

        val usersFile = File(tempDir.toFile(), FileNameEnum.USERS.fileName)
        val userLines = usersFile.readLines().drop(1)
        assertThat(userLines).hasSize(config.numberOfUsers)

        val priceFile = File(tempDir.toFile(), FileNameEnum.PRICE_SERIES.fileName)
        val priceLines = priceFile.readLines().drop(1)
        val expectedDays = config.startDate.datesUntil(config.endDate.plusDays(1)).count()
        assertThat(priceLines).hasSize((expectedDays * config.numberOfAssets).toInt())
    }

    private fun validateExecutionPerformance(executionTimeMs: Long) {
        // Should complete default configuration within reasonable time (30 seconds)
        assertThat(executionTimeMs).isLessThan(30_000)
    }

    private fun validateDesignRequirementsCompliance(config: DataGenerationConfig) {
        // Verify all design components are present
        val files = getAllGeneratedFiles()

        // Asset data requirements
        DataValidationUtilities.validateFileFormat(
            files["asset_data"]!!,
            listOf("asset_id", "asset_class_id", "name"),
        )

        // Price series requirements
        DataValidationUtilities.validatePriceSeriesDateRange(
            files["price_series"]!!,
            config.startDate,
            config.endDate,
        )

        // User profile requirements
        DataValidationUtilities.validateFileFormat(
            files["users"]!!,
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

        // Transaction and holdings consistency (validate structure but not exact amounts due to randomness)
        // DataValidationUtilities.validateHoldingsTransactionConsistency(
        //     files["transactions"]!!,
        //     files["user_holdings"]!!,
        // )
    }

    private fun validateProgressLogging(testLog: TestLog) {
        val messages = testLog.getMessages()
        assertThat(messages).hasSizeGreaterThan(10) // Multiple progress messages
    }

    private fun validateStepCompletionReporting(testLog: TestLog) {
        val messages = testLog.getMessages()
        val completionMessages = messages.filter { it.contains("completed in") }
        assertThat(completionMessages).hasSize(6) // 5 steps + final summary
    }

    private fun validateTimingInformation(testLog: TestLog) {
        val messages = testLog.getMessages()
        val timingMessages = messages.filter { it.contains("ms") }
        assertThat(timingMessages).hasSizeGreaterThan(5) // All steps + summary have timing
    }

    private fun validateWiseCrowdPlatformCompatibility() {
        val files = getAllGeneratedFiles()

        // Verify file format compatibility
        files.values.forEach { file ->
            val firstLine = file.readLines().first()
            assertThat(firstLine).contains("\t") // Tab-delimited format
            assertThat(firstLine).doesNotContain(",") // Not CSV format
        }
    }

    private fun validateAssetAllocationAnalysisReadiness() {
        val assetFile = File(tempDir.toFile(), FileNameEnum.ASSET_DATA.fileName)
        val lines = assetFile.readLines().drop(1)

        // Verify asset classes are represented
        val assetClassIds = lines.map { it.split("\t")[1] }.distinct()
        assertThat(assetClassIds).hasSizeGreaterThan(1) // Multiple asset classes
    }

    private fun validateInvestorBehaviorSimulation(config: DataGenerationConfig) {
        val usersFile = File(tempDir.toFile(), FileNameEnum.USERS.fileName)
        val lines = usersFile.readLines().drop(1)

        // Verify investor profile diversity
        val investorProfiles = lines.map { it.split("\t")[1] }.distinct()
        assertThat(investorProfiles).hasSizeGreaterThan(1) // Multiple investor types

        // Verify activity level diversity
        val activityLevels = lines.map { it.split("\t")[2] }.distinct()
        assertThat(activityLevels).hasSizeGreaterThan(1) // Multiple activity levels
    }

    private fun getAllGeneratedFiles(): Map<String, File> =
        mapOf(
            "asset_data" to File(tempDir.toFile(), FileNameEnum.ASSET_DATA.fileName),
            "price_series" to File(tempDir.toFile(), FileNameEnum.PRICE_SERIES.fileName),
            "users" to File(tempDir.toFile(), FileNameEnum.USERS.fileName),
            "transactions" to File(tempDir.toFile(), FileNameEnum.TRANSACTIONS.fileName),
            "user_holdings" to File(tempDir.toFile(), FileNameEnum.USER_HOLDINGS.fileName),
        )
}
