package com.wisecrowd.data_generator

import com.wisecrowd.data_generator.data_generators.IDataGenerator
import com.wisecrowd.data_generator.data_saver.IDataSaver
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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
        val rows =
            listOf(
                listOf("first", 1),
                listOf("second", 2),
                listOf("third", 3),
            )
        mockGenerator.setRows(rows)

        // When: generate and save
        service.generateAndSave()

        // Then: all rows saved in order with string conversion
        val expectedRows =
            listOf(
                listOf("first", "1"),
                listOf("second", "2"),
                listOf("third", "3"),
            )
        assertThat(mockSaver.savedItems).isEqualTo(expectedRows)
    }

    @Test
    fun `generateAndSave _ uses column data from generator for saver preparation`() {
        // Given: generator with specific column data
        mockGenerator.setRows(listOf(listOf("test")))

        // When: generate and save
        service.generateAndSave()

        // Then: saver prepared with generator's column names
        val expectedColumns = mockGenerator.getColumnNames()
        assertThat(mockSaver.preparedColumnNames).isEqualTo(expectedColumns)
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
            "prepare",
            "saveItem",
            "complete",
        )
    }

    @Test
    fun `generateAndSave _ saver errors collected _ continues processing other rows`() {
        // Given: generator with multiple rows and saver that throws on second row
        mockGenerator.setRows(
            listOf(
                listOf("good_row"),
                listOf("bad_row"),
                listOf("another_good_row"),
            ),
        )
        mockSaver.setThrowOnRow(1) // throw on second row (0-indexed)

        // When: generate and save
        service.generateAndSave()

        // Then: error collected and processing continued
        assertThat(service.hasErrors()).isTrue()
        assertThat(service.getErrors()).hasSize(1)
        assertThat(service.getErrors()[0].message).isEqualTo("Failed to save data row")

        // All rows were attempted to be saved
        assertThat(mockSaver.savedItems).hasSize(2) // 2 successful saves
    }

    @Test
    fun `generateAndSave _ no errors _ hasErrors returns false`() {
        // Given: generator with successful data
        mockGenerator.setRows(listOf(listOf("test")))

        // When: generate and save
        service.generateAndSave()

        // Then: no errors reported
        assertThat(service.hasErrors()).isFalse()
        assertThat(service.getErrors()).isEmpty()
    }

    @Test
    fun `getRowCount _ after successful generation _ returns correct count`() {
        // Given: generator with multiple rows
        mockGenerator.setRows(
            listOf(
                listOf("first", 1),
                listOf("second", 2),
                listOf("third", 3),
            ),
        )

        // When: generate and save
        service.generateAndSave()

        // Then: row count matches successful saves
        assertThat(service.getRowCount()).isEqualTo(3)
    }

    @Test
    fun `getRowCount _ with saver errors _ counts only successful saves`() {
        // Given: generator with multiple rows and saver that throws on second row
        mockGenerator.setRows(
            listOf(
                listOf("good_row"),
                listOf("bad_row"),
                listOf("another_good_row"),
            ),
        )
        mockSaver.setThrowOnRow(1) // throw on second row (0-indexed)

        // When: generate and save
        service.generateAndSave()

        // Then: row count reflects only successful saves
        assertThat(service.getRowCount()).isEqualTo(2)
    }

    @Test
    fun `execute _ successful generation _ logs steps with timing and row count`() {
        // Given: generator with test data and mock log
        val testGenerator = MockDataGenerator()
        val testSaver = MockDataSaver()
        val testLog = TestLog()
        testGenerator.setRows(listOf(listOf("test1"), listOf("test2")))

        // When: execute step
        DataGenerationService.execute(
            testGenerator,
            testSaver,
            testLog,
            2,
            "Generating test data...",
        )

        // Then: proper logging sequence with timing and row count
        val logMessages = testLog.getMessages()
        assertThat(logMessages).hasSize(2)
        assertThat(logMessages[0]).isEqualTo("Step 2 of 5: Generating test data...")
        assertThat(logMessages[1]).matches("Step 2 completed in \\d+ms - 2 rows generated")
    }

    @Test
    fun `execute _ generation with errors _ logs warnings after completion`() {
        // Given: generator and saver that produces errors, plus mock log
        val testGenerator = MockDataGenerator()
        val testSaver = MockDataSaver()
        val testLog = TestLog()
        testGenerator.setRows(listOf(listOf("good"), listOf("bad"), listOf("good")))
        testSaver.setThrowOnRow(1) // throw on second row

        // When: execute step
        DataGenerationService.execute(
            testGenerator,
            testSaver,
            testLog,
            3,
            "Generating test data...",
        )

        // Then: completion message includes warning count and individual warnings logged
        val logMessages = testLog.getMessages()
        assertThat(logMessages).hasSize(3)
        assertThat(logMessages[0]).isEqualTo("Step 3 of 5: Generating test data...")
        assertThat(logMessages[1]).matches("Step 3 completed in \\d+ms - 2 rows generated \\(1 warnings\\)")
        assertThat(logMessages[2]).isEqualTo("Warning: Failed to save data row")
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

        override fun getColumnNames(): List<String> = listOf("test_column", "number_column")

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
        var preparedColumnNames: List<String>? = null
        val savedItems = mutableListOf<List<String>>()
        var prepareCallCount = 0
        var completeCallCount = 0
        val methodCallOrder = mutableListOf<String>()
        private var throwOnRowIndex: Int? = null
        private var currentRowIndex = 0

        fun setThrowOnRow(rowIndex: Int) {
            this.throwOnRowIndex = rowIndex
        }

        override fun prepare(columnNames: List<String>) {
            preparedColumnNames = columnNames
            prepareCallCount++
            methodCallOrder.add("prepare")
        }

        override fun saveItem(data: List<Any>) {
            if (throwOnRowIndex == currentRowIndex) {
                currentRowIndex++
                methodCallOrder.add("saveItem")
                throw RuntimeException("Mock saver error for row $currentRowIndex")
            }
            savedItems.add(data.map { it.toString() })
            currentRowIndex++
            methodCallOrder.add("saveItem")
        }

        override fun complete() {
            completeCallCount++
            methodCallOrder.add("complete")
        }
    }
}
