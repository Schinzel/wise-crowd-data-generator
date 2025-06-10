package com.wisecrowd.data_generator

import com.wisecrowd.data_generator.data_saver.FileDataParser
import com.wisecrowd.data_generator.data_saver.IDataSaver
import com.wisecrowd.data_generator.data_saver.SaveError
import com.wisecrowd.data_generator.data_saver.file_data_saver.FileDataSaver
import com.wisecrowd.data_generator.orchestration.AssetDataGenerationStep
import com.wisecrowd.data_generator.orchestration.PriceSeriesGenerationStep
import com.wisecrowd.data_generator.orchestration.TransactionDataGenerationStep
import com.wisecrowd.data_generator.orchestration.UserDataGenerationStep
import com.wisecrowd.data_generator.orchestration.UserHoldingsGenerationStep
import java.io.File
import java.time.LocalDate
import java.util.UUID
import kotlin.system.measureTimeMillis

/**
 * The purpose of this class is to orchestrate the complete data generation
 * pipeline for WiseCrowd platform test data with detailed progress feedback.
 *
 * This orchestrator coordinates all data generators in the correct dependency
 * sequence, handles file dependencies using FileDataParser, provides timing
 * information, and manages error collection with fail-fast error handling.
 *
 * Generation Sequence:
 * 1. Generate asset_data.txt (AssetDataGenerator)
 * 2. Generate price_series.txt (PriceSeriesDataGenerator - reads asset_data.txt)
 * 3. Generate users.txt (UserDataGenerator)
 * 4. Generate transactions.txt (TransactionDataGenerator - reads price_series.txt + users.txt)
 * 5. Generate user_holdings.txt (UserHoldingsDataGenerator - reads transactions.txt)
 *
 * Written by Claude Sonnet 4
 */
class WiseCrowdDataOrchestrator(
    private val config: DataGenerationConfig,
    private val log: ILog = SystemOutLog(),
) {
    // File parser for reading intermediate generated files
    private val fileDataParser = FileDataParser()

    // Collection of all warnings from generation process
    private val allWarnings = mutableListOf<String>()

    // Generation steps
    private val assetStep = AssetDataGenerationStep(log, ::createFileDataSaver)
    private val priceStep = PriceSeriesGenerationStep(log, ::createFileDataSaver)
    private val userStep = UserDataGenerationStep(log, ::createFileDataSaver)
    private val transactionStep = TransactionDataGenerationStep(log, ::createFileDataSaver)
    private val holdingsStep = UserHoldingsGenerationStep(log, ::createFileDataSaver)

    /**
     * Executes the complete data generation pipeline with progress feedback and timing
     *
     * Creates output directory, generates all 5 files in dependency order,
     * provides detailed progress updates, and reports warning summary.
     *
     * @throws RuntimeException if any fatal error occurs during generation
     * @throws IllegalStateException if generators encounter invalid states
     */
    fun generate() {
        // Create output directory if it doesn't exist
        createOutputDirectory()

        // Record total generation time
        val totalTime =
            measureTimeMillis {
                try {
                    // Execute all generation steps in sequence
                    val assetIds = generateAssetData()
                    generatePriceSeriesData(assetIds)
                    generateUserData()
                    generateTransactionData()
                    generateUserHoldingsData()
                } catch (e: Exception) {
                    // Clean up partial files on failure
                    cleanupPartialFiles()
                    throw e
                }
            }

        // Report completion summary
        reportCompletionSummary(totalTime)
    }

    /**
     * Creates the output directory if it doesn't already exist
     *
     * Uses the output directory path from configuration and creates all parent
     * directories as needed. Fails fast if directory creation is unsuccessful.
     *
     * @throws IllegalArgumentException if directory creation fails
     */
    private fun createOutputDirectory() {
        val outputDir = File(config.outputDirectory)
        if (!outputDir.exists()) {
            val created = outputDir.mkdirs()
            require(created) { "Failed to create output directory: ${config.outputDirectory}" }
        }
    }

    /**
     * Executes Step 1: Generate asset data and extract asset IDs for dependent steps
     *
     * Creates asset_data.txt file containing asset metadata and extracts the generated
     * asset IDs needed by price series generation. Collects any generation warnings.
     *
     * @return List of UUID asset IDs for use by price series generator
     */
    private fun generateAssetData(): List<UUID> {
        val errors = assetStep.execute(config.numberOfAssets)

        // Extract asset IDs from generated file
        val assetIds = extractAssetIds()
        collectWarnings(errors, 1)
        return assetIds
    }

    /**
     * Executes Step 2: Generate price series data for all assets across date range
     *
     * Creates price_series.txt file containing historical price data for each asset
     * across the configured date range using realistic financial modeling.
     * Depends on asset IDs from Step 1.
     *
     * @param assetIds List of asset UUIDs to generate price data for
     */
    private fun generatePriceSeriesData(assetIds: List<UUID>) {
        val errors =
            priceStep.execute(
                assetIds,
                config.startDate,
                config.endDate,
                config.numberOfAssets,
            )
        collectWarnings(errors, 2)
    }

    /**
     * Executes Step 3: Generate user profile data with realistic lifecycle patterns
     *
     * Creates users.txt file containing user profiles distributed across investor types,
     * activity levels, and countries with realistic customer lifecycle events including
     * join dates, departure dates, and status changes.
     */
    private fun generateUserData() {
        val errors =
            userStep.execute(
                config.numberOfUsers,
                config.startDate,
                config.endDate,
            )
        collectWarnings(errors, 3)
    }

    /**
     * Executes Step 4: Generate transaction data based on price series and user profiles
     *
     * Creates transactions.txt file containing buy/sell transactions that reflect
     * realistic trading behavior patterns based on user activity levels, currency
     * preferences, and available price data. Depends on data from Steps 2 and 3.
     */
    private fun generateTransactionData() {
        // Read price series and user data
        val priceSeriesData = readGeneratedFile(FileNameEnum.PRICE_SERIES.fileName)
        val userData = readGeneratedFile(FileNameEnum.USERS.fileName)

        val errors = transactionStep.execute(priceSeriesData, userData)
        collectWarnings(errors, 4)
    }

    /**
     * Executes Step 5: Generate user holdings data by aggregating transaction history
     *
     * Creates user_holdings.txt file containing current net asset positions for each
     * user by processing all transactions and calculating running balances. Only
     * positive holdings are included. Depends on data from Step 4.
     */
    private fun generateUserHoldingsData() {
        // Read transaction data
        val transactionData = readGeneratedFile(FileNameEnum.TRANSACTIONS.fileName)

        val errors = holdingsStep.execute(transactionData)
        collectWarnings(errors, 5)
    }

    /**
     * Factory function to create FileDataSaver instances for generation steps
     *
     * Creates a file data saver configured to write to the specified file within
     * the configured output directory. Used by generation steps to save their data.
     *
     * @param fileName Name of the file to create (e.g., "asset_data.txt")
     * @return Configured IDataSaver instance ready for use
     */
    private fun createFileDataSaver(fileName: String): IDataSaver {
        val filePath = File(config.outputDirectory, fileName).absolutePath
        return FileDataSaver(filePath)
    }

    /**
     * Reads a previously generated file and converts it to properly typed data
     *
     * Uses FileDataParser to read tab-delimited files and converts string data
     * back to appropriate types (UUID, LocalDate, Int, Double) based on the file type.
     * This enables dependent generators to consume properly typed data.
     *
     * @param fileName Name of the file to read from output directory
     * @return List of data rows with properly typed values for generator consumption
     */
    private fun readGeneratedFile(fileName: String): List<List<Any>> {
        val filePath = File(config.outputDirectory, fileName).absolutePath
        val stringData = fileDataParser.parseFile(filePath)

        // Convert List<List<String>> to List<List<Any>> with proper type conversion
        return when (fileName) {
            FileNameEnum.ASSET_DATA.fileName -> convertAssetData(stringData)
            FileNameEnum.PRICE_SERIES.fileName -> convertPriceSeriesData(stringData)
            FileNameEnum.USERS.fileName -> convertUserData(stringData)
            FileNameEnum.TRANSACTIONS.fileName -> convertTransactionData(stringData)
            else -> stringData.map { row -> row.map { it as Any } }
        }
    }

    /**
     * Converts asset data strings to properly typed values
     *
     * Transforms asset data from string format back to typed format with UUID
     * for asset_id, Int for asset_class_id, and String for name.
     *
     * @param stringData Raw string data from asset_data.txt file
     * @return Typed data suitable for dependent generators
     */
    private fun convertAssetData(stringData: List<List<String>>): List<List<Any>> =
        stringData.map { row ->
            require(row.size == 3) { "Asset data row must have 3 columns" }
            listOf(
                UUID.fromString(row[0]), // asset_id
                row[1].toInt(), // asset_class_id
                row[2], // name
            )
        }

    /**
     * Converts price series data strings to properly typed values
     *
     * Transforms price series data from string format back to typed format with UUID
     * for asset_id, LocalDate for date, and Double for price.
     *
     * @param stringData Raw string data from price_series.txt file
     * @return Typed data suitable for dependent generators
     */
    private fun convertPriceSeriesData(stringData: List<List<String>>): List<List<Any>> =
        stringData.map { row ->
            require(row.size == 3) { "Price series row must have 3 columns" }
            listOf(
                UUID.fromString(row[0]), // asset_id
                LocalDate.parse(row[1]), // date
                row[2].toDouble(), // price
            )
        }

    /**
     * Converts user data strings to properly typed values
     *
     * Transforms user data from string format back to typed format with proper
     * types for all user profile fields including UUID, Ints, LocalDates, and Strings.
     *
     * @param stringData Raw string data from users.txt file
     * @return Typed data suitable for dependent generators
     */
    private fun convertUserData(stringData: List<List<String>>): List<List<Any>> =
        stringData.map { row ->
            require(row.size == 7) { "User data row must have 7 columns" }
            listOf(
                UUID.fromString(row[0]), // user_id
                row[1].toInt(), // investor_profile_id
                row[2].toInt(), // activity_level_id
                row[3].toInt(), // country_id
                LocalDate.parse(row[4]), // join_date
                LocalDate.parse(row[5]), // departure_date
                row[6], // customer_status
            )
        }

    /**
     * Converts transaction data strings to properly typed values
     *
     * Transforms transaction data from string format back to typed format with UUIDs
     * for transaction, user, and asset IDs, and proper numeric types for amounts and currency.
     *
     * @param stringData Raw string data from transactions.txt file
     * @return Typed data suitable for dependent generators
     */
    private fun convertTransactionData(stringData: List<List<String>>): List<List<Any>> =
        stringData.map { row ->
            require(row.size == 6) { "Transaction data row must have 6 columns" }
            listOf(
                UUID.fromString(row[0]), // transaction_id
                UUID.fromString(row[1]), // user_id
                UUID.fromString(row[2]), // asset_id
                row[3], // transaction_type
                row[4].toDouble(), // amount
                row[5].toInt(), // currency_id
            )
        }

    /**
     * Extracts asset IDs from the generated asset data file
     *
     * Reads the asset_data.txt file and extracts the UUID asset IDs from the first
     * column. These IDs are needed by the price series generator to create consistent
     * price data for all generated assets.
     *
     * @return List of UUID asset IDs from the generated asset data
     */
    private fun extractAssetIds(): List<UUID> {
        // Read the generated asset file to extract asset IDs
        val assetData = readGeneratedFile(FileNameEnum.ASSET_DATA.fileName)
        return assetData.map { row ->
            row[0] as UUID // First column is asset_id (already converted)
        }
    }

    /**
     * Collects and categorizes warnings from generation step errors
     *
     * Processes errors from a generation step and adds them to the global warning
     * collection with step identification. Allows tracking which generation step
     * produced which warnings for comprehensive error reporting.
     *
     * @param errors List of SaveError instances from a generation step
     * @param stepNumber The step number (1-5) that produced these errors
     */
    private fun collectWarnings(
        errors: List<SaveError>,
        stepNumber: Int,
    ) {
        errors.forEach { error ->
            allWarnings.add("Step $stepNumber: ${error.message}")
        }
    }

    /**
     * Cleans up partially created files when generation fails
     *
     * Removes any files that were created during the generation process before
     * a fatal error occurred. This ensures a clean state and prevents leaving
     * incomplete or corrupted data files on the system.
     */
    private fun cleanupPartialFiles() {
        // Remove any partially created files on failure
        val outputDir = File(config.outputDirectory)
        if (outputDir.exists()) {
            FileNameEnum.entries.forEach { fileEnum ->
                val file = File(outputDir, fileEnum.fileName)
                if (file.exists()) {
                    file.delete()
                }
            }
        }
    }

    /**
     * Reports the final completion summary with timing and warning information
     *
     * Outputs the total generation time and displays a comprehensive summary of
     * any warnings that occurred during the generation process. Provides detailed
     * feedback to help users understand the generation results and any issues.
     *
     * @param totalTime Total time in milliseconds for the complete generation process
     */
    private fun reportCompletionSummary(totalTime: Long) {
        log.writeToLog("All steps completed in ${totalTime}ms")

        // Display warning summary if any warnings occurred
        if (allWarnings.isNotEmpty()) {
            log.writeToLog("")
            log.writeToLog("Warnings encountered:")
            allWarnings.forEach { warning ->
                log.writeToLog("- $warning")
            }
        }
    }
}
