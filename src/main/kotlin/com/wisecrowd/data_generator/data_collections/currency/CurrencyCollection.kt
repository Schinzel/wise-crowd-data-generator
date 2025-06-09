package com.wisecrowd.data_generator.data_collections.currency

import com.wisecrowd.data_generator.data_collections.utils.DistributionUtils

/**
 * The purpose of this class is to manage a collection of currencies,
 * providing methods to add, retrieve, and filter currency data.
 *
 * Written by Claude Sonnet 4
 */
class CurrencyCollection {
    private val currencies = mutableListOf<Currency>()

    fun addCurrency(currency: Currency) {
        currencies.add(currency)
    }

    fun getAllCurrencies(): List<Currency> = currencies.toList()

    fun getById(id: Int): Currency =
        currencies.find { it.id == id }
            ?: throw IllegalArgumentException("Currency with ID $id not found")

    fun getByCode(code: String): Currency =
        currencies.find { it.code.equals(code, ignoreCase = true) }
            ?: throw IllegalArgumentException("Currency with code '$code' not found")

    fun getByDistributionRange(
        minPercentage: Double,
        maxPercentage: Double,
    ): List<Currency> =
        DistributionUtils.filterByDistribution(
            currencies,
            minPercentage,
            maxPercentage,
        ) { it.distributionPercentage }

    fun size(): Int = currencies.size

    companion object {
        fun createDefaultCollection(): CurrencyCollection {
            val collection = CurrencyCollection()

            collection.addCurrency(Currency(1, "SEK", "Swedish Krona", 60.0, 1.0))
            collection.addCurrency(Currency(2, "EUR", "Euro", 20.0, 11.96))
            collection.addCurrency(Currency(3, "USD", "US Dollar", 10.0, 10.32))
            collection.addCurrency(Currency(4, "NOK", "Norwegian Krone", 3.0, 0.90))
            collection.addCurrency(Currency(5, "DKK", "Danish Krone", 3.0, 1.55))
            collection.addCurrency(Currency(6, "GBP", "British Pound", 3.0, 13.88))
            collection.addCurrency(Currency(7, "JPY", "Japanese Yen", 0.5, 0.070))
            collection.addCurrency(Currency(8, "CHF", "Swiss Franc", 0.5, 12.08))

            return collection
        }
    }
}
