package com.wisecrowd.data_generator.data_collections.asset_class

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class AssetClassTest {

    @Test
    fun `constructor _ valid asset class _ creates object with correct id`() {
        // Arrange
        val id = 1
        val name = "Nordic stocks"
        val description = "Public company shares from Nordic high-growth markets"
        val volatilityLevel = VolatilityLevelEnum.VERY_HIGH
        val prevalencePercentage = 33
        
        // Act
        val assetClass = AssetClass(
            id = id,
            name = name,
            description = description,
            volatilityLevel = volatilityLevel,
            prevalencePercentage = prevalencePercentage
        )
        
        // Assert
        assertThat(assetClass.id).isEqualTo(id)
    }
    
    @Test
    fun `constructor _ valid asset class _ creates object with correct name`() {
        // Arrange
        val name = "Nordic stocks"
        
        // Act
        val assetClass = AssetClass(
            id = 1,
            name = name,
            description = "Test Description",
            volatilityLevel = VolatilityLevelEnum.VERY_HIGH,
            prevalencePercentage = 33
        )
        
        // Assert
        assertThat(assetClass.name).isEqualTo(name)
    }
    
    @Test
    fun `constructor _ valid asset class _ creates object with correct description`() {
        // Arrange
        val description = "Public company shares from Nordic high-growth markets"
        
        // Act
        val assetClass = AssetClass(
            id = 1,
            name = "Nordic stocks",
            description = description,
            volatilityLevel = VolatilityLevelEnum.VERY_HIGH,
            prevalencePercentage = 33
        )
        
        // Assert
        assertThat(assetClass.description).isEqualTo(description)
    }
    
    @Test
    fun `constructor _ valid asset class _ creates object with correct volatility level`() {
        // Arrange
        val volatilityLevel = VolatilityLevelEnum.VERY_HIGH
        
        // Act
        val assetClass = AssetClass(
            id = 1,
            name = "Nordic stocks",
            description = "Test Description",
            volatilityLevel = volatilityLevel,
            prevalencePercentage = 33
        )
        
        // Assert
        assertThat(assetClass.volatilityLevel).isEqualTo(volatilityLevel)
    }
    
    @Test
    fun `constructor _ valid asset class _ creates object with correct prevalence percentage`() {
        // Arrange
        val prevalencePercentage = 33
        
        // Act
        val assetClass = AssetClass(
            id = 1,
            name = "Nordic stocks",
            description = "Test Description",
            volatilityLevel = VolatilityLevelEnum.VERY_HIGH,
            prevalencePercentage = prevalencePercentage
        )
        
        // Assert
        assertThat(assetClass.prevalencePercentage).isEqualTo(prevalencePercentage)
    }
    
    @Test
    fun `constructor _ negative id _ throws IllegalArgumentException`() {
        // Act & Assert
        assertThatThrownBy {
            AssetClass(
                id = -1,
                name = "Test Asset",
                description = "Test Description",
                volatilityLevel = VolatilityLevelEnum.LOW,
                prevalencePercentage = 10
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Asset class ID")
            .hasMessageContaining("must be positive")
    }
    
    @Test
    fun `constructor _ zero id _ throws IllegalArgumentException`() {
        // Act & Assert
        assertThatThrownBy {
            AssetClass(
                id = 0,
                name = "Test Asset",
                description = "Test Description",
                volatilityLevel = VolatilityLevelEnum.LOW,
                prevalencePercentage = 10
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Asset class ID")
            .hasMessageContaining("must be positive")
    }
    
    @Test
    fun `constructor _ empty name _ throws IllegalArgumentException`() {
        // Act & Assert
        assertThatThrownBy {
            AssetClass(
                id = 1,
                name = "",
                description = "Test Description",
                volatilityLevel = VolatilityLevelEnum.LOW,
                prevalencePercentage = 10
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Asset class name cannot be blank")
    }
    
    @Test
    fun `constructor _ blank name _ throws IllegalArgumentException`() {
        // Act & Assert
        assertThatThrownBy {
            AssetClass(
                id = 1,
                name = "   ",
                description = "Test Description",
                volatilityLevel = VolatilityLevelEnum.LOW,
                prevalencePercentage = 10
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Asset class name cannot be blank")
    }
    
    @Test
    fun `constructor _ empty description _ throws IllegalArgumentException`() {
        // Act & Assert
        assertThatThrownBy {
            AssetClass(
                id = 1,
                name = "Test Asset",
                description = "",
                volatilityLevel = VolatilityLevelEnum.LOW,
                prevalencePercentage = 10
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Asset class description cannot be blank")
    }
    
    @Test
    fun `constructor _ blank description _ throws IllegalArgumentException`() {
        // Act & Assert
        assertThatThrownBy {
            AssetClass(
                id = 1,
                name = "Test Asset",
                description = "   ",
                volatilityLevel = VolatilityLevelEnum.LOW,
                prevalencePercentage = 10
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Asset class description cannot be blank")
    }
    
    
    @Test
    fun `constructor _ negative prevalence percentage _ throws IllegalArgumentException`() {
        // Act & Assert
        assertThatThrownBy {
            AssetClass(
                id = 1,
                name = "Test Asset",
                description = "Test Description",
                volatilityLevel = VolatilityLevelEnum.LOW,
                prevalencePercentage = -1
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Prevalence percentage")
            .hasMessageContaining("must be between 0 and 100")
    }
    
    @Test
    fun `constructor _ prevalence percentage over 100 _ throws IllegalArgumentException`() {
        // Act & Assert
        assertThatThrownBy {
            AssetClass(
                id = 1,
                name = "Test Asset",
                description = "Test Description",
                volatilityLevel = VolatilityLevelEnum.LOW,
                prevalencePercentage = 101
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Prevalence percentage")
            .hasMessageContaining("must be between 0 and 100")
    }
    
    @Test
    fun `constructor _ minimum valid prevalence percentage _ creates object correctly`() {
        // Act
        val assetClass = AssetClass(
            id = 1,
            name = "Test Asset",
            description = "Test Description",
            volatilityLevel = VolatilityLevelEnum.LOW,
            prevalencePercentage = 0
        )
        
        // Assert
        assertThat(assetClass.prevalencePercentage).isEqualTo(0)
    }
    
    @Test
    fun `constructor _ maximum valid prevalence percentage _ creates object correctly`() {
        // Act
        val assetClass = AssetClass(
            id = 1,
            name = "Test Asset",
            description = "Test Description",
            volatilityLevel = VolatilityLevelEnum.HIGH,
            prevalencePercentage = 100
        )
        
        // Assert
        assertThat(assetClass.prevalencePercentage).isEqualTo(100)
    }
}