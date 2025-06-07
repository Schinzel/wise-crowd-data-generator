package com.wisecrowd.data_generator.data_generators.transaction_data_generator

import com.wisecrowd.data_generator.data_generators.user_data_generator.CustomerStatus
import com.wisecrowd.data_generator.utils.WeightedRandomSelector
import java.time.LocalDate
import java.util.*
import kotlin.random.Random

/**
 * The purpose of this class is to generate individual transactions based on
 * user behavior patterns, market conditions, and customer lifecycle events.
 *
 * This class separates transaction generation logic from data management,
 * providing pure functions that return transaction data without side effects.
 * Each method handles a specific aspect of transaction generation following
 * single responsibility principle.
 *
 * Written by Claude Sonnet 4
 */
class TransactionGenerator(
    private val userCurrencyPreferences: UserCurrencyPreferences,
    private val random: Random = Random.Default
) {
    
    /**
     * Generates all transactions for a specific user based on their lifecycle and activity
     * 
     * This method orchestrates the transaction generation process by determining
     * whether to generate regular transactions, sell-off transactions, or both
     * based on the user's customer status and lifecycle dates.
     * 
     * @param userData The user's profile and lifecycle information
     * @param assetPriceCollection Price data for all available assets
     * @return List of transaction requests for this user
     */
    fun generateTransactionsForUser(
        userData: UserData,
        assetPriceCollection: AssetPriceCollection
    ): List<TransactionRequest> {
        val transactions = mutableListOf<TransactionRequest>()
        
        // Generate user-specific currency preferences based on their country
        val userCurrencySelector = userCurrencyPreferences.generateUserCurrencyPreferences(userData.countryId)
        
        // Determine the effective departure date for active vs departed customers
        val actualDepartureDate = if (userData.customerStatus == CustomerStatus.ACTIVE) {
            null
        } else {
            userData.departureDate
        }
        
        // Calculate annual transaction frequency based on activity level
        val annualTransactionCount = getAnnualTransactionCount(userData.activityLevelId)
        
        // Generate regular buy/sell transactions during active period
        val regularTransactions = generateRegularTransactions(
            userData.userId,
            userData.joinDate,
            actualDepartureDate,
            annualTransactionCount,
            assetPriceCollection,
            userCurrencySelector
        )
        transactions.addAll(regularTransactions)
        
        // Generate sell-off transactions for departed customers
        if (userData.customerStatus == CustomerStatus.DEPARTED && actualDepartureDate != null) {
            val sellOffTransactions = generateSellOffTransactions(
                userData.userId,
                actualDepartureDate,
                assetPriceCollection,
                userCurrencySelector
            )
            transactions.addAll(sellOffTransactions)
        }
        
        return transactions
    }
    
    /**
     * Generates regular buy/sell transactions during the user's active period
     * 
     * This method creates transactions spread across the user's active timeframe
     * based on their activity level. Transaction frequency is calculated as a
     * proportion of annual activity scaled to the active period length.
     * 
     * @param userId The user making the transactions
     * @param startDate When the user became active
     * @param endDate When the user departed (null if still active)
     * @param annualTransactionCount Expected transactions per year for this activity level
     * @param assetPriceCollection Price data for transaction generation
     * @param userCurrencySelector User-specific currency preference selector
     * @return List of regular transaction requests
     */
    private fun generateRegularTransactions(
        userId: UUID,
        startDate: LocalDate,
        endDate: LocalDate?,
        annualTransactionCount: Int,
        assetPriceCollection: AssetPriceCollection,
        userCurrencySelector: WeightedRandomSelector<Int>
    ): List<TransactionRequest> {
        // Determine the effective end date for transaction generation
        val effectiveEndDate = endDate ?: assetPriceCollection.getLatestPriceDate()
        
        // Validate that we have a meaningful time period
        if (effectiveEndDate <= startDate) {
            return emptyList()
        }
        
        // Calculate the number of days in the active period
        val daysInPeriod = startDate.until(effectiveEndDate).days
        if (daysInPeriod <= 0) {
            return emptyList()
        }
        
        // Scale annual transaction count to the actual period length
        // Ensure at least 1 transaction if the period is long enough (more than 1 day)
        val periodTransactionCount = if (daysInPeriod > 1) {
            ((annualTransactionCount * daysInPeriod) / 365.0).toInt().coerceAtLeast(1)
        } else {
            ((annualTransactionCount * daysInPeriod) / 365.0).toInt()
        }
        
        // Generate the calculated number of transactions
        return (0 until periodTransactionCount).mapNotNull {
            generateSingleRegularTransaction(userId, startDate, effectiveEndDate, assetPriceCollection, userCurrencySelector)
        }
    }
    
    /**
     * Generates sell-off transactions for departed customers on their departure date
     * 
     * When a customer departs, they typically liquidate their portfolio by selling
     * multiple assets. This method simulates this behavior by generating several
     * sell transactions with larger amounts than typical regular transactions.
     * 
     * @param userId The user who is departing
     * @param departureDate The date of departure
     * @param assetPriceCollection Price data for determining which assets to sell
     * @param userCurrencySelector User-specific currency preference selector
     * @return List of sell-off transaction requests
     */
    private fun generateSellOffTransactions(
        userId: UUID,
        departureDate: LocalDate,
        assetPriceCollection: AssetPriceCollection,
        userCurrencySelector: WeightedRandomSelector<Int>
    ): List<TransactionRequest> {
        // Get all assets that have price data available on the departure date
        val availableAssets = assetPriceCollection.getAssetsWithPriceOnDate(departureDate)
        
        if (availableAssets.isEmpty()) {
            return emptyList()
        }
        
        // Select 2-5 different assets for portfolio liquidation
        val numberOfAssetsToSell = random.nextInt(2, 6).coerceAtMost(availableAssets.size)
        val assetsToSell = availableAssets.shuffled(random).take(numberOfAssetsToSell)
        
        // Generate sell transactions for each selected asset
        return assetsToSell.mapNotNull { assetId ->
            val price = assetPriceCollection.getPriceOnDate(assetId, departureDate)
            if (price != null) {
                val amount = generateTransactionAmount(price) * 2.0  // Larger amounts for liquidation
                val currencyId = userCurrencySelector.getRandomItem()
                
                TransactionRequest(
                    userId = userId,
                    assetId = assetId,
                    transactionType = "sell",  // Always sell during departure
                    transactionDate = departureDate,
                    amount = amount,
                    currencyId = currencyId
                )
            } else {
                null
            }
        }
    }
    
    /**
     * Generates a single regular transaction for a user within their active period
     * 
     * This method handles the random selection of transaction date, asset, type,
     * and amount for a normal trading activity. It ensures that the selected
     * asset has price data available on the chosen transaction date.
     * 
     * @param userId The user making the transaction
     * @param startDate The earliest possible transaction date
     * @param endDate The latest possible transaction date
     * @param assetPriceCollection Price data for asset and date validation
     * @param userCurrencySelector User-specific currency preference selector
     * @return A single transaction request or null if no valid transaction can be generated
     */
    private fun generateSingleRegularTransaction(
        userId: UUID,
        startDate: LocalDate,
        endDate: LocalDate,
        assetPriceCollection: AssetPriceCollection,
        userCurrencySelector: WeightedRandomSelector<Int>
    ): TransactionRequest? {
        // Generate a random transaction date within the active period
        val daysInPeriod = startDate.until(endDate).days
        val randomDays = random.nextInt(daysInPeriod)
        val transactionDate = startDate.plusDays(randomDays.toLong())
        
        // Find assets that have price data available on this date
        val availableAssets = assetPriceCollection.getAssetsWithPriceOnDate(transactionDate)
        if (availableAssets.isEmpty()) {
            return null
        }
        
        // Randomly select an asset and get its price
        val assetId = availableAssets.random(random)
        val price = assetPriceCollection.getPriceOnDate(assetId, transactionDate)
            ?: return null
        
        // Generate transaction details
        val transactionType = if (random.nextBoolean()) "buy" else "sell"
        val amount = generateTransactionAmount(price)
        val currencyId = userCurrencySelector.getRandomItem()
        
        return TransactionRequest(
            userId = userId,
            assetId = assetId,
            transactionType = transactionType,
            transactionDate = transactionDate,
            amount = amount,
            currencyId = currencyId
        )
    }
    
    /**
     * Maps activity level ID to annual transaction count based on design specification
     * 
     * The activity levels follow the design document specifications:
     * - Inactive: 0-1 times per year
     * - Low: 2-4 times per year
     * - Moderate: 5-12 times per year
     * - Active: 13-52 times per year
     * - Hyperactive: 53+ times per year
     * 
     * @param activityLevelId The activity level identifier
     * @return Expected number of transactions per year
     */
    private fun getAnnualTransactionCount(activityLevelId: Int): Int {
        return when (activityLevelId) {
            1 -> 1     // Inactive: using 1 for simplicity (0-1 range)
            2 -> 3     // Low: using middle value of 2-4 range
            3 -> 8     // Moderate: using middle value of 5-12 range
            4 -> 32    // Active: using middle value of 13-52 range
            5 -> 75    // Hyperactive: using 75 for 53+ range
            else -> 3  // Default to low activity for unknown levels
        }
    }
    
    /**
     * Generates realistic transaction amount based on asset price and market behavior
     * 
     * Transaction amounts are scaled to create realistic portfolio positions.
     * The method ensures reasonable share quantities while maintaining
     * meaningful transaction values in SEK equivalent.
     * 
     * @param price The current price of the asset being traded
     * @return The monetary amount for the transaction
     */
    private fun generateTransactionAmount(price: Double): Double {
        // Set realistic transaction range in SEK equivalent
        val minAmount = 1_000.0
        val maxAmount = 50_000.0
        
        // Generate base transaction amount within the range
        val baseAmount = random.nextDouble(minAmount, maxAmount)
        
        // Calculate maximum shares that can be purchased with base amount
        val maxShares = (baseAmount / price).toInt().coerceAtLeast(1)
        
        // Select actual number of shares to trade
        val actualShares = random.nextInt(1, maxShares + 1)
        
        // Return the actual transaction amount
        return actualShares * price
    }
}