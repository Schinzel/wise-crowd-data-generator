package com.wisecrowd.data_generator.data_collections.investor_profile

import com.wisecrowd.data_generator.data_collections.utils.DistributionUtils

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

    fun getById(id: Int): InvestorProfile =
        investorProfiles.find { it.id == id }
            ?: throw IllegalArgumentException("Investor profile with ID $id not found")

    fun getByName(name: String): InvestorProfile =
        investorProfiles.find { it.name.equals(name, ignoreCase = true) }
            ?: throw IllegalArgumentException("Investor profile with name '$name' not found")

    fun getByDistributionRange(
        minPercentage: Double,
        maxPercentage: Double,
    ): List<InvestorProfile> =
        DistributionUtils.filterByDistribution(
            investorProfiles,
            minPercentage,
            maxPercentage,
        ) { it.distributionPercentage }

    fun size(): Int = investorProfiles.size

    companion object {
        fun createDefaultCollection(): InvestorProfileCollection {
            val collection = InvestorProfileCollection()

            collection.addInvestorProfile(
                InvestorProfile(
                    id = 1,
                    name = "Conservative",
                    description = "Risk-averse strategy prioritizing capital preservation",
                    distributionPercentage = 25.0,
                ),
            )
            collection.addInvestorProfile(
                InvestorProfile(
                    id = 2,
                    name = "Balanced",
                    description = "Moderate approach balancing growth and stability",
                    distributionPercentage = 40.0,
                ),
            )
            collection.addInvestorProfile(
                InvestorProfile(
                    id = 3,
                    name = "Aggressive",
                    description = "High risk strategy seeking maximum returns",
                    distributionPercentage = 20.0,
                ),
            )
            collection.addInvestorProfile(
                InvestorProfile(
                    id = 4,
                    name = "Income",
                    description = "Focus on dividend/interest generating assets",
                    distributionPercentage = 10.0,
                ),
            )
            collection.addInvestorProfile(
                InvestorProfile(
                    id = 5,
                    name = "Trend",
                    description = "Follows market momentum, adapting to conditions",
                    distributionPercentage = 5.0,
                ),
            )

            return collection
        }
    }
}
