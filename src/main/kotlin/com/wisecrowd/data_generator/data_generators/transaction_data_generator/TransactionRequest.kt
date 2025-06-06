package com.wisecrowd.data_generator.data_generators.transaction_data_generator

import java.time.LocalDate
import java.util.*

/**
 * The purpose of this data class is to represent a transaction that needs
 * to be generated with all necessary details for transaction creation.
 *
 * Written by Claude Sonnet 4
 */
data class TransactionRequest(
    /** The user making the transaction */
    val userId: UUID,
    
    /** The asset being traded */
    val assetId: UUID,
    
    /** The type of transaction (buy or sell) */
    val transactionType: String,
    
    /** The date of the transaction */
    val transactionDate: LocalDate,
    
    /** The amount of money involved in the transaction */
    val amount: Double,
    
    /** The currency ID for the transaction */
    val currencyId: Int
) {
    init {
        require(transactionType == "buy" || transactionType == "sell") { 
            "Transaction type must be 'buy' or 'sell', but was: $transactionType" 
        }
        require(amount > 0.0) { "Amount must be positive, but was: $amount" }
        require(currencyId > 0) { "Currency ID must be positive, but was: $currencyId" }
    }
}