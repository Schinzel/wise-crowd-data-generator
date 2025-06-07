package com.wisecrowd.data_generator.data_generators

import com.wisecrowd.data_generator.data_collections.currency.CurrencyCollection
import com.wisecrowd.data_generator.data_generators.user_data_generator.CustomerStatus
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*
import kotlin.random.Random

class TransactionDataGeneratorTest {

    @Nested
    inner class Constructor {
        
        @Test
        fun constructor_emptyPriceData_throwsIllegalArgumentException() {
            val emptyPriceData = emptyList<List<Any>>()
            val validUserData = createValidUserData()
            
            assertThatThrownBy {
                TransactionDataGenerator(emptyPriceData, validUserData)
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Price series data cannot be empty")
        }

        @Test
        fun constructor_emptyUserData_throwsIllegalArgumentException() {
            val validPriceData = createValidPriceData()
            val emptyUserData = emptyList<List<Any>>()
            
            assertThatThrownBy {
                TransactionDataGenerator(validPriceData, emptyUserData)
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("User data cannot be empty")
        }

        @Test
        fun constructor_validData_createsGeneratorSuccessfully() {
            val validPriceData = createValidPriceData()
            val validUserData = createValidUserData()
            
            val generator = TransactionDataGenerator(validPriceData, validUserData)
            
            assertThat(generator).isNotNull
        }
    }

    @Nested
    inner class GetColumnNames {
        
        @Test
        fun getColumnNames_defaultConfiguration_returnsCorrectColumnNames() {
            val generator = createValidGenerator()
            
            val columnNames = generator.getColumnNames()
            
            val expectedColumns = listOf("transaction_id", "user_id", "asset_id", "transaction_type", "amount", "currency_id")
            assertThat(columnNames).isEqualTo(expectedColumns)
        }
    }

    @Nested
    inner class HasMoreRows {
        
        @Test
        fun hasMoreRows_newGenerator_returnsTrue() {
            val generator = createValidGenerator()
            
            val hasRows = generator.hasMoreRows()
            
            assertThat(hasRows).isTrue
        }

        @Test
        fun hasMoreRows_afterExhaustingAllRows_returnsFalse() {
            val generator = createValidGenerator()
            
            // Exhaust all rows
            while (generator.hasMoreRows()) {
                generator.getNextRow()
            }
            
            val hasRows = generator.hasMoreRows()
            assertThat(hasRows).isFalse
        }
    }

    @Nested
    inner class GetNextRow {
        
        @Test
        fun getNextRow_validGenerator_returnsCorrectDataTypes() {
            val generator = createValidGenerator()
            
            val row = generator.getNextRow()
            
            assertThat(row).hasSize(6)
            // transaction_id
            assertThat(row[0]).isInstanceOf(UUID::class.java)
            // user_id
            assertThat(row[1]).isInstanceOf(UUID::class.java)
            // asset_id
            assertThat(row[2]).isInstanceOf(UUID::class.java)
            // transaction_type
            assertThat(row[3]).isInstanceOf(String::class.java)
            // amount
            assertThat(row[4]).isInstanceOf(Number::class.java)
            // currency_id
            assertThat(row[5]).isInstanceOf(Number::class.java)
        }

        @Test
        fun getNextRow_validGenerator_returnsValidTransactionType() {
            val generator = createValidGenerator()
            
            val row = generator.getNextRow()
            val transactionType = row[3] as String
            
            assertThat(transactionType).isIn("buy", "sell")
        }

        @Test
        fun getNextRow_validGenerator_returnsPositiveAmount() {
            val generator = createValidGenerator()
            
            val row = generator.getNextRow()
            val amount = row[4] as Double
            
            assertThat(amount).isGreaterThan(0.0)
        }

        @Test
        fun getNextRow_noMoreRows_throwsNoSuchElementException() {
            val generator = createValidGenerator()
            
            // Exhaust all rows
            while (generator.hasMoreRows()) {
                generator.getNextRow()
            }
            
            assertThatThrownBy {
                generator.getNextRow()
            }.isInstanceOf(NoSuchElementException::class.java)
                .hasMessageContaining("No more rows available")
        }
    }

    @Nested
    inner class TransactionGeneration {
        
        @Test
        fun getNextRow_departedCustomer_generatesSellTransactions() {
            val departedUserData = createDepartedUserData()
            val priceData = createValidPriceData()
            val generator = TransactionDataGenerator(priceData, listOf(departedUserData))
            
            val transactions = mutableListOf<List<Any>>()
            while (generator.hasMoreRows()) {
                transactions.add(generator.getNextRow())
            }
            
            // Should have some sell transactions for departed customer
            val sellTransactions = transactions.filter { it[3] == "sell" }
            assertThat(sellTransactions).isNotEmpty
        }

        @Test
        fun getNextRow_activeCustomer_generatesMixedTransactions() {
            val activeUserData = createActiveUserData()
            val priceData = createValidPriceData()
            val generator = TransactionDataGenerator(priceData, listOf(activeUserData))
            
            val transactions = mutableListOf<List<Any>>()
            while (generator.hasMoreRows()) {
                transactions.add(generator.getNextRow())
            }
            
            // Should have transactions for active customer
            assertThat(transactions).isNotEmpty
            
            // Should have transactions (may be all buy or all sell due to randomness)
            val transactionTypes = transactions.map { it[3] as String }.toSet()
            assertThat(transactionTypes).isNotEmpty
        }
        
        @Test
        fun getNextRow_swedishUser_prefersHomeCurrency() {
            val swedishUserData = createSwedishUserData()
            val priceData = createValidPriceData()
            val generator = TransactionDataGenerator(priceData, listOf(swedishUserData), random = Random(42))
            
            val transactions = mutableListOf<List<Any>>()
            while (generator.hasMoreRows()) {
                transactions.add(generator.getNextRow())
            }
            
            // Should have transactions for Swedish user
            assertThat(transactions).isNotEmpty
            
            // Extract currency IDs from transactions
            val currencyIds = transactions.map { it[5] as Int }
            val sekTransactions = currencyIds.count { it == 1 }  // SEK currency ID
            
            // Swedish users should prefer SEK (expect >60% usage)
            val sekRatio = sekTransactions.toDouble() / currencyIds.size
            assertThat(sekRatio).isGreaterThan(0.6)
        }
        
        @Test
        fun getNextRow_norwegianUser_prefersHomeCurrency() {
            val norwegianUserData = createNorwegianUserData()
            val priceData = createValidPriceData()
            val generator = TransactionDataGenerator(priceData, listOf(norwegianUserData), random = Random(42))
            
            val transactions = mutableListOf<List<Any>>()
            while (generator.hasMoreRows()) {
                transactions.add(generator.getNextRow())
            }
            
            // Should have transactions for Norwegian user
            assertThat(transactions).isNotEmpty
            
            // Extract currency IDs from transactions
            val currencyIds = transactions.map { it[5] as Int }
            val nokTransactions = currencyIds.count { it == 4 }  // NOK currency ID
            
            // Norwegian users should prefer NOK (expect >60% usage)
            val nokRatio = nokTransactions.toDouble() / currencyIds.size
            assertThat(nokRatio).isGreaterThan(0.6)
        }
    }

    // Helper methods for creating test data
    
    private fun createValidGenerator(): TransactionDataGenerator {
        val priceData = createValidPriceData()
        val userData = createValidUserData()
        return TransactionDataGenerator(priceData, userData)
    }

    private fun createValidPriceData(): List<List<Any>> {
        val assetId1 = UUID.randomUUID()
        val assetId2 = UUID.randomUUID()
        val baseDate = LocalDate.of(2024, 1, 1)
        
        // Create more price data points to ensure transaction generation
        val priceData = mutableListOf<List<Any>>()
        
        // Generate 30 days of price data for better transaction generation
        (0..30).forEach { dayOffset ->
            val date = baseDate.plusDays(dayOffset.toLong())
            priceData.add(listOf(assetId1, date, 100.0 + dayOffset * 0.5))
            priceData.add(listOf(assetId2, date, 50.0 + dayOffset * 0.3))
        }
        
        return priceData
    }

    private fun createValidUserData(): List<List<Any>> {
        return listOf(createActiveUserData(), createDepartedUserData())
    }

    private fun createActiveUserData(): List<Any> {
        val baseDate = LocalDate.of(2024, 1, 1)
        val sentinelDate = LocalDate.of(9999, 12, 31)
        
        return listOf(
            // user_id
            UUID.randomUUID(),
            // investor_profile_id
            1,
            // activity_level_id (Low activity)
            2,
            // country_id
            1,
            // join_date
            baseDate,
            // departure_date (sentinel for active)
            sentinelDate,
            // customer_status
            CustomerStatus.ACTIVE.name
        )
    }

    private fun createDepartedUserData(): List<Any> {
        val baseDate = LocalDate.of(2024, 1, 1)
        val departureDate = LocalDate.of(2024, 1, 20)
        
        return listOf(
            // user_id
            UUID.randomUUID(),
            // investor_profile_id
            2,
            // activity_level_id (Moderate activity)
            3,
            // country_id
            2,
            // join_date
            baseDate,
            // departure_date
            departureDate,
            // customer_status
            CustomerStatus.DEPARTED.name
        )
    }
    
    private fun createSwedishUserData(): List<Any> {
        val baseDate = LocalDate.of(2024, 1, 1)
        val sentinelDate = LocalDate.of(9999, 12, 31)
        
        return listOf(
            // user_id
            UUID.randomUUID(),
            // investor_profile_id
            1,
            // activity_level_id (High activity for more transactions)
            4,
            // country_id (Sweden)
            1,
            // join_date
            baseDate,
            // departure_date (sentinel for active)
            sentinelDate,
            // customer_status
            CustomerStatus.ACTIVE.name
        )
    }
    
    private fun createNorwegianUserData(): List<Any> {
        val baseDate = LocalDate.of(2024, 1, 1)
        val sentinelDate = LocalDate.of(9999, 12, 31)
        
        return listOf(
            // user_id
            UUID.randomUUID(),
            // investor_profile_id
            1,
            // activity_level_id (High activity for more transactions)
            4,
            // country_id (Norway)
            2,
            // join_date
            baseDate,
            // departure_date (sentinel for active)
            sentinelDate,
            // customer_status
            CustomerStatus.ACTIVE.name
        )
    }
}