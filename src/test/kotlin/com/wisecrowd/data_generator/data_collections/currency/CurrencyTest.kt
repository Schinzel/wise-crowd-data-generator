package com.wisecrowd.data_generator.data_collections.currency

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class CurrencyTest {
    @Test
    fun constructor_validInputs_setsId() {
        val currency = Currency(1, "SEK", "Swedish Krona", 60.0, 1.0)

        assertThat(currency.id).isEqualTo(1)
    }

    @Test
    fun constructor_validInputs_setsCode() {
        val currency = Currency(1, "SEK", "Swedish Krona", 60.0, 1.0)

        assertThat(currency.code).isEqualTo("SEK")
    }

    @Test
    fun constructor_validInputs_setsName() {
        val currency = Currency(1, "SEK", "Swedish Krona", 60.0, 1.0)

        assertThat(currency.name).isEqualTo("Swedish Krona")
    }

    @Test
    fun constructor_validInputs_setsDistributionPercentage() {
        val currency = Currency(1, "SEK", "Swedish Krona", 60.0, 1.0)

        assertThat(currency.distributionPercentage).isEqualTo(60.0)
    }

    @Test
    fun constructor_validInputs_setsConversionToSek() {
        val currency = Currency(1, "SEK", "Swedish Krona", 60.0, 1.0)

        assertThat(currency.conversionToSek).isEqualTo(1.0)
    }

    @Test
    fun constructor_negativeId_throwsException() {
        assertThatThrownBy {
            Currency(-1, "SEK", "Swedish Krona", 60.0, 1.0)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_zeroId_throwsException() {
        assertThatThrownBy {
            Currency(0, "SEK", "Swedish Krona", 60.0, 1.0)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_blankCode_throwsException() {
        assertThatThrownBy {
            Currency(1, "", "Swedish Krona", 60.0, 1.0)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_whitespaceCode_throwsException() {
        assertThatThrownBy {
            Currency(1, "   ", "Swedish Krona", 60.0, 1.0)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_blankName_throwsException() {
        assertThatThrownBy {
            Currency(1, "SEK", "", 60.0, 1.0)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_whitespaceName_throwsException() {
        assertThatThrownBy {
            Currency(1, "SEK", "   ", 60.0, 1.0)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_negativeDistributionPercentage_throwsException() {
        assertThatThrownBy {
            Currency(1, "SEK", "Swedish Krona", -1.0, 1.0)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_distributionPercentageAbove100_throwsException() {
        assertThatThrownBy {
            Currency(1, "SEK", "Swedish Krona", 101.0, 1.0)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_zeroDistributionPercentage_succeeds() {
        val currency = Currency(1, "SEK", "Swedish Krona", 0.0, 1.0)

        assertThat(currency.distributionPercentage).isEqualTo(0.0)
    }

    @Test
    fun constructor_exactlyOneHundredDistributionPercentage_succeeds() {
        val currency = Currency(1, "SEK", "Swedish Krona", 100.0, 1.0)

        assertThat(currency.distributionPercentage).isEqualTo(100.0)
    }

    @Test
    fun constructor_negativeConversionToSek_throwsException() {
        assertThatThrownBy {
            Currency(1, "SEK", "Swedish Krona", 60.0, -1.0)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_zeroConversionToSek_throwsException() {
        assertThatThrownBy {
            Currency(1, "SEK", "Swedish Krona", 60.0, 0.0)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_smallConversionToSek_succeeds() {
        val expectedConversion = 0.070
        val currency = Currency(1, "JPY", "Japanese Yen", 0.5, expectedConversion)

        assertThat(currency.conversionToSek).isEqualTo(expectedConversion)
    }

    @Test
    fun constructor_largeConversionToSek_succeeds() {
        val expectedConversion = 13.88
        val currency = Currency(1, "GBP", "British Pound", 3.0, expectedConversion)

        assertThat(currency.conversionToSek).isEqualTo(expectedConversion)
    }
}
