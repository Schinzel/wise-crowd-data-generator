package com.wisecrowd.data_generator.data_saver

/**
 * Interface for saving generated data to various destinations with error handling
 */
interface IDataSaver {

    /**
     * Prepare the destination for saving data with column names
     * This allows implementations to set up headers and structure for data storage
     *
     * @param columnNames list of column names for the data
     */
    fun prepare(columnNames: List<String>)

    /**
     * Save a single data item as a list of values.
     *
     * IMPORTANT: This method should NOT throw exceptions. If an error occurs during saving,
     * the implementation should:
     * 1. Catch any exceptions internally
     * 2. Record the error information for later retrieval via getErrors()
     * 3. Continue processing subsequent items
     *
     * This ensures that a failure in one row does not prevent other rows from being saved.
     *
     * @param data the data values as Any type (String, Int, Double, Boolean, etc.)
     */
    fun saveItem(data: List<Any>)

    /**
     * Get all errors that occurred during the saving process
     * @return list of error details including row data, error messages, and exceptions
     */
    fun getErrors(): List<SaveError>

    /**
     * Check if any errors occurred during saving
     * @return true if errors occurred, false otherwise
     */
    fun hasErrors(): Boolean

    /**
     * Finish saving process (close file, commit transaction, etc.)
     * This should be called after all data has been saved.
     */
    fun complete()
}