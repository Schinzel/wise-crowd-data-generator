package com.wisecrowd.data_generator.data_saver.file_data_saver

import com.wisecrowd.data_generator.data_saver.FileFormatConstants
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileDataSaverTest {

    @TempDir
    lateinit var tempDir: Path
    private lateinit var testFilePath: String
    private lateinit var fileDataSaver: FileDataSaver
    
    // Constants from FileFormatConstants for better readability
    private val columnDelimiter = FileFormatConstants.COLUMN_DELIMITER
    private val rowDelimiter = FileFormatConstants.ROW_DELIMITER
    private val stringQualifier = FileFormatConstants.STRING_QUALIFIER

    @BeforeEach
    fun setUp() {
        testFilePath = tempDir.resolve("test_output.txt").toString()
        fileDataSaver = FileDataSaver(testFilePath)
    }

    @AfterEach
    fun tearDown() {
        // Clean up any resources if needed
        fileDataSaver.complete()
    }

    @Nested
    inner class Initialization {
        @Test
        fun `parent directories don't exist _ creates directories`() {
            // Arrange
            val nestedFilePath = tempDir.resolve("nested/directories/test.txt").toString()
            
            // Act
            FileDataSaver(nestedFilePath)
            
            // Assert
            val parentDir = File(nestedFilePath).parentFile
            assertThat(parentDir.exists()).isTrue()
        }

        @Test
        fun `path is valid _ creates successfully`() {
            // Assert - no exception thrown during construction
            assertThat(fileDataSaver).isNotNull()
        }
    }

    @Nested
    inner class Prepare {
        @Test
        fun `valid columns provided _ creates file with header row`() {
            // Arrange
            val columns = listOf("id", "name", "age")
            
            // Act
            fileDataSaver.prepare(columns)
            fileDataSaver.complete()
            
            // Assert
            val fileContent = Files.readString(Path.of(testFilePath))
            assertThat(fileContent).isEqualTo("id${columnDelimiter}name${columnDelimiter}age${rowDelimiter}")
        }

        @Test
        fun `empty column data _ throws exception`() {
            // Arrange
            val emptyColumns = emptyList<String>()
            
            // Act & Assert
            assertThatThrownBy { fileDataSaver.prepare(emptyColumns) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Column names cannot be empty")
        }
    }

    @Nested
    inner class SupportedDataTypesFormatting {
        
        @BeforeEach
        fun prepareColumns() {
            val columns = listOf("uuid", "integer", "double", "string", "date", "datetime")
            fileDataSaver.prepare(columns)
        }

        @Test
        fun `officially supported data types _ formats correctly`() {
            // Arrange
            val testUuid = java.util.UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
            val testInt = 123
            val testDouble = 123.456 // Should round to 123.46
            val testString = "Sample Text"
            val testDate = java.time.LocalDate.of(2025, 5, 20)
            val testDateTime = java.time.LocalDateTime.of(2025, 5, 20, 14, 30, 0)
            
            val data = listOf(testUuid, testInt, testDouble, testString, testDate, testDateTime)
            
            // Act
            fileDataSaver.saveItem(data)
            fileDataSaver.complete()
            
            // Assert
            val fileContent = Files.readString(Path.of(testFilePath))
            val expectedContent = "uuid${columnDelimiter}integer${columnDelimiter}double${columnDelimiter}string${columnDelimiter}date${columnDelimiter}datetime${rowDelimiter}" +
                    "123e4567-e89b-12d3-a456-426614174000${columnDelimiter}123${columnDelimiter}123.46${columnDelimiter}${stringQualifier}Sample Text${stringQualifier}${columnDelimiter}2025-05-20${columnDelimiter}2025-05-20T14:30:00Z${rowDelimiter}"
            assertThat(fileContent).isEqualTo(expectedContent)
        }

        @Test
        fun `double formatting _ always two decimals`() {
            // Arrange
            val columns = listOf("whole", "decimal", "rounded")
            fileDataSaver.prepare(columns)
            
            val data = listOf(100.0, 123.4, 123.456)
            
            // Act
            fileDataSaver.saveItem(data)
            fileDataSaver.complete()
            
            // Assert
            val fileContent = Files.readString(Path.of(testFilePath))
            assertThat(fileContent).contains("100.00${columnDelimiter}123.40${columnDelimiter}123.46")
        }

        @Test
        fun `string formatting _ always wrapped`() {
            // Arrange
            val columns = listOf("text", "number_string", "date_string", "identifier")
            fileDataSaver.prepare(columns)
            
            val data = listOf("Regular Text", "123", "2025-05-20", "abc-123")
            
            // Act
            fileDataSaver.saveItem(data)
            fileDataSaver.complete()
            
            // Assert
            val fileContent = Files.readString(Path.of(testFilePath))
            val expectedContent = "text${columnDelimiter}number_string${columnDelimiter}date_string${columnDelimiter}identifier${rowDelimiter}" +
                    "${stringQualifier}Regular Text${stringQualifier}${columnDelimiter}${stringQualifier}123${stringQualifier}${columnDelimiter}${stringQualifier}2025-05-20${stringQualifier}${columnDelimiter}${stringQualifier}abc-123${stringQualifier}${rowDelimiter}"
            assertThat(fileContent).isEqualTo(expectedContent)
        }

        @Test
        fun `datetime formatting _ includes seconds and UTC indicator`() {
            // Arrange
            val columns = listOf("with_seconds", "without_seconds")
            fileDataSaver.prepare(columns)
            
            val dateTimeWithSeconds = java.time.LocalDateTime.of(2025, 5, 20, 14, 30, 15)
            val dateTimeWithoutSeconds = java.time.LocalDateTime.of(2025, 5, 20, 14, 30, 0)
            
            val data = listOf(dateTimeWithSeconds, dateTimeWithoutSeconds)
            
            // Act
            fileDataSaver.saveItem(data)
            fileDataSaver.complete()
            
            // Assert
            val fileContent = Files.readString(Path.of(testFilePath))
            assertThat(fileContent).contains("2025-05-20T14:30:15Z${columnDelimiter}2025-05-20T14:30:00Z")
        }

        @Test
        fun `unsupported data type _ throws exception with helpful message`() {
            // Arrange
            val columns = listOf("unsupported")
            fileDataSaver.prepare(columns)
            
            val unsupportedValue = listOf(java.math.BigDecimal("123.45")) // Unsupported type
            
            // Act & Assert
            assertThatThrownBy { fileDataSaver.saveItem(unsupportedValue) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Unsupported data type: BigDecimal")
                .hasMessageContaining("Supported types: UUID, Int, Double, String, LocalDate, LocalDateTime")
        }
    }

}
