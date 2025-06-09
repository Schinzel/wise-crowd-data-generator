package com.wisecrowd.data_generator.data_generators

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * The purpose of this class is to provide a comprehensive contract test
 * for IDataGenerator implementations to ensure consistent behavior.
 *
 * This abstract class tests the fundamental contracts that all IDataGenerator
 * implementations must satisfy, including iterator behavior, column schema
 * consistency, and error handling patterns.
 *
 * Written by Claude Sonnet 4
 */
abstract class IDataGeneratorContractTest {
    /**
     * Creates a generator instance with data for testing
     * Subclasses must provide a generator that has at least one row of data
     */
    protected abstract fun createGenerator(): IDataGenerator

    /**
     * Creates an empty generator instance for testing
     * Subclasses must provide a generator that has no rows of data
     */
    protected abstract fun createEmptyGenerator(): IDataGenerator

    @Nested
    inner class GetColumnNamesBehavior {
        @Test
        fun `getColumnNames _ any generator _ returns column names`() {
            val generator = createGenerator()

            val result = generator.getColumnNames()

            assertThat(result).isNotNull
            assertThat(result).isNotEmpty
        }

        @Test
        fun `getColumnNames _ called multiple times _ returns consistent result`() {
            val generator = createGenerator()

            val firstCall = generator.getColumnNames()
            val secondCall = generator.getColumnNames()

            assertThat(firstCall).isEqualTo(secondCall)
        }

        @Test
        fun `getColumnNames _ structure matches data rows _ consistent schema`() {
            val generator = createGenerator()

            val columnNames = generator.getColumnNames()
            val dataRow = generator.getNextRow()

            assertThat(dataRow).hasSize(columnNames.size)
        }

        @Test
        fun `getColumnNames _ all names not blank _ valid column names`() {
            val generator = createGenerator()

            val columnNames = generator.getColumnNames()

            columnNames.forEach { columnName ->
                assertThat(columnName).isNotBlank()
            }
        }

        @Test
        fun `getColumnNames _ no duplicate names _ unique column names`() {
            val generator = createGenerator()

            val columnNames = generator.getColumnNames()
            val uniqueNames = columnNames.toSet()

            assertThat(columnNames).hasSize(uniqueNames.size)
        }

        @Test
        fun `getColumnNames _ multiple generators _ consistent naming pattern`() {
            val generator1 = createGenerator()
            val generator2 = createGenerator()

            val columnNames1 = generator1.getColumnNames()
            val columnNames2 = generator2.getColumnNames()

            assertThat(columnNames1).isEqualTo(columnNames2)
        }
    }

    @Nested
    inner class HasMoreRowsBehavior {
        @Test
        fun `hasMoreRows _ generator with rows _ returns true`() {
            val generator = createGenerator()

            val result = generator.hasMoreRows()

            assertThat(result).isTrue
        }

        @Test
        fun `hasMoreRows _ empty generator _ returns false`() {
            val generator = createEmptyGenerator()

            val result = generator.hasMoreRows()

            assertThat(result).isFalse
        }

        @Test
        fun `hasMoreRows _ called multiple times without consuming _ consistent result`() {
            val generator = createGenerator()

            val firstCall = generator.hasMoreRows()
            val secondCall = generator.hasMoreRows()

            assertThat(firstCall).isEqualTo(secondCall)
        }
    }

    @Nested
    inner class GetNextRowBehavior {
        @Test
        fun `getNextRow _ generator with rows _ returns data`() {
            val generator = createGenerator()

            val result = generator.getNextRow()

            assertThat(result).isNotNull
            assertThat(result).isInstanceOf(List::class.java)
        }

        @Test
        fun `getNextRow _ empty generator _ throws NoSuchElementException`() {
            val generator = createEmptyGenerator()

            assertThatThrownBy { generator.getNextRow() }
                .isInstanceOf(NoSuchElementException::class.java)
        }

        @Test
        fun `getNextRow _ after consuming all rows _ throws NoSuchElementException`() {
            val generator = createEmptyGenerator()

            // Ensure hasMoreRows returns false first
            assertThat(generator.hasMoreRows()).isFalse

            assertThatThrownBy { generator.getNextRow() }
                .isInstanceOf(NoSuchElementException::class.java)
        }
    }

    @Nested
    inner class IteratorBehavior {
        @Test
        fun `iterator pattern _ consume all rows _ hasMoreRows becomes false`() {
            val generator = createGenerator()

            // Consume all available rows
            while (generator.hasMoreRows()) {
                generator.getNextRow()
            }

            assertThat(generator.hasMoreRows()).isFalse
        }

        @Test
        fun `iterator pattern _ row structure consistency _ all rows same size`() {
            val generator = createGenerator()
            val columnCount = generator.getColumnNames().size

            while (generator.hasMoreRows()) {
                val row = generator.getNextRow()
                assertThat(row).hasSize(columnCount)
            }
        }

        @Test
        fun `iterator pattern _ proper sequence _ getNextRow only when hasMoreRows true`() {
            val generator = createGenerator()

            while (generator.hasMoreRows()) {
                generator.getNextRow() // Should not throw
            }

            assertThat(generator.hasMoreRows()).isFalse
        }
    }
}
