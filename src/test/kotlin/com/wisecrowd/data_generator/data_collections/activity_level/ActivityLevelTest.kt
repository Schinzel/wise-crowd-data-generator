package com.wisecrowd.data_generator.data_collections.activity_level

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class ActivityLevelTest {

    @Test
    fun constructor_validInputs_setsId() {
        val activityLevel = ActivityLevel(
            id = 1, 
            name = "Inactive", 
            description = "Trades 0-1 times per year", 
            distributionPercentage = 20.0
        )
        
        assertThat(activityLevel.id).isEqualTo(1)
    }

    @Test
    fun constructor_validInputs_setsName() {
        val activityLevel = ActivityLevel(
            id = 1, 
            name = "Inactive", 
            description = "Trades 0-1 times per year", 
            distributionPercentage = 20.0
        )
        
        assertThat(activityLevel.name).isEqualTo("Inactive")
    }

    @Test
    fun constructor_validInputs_setsDescription() {
        val activityLevel = ActivityLevel(
            id = 1, 
            name = "Inactive", 
            description = "Trades 0-1 times per year", 
            distributionPercentage = 20.0
        )
        
        assertThat(activityLevel.description).isEqualTo("Trades 0-1 times per year")
    }

    @Test
    fun constructor_validInputs_setsDistributionPercentage() {
        val activityLevel = ActivityLevel(
            id = 1, 
            name = "Inactive", 
            description = "Trades 0-1 times per year", 
            distributionPercentage = 20.0
        )
        
        assertThat(activityLevel.distributionPercentage).isEqualTo(20.0)
    }

    @Test
    fun constructor_negativeId_throwsException() {
        assertThatThrownBy {
            ActivityLevel(
                id = -1, 
                name = "Inactive", 
                description = "Trades 0-1 times per year", 
                distributionPercentage = 20.0
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_zeroId_throwsException() {
        assertThatThrownBy {
            ActivityLevel(
                id = 0, 
                name = "Inactive", 
                description = "Trades 0-1 times per year", 
                distributionPercentage = 20.0
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_blankName_throwsException() {
        assertThatThrownBy {
            ActivityLevel(
                id = 1, 
                name = "", 
                description = "Trades 0-1 times per year", 
                distributionPercentage = 20.0
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_whitespaceName_throwsException() {
        assertThatThrownBy {
            ActivityLevel(
                id = 1, 
                name = "   ", 
                description = "Trades 0-1 times per year", 
                distributionPercentage = 20.0
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_blankDescription_throwsException() {
        assertThatThrownBy {
            ActivityLevel(
                id = 1, 
                name = "Inactive", 
                description = "", 
                distributionPercentage = 20.0
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_whitespaceDescription_throwsException() {
        assertThatThrownBy {
            ActivityLevel(
                id = 1, 
                name = "Inactive", 
                description = "   ", 
                distributionPercentage = 20.0
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_negativeDistributionPercentage_throwsException() {
        assertThatThrownBy {
            ActivityLevel(
                id = 1, 
                name = "Inactive", 
                description = "Trades 0-1 times per year", 
                distributionPercentage = -1.0
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_distributionPercentageAbove100_throwsException() {
        assertThatThrownBy {
            ActivityLevel(
                id = 1, 
                name = "Inactive", 
                description = "Trades 0-1 times per year", 
                distributionPercentage = 101.0
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun constructor_zeroDistributionPercentage_succeeds() {
        val activityLevel = ActivityLevel(
            id = 1, 
            name = "Inactive", 
            description = "Trades 0-1 times per year", 
            distributionPercentage = 0.0
        )
        
        assertThat(activityLevel.distributionPercentage).isEqualTo(0.0)
    }

    @Test
    fun constructor_exactlyOneHundredDistributionPercentage_succeeds() {
        val activityLevel = ActivityLevel(
            id = 1, 
            name = "Inactive", 
            description = "Trades 0-1 times per year", 
            distributionPercentage = 100.0
        )
        
        assertThat(activityLevel.distributionPercentage).isEqualTo(100.0)
    }

    @Test
    fun constructor_longName_succeeds() {
        val longName = "Very Long Activity Level Name That Exceeds Normal Length"
        val activityLevel = ActivityLevel(
            id = 1, 
            name = longName, 
            description = "Trades 0-1 times per year", 
            distributionPercentage = 20.0
        )
        
        assertThat(activityLevel.name).isEqualTo(longName)
    }

    @Test
    fun constructor_longDescription_succeeds() {
        val longDescription = "This is a very long description that explains the activity level in great detail and provides comprehensive information about the trading frequency and behavior patterns"
        val activityLevel = ActivityLevel(
            id = 1, 
            name = "Inactive", 
            description = longDescription, 
            distributionPercentage = 20.0
        )
        
        assertThat(activityLevel.description).isEqualTo(longDescription)
    }
}