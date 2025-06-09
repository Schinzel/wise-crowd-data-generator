package com.wisecrowd.data_generator.data_collections.customer_country

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class CustomerCountryTest {
    @Test
    fun constructor_validInputs_setsId() {
        val customerCountry =
            CustomerCountry(
                id = 1,
                name = "Sweden",
                countryCode = "SE",
                description = "Home market with largest customer base",
                distributionPercentage = 60.0,
            )

        assertThat(customerCountry.id).isEqualTo(1)
    }

    @Test
    fun constructor_validInputs_setsName() {
        val customerCountry =
            CustomerCountry(
                id = 1,
                name = "Sweden",
                countryCode = "SE",
                description = "Home market with largest customer base",
                distributionPercentage = 60.0,
            )

        assertThat(customerCountry.name).isEqualTo("Sweden")
    }

    @Test
    fun constructor_validInputs_setsCountryCode() {
        val customerCountry =
            CustomerCountry(
                id = 1,
                name = "Sweden",
                countryCode = "SE",
                description = "Home market with largest customer base",
                distributionPercentage = 60.0,
            )

        assertThat(customerCountry.countryCode).isEqualTo("SE")
    }

    @Test
    fun constructor_validInputs_setsDescription() {
        val customerCountry =
            CustomerCountry(
                id = 1,
                name = "Sweden",
                countryCode = "SE",
                description = "Home market with largest customer base",
                distributionPercentage = 60.0,
            )

        assertThat(customerCountry.description).isEqualTo("Home market with largest customer base")
    }

    @Test
    fun constructor_validInputs_setsDistributionPercentage() {
        val customerCountry =
            CustomerCountry(
                id = 1,
                name = "Sweden",
                countryCode = "SE",
                description = "Home market with largest customer base",
                distributionPercentage = 60.0,
            )

        assertThat(customerCountry.distributionPercentage).isEqualTo(60.0)
    }

    @Test
    fun constructor_negativeId_throwsException() {
        assertThatThrownBy {
            CustomerCountry(
                id = -1,
                name = "Sweden",
                countryCode = "SE",
                description = "Home market with largest customer base",
                distributionPercentage = 60.0,
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_zeroId_throwsException() {
        assertThatThrownBy {
            CustomerCountry(
                id = 0,
                name = "Sweden",
                countryCode = "SE",
                description = "Home market with largest customer base",
                distributionPercentage = 60.0,
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_blankName_throwsException() {
        assertThatThrownBy {
            CustomerCountry(
                id = 1,
                name = "",
                countryCode = "SE",
                description = "Home market with largest customer base",
                distributionPercentage = 60.0,
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_whitespaceName_throwsException() {
        assertThatThrownBy {
            CustomerCountry(
                id = 1,
                name = "   ",
                countryCode = "SE",
                description = "Home market with largest customer base",
                distributionPercentage = 60.0,
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_blankCountryCode_throwsException() {
        assertThatThrownBy {
            CustomerCountry(
                id = 1,
                name = "Sweden",
                countryCode = "",
                description = "Home market with largest customer base",
                distributionPercentage = 60.0,
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_whitespaceCountryCode_throwsException() {
        assertThatThrownBy {
            CustomerCountry(
                id = 1,
                name = "Sweden",
                countryCode = "   ",
                description = "Home market with largest customer base",
                distributionPercentage = 60.0,
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_blankDescription_throwsException() {
        assertThatThrownBy {
            CustomerCountry(
                id = 1,
                name = "Sweden",
                countryCode = "SE",
                description = "",
                distributionPercentage = 60.0,
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_whitespaceDescription_throwsException() {
        assertThatThrownBy {
            CustomerCountry(
                id = 1,
                name = "Sweden",
                countryCode = "SE",
                description = "   ",
                distributionPercentage = 60.0,
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_negativeDistributionPercentage_throwsException() {
        assertThatThrownBy {
            CustomerCountry(
                id = 1,
                name = "Sweden",
                countryCode = "SE",
                description = "Home market with largest customer base",
                distributionPercentage = -1.0,
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_distributionPercentageAbove100_throwsException() {
        assertThatThrownBy {
            CustomerCountry(
                id = 1,
                name = "Sweden",
                countryCode = "SE",
                description = "Home market with largest customer base",
                distributionPercentage = 101.0,
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_zeroDistributionPercentage_succeeds() {
        val customerCountry =
            CustomerCountry(
                id = 1,
                name = "Sweden",
                countryCode = "SE",
                description = "Home market with largest customer base",
                distributionPercentage = 0.0,
            )

        assertThat(customerCountry.distributionPercentage).isEqualTo(0.0)
    }

    @Test
    fun constructor_exactlyOneHundredDistributionPercentage_succeeds() {
        val customerCountry =
            CustomerCountry(
                id = 1,
                name = "Sweden",
                countryCode = "SE",
                description = "Home market with largest customer base",
                distributionPercentage = 100.0,
            )

        assertThat(customerCountry.distributionPercentage).isEqualTo(100.0)
    }

    @Test
    fun constructor_longName_succeeds() {
        val longName = "Very Long Country Name That Exceeds Normal Length"
        val customerCountry =
            CustomerCountry(
                id = 1,
                name = longName,
                countryCode = "SE",
                description = "Home market with largest customer base",
                distributionPercentage = 60.0,
            )

        assertThat(customerCountry.name).isEqualTo(longName)
    }

    @Test
    fun constructor_longDescription_succeeds() {
        val longDescription =
            "This is a very long description that explains the customer country in great detail " +
                "and provides comprehensive information about the market characteristics and customer behavior patterns"
        val customerCountry =
            CustomerCountry(
                id = 1,
                name = "Sweden",
                countryCode = "SE",
                description = longDescription,
                distributionPercentage = 60.0,
            )

        assertThat(customerCountry.description).isEqualTo(longDescription)
    }
}
