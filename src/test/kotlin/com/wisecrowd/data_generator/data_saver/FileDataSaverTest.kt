package com.wisecrowd.data_generator.data_saver

import io.schinzel.basicutils.FunnyChars
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileDataSaverTest {

    @TempDir
    lateinit var tempDir: Path
    private lateinit var testFilePath: String
    private lateinit var fileDataSaver: FileDataSaver
    
    // Constants from FileDataSaver for better readability
    private val columnDelimiter = FileDataSaver.COLUMN_DELIMITER
    private val rowDelimiter = FileDataSaver.ROW_DELIMITER
    private val stringQualifier = FileDataSaver.STRING_QUALIFIER

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
        fun `path is valid _ has no errors`() {
            // Assert
            assertThat(fileDataSaver.hasErrors()).isFalse()
        }
    }

    @Nested
    inner class Prepare {
        @Test
        fun `valid columns provided _ creates file with header row`() {
            // Arrange
            val columns = listOf(
                ColumnData("id", DataTypeEnum.IDENTIFIER),
                ColumnData("name", DataTypeEnum.STRING),
                ColumnData("age", DataTypeEnum.INTEGER)
            )
            
            // Act
            fileDataSaver.prepare(columns)
            fileDataSaver.complete()
            
            // Assert
            val fileContent = Files.readString(Path.of(testFilePath))
            assertThat(fileContent).isEqualTo("id${columnDelimiter}name${columnDelimiter}age${rowDelimiter}")
        }

        @Test
        fun `empty column data _ adds error`() {
            // Arrange
            val emptyColumns = emptyList<ColumnData>()
            
            // Act
            fileDataSaver.prepare(emptyColumns)
            
            // Assert
            assertThat(fileDataSaver.hasErrors()).isTrue()
            assertThat(fileDataSaver.getErrors()).hasSize(1)
            assertThat(fileDataSaver.getErrors()[0].message).contains("Column data cannot be empty")
        }
    }

    @Nested
    inner class SaveItem {
        private val columns = listOf(
            ColumnData("id", DataTypeEnum.IDENTIFIER),
            ColumnData("name", DataTypeEnum.STRING),
            ColumnData("age", DataTypeEnum.INTEGER)
        )

        @BeforeEach
        fun prepareColumns() {
            fileDataSaver.prepare(columns)
        }

        @Test
        fun `valid data row _ writes with proper formatting`() {
            // Arrange
            val data = listOf("1", "John Doe", "30")
            
            // Act
            fileDataSaver.saveItem(data)
            fileDataSaver.complete()
            
            // Assert
            val fileContent = Files.readString(Path.of(testFilePath))
            val expectedContent = "id${columnDelimiter}name${columnDelimiter}age${rowDelimiter}" +
                    "1${columnDelimiter}${stringQualifier}John Doe${stringQualifier}${columnDelimiter}30${rowDelimiter}"
            assertThat(fileContent).isEqualTo(expectedContent)
        }

        @Test
        fun `data size doesn't match column count _ adds error`() {
            // Arrange
            val invalidData = listOf("1", "John Doe") // Missing age
            
            // Act
            fileDataSaver.saveItem(invalidData)
            
            // Assert
            assertThat(fileDataSaver.hasErrors()).isTrue()
            assertThat(fileDataSaver.getErrors()).hasSize(1)
            assertThat(fileDataSaver.getErrors()[0].message).contains("Data size")
            assertThat(fileDataSaver.getErrors()[0].rowData).isEqualTo(invalidData)
        }

        @Test
        fun `prepare was not called _ adds error`() {
            // Arrange
            val newSaver = FileDataSaver(testFilePath)
            val data = listOf("1", "John Doe", "30")
            
            // Act
            newSaver.saveItem(data)
            
            // Assert
            assertThat(newSaver.hasErrors()).isTrue()
            assertThat(newSaver.getErrors()).hasSize(1)
            assertThat(newSaver.getErrors()[0].message).contains("prepare() must be called")
        }
        
        @Test
        fun `multiple rows provided _ writes all rows correctly`() {
            // Arrange
            val rows = listOf(
                listOf("1", "John Doe", "30"),
                listOf("2", "Jane Smith", "25"),
                listOf("3", "Bob Johnson", "40")
            )
            
            // Act
            rows.forEach { fileDataSaver.saveItem(it) }
            fileDataSaver.complete()
            
            // Assert
            val fileContent = Files.readString(Path.of(testFilePath))
            val expectedContent = "id${columnDelimiter}name${columnDelimiter}age${rowDelimiter}" +
                    "1${columnDelimiter}${stringQualifier}John Doe${stringQualifier}${columnDelimiter}30${rowDelimiter}" +
                    "2${columnDelimiter}${stringQualifier}Jane Smith${stringQualifier}${columnDelimiter}25${rowDelimiter}" +
                    "3${columnDelimiter}${stringQualifier}Bob Johnson${stringQualifier}${columnDelimiter}40${rowDelimiter}"
            assertThat(fileContent).isEqualTo(expectedContent)
        }
    }

    @Nested
    inner class DataTypes {
        @Test
        fun `different data types _ formats each type correctly`() {
            // Arrange
            val columns = listOf(
                ColumnData("string", DataTypeEnum.STRING),
                ColumnData("integer", DataTypeEnum.INTEGER),
                ColumnData("decimal", DataTypeEnum.DECIMAL),
                ColumnData("date", DataTypeEnum.DATE),
                ColumnData("datetime", DataTypeEnum.DATETIME),
                ColumnData("boolean", DataTypeEnum.BOOLEAN),
                ColumnData("identifier", DataTypeEnum.IDENTIFIER)
            )
            
            val data = listOf(
                "Sample Text",
                "123",
                "123.45",
                "2025-05-20",
                "2025-05-20T14:30:00",
                "true",
                "abc-123"
            )
            
            // Act
            fileDataSaver.prepare(columns)
            fileDataSaver.saveItem(data)
            fileDataSaver.complete()
            
            // Assert
            val fileContent = Files.readString(Path.of(testFilePath))
            val expectedContent = "string${columnDelimiter}integer${columnDelimiter}decimal${columnDelimiter}" +
                    "date${columnDelimiter}datetime${columnDelimiter}boolean${columnDelimiter}identifier${rowDelimiter}" +
                    "${stringQualifier}Sample Text${stringQualifier}${columnDelimiter}123${columnDelimiter}123.45${columnDelimiter}" +
                    "2025-05-20${columnDelimiter}2025-05-20T14:30:00${columnDelimiter}true${columnDelimiter}abc-123${rowDelimiter}"
            assertThat(fileContent).isEqualTo(expectedContent)
        }
        
        @Test
        fun `special characters including Cyrillic and Polish _ handles them correctly`() {
            // Arrange
            val columns = listOf(
                ColumnData("special", DataTypeEnum.STRING)
            )
            
            // Create a string with special characters
            val specialChars = "ЯрусскийШЩЪЬ" + "ŁóźćĄ" + "ñ§µ€¥£"
            val data = listOf(specialChars)
            
            // Act
            fileDataSaver.prepare(columns)
            fileDataSaver.saveItem(data)
            fileDataSaver.complete()
            
            // Assert
            val fileContent = Files.readString(Path.of(testFilePath))
            val expectedRow = "${stringQualifier}$specialChars${stringQualifier}${rowDelimiter}"
            assertThat(fileContent).contains(expectedRow)
        }
        
        @Test
        fun `extremely long string _ handles it correctly`() {
            // Arrange
            val columns = listOf(
                ColumnData("longText", DataTypeEnum.STRING)
            )
            
            // Create a very long string (10,000 characters)
            val longString = "a".repeat(10_000)
            val data = listOf(longString)
            
            // Act
            fileDataSaver.prepare(columns)
            fileDataSaver.saveItem(data)
            fileDataSaver.complete()
            
            // Assert
            val fileContent = Files.readString(Path.of(testFilePath))
            assertThat(fileContent).contains("${stringQualifier}$longString${stringQualifier}")
        }
        
        @Test
        fun `empty string _ handles it correctly`() {
            // Arrange
            val columns = listOf(
                ColumnData("emptyText", DataTypeEnum.STRING)
            )
            
            val data = listOf("")
            
            // Act
            fileDataSaver.prepare(columns)
            fileDataSaver.saveItem(data)
            fileDataSaver.complete()
            
            // Assert
            val fileContent = Files.readString(Path.of(testFilePath))
            assertThat(fileContent).contains("${stringQualifier}${stringQualifier}")
        }
    }

    @Nested
    inner class ErrorHandling {
        @Test
        fun `no errors occurred _ returns empty list`() {
            // Assert
            assertThat(fileDataSaver.getErrors()).isEmpty()
        }

        @Test
        fun `no errors occurred _ hasErrors returns false`() {
            // Assert
            assertThat(fileDataSaver.hasErrors()).isFalse()
        }
        
        @Test
        fun `errors occurred _ hasErrors returns true`() {
            // Arrange - create an error condition by preparing with empty columns
            fileDataSaver.prepare(emptyList())
            
            // Assert
            assertThat(fileDataSaver.hasErrors()).isTrue()
        }
    }
}