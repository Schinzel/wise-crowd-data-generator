package com.wisecrowd.data_generator

import com.wisecrowd.data_generator.data_generators.IDataGenerator
import com.wisecrowd.data_generator.data_saver.IDataSaver

/**
 * The purpose of this class is to orchestrate the complete data generation 
 * and saving workflow by combining IDataGenerator and IDataSaver components.
 *
 * This service follows the pull-based iterator pattern where it pulls data from 
 * the generator and pushes it to the saver. It handles the conversion between 
 * the generator's mixed-type output (List<Any>) and the saver's string-based 
 * input (List<String>) using toString() conversion.
 *
 * The service is self-contained and handles all errors appropriately, delegating
 * error handling to the individual components while allowing generator exceptions
 * to propagate for proper error visibility.
 *
 * Written by Claude Sonnet 4
 */
class DataGenerationService(
    private val generator: IDataGenerator,
    private val saver: IDataSaver
) {
    
    /**
     * Executes the complete generate-and-save workflow
     * 
     * This method orchestrates the entire process:
     * 1. Obtains column schema from the generator (self-describing)
     * 2. Prepares the saver with the column metadata
     * 3. Iterates through all available data rows from the generator
     * 4. Converts each row from List<Any> to List<String> using toString()
     * 5. Saves each converted row using the saver
     * 6. Completes the saving process
     * 
     * Generator exceptions (from hasMoreRows() or getNextRow()) will propagate
     * to the caller for proper error handling and visibility. Saver errors are
     * handled internally by the saver and can be retrieved via saver.getErrors().
     * 
     * @throws RuntimeException if the generator encounters errors during iteration
     * @throws IllegalStateException if the generator is in an invalid state
     */
    fun generateAndSave() {
        // Get self-describing column metadata from generator
        val columnData = generator.getColumnData()
        
        // Prepare the saver with column structure
        saver.prepare(columnData)
        
        // Iterate through all available data using pull-based pattern
        while (generator.hasMoreRows()) {
            // Get next row as mixed types from generator
            val dataRow = generator.getNextRow()
            
            // Convert to strings for saver (toString() conversion)
            val stringData = dataRow.map { it.toString() }
            
            // Save converted data (saver handles errors internally)
            saver.saveItem(stringData)
        }
        
        // Complete the saving process
        saver.complete()
    }
}
