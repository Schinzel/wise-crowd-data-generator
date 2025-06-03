package com.wisecrowd.data_generator

import com.wisecrowd.data_generator.data_generators.IDataGenerator
import com.wisecrowd.data_generator.data_saver.ColumnData
import com.wisecrowd.data_generator.data_saver.DataTypeEnum
import com.wisecrowd.data_generator.data_saver.IDataSaver
import com.wisecrowd.data_generator.data_saver.SaveError
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested

/**
 * The purpose of this class is to test the DataGenerationService orchestration
 * between IDataGenerator and IDataSaver components using mock implementations.
 *
 * Written by Claude Sonnet 4
 */
class DataGenerationServiceTest {
    
    private lateinit var mockGenerator: MockDataGenerator
    private lateinit var mockSaver: MockDataSaver
    private lateinit var service: DataGenerationService
    
    @BeforeEach
    fun setUp() {
        mockGenerator = MockDataGenerator()
        mockSaver = MockDataSaver()
        service = DataGenerationService(mockGenerator, mockSaver)
    }
    
    @Test
    fun `generateAndSave _ empty generator _ calls prepare and complete without rows`() {
        // Given: empty generator
        mockGenerator.setRows(emptyList())
        
        // When: generate and save
        service.generateAndSave()
        
        // Then: prepare and complete called, no items saved
        assertThat(mockSaver.prepareCallCount).isEqualTo(1)
        assertThat(mockSaver.savedItems).isEmpty()
        assertThat(mockSaver.completeCallCount).isEqualTo(1)
    }
    
    @Test
    fun `generateAndSave _ single row generator _ saves converted row correctly`() {
        // Given: generator with single mixed-type row
        val testRow = listOf("test", 42, 3.14, true)
        mockGenerator.setRows(listOf(testRow))
        
        // When: generate and save
        service.generateAndSave()
        
        // Then: row converted to strings and saved
        val expectedSavedRow = listOf("test", "42", "3.14", "true")
        assertThat(mockSaver.savedItems).hasSize(1)
        assertThat(mockSaver.savedItems[0]).isEqualTo(expectedSavedRow)
    }
    
    @Test
    fun `generateAndSave _ multiple rows _ saves all rows in correct order`() {
        // Given: generator with multiple rows
        val rows = listOf(
            listOf("first", 1),
            listOf("second", 2), 
            listOf("third", 3)
        )
        mockGenerator.setRows(rows)
        
        // When: generate and save
        service.generateAndSave()
        
        // Then: all rows saved in order with string conversion
        val expectedRows = listOf(
            listOf("first", "1"),
            listOf("second", "2"),
            listOf("third", "3")
        )
        assertThat(mockSaver.savedItems).isEqualTo(expectedRows)
    }
    
    @Test
    fun `generateAndSave _ uses column data from generator for saver preparation`() {
        // Given: generator with specific column data
        mockGenerator.setRows(listOf(listOf("test")))
        
        // When: generate and save
        service.generateAndSave()
        
        // Then: saver prepared with generator's column data
        val expectedColumns = mockGenerator.getColumnData()
        assertThat(mockSaver.preparedColumnData).isEqualTo(expectedColumns)
    }
    
    @Test
    fun `generateAndSave _ generator exception _ propagates to caller`() {
        // Given: generator that throws exception during iteration
        mockGenerator.setThrowException(true)
        mockGenerator.setRows(listOf(listOf("test")))
        
        // When & Then: exception propagated
        assertThatThrownBy { service.generateAndSave() }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("Mock generator error")
    }
    
    @Test
    fun `generateAndSave _ follows correct workflow sequence`() {
        // Given: generator with one row
        mockGenerator.setRows(listOf(listOf("test")))
        
        // When: generate and save
        service.generateAndSave()
        
        // Then: correct sequence maintained
        assertThat(mockSaver.methodCallOrder).containsExactly(
            "prepare", "saveItem", "complete"
        )
    }
    
    /**
     * Mock implementation of IDataGenerator for testing
     */
    private class MockDataGenerator : IDataGenerator {
        private var rows: List<List<Any>> = emptyList()
        private var currentIndex = 0
        private var shouldThrowException = false
        
        fun setRows(rows: List<List<Any>>) {
            this.rows = rows
            this.currentIndex = 0
        }
        
        fun setThrowException(shouldThrow: Boolean) {
            this.shouldThrowException = shouldThrow
        }
        
        override fun getColumnData(): List<ColumnData> {
            return listOf(
                ColumnData("test_column", DataTypeEnum.STRING),
                ColumnData("number_column", DataTypeEnum.INTEGER)
            )
        }
        
        override fun hasMoreRows(): Boolean {
            if (shouldThrowException && currentIndex > 0) {
                throw RuntimeException("Mock generator error")
            }
            return currentIndex < rows.size
        }
        
        override fun getNextRow(): List<Any> {
            if (!hasMoreRows()) {
                throw NoSuchElementException("No more rows")
            }
            return rows[currentIndex++]
        }
    }
    
    /**
     * Mock implementation of IDataSaver for testing
     */
    private class MockDataSaver : IDataSaver {
        var preparedColumnData: List<ColumnData>? = null
        val savedItems = mutableListOf<List<String>>()
        var prepareCallCount = 0
        var completeCallCount = 0
        val methodCallOrder = mutableListOf<String>()
        
        override fun prepare(columnData: List<ColumnData>) {
            preparedColumnData = columnData
            prepareCallCount++
            methodCallOrder.add("prepare")
        }
        
        override fun saveItem(data: List<String>) {
            savedItems.add(data)
            methodCallOrder.add("saveItem")
        }
        
        override fun getErrors(): List<SaveError> = emptyList()
        
        override fun hasErrors(): Boolean = false
        
        override fun complete() {
            completeCallCount++
            methodCallOrder.add("complete")
        }
    }
}
