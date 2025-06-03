package com.wisecrowd.data_generator.data_generators

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * Tests for TestStringDataGenerator that verify it correctly implements
 * the IDataGenerator contract.
 */
class TestStringDataGeneratorTest : IDataGeneratorContractTest<String>() {
    
    override fun createGenerator(): IDataGenerator<String> {
        return TestStringDataGenerator(listOf("item1", "item2", "item3", "item4"))
    }
    
    override fun createEmptyGenerator(): IDataGenerator<String> {
        return TestStringDataGenerator(emptyList())
    }
    
    @Test
    fun `constructor _ valid items list _ creates generator correctly`() {
        val items = listOf("test1", "test2")
        
        val generator = TestStringDataGenerator(items)
        
        assertThat(generator.hasNext()).isTrue
    }
    
    @Test
    fun `getNext _ specific items _ returns items in order`() {
        val items = listOf("first", "second", "third")
        val generator = TestStringDataGenerator(items)
        
        val firstItem = generator.getNext()
        val secondItem = generator.getNext()
        val thirdItem = generator.getNext()
        
        assertThat(firstItem).isEqualTo("first")
        assertThat(secondItem).isEqualTo("second")
        assertThat(thirdItem).isEqualTo("third")
    }
}
