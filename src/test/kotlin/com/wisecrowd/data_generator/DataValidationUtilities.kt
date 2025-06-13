package com.wisecrowd.data_generator

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * The purpose of this object is to provide comprehensive data validation
 * utilities for verifying the consistency and correctness of generated
 * WiseCrowd data files across the complete pipeline.
 *
 * Written by Claude Sonnet 4
 */
object DataValidationUtilities {
    /**
     * Validates that all asset IDs in price_series.txt exist in asset_data.txt
     */
    fun validateAssetIdConsistency(
        assetDataFile: File,
        priceSeriesFile: File,
    ) {
        val assetIds = extractAssetIds(assetDataFile)
        val priceSeriesAssetIds = extractAssetIdsFromPriceSeries(priceSeriesFile)

        priceSeriesAssetIds.forEach { assetId ->
            assertThat(assetIds).contains(assetId)
        }
    }

    /**
     * Validates that all user IDs are consistent across users.txt, transactions.txt, and user_holdings.txt
     */
    fun validateUserIdConsistency(
        usersFile: File,
        transactionsFile: File,
        userHoldingsFile: File,
    ) {
        val userIds = extractUserIds(usersFile)
        val transactionUserIds = extractUserIdsFromTransactions(transactionsFile)
        val holdingsUserIds = extractUserIdsFromHoldings(userHoldingsFile)

        transactionUserIds.forEach { userId ->
            assertThat(userIds).contains(userId)
        }

        holdingsUserIds.forEach { userId ->
            assertThat(userIds).contains(userId)
        }
    }

    /**
     * Validates that price series covers the complete date range for all assets
     */
    fun validatePriceSeriesDateRange(
        priceSeriesFile: File,
        expectedStartDate: LocalDate,
        expectedEndDate: LocalDate,
    ) {
        val lines = priceSeriesFile.readLines().drop(1) // Skip header
        val assetDatePairs =
            lines.map { line ->
                val columns = line.split("\t")
                val assetId = columns[0]
                val date = LocalDate.parse(columns[1], DateTimeFormatter.ISO_LOCAL_DATE)
                assetId to date
            }

        val assetIds = assetDatePairs.map { it.first }.distinct()
        val expectedDates = generateDateRange(expectedStartDate, expectedEndDate)

        assetIds.forEach { assetId ->
            val assetDates = assetDatePairs.filter { it.first == assetId }.map { it.second }
            expectedDates.forEach { expectedDate ->
                assertThat(assetDates).contains(expectedDate)
            }
        }
    }

    /**
     * Validates that user holdings are consistent with transaction history
     */
    fun validateHoldingsTransactionConsistency(
        transactionsFile: File,
        userHoldingsFile: File,
    ) {
        val transactions = parseTransactions(transactionsFile)
        val holdings = parseHoldings(userHoldingsFile)

        val calculatedHoldings = calculateHoldingsFromTransactions(transactions)

        holdings.forEach { (userAssetKey, holdingAmount) ->
            val calculatedAmount = calculatedHoldings[userAssetKey] ?: 0.0
            assertThat(holdingAmount).isCloseTo(
                calculatedAmount,
                org.assertj.core.data.Offset
                    .offset(1.0),
            )
        }
    }

    /**
     * Validates that no orphaned references exist between files
     */
    fun validateNoOrphanedReferences(
        assetDataFile: File,
        usersFile: File,
        transactionsFile: File,
        userHoldingsFile: File,
    ) {
        val assetIds = extractAssetIds(assetDataFile)
        val userIds = extractUserIds(usersFile)

        // Check transactions don't reference non-existent assets or users
        val transactions = parseTransactions(transactionsFile)
        transactions.forEach { transaction ->
            assertThat(assetIds).contains(transaction.assetId)
            assertThat(userIds).contains(transaction.userId)
        }

        // Check holdings don't reference non-existent assets or users
        val holdings = parseHoldings(userHoldingsFile)
        holdings.keys.forEach { userAssetKey ->
            val (userId, assetId) = userAssetKey.split("_")
            assertThat(userIds).contains(userId)
            assertThat(assetIds).contains(assetId)
        }
    }

    /**
     * Validates file format and structure
     */
    fun validateFileFormat(
        file: File,
        expectedHeaders: List<String>,
    ) {
        assertThat(file.exists()).isTrue()
        assertThat(file.length()).isGreaterThan(0)

        val lines = file.readLines()
        assertThat(lines).isNotEmpty()

        val headers = lines[0].split("\t")
        assertThat(headers).containsExactlyElementsOf(expectedHeaders)

        if (lines.size > 1) {
            val dataLines = lines.drop(1)
            dataLines.forEach { line ->
                val columns = line.split("\t")
                assertThat(columns).hasSize(expectedHeaders.size)
                columns.forEach { column ->
                    assertThat(column).isNotBlank()
                }
            }
        }
    }

    /**
     * Validates that generated data is within expected ranges
     */
    fun validateDataRanges(
        assetDataFile: File,
        usersFile: File,
        expectedAssetCount: Int,
        expectedUserCount: Int,
    ) {
        val assetLines = assetDataFile.readLines().drop(1)
        assertThat(assetLines).hasSize(expectedAssetCount)

        val userLines = usersFile.readLines().drop(1)
        assertThat(userLines).hasSize(expectedUserCount)
    }

    // Private helper methods
    private fun extractAssetIds(assetDataFile: File): Set<String> =
        assetDataFile
            .readLines()
            .drop(1)
            .map { line ->
                line.split("\t")[0]
            }.toSet()

    private fun extractAssetIdsFromPriceSeries(priceSeriesFile: File): Set<String> =
        priceSeriesFile
            .readLines()
            .drop(1)
            .map { line ->
                line.split("\t")[0]
            }.toSet()

    private fun extractUserIds(usersFile: File): Set<String> =
        usersFile
            .readLines()
            .drop(1)
            .map { line ->
                line.split("\t")[0]
            }.toSet()

    private fun extractUserIdsFromTransactions(transactionsFile: File): Set<String> {
        val lines = transactionsFile.readLines().drop(1)
        if (lines.isEmpty()) return emptySet()

        return lines
            .map { line ->
                line.split("\t")[1] // user_id is second column
            }.toSet()
    }

    private fun extractUserIdsFromHoldings(userHoldingsFile: File): Set<String> {
        val lines = userHoldingsFile.readLines().drop(1)
        if (lines.isEmpty()) return emptySet()

        return lines
            .map { line ->
                line.split("\t")[0] // user_id is first column
            }.toSet()
    }

    private fun generateDateRange(
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        var currentDate = startDate
        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate)
            currentDate = currentDate.plusDays(1)
        }
        return dates
    }

    private data class Transaction(
        val transactionId: String,
        val userId: String,
        val assetId: String,
        val transactionType: String,
        val amount: Double,
        val currency: String,
    )

    private fun parseTransactions(transactionsFile: File): List<Transaction> {
        val lines = transactionsFile.readLines().drop(1)
        if (lines.isEmpty()) return emptyList()

        return lines.map { line ->
            val columns = line.split("\t")
            Transaction(
                transactionId = columns[0],
                userId = columns[1],
                assetId = columns[2],
                transactionType = columns[3],
                amount = columns[4].toDouble(),
                currency = columns[5],
            )
        }
    }

    private fun parseHoldings(userHoldingsFile: File): Map<String, Double> {
        val lines = userHoldingsFile.readLines().drop(1)
        if (lines.isEmpty()) return emptyMap()

        return lines.associate { line ->
            val columns = line.split("\t")
            val userAssetKey = "${columns[0].removeStringQualifiers()}_${columns[1].removeStringQualifiers()}"
            val amount = columns[2].toDouble()
            userAssetKey to amount
        }
    }

    private fun calculateHoldingsFromTransactions(transactions: List<Transaction>): Map<String, Double> {
        val holdings = mutableMapOf<String, Double>()

        transactions.forEach { transaction ->
            val key = "${transaction.userId.removeStringQualifiers()}_${transaction.assetId.removeStringQualifiers()}"
            val currentAmount = holdings.getOrDefault(key, 0.0)

            val transactionType = transaction.transactionType.removeStringQualifiers().lowercase()
            val newAmount =
                when (transactionType) {
                    "buy" -> currentAmount + transaction.amount
                    "sell" -> currentAmount - transaction.amount
                    else -> currentAmount
                }

            if (newAmount > 0.0) {
                holdings[key] = newAmount
            } else {
                holdings.remove(key)
            }
        }

        return holdings
    }

    private fun String.removeStringQualifiers(): String = this.removePrefix("###").removeSuffix("###")
}
