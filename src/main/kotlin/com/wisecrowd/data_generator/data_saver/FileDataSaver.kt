package com.wisecrowd.data_generator.data_saver

import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

/**
 * The purpose of this class is to save data to a text file in a delimited format
 * with proper handling of different data types and robust error management.
 *
 * Written by Claude 3.7
 */
class FileDataSaver(private val filePath: String) : IDataSaver {

    companion object {
        /** Delimiter used between rows */
        const val ROW_DELIMITER = "\n"
        
        /** Delimiter used between columns */
        const val COLUMN_DELIMITER = "\t"
        
        /** String qualifier used to wrap text data */
        const val STRING_QUALIFIER = "###"
    }

    // Writer used to output data to the file
    private var writer: BufferedWriter? = null
    // Collection of errors that occurred during operations
    private val errors = mutableListOf<SaveError>()
    // Metadata about the columns being saved
    private var columnData: List<ColumnData> = emptyList()

    init {
        try {
            // Get a File object from the provided path
            val file = File(filePath)
            // Extract the parent directory
            val parentDir = file.parentFile
            // Create all necessary directories if they don't exist
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs()
            }
        } catch (e: Exception) {
            // Record any errors that occur during directory creation
            errors.add(
                SaveError(
                    message = "Failed to create directory structure for file: $filePath",
                    exception = e
                )
            )
        }
    }

    override fun prepare(columnData: List<ColumnData>) {
        // Validate that column data is not empty
        if (columnData.isEmpty()) {
            // Add an error if no columns were provided
            errors.add(SaveError(message = "Column data cannot be empty"))
            return
        }

        // Store the column metadata for later use
        this.columnData = columnData
        
        try {
            // Create a UTF-8 buffered writer for the output file
            writer = BufferedWriter(
                OutputStreamWriter(
                    FileOutputStream(filePath),
                    StandardCharsets.UTF_8
                )
            )
            
            // Convert column names to a delimited string
            val headerRow = columnData.joinToString(COLUMN_DELIMITER) { it.name }
            // Write the header row to the file
            writer?.write(headerRow)
            // Add a row delimiter after the header
            writer?.write(ROW_DELIMITER)
        } catch (e: Exception) {
            // Record any errors that occur during file creation or header writing
            errors.add(
                SaveError(
                    message = "Failed to create file or write header: $filePath",
                    exception = e
                )
            )
        }
    }

    override fun saveItem(data: List<Any>) {
        // Check if prepare() was called before trying to save data
        if (writer == null) {
            // Add an error if the writer wasn't initialized
            errors.add(SaveError(
                message = "Cannot save item: prepare() must be called first",
                rowData = data
            ))
            return
        }

        // Validate that data size matches the number of columns
        if (data.size != columnData.size) {
            // Add an error if the data size is incorrect
            errors.add(SaveError(
                message = "Data size (${data.size}) does not match column count (${columnData.size})",
                rowData = data
            ))
            return
        }

        try {
            // Format each value based on its data type and join with the column delimiter
            val rowData = data.mapIndexed { i, value -> 
                formatValue(value, columnData[i].dataType)
            }.joinToString(COLUMN_DELIMITER)
            
            // Write the formatted row to the file
            writer?.write(rowData)
            // Add a row delimiter after the data
            writer?.write(ROW_DELIMITER)
        } catch (e: Exception) {
            // Record any errors that occur during row writing
            errors.add(SaveError(
                message = "Failed to write data row",
                rowData = data,
                exception = e
            ))
        }
    }

    override fun complete() {
        try {
            // Flush any buffered data to the file
            writer?.flush()
            // Close the writer to release resources
            writer?.close()
            // Reset the writer to prevent further writing
            writer = null
        } catch (e: Exception) {
            // Record any errors that occur during closing
            errors.add(SaveError(
                message = "Failed to close file: $filePath",
                exception = e
            ))
        }
    }

    // Return a copy of the errors list to prevent external modification
    override fun getErrors(): List<SaveError> = errors.toList()

    // Check if any errors occurred during operations
    override fun hasErrors(): Boolean = errors.isNotEmpty()

    /**
     * Formats a value based on its data type.
     * Strings are wrapped with qualifiers, other types are passed through.
     *
     * @param value the value of any type (String, Int, Double, Boolean, etc.)
     * @param dataType the type of data as defined in DataTypeEnum
     * @return formatted value as a string
     */
    private fun formatValue(value: Any, dataType: DataTypeEnum): String {
        // Apply different formatting based on the data type
        return when (dataType) {
            // Add qualifiers around string values to handle special characters
            DataTypeEnum.STRING -> "$STRING_QUALIFIER$value$STRING_QUALIFIER"
            // Other types don't need qualifiers
            else -> value.toString()
        }
    }
}