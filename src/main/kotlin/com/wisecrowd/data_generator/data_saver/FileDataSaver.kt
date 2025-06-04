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
    // Names of the columns being saved
    private var columnNames: List<String> = emptyList()

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

    override fun prepare(columnNames: List<String>) {
        // Validate that column names are not empty
        if (columnNames.isEmpty()) {
            // Add an error if no columns were provided
            errors.add(SaveError(message = "Column names cannot be empty"))
            return
        }

        // Store the column names for later use
        this.columnNames = columnNames
        
        try {
            // Create a UTF-8 buffered writer for the output file
            writer = BufferedWriter(
                OutputStreamWriter(
                    FileOutputStream(filePath),
                    StandardCharsets.UTF_8
                )
            )
            
            // Convert column names to a delimited string
            val headerRow = columnNames.joinToString(COLUMN_DELIMITER)
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
        if (data.size != columnNames.size) {
            // Add an error if the data size is incorrect
            errors.add(SaveError(
                message = "Data size (${data.size}) does not match column count (${columnNames.size})",
                rowData = data
            ))
            return
        }

        try {
            // Format each value and join with the column delimiter (all converted to strings)
            val rowData = data.map { value -> 
                formatValue(value)
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
     * Numeric strings are treated as numbers and not wrapped.
     *
     * @param value the value of any type
     * @return formatted value as a string
     */
    private fun formatValue(value: Any): String {
        return when (value) {
            is String -> {
                // Check if the string should not be wrapped (numbers, dates, identifiers)
                if (shouldNotWrapString(value)) {
                    value
                } else {
                    // Wrap strings that contain spaces or special characters
                    "$STRING_QUALIFIER$value$STRING_QUALIFIER"
                }
            }
            // Other types don't need qualifiers
            else -> value.toString()
        }
    }
    
    /**
     * Checks if a string represents a value that should not be wrapped
     * (numbers, dates, identifiers, etc.)
     */
    private fun shouldNotWrapString(value: String): Boolean {
        if (value.isBlank()) return false
        
        // Don't process very long strings as identifiers
        if (value.length > 50) return false
        
        // Check if it's a number
        if (isNumeric(value)) return true
        
        // Check if it's a date (YYYY-MM-DD format)
        if (value.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) return true
        
        // Check if it's a datetime (ISO format)
        if (value.matches(Regex("""\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}"""))) return true
        
        // Check if it's a simple identifier pattern (must contain alphanumeric and dashes, but not just letters)
        if (value.matches(Regex("""[a-zA-Z0-9\-]+""")) && 
            (value.contains('-') || value.any { it.isDigit() })) {
            return true
        }
        
        return false
    }
    
    /**
     * Checks if a string represents a numeric value
     */
    private fun isNumeric(value: String): Boolean {
        if (value.isBlank()) return false
        return try {
            value.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }
}