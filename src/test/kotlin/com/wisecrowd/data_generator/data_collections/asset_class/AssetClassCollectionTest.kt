package com.wisecrowd.data_generator.data_collections.asset_class

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach

class AssetClassCollectionTest {
    
    private lateinit var collection: AssetClassCollection
    private lateinit var assetClass1: AssetClass
    private lateinit var assetClass2: AssetClass
    
    @BeforeEach
    fun setUp() {
        collection = AssetClassCollection()
        assetClass1 = AssetClass(
            id = 1,
            name = "Nordic stocks",
            description = "Public company shares from Nordic high-growth markets",
            volatilityLevel = "Very High",
            prevalencePercentage = 33
        )
        assetClass2 = AssetClass(
            id = 2,
            name = "Government bond",
            description = "Fixed income security issued by sovereign governments",
            volatilityLevel = "Low",
            prevalencePercentage = 21
        )
    }
    
    @Test
    fun `add _ single asset class _ increases collection size to one`() {
        // Act
        collection.add(assetClass1)
        val size = collection.size()
        
        // Assert
        assertThat(size).isEqualTo(1)
    }
    
    @Test
    fun `add _ single asset class _ makes collection not empty`() {
        // Act
        collection.add(assetClass1)
        val isEmpty = collection.isEmpty()
        
        // Assert
        assertThat(isEmpty).isFalse()
    }
    
    @Test
    fun `addAll _ multiple asset classes _ increases collection size correctly`() {
        // Act
        collection.addAll(listOf(assetClass1, assetClass2))
        val size = collection.size()
        
        // Assert
        assertThat(size).isEqualTo(2)
    }
    
    @Test
    fun `getAll _ multiple asset classes _ returns all added asset classes`() {
        // Arrange
        collection.add(assetClass1)
        collection.add(assetClass2)
        
        // Act
        val result = collection.getAll()
        
        // Assert
        assertThat(result).containsExactly(assetClass1, assetClass2)
    }
    
    @Test
    fun `getAll _ empty collection _ returns empty list`() {
        // Act
        val result = collection.getAll()
        
        // Assert
        assertThat(result).isEmpty()
    }
    
    @Test
    fun `size _ empty collection _ returns zero`() {
        // Act
        val result = collection.size()
        
        // Assert
        assertThat(result).isEqualTo(0)
    }
    
    @Test
    fun `isEmpty _ empty collection _ returns true`() {
        // Act
        val result = collection.isEmpty()
        
        // Assert
        assertThat(result).isTrue()
    }
    
    @Test
    fun `getById _ existing id _ returns correct asset class`() {
        // Arrange
        collection.add(assetClass1)
        collection.add(assetClass2)
        
        // Act
        val result = collection.getById(1)
        
        // Assert
        assertThat(result).isEqualTo(assetClass1)
    }
    
    @Test
    fun `getById _ non existing id _ throws IllegalArgumentException`() {
        // Arrange
        collection.add(assetClass1)
        
        // Act & Assert
        assertThatThrownBy {
            collection.getById(999)
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Asset class with ID 999 not found")
    }
    
    @Test
    fun `getByVolatilityLevel _ existing level exact case _ returns matching asset classes`() {
        // Arrange
        collection.add(assetClass1) // Very High
        collection.add(assetClass2) // Low
        
        // Act
        val result = collection.getByVolatilityLevel("Low")
        
        // Assert
        assertThat(result).containsExactly(assetClass2)
    }
    
    @Test
    fun `getByVolatilityLevel _ existing level ignore case _ returns matching asset classes`() {
        // Arrange
        collection.add(assetClass1) // Very High
        collection.add(assetClass2) // Low
        
        // Act
        val result = collection.getByVolatilityLevel("low")
        
        // Assert
        assertThat(result).containsExactly(assetClass2)
    }
    
    @Test
    fun `getByVolatilityLevel _ non existing level _ returns empty list`() {
        // Arrange
        collection.add(assetClass1)
        
        // Act
        val result = collection.getByVolatilityLevel("NonExistent")
        
        // Assert
        assertThat(result).isEmpty()
    }
    
    @Test
    fun `getByPrevalenceRange _ valid range _ returns matching asset classes`() {
        // Arrange
        collection.add(assetClass1) // 33%
        collection.add(assetClass2) // 21%
        
        // Act
        val result = collection.getByPrevalenceRange(20, 25)
        
        // Assert
        assertThat(result).containsExactly(assetClass2)
    }
    
    @Test
    fun `getByPrevalenceRange _ range covers all _ returns all asset classes`() {
        // Arrange
        collection.add(assetClass1) // 33%
        collection.add(assetClass2) // 21%
        
        // Act
        val result = collection.getByPrevalenceRange(0, 100)
        
        // Assert
        assertThat(result).containsExactly(assetClass1, assetClass2)
    }
    
    @Test
    fun `getByPrevalenceRange _ no matches _ returns empty list`() {
        // Arrange
        collection.add(assetClass1) // 33%
        collection.add(assetClass2) // 21%
        
        // Act
        val result = collection.getByPrevalenceRange(50, 60)
        
        // Assert
        assertThat(result).isEmpty()
    }
    
    @Test
    fun `getByPrevalenceRange _ negative minimum _ throws IllegalArgumentException`() {
        // Act & Assert
        assertThatThrownBy {
            collection.getByPrevalenceRange(-1, 50)
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Minimum prevalence must be non-negative")
    }
    
    @Test
    fun `getByPrevalenceRange _ maximum over 100 _ throws IllegalArgumentException`() {
        // Act & Assert
        assertThatThrownBy {
            collection.getByPrevalenceRange(0, 101)
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Maximum prevalence must not exceed 100")
    }
    
    @Test
    fun `getByPrevalenceRange _ minimum greater than maximum _ throws IllegalArgumentException`() {
        // Act & Assert
        assertThatThrownBy {
            collection.getByPrevalenceRange(60, 50)
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Minimum prevalence cannot be greater than maximum prevalence")
    }
    
    @Test
    fun `createDefaultCollection _ creates collection with expected size`() {
        // Act
        val result = AssetClassCollection.createDefaultCollection()
        val size = result.size()
        
        // Assert
        assertThat(size).isEqualTo(8)
    }
    
    @Test
    fun `createDefaultCollection _ contains Nordic stocks with correct properties`() {
        // Act
        val result = AssetClassCollection.createDefaultCollection()
        val nordicStocks = result.getById(1)
        val name = nordicStocks.name
        
        // Assert
        assertThat(name).isEqualTo("Nordic stocks")
    }
    
    @Test
    fun `createDefaultCollection _ contains Government bond with correct properties`() {
        // Act
        val result = AssetClassCollection.createDefaultCollection()
        val governmentBond = result.getById(2)
        val name = governmentBond.name
        
        // Assert
        assertThat(name).isEqualTo("Government bond")
    }
    
    @Test
    fun `createDefaultCollection _ contains Crypto with correct prevalence`() {
        // Act
        val result = AssetClassCollection.createDefaultCollection()
        val crypto = result.getById(8)
        val prevalence = crypto.prevalencePercentage
        
        // Assert
        assertThat(prevalence).isEqualTo(1)
    }
    
    @Test
    fun `createDefaultCollection _ very high volatility assets _ returns correct count`() {
        // Act
        val result = AssetClassCollection.createDefaultCollection()
        val veryHighVolatilityAssets = result.getByVolatilityLevel("Very High")
        val count = veryHighVolatilityAssets.size
        
        // Assert
        assertThat(count).isEqualTo(2)
    }
}