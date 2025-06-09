package com.wisecrowd.data_generator.data_generators

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.UUID

class UserHoldingsDataGeneratorTest {
    @Test
    fun getColumnNames_defaultConfiguration_returnsCorrectColumnNames() {
        // Arrange
        val user1 = UUID.randomUUID()
        val asset1 = UUID.randomUUID()
        val transactionData =
            listOf(
                listOf(UUID.randomUUID(), user1, asset1, "buy", 1000.0, 1),
            )
        val generator = UserHoldingsDataGenerator(transactionData)

        // Act
        val columnNames = generator.getColumnNames()

        // Assert
        val expectedColumns = listOf("user_id", "asset_id", "amount", "currency_id")
        assertThat(columnNames).isEqualTo(expectedColumns)
    }

    @Test
    fun hasMoreRows_newGenerator_returnsTrue() {
        // Arrange
        val user1 = UUID.randomUUID()
        val asset1 = UUID.randomUUID()
        val transactionData =
            listOf(
                listOf(UUID.randomUUID(), user1, asset1, "buy", 1000.0, 1),
            )
        val generator = UserHoldingsDataGenerator(transactionData)

        // Act
        val hasMoreRows = generator.hasMoreRows()

        // Assert
        assertThat(hasMoreRows).isTrue()
    }

    @Test
    fun hasMoreRows_allHoldingsRetrieved_returnsFalse() {
        // Arrange
        val user1 = UUID.randomUUID()
        val asset1 = UUID.randomUUID()
        val transactionData =
            listOf(
                listOf(UUID.randomUUID(), user1, asset1, "buy", 1000.0, 1),
            )
        val generator = UserHoldingsDataGenerator(transactionData)
        generator.getNextRow()

        // Act
        val hasMoreRows = generator.hasMoreRows()

        // Assert
        assertThat(hasMoreRows).isFalse()
    }

    @Test
    fun getNextRow_noMoreRowsAvailable_throwsNoSuchElementException() {
        // Arrange
        val user1 = UUID.randomUUID()
        val asset1 = UUID.randomUUID()
        val transactionData =
            listOf(
                listOf(UUID.randomUUID(), user1, asset1, "buy", 1000.0, 1),
            )
        val generator = UserHoldingsDataGenerator(transactionData)
        generator.getNextRow()

        // Act & Assert
        assertThatThrownBy {
            generator.getNextRow()
        }.isInstanceOf(NoSuchElementException::class.java)
    }

    @Test
    fun getNextRow_singleBuyTransaction_returnsCorrectHolding() {
        // Arrange
        val user1 = UUID.randomUUID()
        val asset1 = UUID.randomUUID()
        val transactionData =
            listOf(
                listOf(UUID.randomUUID(), user1, asset1, "buy", 1500.0, 1),
            )
        val generator = UserHoldingsDataGenerator(transactionData)

        // Act
        val holding = generator.getNextRow()

        // Assert
        assertThat(holding[0]).isEqualTo(user1)
        assertThat(holding[1]).isEqualTo(asset1)
        assertThat(holding[2]).isEqualTo(1500.0)
        assertThat(holding[3]).isEqualTo(1)
    }

    @Test
    fun getNextRow_multipleBuyAndSellTransactions_returnsCorrectNetHolding() {
        // Arrange
        val user1 = UUID.randomUUID()
        val asset1 = UUID.randomUUID()
        val transactionData =
            listOf(
                listOf(UUID.randomUUID(), user1, asset1, "buy", 2000.0, 1),
                listOf(UUID.randomUUID(), user1, asset1, "sell", 500.0, 1),
                listOf(UUID.randomUUID(), user1, asset1, "buy", 300.0, 1),
                listOf(UUID.randomUUID(), user1, asset1, "sell", 200.0, 1),
            )
        val generator = UserHoldingsDataGenerator(transactionData)

        // Act
        val holding = generator.getNextRow()

        // Assert
        val expectedNetAmount = 1600.0
        assertThat(holding[2]).isEqualTo(expectedNetAmount)
    }

    @Test
    fun getNextRow_zeroNetAmount_excludesFromOutput() {
        // Arrange
        val user1 = UUID.randomUUID()
        val asset1 = UUID.randomUUID()
        val transactionData =
            listOf(
                listOf(UUID.randomUUID(), user1, asset1, "buy", 1000.0, 1),
                listOf(UUID.randomUUID(), user1, asset1, "sell", 1000.0, 1),
            )
        val generator = UserHoldingsDataGenerator(transactionData)

        // Act
        val hasMoreRows = generator.hasMoreRows()

        // Assert
        assertThat(hasMoreRows).isFalse()
    }

    @Test
    fun getNextRow_negativeNetAmount_excludesFromOutput() {
        // Arrange
        val user1 = UUID.randomUUID()
        val asset1 = UUID.randomUUID()
        val transactionData =
            listOf(
                listOf(UUID.randomUUID(), user1, asset1, "buy", 500.0, 1),
                listOf(UUID.randomUUID(), user1, asset1, "sell", 600.0, 1),
            )
        val generator = UserHoldingsDataGenerator(transactionData)

        // Act
        val hasMoreRows = generator.hasMoreRows()

        // Assert
        assertThat(hasMoreRows).isFalse()
    }

    @Test
    fun getNextRow_sameUserAssetDifferentCurrencies_returnsMultipleHoldings() {
        // Arrange
        val user1 = UUID.randomUUID()
        val asset1 = UUID.randomUUID()
        val transactionData =
            listOf(
                listOf(UUID.randomUUID(), user1, asset1, "buy", 1000.0, 1),
                listOf(UUID.randomUUID(), user1, asset1, "buy", 500.0, 2),
                listOf(UUID.randomUUID(), user1, asset1, "sell", 100.0, 1),
                listOf(UUID.randomUUID(), user1, asset1, "buy", 200.0, 2),
            )
        val generator = UserHoldingsDataGenerator(transactionData)

        // Act
        val holdings = mutableListOf<List<Any>>()
        while (generator.hasMoreRows()) {
            holdings.add(generator.getNextRow())
        }

        // Assert
        val expectedHoldingsCount = 2
        assertThat(holdings).hasSize(expectedHoldingsCount)

        val sekHolding = holdings.find { it[3] == 1 }
        val expectedSekAmount = 900.0
        assertThat(sekHolding?.get(2)).isEqualTo(expectedSekAmount)

        val eurHolding = holdings.find { it[3] == 2 }
        val expectedEurAmount = 700.0
        assertThat(eurHolding?.get(2)).isEqualTo(expectedEurAmount)
    }

    @Test
    fun getNextRow_multipleUsersMultipleAssetsMultipleCurrencies_returnsCorrectHoldings() {
        // Arrange
        val user1 = UUID.randomUUID()
        val user2 = UUID.randomUUID()
        val asset1 = UUID.randomUUID()
        val asset2 = UUID.randomUUID()
        val transactionData =
            listOf(
                listOf(UUID.randomUUID(), user1, asset1, "buy", 1000.0, 1),
                listOf(UUID.randomUUID(), user1, asset2, "buy", 500.0, 2),
                listOf(UUID.randomUUID(), user2, asset1, "buy", 750.0, 1),
                listOf(UUID.randomUUID(), user2, asset2, "buy", 250.0, 3),
                listOf(UUID.randomUUID(), user1, asset1, "sell", 200.0, 1),
            )
        val generator = UserHoldingsDataGenerator(transactionData)

        // Act
        val holdings = mutableListOf<List<Any>>()
        while (generator.hasMoreRows()) {
            holdings.add(generator.getNextRow())
        }

        // Assert
        val expectedHoldingsCount = 4
        assertThat(holdings).hasSize(expectedHoldingsCount)

        val user1Asset1Holding =
            holdings.find {
                it[0] == user1 && it[1] == asset1 && it[3] == 1
            }
        val expectedUser1Asset1Amount = 800.0
        assertThat(user1Asset1Holding?.get(2)).isEqualTo(expectedUser1Asset1Amount)
    }

    @Test
    fun getNextRow_largeTransactionDataset_aggregatesCorrectly() {
        // Arrange
        val user1 = UUID.randomUUID()
        val asset1 = UUID.randomUUID()
        val largeTransactionData = mutableListOf<List<Any>>()

        repeat(1000) {
            largeTransactionData.add(
                listOf(UUID.randomUUID(), user1, asset1, "buy", 100.0, 1),
            )
        }
        val generator = UserHoldingsDataGenerator(largeTransactionData)

        // Act
        val holding = generator.getNextRow()

        // Assert
        val expectedTotalAmount = 100_000.0
        assertThat(holding[2]).isEqualTo(expectedTotalAmount)
    }

    @Test
    fun constructor_emptyTransactionData_throwsIllegalArgumentException() {
        // Arrange
        val emptyTransactionData = emptyList<List<Any>>()

        // Act & Assert
        assertThatThrownBy {
            UserHoldingsDataGenerator(emptyTransactionData)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_invalidTransactionRowStructure_throwsIllegalArgumentException() {
        // Arrange
        val invalidTransactionData =
            listOf(
                listOf(UUID.randomUUID(), UUID.randomUUID(), "buy", 1000.0),
            )

        // Act & Assert
        assertThatThrownBy {
            UserHoldingsDataGenerator(invalidTransactionData)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_invalidTransactionType_throwsIllegalArgumentException() {
        // Arrange
        val user1 = UUID.randomUUID()
        val asset1 = UUID.randomUUID()
        val invalidTransactionData =
            listOf(
                listOf(UUID.randomUUID(), user1, asset1, "trade", 1000.0, 1),
            )

        // Act & Assert
        assertThatThrownBy {
            UserHoldingsDataGenerator(invalidTransactionData)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_negativeTransactionAmount_throwsIllegalArgumentException() {
        // Arrange
        val user1 = UUID.randomUUID()
        val asset1 = UUID.randomUUID()
        val invalidTransactionData =
            listOf(
                listOf(UUID.randomUUID(), user1, asset1, "buy", -500.0, 1),
            )

        // Act & Assert
        assertThatThrownBy {
            UserHoldingsDataGenerator(invalidTransactionData)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }
}
