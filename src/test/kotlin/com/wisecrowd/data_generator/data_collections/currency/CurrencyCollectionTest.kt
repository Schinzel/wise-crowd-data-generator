package com.wisecrowd.data_generator.data_collections.currency

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CurrencyCollectionTest {
    private lateinit var collection: CurrencyCollection
    private lateinit var sekCurrency: Currency
    private lateinit var eurCurrency: Currency
    private lateinit var usdCurrency: Currency

    @BeforeEach
    fun setUp() {
        collection = CurrencyCollection()
        sekCurrency = Currency(1, "SEK", "Swedish Krona", 60.0, 1.0)
        eurCurrency = Currency(2, "EUR", "Euro", 20.0, 11.96)
        usdCurrency = Currency(3, "USD", "US Dollar", 10.0, 10.32)

        collection.addCurrency(sekCurrency)
        collection.addCurrency(eurCurrency)
        collection.addCurrency(usdCurrency)
    }

    @Test
    fun addCurrency_validCurrency_increasesSizeByOne() {
        val nokCurrency = Currency(4, "NOK", "Norwegian Krone", 3.0, 0.90)

        collection.addCurrency(nokCurrency)

        assertThat(collection.size()).isEqualTo(4)
    }

    @Test
    fun getAllCurrencies_withThreeCurrencies_returnsAllThree() {
        val currencies = collection.getAllCurrencies()

        assertThat(currencies).hasSize(3)
    }

    @Test
    fun getAllCurrencies_withThreeCurrencies_containsSekCurrency() {
        val currencies = collection.getAllCurrencies()

        assertThat(currencies).contains(sekCurrency)
    }

    @Test
    fun getAllCurrencies_withThreeCurrencies_containsEurCurrency() {
        val currencies = collection.getAllCurrencies()

        assertThat(currencies).contains(eurCurrency)
    }

    @Test
    fun getAllCurrencies_withThreeCurrencies_containsUsdCurrency() {
        val currencies = collection.getAllCurrencies()

        assertThat(currencies).contains(usdCurrency)
    }

    @Test
    fun getById_existingId_returnsCurrency() {
        val currency = collection.getById(1)

        assertThat(currency).isEqualTo(sekCurrency)
    }

    @Test
    fun getById_nonExistingId_throwsException() {
        assertThatThrownBy {
            collection.getById(999)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun getByCode_existingCodeUppercase_returnsCurrency() {
        val currency = collection.getByCode("SEK")

        assertThat(currency).isEqualTo(sekCurrency)
    }

    @Test
    fun getByCode_existingCodeLowercase_returnsCurrency() {
        val currency = collection.getByCode("sek")

        assertThat(currency).isEqualTo(sekCurrency)
    }

    @Test
    fun getByCode_existingCodeMixedCase_returnsCurrency() {
        val currency = collection.getByCode("SeK")

        assertThat(currency).isEqualTo(sekCurrency)
    }

    @Test
    fun getByCode_nonExistingCode_throwsException() {
        assertThatThrownBy {
            collection.getByCode("XYZ")
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun getByDistributionRange_validRange_returnsFilteredResults() {
        val currencies = collection.getByDistributionRange(15.0, 65.0)

        assertThat(currencies).hasSize(2)
        assertThat(currencies).containsExactlyInAnyOrder(sekCurrency, eurCurrency)
    }

    @Test
    fun size_withThreeCurrencies_returnsThree() {
        assertThat(collection.size()).isEqualTo(3)
    }

    @Test
    fun createDefaultCollection_createsCurrencies_hasCorrectSize() {
        val defaultCollection = CurrencyCollection.createDefaultCollection()

        assertThat(defaultCollection.size()).isEqualTo(8)
    }

    @Test
    fun createDefaultCollection_createsCurrencies_containsSek() {
        val defaultCollection = CurrencyCollection.createDefaultCollection()
        val sekFromDefault = defaultCollection.getById(1)

        assertThat(sekFromDefault.code).isEqualTo("SEK")
    }

    @Test
    fun createDefaultCollection_createsCurrencies_containsEur() {
        val defaultCollection = CurrencyCollection.createDefaultCollection()
        val eurFromDefault = defaultCollection.getById(2)

        assertThat(eurFromDefault.code).isEqualTo("EUR")
    }

    @Test
    fun createDefaultCollection_createsCurrencies_containsUsd() {
        val defaultCollection = CurrencyCollection.createDefaultCollection()
        val usdFromDefault = defaultCollection.getById(3)

        assertThat(usdFromDefault.code).isEqualTo("USD")
    }

    @Test
    fun createDefaultCollection_createsCurrencies_containsNok() {
        val defaultCollection = CurrencyCollection.createDefaultCollection()
        val nokFromDefault = defaultCollection.getById(4)

        assertThat(nokFromDefault.code).isEqualTo("NOK")
    }

    @Test
    fun createDefaultCollection_createsCurrencies_containsDkk() {
        val defaultCollection = CurrencyCollection.createDefaultCollection()
        val dkkFromDefault = defaultCollection.getById(5)

        assertThat(dkkFromDefault.code).isEqualTo("DKK")
    }

    @Test
    fun createDefaultCollection_createsCurrencies_containsGbp() {
        val defaultCollection = CurrencyCollection.createDefaultCollection()
        val gbpFromDefault = defaultCollection.getById(6)

        assertThat(gbpFromDefault.code).isEqualTo("GBP")
    }

    @Test
    fun createDefaultCollection_createsCurrencies_containsJpy() {
        val defaultCollection = CurrencyCollection.createDefaultCollection()
        val jpyFromDefault = defaultCollection.getById(7)

        assertThat(jpyFromDefault.code).isEqualTo("JPY")
    }

    @Test
    fun createDefaultCollection_createsCurrencies_containsChf() {
        val defaultCollection = CurrencyCollection.createDefaultCollection()
        val chfFromDefault = defaultCollection.getById(8)

        assertThat(chfFromDefault.code).isEqualTo("CHF")
    }

    @Test
    fun createDefaultCollection_sekCurrency_hasCorrectDistribution() {
        val defaultCollection = CurrencyCollection.createDefaultCollection()
        val sekFromDefault = defaultCollection.getById(1)
        val expectedDistribution = 60.0

        assertThat(sekFromDefault.distributionPercentage).isEqualTo(expectedDistribution)
    }

    @Test
    fun createDefaultCollection_jpyCurrency_hasCorrectConversion() {
        val defaultCollection = CurrencyCollection.createDefaultCollection()
        val jpyFromDefault = defaultCollection.getById(7)
        val expectedConversion = 0.070

        assertThat(jpyFromDefault.conversionToSek).isEqualTo(expectedConversion)
    }
}
