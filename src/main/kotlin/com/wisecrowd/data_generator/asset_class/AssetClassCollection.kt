package com.wisecrowd.data_generator.asset_class

/**
 * The purpose of this class is to manage a collection of asset classes and
 * provide functionality to access and filter them.
 *
 * Written by Claude 3.7 Sonnet
 */
class AssetClassCollection {
    private val assetClasses = mutableListOf<AssetClass>()
    
    /**
     * Adds an asset class to the collection
     * 
     * @param assetClass The asset class to add
     */
    fun add(assetClass: AssetClass) {
        assetClasses.add(assetClass)
    }
    
    /**
     * Adds multiple asset classes to the collection
     * 
     * @param assetClassList The list of asset classes to add
     */
    fun addAll(assetClassList: List<AssetClass>) {
        assetClasses.addAll(assetClassList)
    }
    
    /**
     * Gets all asset classes in the collection
     * 
     * @return Immutable list of all asset classes
     */
    fun getAll(): List<AssetClass> = assetClasses.toList()
    
    /**
     * Gets the number of asset classes in the collection
     * 
     * @return The count of asset classes
     */
    fun size(): Int = assetClasses.size
    
    /**
     * Checks if the collection is empty
     * 
     * @return True if there are no asset classes, false otherwise
     */
    fun isEmpty(): Boolean = assetClasses.isEmpty()
    
    /**
     * Gets an asset class by its ID
     * 
     * @param id The ID of the asset class to find
     * @return The asset class with the specified ID
     * @throws IllegalArgumentException if no asset class with the specified ID exists
     */
    fun getById(id: Int): AssetClass {
        return assetClasses.find { it.id == id }
            ?: throw IllegalArgumentException("Asset class with ID $id not found")
    }
    
    /**
     * Gets asset classes by volatility level
     * 
     * @param volatilityLevel The volatility level to filter by
     * @return List of asset classes with the specified volatility level
     */
    fun getByVolatilityLevel(volatilityLevel: String): List<AssetClass> {
        return assetClasses.filter { 
            it.volatilityLevel.equals(volatilityLevel, ignoreCase = true) 
        }
    }
    
    /**
     * Gets asset classes within a specific prevalence range
     * 
     * @param minPrevalence Minimum prevalence percentage (inclusive)
     * @param maxPrevalence Maximum prevalence percentage (inclusive)
     * @return List of asset classes within the prevalence range
     */
    fun getByPrevalenceRange(minPrevalence: Int, maxPrevalence: Int): List<AssetClass> {
        require(minPrevalence >= 0) { "Minimum prevalence must be non-negative" }
        require(maxPrevalence <= 100) { "Maximum prevalence must not exceed 100" }
        require(minPrevalence <= maxPrevalence) { 
            "Minimum prevalence cannot be greater than maximum prevalence" 
        }
        
        return assetClasses.filter { 
            it.prevalencePercentage in minPrevalence..maxPrevalence 
        }
    }
    
    /**
     * Creates default asset class data as specified in the design document
     * 
     * @return A populated AssetClassCollection
     */
    companion object {
        fun createDefaultCollection(): AssetClassCollection {
            val collection = AssetClassCollection()
            
            // Add asset classes from the design document
            collection.addAll(listOf(
                AssetClass(
                    id = 1,
                    name = "Nordic stocks",
                    description = "Public company shares from Nordic high-growth markets",
                    volatilityLevel = "Very High",
                    prevalencePercentage = 33
                ),
                AssetClass(
                    id = 2,
                    name = "Government bond",
                    description = "Fixed income security issued by sovereign governments",
                    volatilityLevel = "Low",
                    prevalencePercentage = 21
                ),
                AssetClass(
                    id = 3,
                    name = "Corporate Bond",
                    description = "Fixed income security issued by private companies",
                    volatilityLevel = "Low-Medium",
                    prevalencePercentage = 13
                ),
                AssetClass(
                    id = 4,
                    name = "Medium-Risk Fund",
                    description = "Diversified fund with both stocks and bonds",
                    volatilityLevel = "Medium",
                    prevalencePercentage = 20
                ),
                AssetClass(
                    id = 5,
                    name = "Large-Cap Equity",
                    description = "Shares in large to established foreign companies",
                    volatilityLevel = "Medium-High",
                    prevalencePercentage = 25
                ),
                AssetClass(
                    id = 6,
                    name = "Gold / Precious Metals",
                    description = "Physical commodity or related securities",
                    volatilityLevel = "High",
                    prevalencePercentage = 5
                ),
                AssetClass(
                    id = 7,
                    name = "REITs",
                    description = "Real estate investment trusts",
                    volatilityLevel = "Medium",
                    prevalencePercentage = 3
                ),
                AssetClass(
                    id = 8,
                    name = "Crypto",
                    description = "Digital currencies, a higher risk exposure",
                    volatilityLevel = "Very High",
                    prevalencePercentage = 1
                )
            ))
            
            return collection
        }
    }
}