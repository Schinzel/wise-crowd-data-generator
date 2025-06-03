package com.wisecrowd.data_generator.data_generators

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Contract tests for IDataGenerator interface implementations.
 * 
 * Any class implementing IDataGenerator should extend this test class
 * and implement the createGenerator method to ensure contract compliance.
 */
abstract class IDataGeneratorContractTest {
    
    /**
     * Creates a generator instance for testing.
     * Should return a generator that produces at least 3 rows for comprehensive testing.
     */
    abstract fun createGenerator(): IDataGenerator
    
    /**
     * Creates an empty generator instance for testing edge cases.
     */
    abstract fun createEmptyGenerator(): IDataGenerator
    
    @Nested
    inner class GetColumnDataBehavior {
        
        @Test
        fun `getColumnData _ any generator _ returns column definitions`() {
            val generator = createGenerator()
            
            val result = generator.getColumnData()
            
            assertThat(result).isNotNull
            assertThat(result).isNotEmpty
        }
        
        @Test
        fun `getColumnData _ called multiple times _ returns consistent result`() {
            val generator = createGenerator()
            
            val firstCall = generator.getColumnData()
            val secondCall = generator.getColumnData()
            
            assertThat(firstCall).isEqualTo(secondCall)
        }
        
        @Test
        fun `getColumnData _ structure matches data rows _ consistent schema`() {
            val generator = createGenerator()
            
            val columnData = generator.getColumnData()
            val dataRow = generator.getNextRow()
            
            assertThat(dataRow).hasSize(columnData.size)
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
        fun `hasMoreRows _ called multiple times without consuming _ returns consistent result`() {
            val generator = createGenerator()
            
            val firstCall = generator.hasMoreRows()
            val secondCall = generator.hasMoreRows()
            val thirdCall = generator.hasMoreRows()
            
            assertThat(firstCall).isEqualTo(secondCall)
            assertThat(secondCall).isEqualTo(thirdCall)
        }
    }
    
    @Nested
    inner class GetNextRowBehavior {
        
        @Test
        fun `getNextRow _ generator with rows _ returns data row`() {
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
        fun `getNextRow _ called after hasMoreRows returns false _ throws NoSuchElementException`() {
            val generator = createEmptyGenerator()
            val hasMoreRows = generator.hasMoreRows()
            
            assertThat(hasMoreRows).isFalse
            assertThatThrownBy { generator.getNextRow() }
                .isInstanceOf(NoSuchElementException::class.java)
        }
    }
    
    @Nested
    inner class IteratorBehavior {
        
        @Test
        fun `hasMoreRows and getNextRow _ multiple rows _ works correctly`() {
            val generator = createGenerator()
            val rows = mutableListOf<List<Any>>()
            
            while (generator.hasMoreRows()) {
                rows.add(generator.getNextRow())
            }
            
            assertThat(rows).isNotEmpty
            assertThat(generator.hasMoreRows()).isFalse
        }
        
        @Test
        fun `hasMoreRows _ after consuming all rows _ returns false`() {
            val generator = createGenerator()
            
            // Consume all rows
            while (generator.hasMoreRows()) {
                generator.getNextRow()
            }
            
            val result = generator.hasMoreRows()
            
            assertThat(result).isFalse
        }
        
        @Test
        fun `getNextRow _ after consuming all rows _ throws NoSuchElementException`() {
            val generator = createGenerator()
            
            // Consume all rows
            while (generator.hasMoreRows()) {
                generator.getNextRow()
            }
            
            assertThatThrownBy { generator.getNextRow() }
                .isInstanceOf(NoSuchElementException::class.java)
        }
    }
}
