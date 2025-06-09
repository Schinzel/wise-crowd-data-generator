package com.wisecrowd.data_generator.data_saver.file_data_saver

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class FileWriterTest {
    @TempDir
    lateinit var tempDir: Path
    private lateinit var testFilePath: String
    private lateinit var fileWriter: FileWriter

    @BeforeEach
    fun setUp() {
        testFilePath = tempDir.resolve("test_output.txt").toString()
        fileWriter = FileWriter(testFilePath)
    }

    @AfterEach
    fun tearDown() {
        if (fileWriter.isOpen()) {
            fileWriter.close()
        }
    }

    @Nested
    inner class DirectoryCreation {
        @Test
        fun `parent directories don't exist _ creates directories`() {
            // Arrange
            val nestedFilePath = tempDir.resolve("nested/deep/directories/test.txt").toString()
            val nestedFileWriter = FileWriter(nestedFilePath)

            // Act
            nestedFileWriter.createDirectoryStructure()

            // Assert
            val parentDir = File(nestedFilePath).parentFile
            assertThat(parentDir.exists()).isTrue()
        }

        @Test
        fun `parent directories exist _ doesn't throw exception`() {
            // Act & Assert
            fileWriter.createDirectoryStructure()
            // Should not throw exception
        }
    }

    @Nested
    inner class FileOperations {
        @Test
        fun `file not opened _ isOpen returns false`() {
            // Assert
            assertThat(fileWriter.isOpen()).isFalse()
        }

        @Test
        fun `file opened _ isOpen returns true`() {
            // Act
            fileWriter.open()

            // Assert
            assertThat(fileWriter.isOpen()).isTrue()
        }

        @Test
        fun `file closed _ isOpen returns false`() {
            // Arrange
            fileWriter.open()

            // Act
            fileWriter.close()

            // Assert
            assertThat(fileWriter.isOpen()).isFalse()
        }

        @Test
        fun `write content _ writes to file`() {
            // Arrange
            fileWriter.open()
            val content = "Hello World"

            // Act
            fileWriter.writeLine(content)
            fileWriter.close()

            // Assert
            val fileContent = Files.readString(Path.of(testFilePath))
            assertThat(fileContent).isEqualTo(content)
        }

        @Test
        fun `write multiple lines _ writes all content`() {
            // Arrange
            fileWriter.open()
            val line1 = "First line\n"
            val line2 = "Second line\n"

            // Act
            fileWriter.writeLine(line1)
            fileWriter.writeLine(line2)
            fileWriter.close()

            // Assert
            val fileContent = Files.readString(Path.of(testFilePath))
            assertThat(fileContent).isEqualTo(line1 + line2)
        }
    }

    @Nested
    inner class ErrorConditions {
        @Test
        fun `invalid file path _ throws exception on open`() {
            // Arrange
            val invalidFileWriter = FileWriter("/invalid/path/that/cannot/be/created/file.txt")

            // Act & Assert
            assertThatThrownBy { invalidFileWriter.open() }
                .isInstanceOf(Exception::class.java)
        }

        @Test
        fun `write without opening _ throws IllegalStateException`() {
            // Act & Assert
            assertThatThrownBy { fileWriter.writeLine("content") }
                .isInstanceOf(IllegalStateException::class.java)
                .hasMessageContaining("File writer must be opened before writing")
        }
    }
}
