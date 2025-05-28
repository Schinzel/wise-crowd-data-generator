package com.wisecrowd.data_generator.data_collections.investor_profile

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InvestorProfileCollectionTest {

    private lateinit var collection: InvestorProfileCollection
    private lateinit var conservativeProfile: InvestorProfile
    private lateinit var balancedProfile: InvestorProfile
    private lateinit var aggressiveProfile: InvestorProfile

    @BeforeEach
    fun setUp() {
        collection = InvestorProfileCollection()
        conservativeProfile = InvestorProfile(
            id = 1, 
            name = "Conservative", 
            description = "Risk-averse strategy", 
            distributionPercentage = 25.0
        )
        balancedProfile = InvestorProfile(
            id = 2, 
            name = "Balanced", 
            description = "Moderate approach", 
            distributionPercentage = 40.0
        )
        aggressiveProfile = InvestorProfile(
            id = 3, 
            name = "Aggressive", 
            description = "High risk strategy", 
            distributionPercentage = 20.0
        )
        
        collection.addInvestorProfile(conservativeProfile)
        collection.addInvestorProfile(balancedProfile)
        collection.addInvestorProfile(aggressiveProfile)
    }

    @Test
    fun addInvestorProfile_validProfile_increasesSizeByOne() {
        val incomeProfile = InvestorProfile(
            id = 4, 
            name = "Income", 
            description = "Focus on dividend generating assets", 
            distributionPercentage = 10.0
        )
        
        collection.addInvestorProfile(incomeProfile)
        
        assertThat(collection.size()).isEqualTo(4)
    }

    @Test
    fun getAllInvestorProfiles_withThreeProfiles_returnsAllThree() {
        val profiles = collection.getAllInvestorProfiles()
        
        assertThat(profiles).hasSize(3)
    }

    @Test
    fun getAllInvestorProfiles_withThreeProfiles_containsConservativeProfile() {
        val profiles = collection.getAllInvestorProfiles()
        
        assertThat(profiles).contains(conservativeProfile)
    }

    @Test
    fun getAllInvestorProfiles_withThreeProfiles_containsBalancedProfile() {
        val profiles = collection.getAllInvestorProfiles()
        
        assertThat(profiles).contains(balancedProfile)
    }

    @Test
    fun getAllInvestorProfiles_withThreeProfiles_containsAggressiveProfile() {
        val profiles = collection.getAllInvestorProfiles()
        
        assertThat(profiles).contains(aggressiveProfile)
    }

    @Test
    fun getById_existingId_returnsProfile() {
        val profile = collection.getById(1)
        
        assertThat(profile).isEqualTo(conservativeProfile)
    }

    @Test
    fun getById_nonExistingId_throwsException() {
        assertThatThrownBy {
            collection.getById(999)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun getByName_existingNameExactCase_returnsProfile() {
        val profile = collection.getByName("Conservative")
        
        assertThat(profile).isEqualTo(conservativeProfile)
    }

    @Test
    fun getByName_existingNameLowercase_returnsProfile() {
        val profile = collection.getByName("conservative")
        
        assertThat(profile).isEqualTo(conservativeProfile)
    }

    @Test
    fun getByName_existingNameMixedCase_returnsProfile() {
        val profile = collection.getByName("CoNsErVaTiVe")
        
        assertThat(profile).isEqualTo(conservativeProfile)
    }

    @Test
    fun getByName_nonExistingName_throwsException() {
        assertThatThrownBy {
            collection.getByName("NonExistent")
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun getByDistributionRange_validRange_returnsMatchingProfiles() {
        val profiles = collection.getByDistributionRange(25.0, 45.0)
        
        assertThat(profiles).hasSize(2)
    }

    @Test
    fun getByDistributionRange_validRange_containsConservativeProfile() {
        val profiles = collection.getByDistributionRange(25.0, 45.0)
        
        assertThat(profiles).contains(conservativeProfile)
    }

    @Test
    fun getByDistributionRange_validRange_containsBalancedProfile() {
        val profiles = collection.getByDistributionRange(25.0, 45.0)
        
        assertThat(profiles).contains(balancedProfile)
    }

    @Test
    fun getByDistributionRange_negativeMinPercentage_throwsException() {
        assertThatThrownBy {
            collection.getByDistributionRange(-1.0, 50.0)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun getByDistributionRange_maxPercentageAbove100_throwsException() {
        assertThatThrownBy {
            collection.getByDistributionRange(0.0, 101.0)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun getByDistributionRange_minGreaterThanMax_throwsException() {
        assertThatThrownBy {
            collection.getByDistributionRange(60.0, 50.0)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun size_withThreeProfiles_returnsThree() {
        assertThat(collection.size()).isEqualTo(3)
    }

    @Test
    fun createDefaultCollection_createsProfiles_hasCorrectSize() {
        val defaultCollection = InvestorProfileCollection.createDefaultCollection()
        
        assertThat(defaultCollection.size()).isEqualTo(5)
    }

    @Test
    fun createDefaultCollection_createsProfiles_containsConservative() {
        val defaultCollection = InvestorProfileCollection.createDefaultCollection()
        val conservativeFromDefault = defaultCollection.getById(1)
        
        assertThat(conservativeFromDefault.name).isEqualTo("Conservative")
    }

    @Test
    fun createDefaultCollection_createsProfiles_containsBalanced() {
        val defaultCollection = InvestorProfileCollection.createDefaultCollection()
        val balancedFromDefault = defaultCollection.getById(2)
        
        assertThat(balancedFromDefault.name).isEqualTo("Balanced")
    }

    @Test
    fun createDefaultCollection_createsProfiles_containsAggressive() {
        val defaultCollection = InvestorProfileCollection.createDefaultCollection()
        val aggressiveFromDefault = defaultCollection.getById(3)
        
        assertThat(aggressiveFromDefault.name).isEqualTo("Aggressive")
    }

    @Test
    fun createDefaultCollection_createsProfiles_containsIncome() {
        val defaultCollection = InvestorProfileCollection.createDefaultCollection()
        val incomeFromDefault = defaultCollection.getById(4)
        
        assertThat(incomeFromDefault.name).isEqualTo("Income")
    }

    @Test
    fun createDefaultCollection_createsProfiles_containsTrend() {
        val defaultCollection = InvestorProfileCollection.createDefaultCollection()
        val trendFromDefault = defaultCollection.getById(5)
        
        assertThat(trendFromDefault.name).isEqualTo("Trend")
    }

    @Test
    fun createDefaultCollection_conservativeProfile_hasCorrectDistribution() {
        val defaultCollection = InvestorProfileCollection.createDefaultCollection()
        val conservativeFromDefault = defaultCollection.getById(1)
        val expectedDistribution = 25.0
        
        assertThat(conservativeFromDefault.distributionPercentage).isEqualTo(expectedDistribution)
    }

    @Test
    fun createDefaultCollection_balancedProfile_hasCorrectDistribution() {
        val defaultCollection = InvestorProfileCollection.createDefaultCollection()
        val balancedFromDefault = defaultCollection.getById(2)
        val expectedDistribution = 40.0
        
        assertThat(balancedFromDefault.distributionPercentage).isEqualTo(expectedDistribution)
    }

    @Test
    fun createDefaultCollection_aggressiveProfile_hasCorrectDescription() {
        val defaultCollection = InvestorProfileCollection.createDefaultCollection()
        val aggressiveFromDefault = defaultCollection.getById(3)
        val expectedDescription = "High risk strategy seeking maximum returns"
        
        assertThat(aggressiveFromDefault.description).isEqualTo(expectedDescription)
    }

    @Test
    fun createDefaultCollection_incomeProfile_hasCorrectDescription() {
        val defaultCollection = InvestorProfileCollection.createDefaultCollection()
        val incomeFromDefault = defaultCollection.getById(4)
        val expectedDescription = "Focus on dividend/interest generating assets"
        
        assertThat(incomeFromDefault.description).isEqualTo(expectedDescription)
    }

    @Test
    fun createDefaultCollection_trendProfile_hasCorrectDistribution() {
        val defaultCollection = InvestorProfileCollection.createDefaultCollection()
        val trendFromDefault = defaultCollection.getById(5)
        val expectedDistribution = 5.0
        
        assertThat(trendFromDefault.distributionPercentage).isEqualTo(expectedDistribution)
    }
}