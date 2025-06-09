package com.wisecrowd.data_generator.data_collections.customer_country

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CustomerCountriesCollectionTest {
    private lateinit var collection: CustomerCountriesCollection
    private lateinit var sweden: CustomerCountry
    private lateinit var norway: CustomerCountry
    private lateinit var denmark: CustomerCountry

    @BeforeEach
    fun setUp() {
        collection = CustomerCountriesCollection()
        sweden = CustomerCountry(1, "Sweden", "SE", "Home market", 60.0)
        norway = CustomerCountry(2, "Norway", "NO", "Oil wealth economy", 15.0)
        denmark = CustomerCountry(3, "Denmark", "DK", "Strong financial sector", 12.0)
    }

    @Test
    fun addCountry_validCountry_increasesSize() {
        val initialSize = collection.size()

        collection.addCountry(sweden)

        assertThat(collection.size()).isEqualTo(initialSize + 1)
    }

    @Test
    fun getAll_emptyCollection_returnsEmptyList() {
        val result = collection.getAll()

        assertThat(result).isEmpty()
    }

    @Test
    fun getAll_withCountries_returnsAllCountries() {
        collection.addCountry(sweden)
        collection.addCountry(norway)

        val result = collection.getAll()

        assertThat(result).hasSize(2)
        assertThat(result).contains(sweden, norway)
    }

    @Test
    fun getById_existingId_returnsCorrectCountry() {
        collection.addCountry(sweden)
        collection.addCountry(norway)

        val result = collection.getById(1)

        assertThat(result).isEqualTo(sweden)
    }

    @Test
    fun getById_nonExistingId_throwsException() {
        collection.addCountry(sweden)

        assertThatThrownBy {
            collection.getById(999)
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Customer country with ID 999 not found")
    }

    @Test
    fun getByCountryCode_existingCode_returnsCorrectCountry() {
        collection.addCountry(sweden)
        collection.addCountry(norway)

        val result = collection.getByCountryCode("SE")

        assertThat(result).isEqualTo(sweden)
    }

    @Test
    fun getByCountryCode_existingCodeDifferentCase_returnsCorrectCountry() {
        collection.addCountry(sweden)

        val result = collection.getByCountryCode("se")

        assertThat(result).isEqualTo(sweden)
    }

    @Test
    fun getByCountryCode_nonExistingCode_throwsException() {
        collection.addCountry(sweden)

        assertThatThrownBy {
            collection.getByCountryCode("XX")
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Customer country with code 'XX' not found")
    }

    @Test
    fun getByDistribution_validRange_returnsFilteredResults() {
        collection.addCountry(sweden) // 60.0%
        collection.addCountry(norway) // 15.0%
        collection.addCountry(denmark) // 12.0%

        val result = collection.getByDistribution(10.0, 20.0)

        assertThat(result).hasSize(2)
        assertThat(result).containsExactlyInAnyOrder(norway, denmark)
    }

    @Test
    fun size_emptyCollection_returnsZero() {
        assertThat(collection.size()).isEqualTo(0)
    }

    @Test
    fun size_withCountries_returnsCorrectCount() {
        collection.addCountry(sweden)
        collection.addCountry(norway)

        assertThat(collection.size()).isEqualTo(2)
    }

    @Test
    fun createDefaultCollection_returnsCollectionWithNordicCountries() {
        val defaultCollection = CustomerCountriesCollection.createDefaultCollection()

        assertThat(defaultCollection.size()).isEqualTo(5)

        val sweden = defaultCollection.getByCountryCode("SE")
        assertThat(sweden.name).isEqualTo("Sweden")
        assertThat(sweden.distributionPercentage).isEqualTo(60.0)

        val norway = defaultCollection.getByCountryCode("NO")
        assertThat(norway.name).isEqualTo("Norway")
        assertThat(norway.distributionPercentage).isEqualTo(15.0)

        val denmark = defaultCollection.getByCountryCode("DK")
        assertThat(denmark.name).isEqualTo("Denmark")
        assertThat(denmark.distributionPercentage).isEqualTo(12.0)

        val finland = defaultCollection.getByCountryCode("FI")
        assertThat(finland.name).isEqualTo("Finland")
        assertThat(finland.distributionPercentage).isEqualTo(8.0)

        val iceland = defaultCollection.getByCountryCode("IS")
        assertThat(iceland.name).isEqualTo("Iceland")
        assertThat(iceland.distributionPercentage).isEqualTo(5.0)
    }

    @Test
    fun createDefaultCollection_distributionsAddTo100Percent() {
        val defaultCollection = CustomerCountriesCollection.createDefaultCollection()

        val totalPercentage =
            defaultCollection
                .getAll()
                .sumOf { it.distributionPercentage }

        assertThat(totalPercentage).isEqualTo(100.0)
    }

    @Test
    fun createDefaultCollection_allCountriesHaveValidData() {
        val defaultCollection = CustomerCountriesCollection.createDefaultCollection()

        defaultCollection.getAll().forEach { country ->
            assertThat(country.id).isPositive()
            assertThat(country.name).isNotBlank()
            assertThat(country.countryCode).isNotBlank()
            assertThat(country.description).isNotBlank()
            assertThat(country.distributionPercentage).isBetween(0.0, 100.0)
        }
    }
}
