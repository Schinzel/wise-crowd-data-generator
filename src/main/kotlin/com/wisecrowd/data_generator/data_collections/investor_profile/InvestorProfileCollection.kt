package com.wisecrowd.data_generator.data_collections.investor_profile

/**
 * The purpose of this class is to manage a collection of investor profiles,
 * providing methods to add, retrieve, and filter investor profile data.
 *
 * Written by Claude Sonnet 4
 */
class InvestorProfileCollection {
    private val investorProfiles = mutableListOf<InvestorProfile>()

    fun addInvestorProfile(investorProfile: InvestorProfile) {
        investorProfiles.add(investorProfile)
    }

    fun getAllInvestorProfiles(): List<InvestorProfile> = investorProfiles.toList()

    fun getById(id: Int): InvestorProfile {
        return investorProfiles.find { it.id == id }
            ?: throw IllegalArgumentException("Investor profile with ID $id not found")
    }

    fun getByName(name: String): InvestorProfile {
        return investorProfiles.find { it.name.equals(name, ignoreCase = true) }
            ?: throw IllegalArgumentException("Investor profile with name '$name' not found")
    }

    fun getByDistributionRange(minPercentage: Double, maxPercentage: Double): List<InvestorProfile> {
        require(minPercentage >= 0.0 && minPercentage <= 100.0) {
            "Min percentage must be between 0 and 100, but was: $minPercentage"
        }
        require(maxPercentage >= 0.0 && maxPercentage <= 100.0) {
            "Max percentage must be between 0 and 100, but was: $maxPercentage"
        }
        require(minPercentage <= maxPercentage) {
            "Min percentage ($minPercentage) must be less than or equal to max percentage ($maxPercentage)"
        }
        
        return investorProfiles.filter { it.distributionPercentage in minPercentage..maxPercentage }
    }

    fun size(): Int = investorProfiles.size

    companion object {
        fun createDefaultCollection(): InvestorProfileCollection {
            val collection = InvestorProfileCollection()
            
            collection.addInvestorProfile(InvestorProfile(
                id = 1, 
                name = "Conservative", 
                description = "Risk-averse strategy prioritizing capital preservation", 
                distributionPercentage = 25.0
            ))
            collection.addInvestorProfile(InvestorProfile(
                id = 2, 
                name = "Balanced", 
                description = "Moderate approach balancing growth and stability", 
                distributionPercentage = 40.0
            ))
            collection.addInvestorProfile(InvestorProfile(
                id = 3, 
                name = "Aggressive", 
                description = "High risk strategy seeking maximum returns", 
                distributionPercentage = 20.0
            ))
            collection.addInvestorProfile(InvestorProfile(
                id = 4, 
                name = "Income", 
                description = "Focus on dividend/interest generating assets", 
                distributionPercentage = 10.0
            ))
            collection.addInvestorProfile(InvestorProfile(
                id = 5, 
                name = "Trend", 
                description = "Follows market momentum, adapting to conditions", 
                distributionPercentage = 5.0
            ))
            
            return collection
        }
    }
}