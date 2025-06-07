package com.wisecrowd.data_generator.data_generators.transaction_data_generator

import com.wisecrowd.data_generator.data_collections.currency.CurrencyCollection
import com.wisecrowd.data_generator.utils.WeightedItem
import com.wisecrowd.data_generator.utils.WeightedRandomSelector
import kotlin.random.Random

/**
 * The purpose of this class is to generate realistic currency preferences
 * for users based on their country and behavioral patterns.
 *
 * Real users typically stick to 1-2 preferred currencies rather than
 * randomly selecting from all available currencies for each transaction.
 * This class models realistic currency usage patterns.
 *
 * Written by Claude Sonnet 4
 */
class UserCurrencyPreferences(
    private val currencyCollection: CurrencyCollection = CurrencyCollection.createDefaultCollection(),
    private val random: Random = Random.Default
) {
    
    /**
     * Generates currency preferences for a user based on their country
     * 
     * This method creates realistic currency preferences where users
     * primarily use their home currency plus potentially one international
     * currency (EUR/USD) for diversification.
     * 
     * @param countryId The user's country ID
     * @return WeightedRandomSelector configured with user's currency preferences
     */
    fun generateUserCurrencyPreferences(countryId: Int): WeightedRandomSelector<Int> {
        val homeCurrencyId = getHomeCurrencyForCountry(countryId)
        val currencyPreferences = mutableListOf<WeightedItem<Int>>()
        
        // Add home currency with high preference (70-80%)
        val homeWeight = random.nextDouble(70.0, 80.0)
        currencyPreferences.add(WeightedItem(homeCurrencyId, homeWeight))
        
        // Determine if user has international diversification (60% chance)
        val hasInternationalCurrency = random.nextDouble() < 0.6
        
        if (hasInternationalCurrency) {
            // Select one international currency (EUR or USD preferred)
            val internationalCurrency = selectInternationalCurrency(homeCurrencyId)
            val internationalWeight = 100.0 - homeWeight  // Ensure exact 100% total
            currencyPreferences.add(WeightedItem(internationalCurrency, internationalWeight))
        } else {
            // If no international currency, adjust home currency to exactly 100%
            currencyPreferences.clear()
            currencyPreferences.add(WeightedItem(homeCurrencyId, 100.0))
        }
        
        return WeightedRandomSelector(currencyPreferences, random)
    }
    
    /**
     * Maps country ID to the most commonly used currency in that country
     * 
     * Based on the customer countries design specification:
     * Sweden(1)→SEK(1), Norway(2)→NOK(4), Denmark(3)→DKK(5), 
     * Finland(4)→EUR(2), Iceland(5)→EUR(2)
     * 
     * @param countryId The country identifier
     * @return The currency ID most commonly used in that country
     */
    private fun getHomeCurrencyForCountry(countryId: Int): Int {
        return when (countryId) {
            1 -> 1  // Sweden → SEK
            2 -> 4  // Norway → NOK  
            3 -> 5  // Denmark → DKK
            4 -> 2  // Finland → EUR (Finland uses Euro)
            5 -> 2  // Iceland → EUR (approximation, Iceland uses ISK but EUR common for international)
            else -> 1  // Default to SEK for unknown countries
        }
    }
    
    /**
     * Selects an appropriate international currency for diversification
     * 
     * Users typically choose EUR or USD for international diversification.
     * The selection avoids the user's home currency and prefers major
     * international currencies.
     * 
     * @param homeCurrencyId The user's home currency to avoid duplication
     * @return A suitable international currency ID
     */
    private fun selectInternationalCurrency(homeCurrencyId: Int): Int {
        val internationalCurrencies = listOf(
            2,  // EUR - European Union currency
            3   // USD - US Dollar
        ).filter { it != homeCurrencyId }
        
        return if (internationalCurrencies.isNotEmpty()) {
            internationalCurrencies.random(random)
        } else {
            // Fallback: select any currency that's not the home currency
            val allCurrencies = currencyCollection.getAllCurrencies()
                .map { it.id }
                .filter { it != homeCurrencyId }
            allCurrencies.randomOrNull(random) ?: homeCurrencyId
        }
    }
}