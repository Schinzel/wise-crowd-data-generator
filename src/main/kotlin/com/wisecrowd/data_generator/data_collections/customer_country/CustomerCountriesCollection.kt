package com.wisecrowd.data_generator.data_collections.customer_country

/**
 * The purpose of this class is to manage a collection of customer countries,
 * providing methods to add, retrieve, and filter customer country data.
 *
 * Written by Claude Sonnet 4
 */
class CustomerCountriesCollection {
    private val countries = mutableListOf<CustomerCountry>()

    fun addCountry(country: CustomerCountry) {
        countries.add(country)
    }

    fun getAll(): List<CustomerCountry> = countries.toList()

    fun getById(id: Int): CustomerCountry {
        return countries.find { it.id == id }
            ?: throw IllegalArgumentException("Customer country with ID $id not found")
    }

    fun getByCountryCode(countryCode: String): CustomerCountry {
        return countries.find { it.countryCode.equals(countryCode, ignoreCase = true) }
            ?: throw IllegalArgumentException("Customer country with code '$countryCode' not found")
    }

    fun getByDistribution(minPercentage: Double, maxPercentage: Double): List<CustomerCountry> {
        require(minPercentage >= 0.0 && minPercentage <= 100.0) {
            "Min percentage must be between 0 and 100, but was: $minPercentage"
        }
        require(maxPercentage >= 0.0 && maxPercentage <= 100.0) {
            "Max percentage must be between 0 and 100, but was: $maxPercentage"
        }
        require(minPercentage <= maxPercentage) {
            "Min percentage ($minPercentage) must be less than or equal to max percentage ($maxPercentage)"
        }
        
        return countries.filter { it.distributionPercentage in minPercentage..maxPercentage }
    }

    fun size(): Int = countries.size

    companion object {
        fun createDefaultCollection(): CustomerCountriesCollection {
            val collection = CustomerCountriesCollection()
            
            collection.addCountry(CustomerCountry(
                id = 1,
                name = "Sweden",
                countryCode = "SE",
                description = "Home market with largest customer base",
                distributionPercentage = 60.0
            ))
            
            collection.addCountry(CustomerCountry(
                id = 2,
                name = "Norway",
                countryCode = "NO",
                description = "Oil wealth economy with active investors",
                distributionPercentage = 15.0
            ))
            
            collection.addCountry(CustomerCountry(
                id = 3,
                name = "Denmark",
                countryCode = "DK",
                description = "Strong financial sector and banking ties",
                distributionPercentage = 12.0
            ))
            
            collection.addCountry(CustomerCountry(
                id = 4,
                name = "Finland",
                countryCode = "FI",
                description = "Tech-savvy market with Nordic connections",
                distributionPercentage = 8.0
            ))
            
            collection.addCountry(CustomerCountry(
                id = 5,
                name = "Iceland",
                countryCode = "IS",
                description = "Small but wealthy per capita customer base",
                distributionPercentage = 5.0
            ))
            
            return collection
        }
    }
}