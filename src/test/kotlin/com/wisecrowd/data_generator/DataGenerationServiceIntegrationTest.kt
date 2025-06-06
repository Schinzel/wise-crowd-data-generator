package com.wisecrowd.data_generator

import com.wisecrowd.data_generator.data_generators.TestStringDataGenerator
import com.wisecrowd.data_generator.data_saver.file_data_saver.FileDataSaver
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

/**
 * The purpose of this class is to test DataGenerationService integration 
 * with FileDataSaver using real file operations and the TestStringDataGenerator.
 *
 * Written by Claude Sonnet 4
 */
class DataGenerationServiceIntegrationTest {
    
    @TempDir
    private lateinit var tempDir: Path
    
    private lateinit var outputFile: File
    private lateinit var fileDataSaver: FileDataSaver
    private lateinit var testGenerator: TestStringDataGenerator
    private lateinit var service: DataGenerationService
    
    @BeforeEach
    fun setUp() {
        outputFile = tempDir.resolve("test_output.txt").toFile()
        fileDataSaver = FileDataSaver(outputFile.absolutePath)
        testGenerator = TestStringDataGenerator(3)
        service = DataGenerationService(testGenerator, fileDataSaver)
    }
    
    @AfterEach
    fun tearDown() {
        if (outputFile.exists()) {
            outputFile.delete()
        }
    }
    
    @Test
    fun `generateAndSave _ with file data saver _ creates correct file content`() {
        // When: generate and save data to file
        service.generateAndSave()
        
        // Then: file exists and contains expected content
        assertThat(outputFile.exists()).isTrue()
        assertThat(fileDataSaver.hasErrors()).isFalse()
        
        val content = outputFile.readText()
        val expectedContent = buildExpectedFileContent()
        assertThat(content).isEqualTo(expectedContent)
    }
    
    @Test
    fun `generateAndSave _ empty generator _ creates file with headers only`() {
        // Given: empty generator
        val emptyGenerator = TestStringDataGenerator(0)
        val emptyService = DataGenerationService(emptyGenerator, fileDataSaver)
        
        // When: generate and save
        emptyService.generateAndSave()
        
        // Then: file contains only headers
        assertThat(outputFile.exists()).isTrue()
        val content = outputFile.readText()
        val expectedContent = "item_name\titem_index\titem_value\n"
        assertThat(content).isEqualTo(expectedContent)
    }
    
    @Test
    fun `generateAndSave _ large dataset _ handles efficiently`() {
        // Given: large dataset generator
        val largeGenerator = TestStringDataGenerator(1000)
        val largeService = DataGenerationService(largeGenerator, fileDataSaver)
        
        // When: generate and save large dataset
        largeService.generateAndSave()
        
        // Then: file created successfully without errors
        assertThat(outputFile.exists()).isTrue()
        assertThat(fileDataSaver.hasErrors()).isFalse()
        
        // Verify file contains expected number of lines (header + 1000 rows)
        val lineCount = outputFile.readLines().size
        val expectedLines = 1001 // header + 1000 data rows
        assertThat(lineCount).isEqualTo(expectedLines)
    }
    
    private fun buildExpectedFileContent(): String {
        val lines = mutableListOf<String>()
        
        // Header line
        lines.add("item_name\titem_index\titem_value")
        
        // Data lines - TestStringDataGenerator creates 3 rows with new Double formatting (always 2 decimals)
        lines.add("###item_0###\t0\t0.00")
        lines.add("###item_1###\t1\t1.50") 
        lines.add("###item_2###\t2\t3.00")
        
        return lines.joinToString("\n") + "\n"
    }
}
