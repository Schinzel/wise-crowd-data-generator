package com.wisecrowd.data_generator.data_collections.activity_level

import com.wisecrowd.data_generator.data_collections.utils.DistributionUtils

/**
 * The purpose of this class is to manage a collection of activity levels,
 * providing methods to add, retrieve, and filter activity level data.
 *
 * Written by Claude Sonnet 4
 */
class ActivityLevelCollection {
    private val activityLevels = mutableListOf<ActivityLevel>()

    fun addActivityLevel(activityLevel: ActivityLevel) {
        activityLevels.add(activityLevel)
    }

    fun getAllActivityLevels(): List<ActivityLevel> = activityLevels.toList()

    fun getById(id: Int): ActivityLevel {
        return activityLevels.find { it.id == id }
            ?: throw IllegalArgumentException("Activity level with ID $id not found")
    }

    fun getByName(name: String): ActivityLevel {
        return activityLevels.find { it.name.equals(name, ignoreCase = true) }
            ?: throw IllegalArgumentException("Activity level with name '$name' not found")
    }

    fun getByDistributionRange(minPercentage: Double, maxPercentage: Double): List<ActivityLevel> {
        return DistributionUtils.filterByDistribution(
            activityLevels, minPercentage, maxPercentage
        ) { it.distributionPercentage }
    }

    fun size(): Int = activityLevels.size

    companion object {
        fun createDefaultCollection(): ActivityLevelCollection {
            val collection = ActivityLevelCollection()
            
            collection.addActivityLevel(ActivityLevel(
                id = 1, 
                name = "Inactive", 
                description = "Trades 0-1 times per year", 
                distributionPercentage = 20.0
            ))
            collection.addActivityLevel(ActivityLevel(
                id = 2, 
                name = "Low", 
                description = "Trades 2-4 times per year", 
                distributionPercentage = 35.0
            ))
            collection.addActivityLevel(ActivityLevel(
                id = 3, 
                name = "Moderate", 
                description = "Trades 5-12 times per year", 
                distributionPercentage = 25.0
            ))
            collection.addActivityLevel(ActivityLevel(
                id = 4, 
                name = "Active", 
                description = "Trades 13-52 times per year", 
                distributionPercentage = 8.0
            ))
            collection.addActivityLevel(ActivityLevel(
                id = 5, 
                name = "Hyperactive", 
                description = "Trades 53+ times per year", 
                distributionPercentage = 2.0
            ))
            
            return collection
        }
    }
}