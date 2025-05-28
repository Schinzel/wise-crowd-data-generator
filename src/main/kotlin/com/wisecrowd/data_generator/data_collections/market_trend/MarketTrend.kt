package com.wisecrowd.data_generator.data_collections.market_trend

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * The purpose of this class is to represent a single market trend entry with
 * its timeframe, type, strength, and description.
 *
 * Written by Claude 3.7 Sonnet
 */
data class MarketTrend(
    /** Start date of the trend period as string (format: YYYY-MM-DD) */
    val startDateString: String,
    
    /** End date of the trend period as string (format: YYYY-MM-DD) */
    val endDateString: String,
    
    /** Type of market trend (e.g., Bull, Bear, Recovery) */
    val trendType: String,
    
    /** Strength factor of the trend (positive for bull, negative for bear) */
    val strength: Double,
    
    /** Description of the market event or period */
    val description: String
) {
    /** Start date of the trend period */
    val startDate: LocalDate = parseDate(startDateString, "start date")
    
    /** End date of the trend period */
    val endDate: LocalDate = parseDate(endDateString, "end date")
    
    private fun parseDate(dateString: String, fieldName: String): LocalDate {
        return try {
            LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException(
                "Invalid $fieldName format '$dateString'. Expected format: YYYY-MM-DD", e
            )
        }
    }
    
    init {
        // Validate that end date is not before start date
        require(endDate >= startDate) { 
            "End date ($endDate) cannot be before start date ($startDate)" 
        }
        
        // Validate strength is within reasonable range (-5.0 to 5.0)
        require(strength in -5.0..5.0) { 
            "Strength ($strength) must be between -5.0 and 5.0" 
        }
        
        // Validate that trend type is not empty
        require(trendType.isNotBlank()) { 
            "Trend type cannot be empty" 
        }
    }
}