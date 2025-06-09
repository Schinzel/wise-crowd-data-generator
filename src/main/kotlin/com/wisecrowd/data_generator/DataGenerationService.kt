package com.wisecrowd.data_generator

import com.wisecrowd.data_generator.data_generators.IDataGenerator
import com.wisecrowd.data_generator.data_saver.IDataSaver
import com.wisecrowd.data_generator.data_saver.SaveError

/**
 * The purpose of this class is to orchestrate the complete data generation
 * and saving workflow by combining IDataGenerator and IDataSaver components.
 *
 * This service follows the pull-based iterator pattern where it pulls data from
 * the generator and pushes it to the saver. It collects and manages all errors
 * from both generator and saver components, providing centralized error reporting.
 *
 * Small errors are collected and can be retrieved after completion. Fatal errors
 * that prevent continued operation will be thrown immediately.
 *
 * Written by Claude Sonnet 4
 */
class DataGenerationService(
    private val generator: IDataGenerator,
    private val saver: IDataSaver,
) {
    // Collection of errors that occurred during operations
    private val errors = mutableListOf<SaveError>()

    /**
     * Executes the complete generate-and-save workflow with centralized error handling
     *
     * Orchestrates the data generation process by preparing the saver, processing
     * all data rows, and completing the save operation. Generator exceptions
     * propagate to caller while saver errors are collected.
     *
     * @throws RuntimeException if the generator encounters errors during iteration
     * @throws IllegalStateException if the generator is in an invalid state
     */
    fun generateAndSave() {
        // Get column names and prepare saver
        val columnNames = generator.getColumnNames()
        prepareSaver(columnNames)

        // Process all data rows from generator
        processAllDataRows()

        // Finalize the saving process
        completeSaving()
    }

    private fun prepareSaver(columnNames: List<String>) {
        // Prepare saver with column names, fatal errors are rethrown
        try {
            saver.prepare(columnNames)
        } catch (e: Exception) {
            recordError("Failed to prepare saver", exception = e)
            throw e
        }
    }

    private fun processAllDataRows() {
        // Iterate through all data rows, collecting individual save errors
        while (generator.hasMoreRows()) {
            val dataRow = generator.getNextRow()
            saveDataRowSafely(dataRow)
        }
    }

    private fun saveDataRowSafely(dataRow: List<Any>) {
        // Save individual row, continue processing on error
        try {
            saver.saveItem(dataRow)
        } catch (e: Exception) {
            recordError("Failed to save data row", dataRow, e)
        }
    }

    private fun completeSaving() {
        // Complete saving process, fatal errors are rethrown
        try {
            saver.complete()
        } catch (e: Exception) {
            recordError("Failed to complete saving process", exception = e)
            throw e
        }
    }

    private fun recordError(
        message: String,
        rowData: List<Any>? = null,
        exception: Exception? = null,
    ) {
        // Add error to collection for later retrieval
        errors.add(
            SaveError(
                message = message,
                rowData = rowData,
                exception = exception,
            ),
        )
    }

    /**
     * Get all errors that occurred during the data generation and saving process
     * @return list of error details including row data, error messages, and exceptions
     */
    fun getErrors(): List<SaveError> = errors.toList()

    /**
     * Check if any errors occurred during data generation and saving
     * @return true if errors occurred, false otherwise
     */
    fun hasErrors(): Boolean = errors.isNotEmpty()
}
