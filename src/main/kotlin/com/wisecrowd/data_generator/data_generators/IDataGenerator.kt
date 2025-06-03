package com.wisecrowd.data_generator.data_generators

import com.wisecrowd.data_generator.data_saver.ColumnData

/**
 * The purpose of this interface is to define the contract for generating data
 * using an iterator pattern that allows consumers to control the flow of data generation.
 *
 * This interface provides a pull-based approach where the consumer calls hasMoreRows()
 * to check availability and getNextRow() to retrieve the next generated data row.
 * Each row is returned as List<Any> to support mixed data types (String, Double, Int, etc.)
 * while remaining flexible for different output formats (files, databases, JSON, etc.).
 *
 * The generator is self-describing through getColumnData(), which provides the column
 * structure and data types that match the rows returned by getNextRow().
 *
 * Written by Claude Sonnet 4
 */
interface IDataGenerator {
    
    /**
     * Retrieves the column structure and metadata for this generator
     * 
     * This method defines the schema of the data rows that will be generated.
     * The returned column data should match the structure of rows returned by getNextRow().
     * This allows the generator to be self-describing and ensures consistency between
     * column definitions and actual data.
     * 
     * @return List of ColumnData describing the structure, names, and types of generated rows
     */
    fun getColumnData(): List<ColumnData>
    
    /**
     * Checks if there are more data rows available to generate
     * 
     * This method should be safe to call multiple times without side effects.
     * It should return true if getNextRow() will successfully return a data row,
     * and false if no more rows are available for generation.
     * 
     * @return true if more rows are available, false otherwise
     */
    fun hasMoreRows(): Boolean
    
    /**
     * Retrieves the next generated data row as a list of mixed-type values
     * 
     * This method should only be called after hasMoreRows() returns true.
     * Calling getNextRow() when hasMoreRows() returns false should result in
     * a NoSuchElementException being thrown.
     * 
     * The returned list contains values of different types (String, Double, Int, Boolean, etc.)
     * that represent one row of generated data. The structure and types should match the
     * column definitions returned by getColumnData(). The implementation may perform the actual 
     * data generation lazily when this method is called, allowing for efficient memory usage.
     * 
     * @return the next generated data row as List<Any> containing mixed-type values
     * @throws NoSuchElementException if no more rows are available
     * @throws IllegalStateException if the generator is in an invalid state
     */
    fun getNextRow(): List<Any>
}
