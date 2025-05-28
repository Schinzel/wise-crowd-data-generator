package com.wisecrowd.data_generator.data_collections.investor_profile

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class InvestorProfileTest {

    @Test
    fun constructor_validInputs_setsId() {
        val investorProfile = InvestorProfile(
            id = 1, 
            name = "Conservative", 
            description = "Risk-averse strategy", 
            distributionPercentage = 25.0
        )
        
        assertThat(investorProfile.id).isEqualTo(1)
    }

    @Test
    fun constructor_validInputs_setsName() {
        val investorProfile = InvestorProfile(
            id = 1, 
            name = "Conservative", 
            description = "Risk-averse strategy", 
            distributionPercentage = 25.0
        )
        
        assertThat(investorProfile.name).isEqualTo("Conservative")
    }

    @Test
    fun constructor_validInputs_setsDescription() {
        val investorProfile = InvestorProfile(
            id = 1, 
            name = "Conservative", 
            description = "Risk-averse strategy", 
            distributionPercentage = 25.0
        )
        
        assertThat(investorProfile.description).isEqualTo("Risk-averse strategy")
    }

    @Test
    fun constructor_validInputs_setsDistributionPercentage() {
        val investorProfile = InvestorProfile(
            id = 1, 
            name = "Conservative", 
            description = "Risk-averse strategy", 
            distributionPercentage = 25.0
        )
        
        assertThat(investorProfile.distributionPercentage).isEqualTo(25.0)
    }

    @Test
    fun constructor_negativeId_throwsException() {
        assertThatThrownBy {
            InvestorProfile(
                id = -1, 
                name = "Conservative", 
                description = "Risk-averse strategy", 
                distributionPercentage = 25.0
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_zeroId_throwsException() {
        assertThatThrownBy {
            InvestorProfile(
                id = 0, 
                name = "Conservative", 
                description = "Risk-averse strategy", 
                distributionPercentage = 25.0
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_blankName_throwsException() {
        assertThatThrownBy {
            InvestorProfile(
                id = 1, 
                name = "", 
                description = "Risk-averse strategy", 
                distributionPercentage = 25.0
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_whitespaceName_throwsException() {
        assertThatThrownBy {
            InvestorProfile(
                id = 1, 
                name = "   ", 
                description = "Risk-averse strategy", 
                distributionPercentage = 25.0
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_blankDescription_throwsException() {
        assertThatThrownBy {
            InvestorProfile(
                id = 1, 
                name = "Conservative", 
                description = "", 
                distributionPercentage = 25.0
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_whitespaceDescription_throwsException() {
        assertThatThrownBy {
            InvestorProfile(
                id = 1, 
                name = "Conservative", 
                description = "   ", 
                distributionPercentage = 25.0
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_negativeDistributionPercentage_throwsException() {
        assertThatThrownBy {
            InvestorProfile(
                id = 1, 
                name = "Conservative", 
                description = "Risk-averse strategy", 
                distributionPercentage = -1.0
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_distributionPercentageAbove100_throwsException() {
        assertThatThrownBy {
            InvestorProfile(
                id = 1, 
                name = "Conservative", 
                description = "Risk-averse strategy", 
                distributionPercentage = 101.0
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_zeroDistributionPercentage_succeeds() {
        val investorProfile = InvestorProfile(
            id = 1, 
            name = "Conservative", 
            description = "Risk-averse strategy", 
            distributionPercentage = 0.0
        )
        
        assertThat(investorProfile.distributionPercentage).isEqualTo(0.0)
    }

    @Test
    fun constructor_exactlyOneHundredDistributionPercentage_succeeds() {
        val investorProfile = InvestorProfile(
            id = 1, 
            name = "Conservative", 
            description = "Risk-averse strategy", 
            distributionPercentage = 100.0
        )
        
        assertThat(investorProfile.distributionPercentage).isEqualTo(100.0)
    }

    @Test
    fun constructor_longName_succeeds() {
        val longName = "Very Long Investor Profile Name That Exceeds Normal Length"
        val investorProfile = InvestorProfile(
            id = 1, 
            name = longName, 
            description = "Risk-averse strategy", 
            distributionPercentage = 25.0
        )
        
        assertThat(investorProfile.name).isEqualTo(longName)
    }

    @Test
    fun constructor_longDescription_succeeds() {
        val longDescription = "This is a very long description that explains the investor profile in great detail and provides comprehensive information about the strategy and approach"
        val investorProfile = InvestorProfile(
            id = 1, 
            name = "Conservative", 
            description = longDescription, 
            distributionPercentage = 25.0
        )
        
        assertThat(investorProfile.description).isEqualTo(longDescription)
    }
}