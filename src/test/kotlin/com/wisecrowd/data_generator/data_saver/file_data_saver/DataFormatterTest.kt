package com.wisecrowd.data_generator.data_saver.file_data_saver

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class DataFormatterTest {
    private lateinit var dataFormatter: DataFormatter

    @BeforeEach
    fun setUp() {
        dataFormatter = DataFormatter()
    }

    @Nested
    inner class SupportedDataTypes {
        @Test
        fun `UUID value _ formats as string without quotes`() {
            // Arrange
            val uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")

            // Act
            val result = dataFormatter.formatValue(uuid)

            // Assert
            assertThat(result).isEqualTo("123e4567-e89b-12d3-a456-426614174000")
        }

        @Test
        fun `Int value _ formats as string without quotes`() {
            // Arrange
            val intValue = 123

            // Act
            val result = dataFormatter.formatValue(intValue)

            // Assert
            assertThat(result).isEqualTo("123")
        }

        @Test
        fun `Double value _ formats with exactly two decimal places`() {
            // Arrange
            val wholeNumber = 100.0
            val oneDecimal = 123.4
            val roundedValue = 123.456

            // Act & Assert
            assertThat(dataFormatter.formatValue(wholeNumber)).isEqualTo("100.00")
            assertThat(dataFormatter.formatValue(oneDecimal)).isEqualTo("123.40")
            assertThat(dataFormatter.formatValue(roundedValue)).isEqualTo("123.46")
        }

        @Test
        fun `String value _ wrapped with qualifiers`() {
            // Arrange
            val textValue = "Sample Text"

            // Act
            val result = dataFormatter.formatValue(textValue)

            // Assert
            assertThat(result).isEqualTo("###Sample Text###")
        }

        @Test
        fun `LocalDate value _ formats as ISO date`() {
            // Arrange
            val date = LocalDate.of(2025, 5, 20)

            // Act
            val result = dataFormatter.formatValue(date)

            // Assert
            assertThat(result).isEqualTo("2025-05-20")
        }

        @Test
        fun `LocalDateTime value _ formats with seconds and UTC indicator`() {
            // Arrange
            val dateTimeWithSeconds = LocalDateTime.of(2025, 5, 20, 14, 30, 15)
            val dateTimeWithoutSeconds = LocalDateTime.of(2025, 5, 20, 14, 30, 0)

            // Act & Assert
            assertThat(dataFormatter.formatValue(dateTimeWithSeconds)).isEqualTo("2025-05-20T14:30:15Z")
            assertThat(dataFormatter.formatValue(dateTimeWithoutSeconds)).isEqualTo("2025-05-20T14:30:00Z")
        }
    }

    @Nested
    inner class UnsupportedDataTypes {
        @Test
        fun `BigDecimal value _ throws IllegalArgumentException`() {
            // Arrange
            val bigDecimal = java.math.BigDecimal("123.45")

            // Act & Assert
            assertThatThrownBy { dataFormatter.formatValue(bigDecimal) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Unsupported data type: BigDecimal")
                .hasMessageContaining("Supported types: UUID, Int, Double, String, LocalDate, LocalDateTime")
        }

        @Test
        fun `Float value _ throws IllegalArgumentException`() {
            // Arrange
            val floatValue = 123.45f

            // Act & Assert
            assertThatThrownBy { dataFormatter.formatValue(floatValue) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Unsupported data type: Float")
        }

        @Test
        fun `Boolean value _ throws IllegalArgumentException`() {
            // Arrange
            val booleanValue = true

            // Act & Assert
            assertThatThrownBy { dataFormatter.formatValue(booleanValue) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Unsupported data type: Boolean")
        }
    }
}
