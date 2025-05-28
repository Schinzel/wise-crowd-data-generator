package com.wisecrowd.data_generator.data_collections.activity_level

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ActivityLevelCollectionTest {

    private lateinit var collection: ActivityLevelCollection
    private lateinit var inactiveLevel: ActivityLevel
    private lateinit var lowLevel: ActivityLevel
    private lateinit var moderateLevel: ActivityLevel

    @BeforeEach
    fun setUp() {
        collection = ActivityLevelCollection()
        inactiveLevel = ActivityLevel(
            id = 1, 
            name = "Inactive", 
            description = "Trades 0-1 times per year", 
            distributionPercentage = 20.0
        )
        lowLevel = ActivityLevel(
            id = 2, 
            name = "Low", 
            description = "Trades 2-4 times per year", 
            distributionPercentage = 35.0
        )
        moderateLevel = ActivityLevel(
            id = 3, 
            name = "Moderate", 
            description = "Trades 5-12 times per year", 
            distributionPercentage = 25.0
        )
        
        collection.addActivityLevel(inactiveLevel)
        collection.addActivityLevel(lowLevel)
        collection.addActivityLevel(moderateLevel)
    }

    @Test
    fun addActivityLevel_validLevel_increasesSizeByOne() {
        val activeLevel = ActivityLevel(
            id = 4, 
            name = "Active", 
            description = "Trades 13-52 times per year", 
            distributionPercentage = 8.0
        )
        
        collection.addActivityLevel(activeLevel)
        
        assertThat(collection.size()).isEqualTo(4)
    }

    @Test
    fun getAllActivityLevels_withThreeLevels_returnsAllThree() {
        val levels = collection.getAllActivityLevels()
        
        assertThat(levels).hasSize(3)
    }

    @Test
    fun getAllActivityLevels_withThreeLevels_containsInactiveLevel() {
        val levels = collection.getAllActivityLevels()
        
        assertThat(levels).contains(inactiveLevel)
    }

    @Test
    fun getAllActivityLevels_withThreeLevels_containsLowLevel() {
        val levels = collection.getAllActivityLevels()
        
        assertThat(levels).contains(lowLevel)
    }

    @Test
    fun getAllActivityLevels_withThreeLevels_containsModerateLevel() {
        val levels = collection.getAllActivityLevels()
        
        assertThat(levels).contains(moderateLevel)
    }

    @Test
    fun getById_existingId_returnsLevel() {
        val level = collection.getById(1)
        
        assertThat(level).isEqualTo(inactiveLevel)
    }

    @Test
    fun getById_nonExistingId_throwsException() {
        assertThatThrownBy {
            collection.getById(999)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun getByName_existingNameExactCase_returnsLevel() {
        val level = collection.getByName("Inactive")
        
        assertThat(level).isEqualTo(inactiveLevel)
    }

    @Test
    fun getByName_existingNameLowercase_returnsLevel() {
        val level = collection.getByName("inactive")
        
        assertThat(level).isEqualTo(inactiveLevel)
    }

    @Test
    fun getByName_existingNameMixedCase_returnsLevel() {
        val level = collection.getByName("InAcTiVe")
        
        assertThat(level).isEqualTo(inactiveLevel)
    }

    @Test
    fun getByName_nonExistingName_throwsException() {
        assertThatThrownBy {
            collection.getByName("NonExistent")
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun getByDistributionRange_validRange_returnsMatchingLevels() {
        val levels = collection.getByDistributionRange(20.0, 35.0)
        
        assertThat(levels).hasSize(3)
    }

    @Test
    fun getByDistributionRange_validRange_containsInactiveLevel() {
        val levels = collection.getByDistributionRange(20.0, 35.0)
        
        assertThat(levels).contains(inactiveLevel)
    }

    @Test
    fun getByDistributionRange_validRange_containsLowLevel() {
        val levels = collection.getByDistributionRange(20.0, 35.0)
        
        assertThat(levels).contains(lowLevel)
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
    fun size_withThreeLevels_returnsThree() {
        assertThat(collection.size()).isEqualTo(3)
    }

    @Test
    fun createDefaultCollection_createsLevels_hasCorrectSize() {
        val defaultCollection = ActivityLevelCollection.createDefaultCollection()
        
        assertThat(defaultCollection.size()).isEqualTo(5)
    }

    @Test
    fun createDefaultCollection_createsLevels_containsInactive() {
        val defaultCollection = ActivityLevelCollection.createDefaultCollection()
        val inactiveFromDefault = defaultCollection.getById(1)
        
        assertThat(inactiveFromDefault.name).isEqualTo("Inactive")
    }

    @Test
    fun createDefaultCollection_createsLevels_containsLow() {
        val defaultCollection = ActivityLevelCollection.createDefaultCollection()
        val lowFromDefault = defaultCollection.getById(2)
        
        assertThat(lowFromDefault.name).isEqualTo("Low")
    }

    @Test
    fun createDefaultCollection_createsLevels_containsModerate() {
        val defaultCollection = ActivityLevelCollection.createDefaultCollection()
        val moderateFromDefault = defaultCollection.getById(3)
        
        assertThat(moderateFromDefault.name).isEqualTo("Moderate")
    }

    @Test
    fun createDefaultCollection_createsLevels_containsActive() {
        val defaultCollection = ActivityLevelCollection.createDefaultCollection()
        val activeFromDefault = defaultCollection.getById(4)
        
        assertThat(activeFromDefault.name).isEqualTo("Active")
    }

    @Test
    fun createDefaultCollection_createsLevels_containsHyperactive() {
        val defaultCollection = ActivityLevelCollection.createDefaultCollection()
        val hyperactiveFromDefault = defaultCollection.getById(5)
        
        assertThat(hyperactiveFromDefault.name).isEqualTo("Hyperactive")
    }

    @Test
    fun createDefaultCollection_inactiveLevel_hasCorrectDistribution() {
        val defaultCollection = ActivityLevelCollection.createDefaultCollection()
        val inactiveFromDefault = defaultCollection.getById(1)
        val expectedDistribution = 20.0
        
        assertThat(inactiveFromDefault.distributionPercentage).isEqualTo(expectedDistribution)
    }

    @Test
    fun createDefaultCollection_lowLevel_hasCorrectDistribution() {
        val defaultCollection = ActivityLevelCollection.createDefaultCollection()
        val lowFromDefault = defaultCollection.getById(2)
        val expectedDistribution = 35.0
        
        assertThat(lowFromDefault.distributionPercentage).isEqualTo(expectedDistribution)
    }

    @Test
    fun createDefaultCollection_activeLevel_hasCorrectDescription() {
        val defaultCollection = ActivityLevelCollection.createDefaultCollection()
        val activeFromDefault = defaultCollection.getById(4)
        val expectedDescription = "Trades 13-52 times per year"
        
        assertThat(activeFromDefault.description).isEqualTo(expectedDescription)
    }

    @Test
    fun createDefaultCollection_hyperactiveLevel_hasCorrectDescription() {
        val defaultCollection = ActivityLevelCollection.createDefaultCollection()
        val hyperactiveFromDefault = defaultCollection.getById(5)
        val expectedDescription = "Trades 53+ times per year"
        
        assertThat(hyperactiveFromDefault.description).isEqualTo(expectedDescription)
    }

    @Test
    fun createDefaultCollection_hyperactiveLevel_hasCorrectDistribution() {
        val defaultCollection = ActivityLevelCollection.createDefaultCollection()
        val hyperactiveFromDefault = defaultCollection.getById(5)
        val expectedDistribution = 2.0
        
        assertThat(hyperactiveFromDefault.distributionPercentage).isEqualTo(expectedDistribution)
    }
}