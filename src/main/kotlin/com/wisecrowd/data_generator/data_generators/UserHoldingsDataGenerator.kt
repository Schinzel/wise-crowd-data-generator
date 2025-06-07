package com.wisecrowd.data_generator.data_generators

import java.util.*

/**
 * The purpose of this class is to generate user holdings data by processing
 * transaction data to calculate current net positions for each user-asset combination.
 *
 * This generator implements IDataGenerator to produce user_holdings.txt file contents
 * by aggregating all buy and sell transactions to determine each user's current
 * portfolio holdings. The generator processes transactions chronologically and 
 * maintains running balances to calculate final positions.
 *
 * Only positive holdings (net buy positions) are included in the output, as
 * users cannot have negative asset holdings in this system. The generator
 * handles multiple currencies by preserving the original transaction currencies
 * for each holding position.
 *
 * Written by Claude Sonnet 4
 */
class UserHoldingsDataGenerator(
    transactionData: List<List<Any>>
) : IDataGenerator {

    // Current position in the generated holdings list
    private var currentIndex = 0
    
    // Pre-calculated holdings for consistent iteration behavior
    private val calculatedHoldings: List<List<Any>>

    init {
        // Validate input parameters using defensive programming principles
        require(transactionData.isNotEmpty()) { "Transaction data cannot be empty" }
        
        // Calculate all holdings during initialization for consistent iteration
        calculatedHoldings = calculateUserHoldings(transactionData)
    }

    override fun getColumnNames(): List<String> {
        return listOf(
            "user_id",
            "asset_id", 
            "amount",
            "currency_id"
        )
    }

    override fun hasMoreRows(): Boolean {
        return currentIndex < calculatedHoldings.size
    }

    override fun getNextRow(): List<Any> {
        if (!hasMoreRows()) {
            throw NoSuchElementException("No more rows available in generator")
        }

        return calculatedHoldings[currentIndex++]
    }

    /**
     * Calculates user holdings by processing all transactions and aggregating by user-asset-currency
     * 
     * This method processes all transactions to calculate net positions for each
     * user-asset-currency combination. Buy transactions add to holdings while sell
     * transactions reduce holdings. Only positive net positions are included in
     * the final output, as users cannot hold negative asset positions.
     * 
     * @param rawTransactionData The raw transaction data as nested lists
     * @return List of holding rows in List<Any> format for iteration
     */
    private fun calculateUserHoldings(rawTransactionData: List<List<Any>>): List<List<Any>> {
        // Map to accumulate holdings: (userId, assetId, currencyId) -> net amount
        val holdingsAccumulator = mutableMapOf<HoldingKey, Double>()
        
        // Process each transaction to update running balances
        rawTransactionData.forEach { transactionRow ->
            // Validate transaction row structure matches expected format
            require(transactionRow.size == 6) { 
                "Transaction data row must have 6 columns (transaction_id, user_id, asset_id, transaction_type, amount, currency_id), but had: ${transactionRow.size}" 
            }
            
            val userId = transactionRow[1] as UUID           // user_id
            val assetId = transactionRow[2] as UUID          // asset_id  
            val transactionType = transactionRow[3] as String // transaction_type
            val amount = transactionRow[4] as Double         // amount
            val currencyId = transactionRow[5] as Int        // currency_id
            
            // Validate transaction type is valid
            require(transactionType == "buy" || transactionType == "sell") { 
                "Transaction type must be 'buy' or 'sell', but was: $transactionType" 
            }
            require(amount > 0.0) { "Transaction amount must be positive, but was: $amount" }
            
            // Create unique key for this user-asset-currency combination
            val holdingKey = HoldingKey(userId, assetId, currencyId)
            
            // Update running balance based on transaction type
            val currentBalance = holdingsAccumulator.getOrDefault(holdingKey, 0.0)
            val newBalance = when (transactionType) {
                "buy" -> currentBalance + amount    // Buy transactions increase holdings
                "sell" -> currentBalance - amount   // Sell transactions decrease holdings
                else -> throw IllegalStateException("Invalid transaction type: $transactionType")
            }
            
            holdingsAccumulator[holdingKey] = newBalance
        }
        
        // Convert positive holdings to output format
        val result = mutableListOf<List<Any>>()
        holdingsAccumulator.forEach { (key, amount) ->
            // Only include positive holdings (users cannot have negative asset positions)
            if (amount > 0.0) {
                val holdingRow = listOf<Any>(
                    key.userId,      // user_id
                    key.assetId,     // asset_id
                    amount,          // amount (net positive holding)
                    key.currencyId   // currency_id
                )
                result.add(holdingRow)
            }
        }
        
        return result
    }

    /**
     * Data class representing a unique holding position key
     * 
     * The purpose of this data class is to create a unique identifier for each
     * user-asset-currency combination to properly aggregate transactions into
     * net holdings positions.
     */
    private data class HoldingKey(
        val userId: UUID,
        val assetId: UUID,
        val currencyId: Int
    )
}