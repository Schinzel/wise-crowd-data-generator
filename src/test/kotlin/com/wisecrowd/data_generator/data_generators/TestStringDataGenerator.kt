package com.wisecrowd.data_generator.data_generators

import com.wisecrowd.data_generator.data_saver.ColumnData
import com.wisecrowd.data_generator.data_saver.DataTypeEnum

/**
 * The purpose of this class is to provide a simple concrete implementation
 * of IDataGenerator for testing and demonstration purposes.
 *
 * This generator produces rows of mixed-type data, allowing us to test
 * the IDataGenerator contract with predictable behavior. Each row contains
 * a String, an Int, and a Double to demonstrate mixed data types.
 *
 * Written by Claude Sonnet 4
 */
class TestStringDataGenerator(
    private val rowCount: Int
) : IDataGenerator {
    
    private var currentIndex = 0
    
    override fun getColumnData(): List<ColumnData> {
        return listOf(
            ColumnData("item_name", DataTypeEnum.STRING),
            ColumnData("item_index", DataTypeEnum.INTEGER),
            ColumnData("item_value", DataTypeEnum.DECIMAL)
        )
    }
    
    override fun hasMoreRows(): Boolean {
        return currentIndex < rowCount
    }
    
    override fun getNextRow(): List<Any> {
        if (!hasMoreRows()) {
            throw NoSuchElementException("No more rows available in generator")
        }
        
        val index = currentIndex++
        return listOf(
            "item_$index",      // String
            index,              // Int  
            index * 1.5         // Double
        )
    }
}
