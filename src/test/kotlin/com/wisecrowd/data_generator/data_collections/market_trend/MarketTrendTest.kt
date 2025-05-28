package com.wisecrowd.data_generator.data_collections.market_trend

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDate

class MarketTrendTest {

    @Test
    fun `valid market trend _ creates object correctly`() {
        // Arrange
        val startDateString = "2020-03-01"
        val endDateString = "2020-06-30"
        val trendType = "Bull"
        val strength = 1.5
        val description = "Test market trend"
        
        // Act
        val marketTrend = MarketTrend(
            startDateString = startDateString,
            endDateString = endDateString,
            trendType = trendType,
            strength = strength,
            description = description
        )
        
        // Assert
        assertThat(marketTrend.startDate).isEqualTo(LocalDate.of(2020, 3, 1))
        assertThat(marketTrend.endDate).isEqualTo(LocalDate.of(2020, 6, 30))
        assertThat(marketTrend.startDateString).isEqualTo(startDateString)
        assertThat(marketTrend.endDateString).isEqualTo(endDateString)
        assertThat(marketTrend.trendType).isEqualTo(trendType)
        assertThat(marketTrend.strength).isEqualTo(strength)
        assertThat(marketTrend.description).isEqualTo(description)
    }
    
    @Test
    fun `end date before start date _ throws IllegalArgumentException`() {
        // Arrange
        val startDateString = "2020-06-30"
        val endDateString = "2020-03-01" // Earlier than startDate
        
        // Act & Assert
        assertThatThrownBy {
            MarketTrend(
                startDateString = startDateString,
                endDateString = endDateString,
                trendType = "Bear",
                strength = -1.0,
                description = "Invalid date range"
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("End date")
            .hasMessageContaining("cannot be before start date")
    }
    
    @Test
    fun `strength outside valid range _ throws IllegalArgumentException`() {
        // Arrange
        val startDateString = "2020-03-01"
        val endDateString = "2020-06-30"
        val invalidStrength = 6.0 // Outside the valid range
        
        // Act & Assert
        assertThatThrownBy {
            MarketTrend(
                startDateString = startDateString,
                endDateString = endDateString,
                trendType = "Bull",
                strength = invalidStrength,
                description = "Invalid strength"
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Strength")
            .hasMessageContaining("must be between")
    }
    
    @Test
    fun `empty trend type _ throws IllegalArgumentException`() {
        // Arrange
        val startDateString = "2020-03-01"
        val endDateString = "2020-06-30"
        val emptyTrendType = ""
        
        // Act & Assert
        assertThatThrownBy {
            MarketTrend(
                startDateString = startDateString,
                endDateString = endDateString,
                trendType = emptyTrendType,
                strength = 1.0,
                description = "Invalid trend type"
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Trend type cannot be empty")
    }
    
    @Test
    fun `invalid start date format _ throws IllegalArgumentException`() {
        // Act & Assert
        assertThatThrownBy {
            MarketTrend(
                startDateString = "invalid-date",
                endDateString = "2020-06-30",
                trendType = "Bull",
                strength = 1.0,
                description = "Invalid start date format"
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Invalid start date format")
            .hasMessageContaining("Expected format: YYYY-MM-DD")
    }
    
    @Test
    fun `invalid end date format _ throws IllegalArgumentException`() {
        // Act & Assert
        assertThatThrownBy {
            MarketTrend(
                startDateString = "2020-03-01",
                endDateString = "30/06/2020",
                trendType = "Bull",
                strength = 1.0,
                description = "Invalid end date format"
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Invalid end date format")
            .hasMessageContaining("Expected format: YYYY-MM-DD")
    }
    
    @Test
    fun `empty start date string _ throws IllegalArgumentException`() {
        // Act & Assert
        assertThatThrownBy {
            MarketTrend(
                startDateString = "",
                endDateString = "2020-06-30",
                trendType = "Bull",
                strength = 1.0,
                description = "Empty start date"
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Invalid start date format")
    }
}