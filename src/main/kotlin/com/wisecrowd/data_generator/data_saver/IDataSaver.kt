package com.wisecrowd.data_generator.data_saver

/**
 * Interface for saving generated data to various destinations
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
     * @param data the data values as Any type (String, Int, Double, Boolean, etc.)
     */
    fun saveItem(data: List<Any>)

    /**
     * Finish saving process (close file, commit transaction, etc.)
     * This should be called after all data has been saved.
     */
    fun complete()
}
