package com.wisecrowd.data_generator.data_generators.transaction_data_generator

import com.wisecrowd.data_generator.data_generators.user_data_generator.CustomerStatus
import java.time.LocalDate
import java.util.*

/**
 * The purpose of this data class is to represent user information needed
 * for transaction generation with clear property names and type safety.
 *
 * Written by Claude Sonnet 4
 */
data class UserData(
    /** The unique identifier for the user */
    val userId: UUID,
    
    /** The investor profile ID indicating risk tolerance */
    val investorProfileId: Int,
    
    /** The activity level ID indicating trading frequency */
    val activityLevelId: Int,
    
    /** The country ID for the user's location */
    val countryId: Int,
    
    /** The date when the user joined the platform */
    val joinDate: LocalDate,
    
    /** The date when the user departed (or sentinel date if still active) */
    val departureDate: LocalDate,
    
    /** The current status of the customer */
    val customerStatus: CustomerStatus
) {
    init {
        require(investorProfileId > 0) { "Investor profile ID must be positive, but was: $investorProfileId" }
        require(activityLevelId > 0) { "Activity level ID must be positive, but was: $activityLevelId" }
        require(countryId > 0) { "Country ID must be positive, but was: $countryId" }
        require(departureDate >= joinDate) { 
            "Departure date ($departureDate) cannot be before join date ($joinDate)" 
        }
    }
}