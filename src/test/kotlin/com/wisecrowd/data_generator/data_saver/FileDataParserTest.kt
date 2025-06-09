package com.wisecrowd.data_generator.data_saver

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileDataParserTest {
    @TempDir
    lateinit var tempDir: Path
    private lateinit var parser: FileDataParser

    // Constants from FileFormatConstants for better readability
    private val columnDelimiter = FileFormatConstants.COLUMN_DELIMITER
    private val rowDelimiter = FileFormatConstants.ROW_DELIMITER
    private val stringQualifier = FileFormatConstants.STRING_QUALIFIER

    @BeforeEach
    fun setUp() {
        parser = FileDataParser()
    }

    @Nested
    inner class FileValidation {
        @Test
        fun `file does not exist _ throws exception`() {
            // Arrange
            val nonExistentPath = tempDir.resolve("nonexistent.txt").toString()

            // Act & Assert
            assertThatThrownBy { parser.parseFile(nonExistentPath) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("File does not exist: $nonExistentPath")
        }

        @Test
        fun `path is directory not file _ throws exception`() {
            // Arrange
            val directoryPath = tempDir.toString()

            // Act & Assert
            assertThatThrownBy { parser.parseFile(directoryPath) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Path is not a file: $directoryPath")
        }
    }

    @Nested
    inner class EmptyFileHandling {
        @Test
        fun `empty file _ returns empty list`() {
            // Arrange
            val emptyFile = createTestFile("")

            // Act
            val result = parser.parseFile(emptyFile)

            // Assert
            assertThat(result).isEmpty()
        }

        @Test
        fun `file with only header row _ returns empty list`() {
            // Arrange
            val headerOnlyContent = "id${columnDelimiter}name${columnDelimiter}age$rowDelimiter"
            val headerOnlyFile = createTestFile(headerOnlyContent)

            // Act
            val result = parser.parseFile(headerOnlyFile)

            // Assert
            assertThat(result).isEmpty()
        }
    }

    @Nested
    inner class BasicParsing {
        @Test
        fun `simple data without string qualifiers _ parses correctly`() {
            // Arrange
            val content =
                buildString {
                    append("id${columnDelimiter}name${columnDelimiter}age$rowDelimiter")
                    append("1${columnDelimiter}John${columnDelimiter}25$rowDelimiter")
                    append("2${columnDelimiter}Jane${columnDelimiter}30$rowDelimiter")
                }
            val file = createTestFile(content)

            // Act
            val result = parser.parseFile(file)

            // Assert
            assertThat(result).hasSize(2)
            assertThat(result[0]).containsExactly("1", "John", "25")
            assertThat(result[1]).containsExactly("2", "Jane", "30")
        }

        @Test
        fun `data with string qualifiers _ removes qualifiers correctly`() {
            // Arrange
            val content =
                buildString {
                    append("id${columnDelimiter}name${columnDelimiter}description$rowDelimiter")
                    append("1${columnDelimiter}${stringQualifier}John Doe${stringQualifier}${columnDelimiter}${stringQualifier}Software Engineer${stringQualifier}$rowDelimiter")
                    append("2${columnDelimiter}${stringQualifier}Jane Smith${stringQualifier}${columnDelimiter}${stringQualifier}Product Manager${stringQualifier}$rowDelimiter")
                }
            val file = createTestFile(content)

            // Act
            val result = parser.parseFile(file)

            // Assert
            assertThat(result).hasSize(2)
            assertThat(result[0]).containsExactly("1", "John Doe", "Software Engineer")
            assertThat(result[1]).containsExactly("2", "Jane Smith", "Product Manager")
        }

        @Test
        fun `mixed data with and without qualifiers _ handles both correctly`() {
            // Arrange
            val content =
                buildString {
                    append("id${columnDelimiter}name${columnDelimiter}age$rowDelimiter")
                    append("1${columnDelimiter}${stringQualifier}John Doe${stringQualifier}${columnDelimiter}25$rowDelimiter")
                    append("2${columnDelimiter}Jane${columnDelimiter}30$rowDelimiter")
                }
            val file = createTestFile(content)

            // Act
            val result = parser.parseFile(file)

            // Assert
            assertThat(result).hasSize(2)
            assertThat(result[0]).containsExactly("1", "John Doe", "25")
            assertThat(result[1]).containsExactly("2", "Jane", "30")
        }
    }

    @Nested
    inner class EdgeCases {
        @Test
        fun `empty lines in middle of file _ skips empty lines`() {
            // Arrange
            val content =
                buildString {
                    append("id${columnDelimiter}name$rowDelimiter")
                    append("1${columnDelimiter}John$rowDelimiter")
                    append(rowDelimiter) // Empty line
                    append("2${columnDelimiter}Jane$rowDelimiter")
                }
            val file = createTestFile(content)

            // Act
            val result = parser.parseFile(file)

            // Assert
            assertThat(result).hasSize(2)
            assertThat(result[0]).containsExactly("1", "John")
            assertThat(result[1]).containsExactly("2", "Jane")
        }

        @Test
        fun `whitespace around lines _ trims correctly`() {
            // Arrange
            val content =
                buildString {
                    append("id${columnDelimiter}name$rowDelimiter")
                    append("  1${columnDelimiter}John  $rowDelimiter")
                    append("  2${columnDelimiter}Jane  $rowDelimiter")
                }
            val file = createTestFile(content)

            // Act
            val result = parser.parseFile(file)

            // Assert
            assertThat(result).hasSize(2)
            assertThat(result[0]).containsExactly("1", "John")
            assertThat(result[1]).containsExactly("2", "Jane")
        }

        @Test
        fun `single column data _ parses correctly`() {
            // Arrange
            val content =
                buildString {
                    append("name$rowDelimiter")
                    append("John$rowDelimiter")
                    append("Jane$rowDelimiter")
                }
            val file = createTestFile(content)

            // Act
            val result = parser.parseFile(file)

            // Assert
            assertThat(result).hasSize(2)
            assertThat(result[0]).containsExactly("John")
            assertThat(result[1]).containsExactly("Jane")
        }

        @Test
        fun `empty columns in middle of row _ preserves empty values`() {
            // Arrange - simulate how FileDataSaver actually writes data
            val data1 = listOf("1", "", "25")
            val data2 = listOf("2", "Jane", "30")
            val content =
                buildString {
                    append("id${columnDelimiter}name${columnDelimiter}age$rowDelimiter")
                    append(data1.joinToString(columnDelimiter) + rowDelimiter)
                    append(data2.joinToString(columnDelimiter) + rowDelimiter)
                }
            val file = createTestFile(content)

            // Act
            val result = parser.parseFile(file)

            // Assert
            assertThat(result).hasSize(2)
            assertThat(result[0]).containsExactly("1", "", "25")
            assertThat(result[1]).containsExactly("2", "Jane", "30")
        }
    }

    @Nested
    inner class StringQualifierHandling {
        @Test
        fun `partial string qualifiers _ treats as regular text`() {
            // Arrange
            val content =
                buildString {
                    append("text$rowDelimiter")
                    append("${stringQualifier}incomplete$rowDelimiter")
                    append("incomplete${stringQualifier}$rowDelimiter")
                }
            val file = createTestFile(content)

            // Act
            val result = parser.parseFile(file)

            // Assert
            assertThat(result).hasSize(2)
            assertThat(result[0]).containsExactly("${stringQualifier}incomplete")
            assertThat(result[1]).containsExactly("incomplete$stringQualifier")
        }

        @Test
        fun `string qualifiers at end of value only _ treats as regular text`() {
            // Arrange
            val content =
                buildString {
                    append("text$rowDelimiter")
                    append("value${stringQualifier}$rowDelimiter")
                }
            val file = createTestFile(content)

            // Act
            val result = parser.parseFile(file)

            // Assert
            assertThat(result).hasSize(1)
            assertThat(result[0]).containsExactly("value$stringQualifier")
        }

        @Test
        fun `empty string with qualifiers _ returns empty string`() {
            // Arrange
            val content =
                buildString {
                    append("text$rowDelimiter")
                    append("${stringQualifier}${stringQualifier}$rowDelimiter")
                }
            val file = createTestFile(content)

            // Act
            val result = parser.parseFile(file)

            // Assert
            assertThat(result).hasSize(1)
            assertThat(result[0]).containsExactly("")
        }
    }

    @Nested
    inner class PerformanceAndLargeFiles {
        @Test
        fun `large file with many rows _ handles efficiently`() {
            // Arrange
            val numberOfRows = 1_000
            val content =
                buildString {
                    append("id${columnDelimiter}name${columnDelimiter}value$rowDelimiter")
                    repeat(numberOfRows) { i ->
                        append("$i${columnDelimiter}${stringQualifier}Name$i${stringQualifier}${columnDelimiter}${i * 100}$rowDelimiter")
                    }
                }
            val file = createTestFile(content)

            // Act
            val startTime = System.currentTimeMillis()
            val result = parser.parseFile(file)
            val endTime = System.currentTimeMillis()

            // Assert
            assertThat(result).hasSize(numberOfRows)
            assertThat(result[0]).containsExactly("0", "Name0", "0")
            assertThat(result[numberOfRows - 1])
                .containsExactly("${numberOfRows - 1}", "Name${numberOfRows - 1}", "${(numberOfRows - 1) * 100}")

            // Performance assertion - should complete in reasonable time
            assertThat(endTime - startTime).isLessThan(1000) // Less than 1 second
        }
    }

    private fun createTestFile(content: String): String {
        val file = tempDir.resolve("test_${System.nanoTime()}.txt")
        Files.write(file, content.toByteArray())
        return file.toString()
    }
}
