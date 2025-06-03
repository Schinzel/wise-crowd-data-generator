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
abstract class IDataGeneratorContractTest<T> {
    
    /**
     * Creates a generator instance for testing.
     * Should return a generator that produces at least 3 items for comprehensive testing.
     */
    abstract fun createGenerator(): IDataGenerator<T>
    
    /**
     * Creates an empty generator instance for testing edge cases.
     */
    abstract fun createEmptyGenerator(): IDataGenerator<T>
    
    @Nested
    inner class HasNextBehavior {
        
        @Test
        fun `hasNext _ generator with items _ returns true`() {
            val generator = createGenerator()
            
            val result = generator.hasNext()
            
            assertThat(result).isTrue
        }
        
        @Test
        fun `hasNext _ empty generator _ returns false`() {
            val generator = createEmptyGenerator()
            
            val result = generator.hasNext()
            
            assertThat(result).isFalse
        }
        
        @Test
        fun `hasNext _ called multiple times without consuming _ returns consistent result`() {
            val generator = createGenerator()
            
            val firstCall = generator.hasNext()
            val secondCall = generator.hasNext()
            val thirdCall = generator.hasNext()
            
            assertThat(firstCall).isEqualTo(secondCall)
            assertThat(secondCall).isEqualTo(thirdCall)
        }
    }
    
    @Nested
    inner class GetNextBehavior {
        
        @Test
        fun `getNext _ generator with items _ returns item`() {
            val generator = createGenerator()
            
            val result = generator.getNext()
            
            assertThat(result).isNotNull
        }
        
        @Test
        fun `getNext _ empty generator _ throws NoSuchElementException`() {
            val generator = createEmptyGenerator()
            
            assertThatThrownBy { generator.getNext() }
                .isInstanceOf(NoSuchElementException::class.java)
        }
        
        @Test
        fun `getNext _ called after hasNext returns false _ throws NoSuchElementException`() {
            val generator = createEmptyGenerator()
            val hasNext = generator.hasNext()
            
            assertThat(hasNext).isFalse
            assertThatThrownBy { generator.getNext() }
                .isInstanceOf(NoSuchElementException::class.java)
        }
    }
    
    @Nested
    inner class IteratorBehavior {
        
        @Test
        fun `hasNext and getNext _ multiple items _ works correctly`() {
            val generator = createGenerator()
            val items = mutableListOf<T>()
            
            while (generator.hasNext()) {
                items.add(generator.getNext())
            }
            
            assertThat(items).isNotEmpty
            assertThat(generator.hasNext()).isFalse
        }
        
        @Test
        fun `hasNext _ after consuming all items _ returns false`() {
            val generator = createGenerator()
            
            // Consume all items
            while (generator.hasNext()) {
                generator.getNext()
            }
            
            val result = generator.hasNext()
            
            assertThat(result).isFalse
        }
        
        @Test
        fun `getNext _ after consuming all items _ throws NoSuchElementException`() {
            val generator = createGenerator()
            
            // Consume all items
            while (generator.hasNext()) {
                generator.getNext()
            }
            
            assertThatThrownBy { generator.getNext() }
                .isInstanceOf(NoSuchElementException::class.java)
        }
    }
}
