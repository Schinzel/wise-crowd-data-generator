package com.wisecrowd.data_generator.data_generators

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * Tests for TestStringDataGenerator that verify it correctly implements
 * the IDataGenerator contract.
 */
class TestStringDataGeneratorTest : IDataGeneratorContractTest() {
    
    override fun createGenerator(): IDataGenerator {
        return TestStringDataGenerator(4)
    }
    
    override fun createEmptyGenerator(): IDataGenerator {
        return TestStringDataGenerator(0)
    }
    
    @Test
    fun `constructor _ valid row count _ creates generator correctly`() {
        val rowCount = 5
        
        val generator = TestStringDataGenerator(rowCount)
        
        assertThat(generator.hasMoreRows()).isTrue
    }
    
    @Test
    fun `getNextRow _ multiple calls _ returns rows with mixed data types`() {
        val generator = TestStringDataGenerator(3)
        
        val firstRow = generator.getNextRow()
        val secondRow = generator.getNextRow()
        val thirdRow = generator.getNextRow()
        
        assertThat(firstRow).containsExactly("item_0", 0, 0.0)
        assertThat(secondRow).containsExactly("item_1", 1, 1.5)
        assertThat(thirdRow).containsExactly("item_2", 2, 3.0)
    }
    
    @Test
    fun `getColumnData _ returns expected column structure`() {
        val generator = TestStringDataGenerator(1)
        
        val columnData = generator.getColumnData()
        
        assertThat(columnData).hasSize(3)
        assertThat(columnData[0].name).isEqualTo("item_name")
        assertThat(columnData[1].name).isEqualTo("item_index")
        assertThat(columnData[2].name).isEqualTo("item_value")
    }
    
    @Test
    fun `getNextRow _ data row structure _ returns expected format`() {
        val generator = TestStringDataGenerator(1)
        
        val row = generator.getNextRow()
        
        assertThat(row).hasSize(3)
        assertThat(row[0]).isEqualTo("item_0")
        assertThat(row[1]).isEqualTo(0)
        assertThat(row[2]).isEqualTo(0.0)
    }
}
