package com.wisecrowd.data_generator.data_generators

import com.wisecrowd.data_generator.data_collections.currency.CurrencyCollection
import com.wisecrowd.data_generator.data_generators.transaction_data_generator.*
import com.wisecrowd.data_generator.data_generators.user_data_generator.CustomerStatus
import java.time.LocalDate
import java.util.*
import kotlin.random.Random

/**
 * The purpose of this class is to generate realistic transaction data with
 * proper currency distribution and user activity patterns based on existing data.
 *
 * This generator implements IDataGenerator to produce transactions.txt file contents
 * incorporating price series data, user lifecycle events, and user-specific currency
 * preferences. Users maintain realistic currency usage patterns based on their
 * country and typically stick to 1-2 preferred currencies.
 *
 * The generator processes all user and price data to create a complete set of
 * transactions that reflect realistic trading behavior patterns including
 * customer lifecycle events and activity-based trading frequencies.
 *
 * Written by Claude Sonnet 4
 */
class TransactionDataGenerator(
    priceSeriesData: List<List<Any>>,
    userData: List<List<Any>>,
    currencyCollection: CurrencyCollection = CurrencyCollection.createDefaultCollection(),
    random: Random = Random.Default
) : IDataGenerator {

    // Current position in the generated transaction list
    private var currentIndex = 0
    
    // Pre-generated transactions for consistent iteration behavior
    private val generatedTransactions: List<List<Any>>

    init {
        // Validate input parameters using defensive programming principles
        require(priceSeriesData.isNotEmpty()) { "Price series data cannot be empty" }
        require(userData.isNotEmpty()) { "User data cannot be empty" }
        require(!currencyCollection.getAllCurrencies().isEmpty()) { "Currency collection cannot be empty" }

        // Create user currency preferences for realistic currency usage patterns
        val userCurrencyPreferences = UserCurrencyPreferences(currencyCollection, random)

        // Convert raw data into structured objects for cleaner processing
        val priceData = convertPriceSeriesData(priceSeriesData)
        val assetPriceCollection = AssetPriceCollection(priceData)
        val users = convertUserData(userData)

        // Create transaction generator with user-specific currency preferences
        val transactionGenerator = TransactionGenerator(userCurrencyPreferences, random)

        // Generate all transactions during initialization for consistent iteration
        generatedTransactions = generateAllTransactions(users, assetPriceCollection, transactionGenerator)
    }

    override fun getColumnNames(): List<String> {
        return listOf(
            "transaction_id",
            "user_id", 
            "asset_id",
            "transaction_type",
            "amount",
            "currency_id"
        )
    }

    override fun hasMoreRows(): Boolean {
        return currentIndex < generatedTransactions.size
    }

    override fun getNextRow(): List<Any> {
        if (!hasMoreRows()) {
            throw NoSuchElementException("No more rows available in generator")
        }

        return generatedTransactions[currentIndex++]
    }

    /**
     * Converts raw price series data into structured PriceData objects
     * 
     * This method transforms the List<List<Any>> format from the price series
     * generator into type-safe PriceData objects for easier manipulation and
     * better code readability throughout the transaction generation process.
     * 
     * @param rawPriceData The raw price series data as nested lists
     * @return List of structured PriceData objects
     */
    private fun convertPriceSeriesData(rawPriceData: List<List<Any>>): List<PriceData> {
        return rawPriceData.map { row ->
            // Validate row structure matches expected price series format
            require(row.size == 3) { 
                "Price series data row must have 3 columns (asset_id, date, price), but had: ${row.size}" 
            }
            
            PriceData(
                assetId = row[0] as UUID,           // asset_id
                date = row[1] as LocalDate,         // date
                price = row[2] as Double            // price
            )
        }
    }

    /**
     * Converts raw user data into structured UserData objects
     * 
     * This method transforms the List<List<Any>> format from the user data
     * generator into type-safe UserData objects that provide clear property
     * access and validation for transaction generation logic.
     * 
     * @param rawUserData The raw user data as nested lists
     * @return List of structured UserData objects
     */
    private fun convertUserData(rawUserData: List<List<Any>>): List<UserData> {
        return rawUserData.map { row ->
            // Validate row structure matches expected user data format
            require(row.size == 7) { 
                "User data row must have 7 columns, but had: ${row.size}" 
            }
            
            UserData(
                userId = row[0] as UUID,                                // user_id
                investorProfileId = row[1] as Int,                      // investor_profile_id
                activityLevelId = row[2] as Int,                        // activity_level_id
                countryId = row[3] as Int,                              // country_id
                joinDate = row[4] as LocalDate,                         // join_date
                departureDate = row[5] as LocalDate,                    // departure_date
                customerStatus = CustomerStatus.valueOf(row[6] as String) // customer_status
            )
        }
    }

    /**
     * Generates all transactions for all users using the transaction generation engine
     * 
     * This method orchestrates the complete transaction generation process by
     * iterating through all users and generating their respective transactions
     * based on their activity patterns and lifecycle events. The transactions
     * are then converted to the List<Any> format required by IDataGenerator.
     * 
     * @param users The list of user data for transaction generation
     * @param assetPriceCollection Price data collection for transaction validation
     * @param transactionGenerator The engine that creates individual transactions
     * @return List of transaction rows in List<Any> format for iteration
     */
    private fun generateAllTransactions(
        users: List<UserData>,
        assetPriceCollection: AssetPriceCollection,
        transactionGenerator: TransactionGenerator
    ): List<List<Any>> {
        val allTransactions = mutableListOf<List<Any>>()
        
        // Process each user to generate their complete transaction history
        users.forEach { user ->
            val userTransactions = transactionGenerator.generateTransactionsForUser(user, assetPriceCollection)
            
            // Convert each transaction request to the required row format
            userTransactions.forEach { transactionRequest ->
                val transactionRow = listOf<Any>(
                    UUID.randomUUID(),                      // transaction_id (unique for each transaction)
                    transactionRequest.userId,              // user_id
                    transactionRequest.assetId,             // asset_id
                    transactionRequest.transactionType,     // transaction_type
                    transactionRequest.amount,              // amount
                    transactionRequest.currencyId           // currency_id
                )
                allTransactions.add(transactionRow)
            }
        }
        
        return allTransactions
    }
}