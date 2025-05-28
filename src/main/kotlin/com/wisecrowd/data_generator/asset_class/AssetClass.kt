package com.wisecrowd.data_generator.asset_class

/**
 * The purpose of this class is to represent a single asset class with
 * its properties including ID, name, description, volatility level and prevalence.
 *
 * Written by Claude 3.7 Sonnet
 */
data class AssetClass(
    /** Unique identifier for the asset class */
    val id: Int,
    
    /** Name of the asset class */
    val name: String,
    
    /** Description of the asset class */
    val description: String,
    
    /** Volatility level (Low, Low-Medium, Medium, Medium-High, High, Very High) */
    val volatilityLevel: String,
    
    /** Prevalence percentage in the market (0-100) */
    val prevalencePercentage: Int
) {
    init {
        // Validate that ID is positive
        require(id > 0) { 
            "Asset class ID ($id) must be positive" 
        }
        
        // Validate that name is not empty
        require(name.isNotBlank()) { 
            "Asset class name cannot be empty" 
        }
        
        // Validate that description is not empty
        require(description.isNotBlank()) { 
            "Asset class description cannot be empty" 
        }
        
        // Validate that volatility level is not empty
        require(volatilityLevel.isNotBlank()) { 
            "Volatility level cannot be empty" 
        }
        
        // Validate prevalence percentage is within valid range
        require(prevalencePercentage in 0..100) { 
            "Prevalence percentage ($prevalencePercentage) must be between 0 and 100" 
        }
    }
}