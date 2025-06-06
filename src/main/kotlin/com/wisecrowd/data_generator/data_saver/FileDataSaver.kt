package com.wisecrowd.data_generator.data_saver

import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * The purpose of this class is to save data to a text file in a delimited format
 * with proper handling of different data types and robust error management.
 *
 * SUPPORTED DATA TYPES:
 * - UUID: Formatted as standard UUID string without quotes (e.g., 123e4567-e89b-12d3-a456-426614174000)
 * - Int: Integer numbers formatted without quotes (e.g., 123)
 * - Double: Decimal numbers with exactly 2 decimal places, rounded, without quotes (e.g., 123.45)
 * - String: Text values always wrapped with qualifiers (e.g., ###Sample Text###)
 * - LocalDate: Date values formatted as YYYY-MM-DD without quotes (e.g., 2025-05-20)
 * - LocalDateTime: Date-time values formatted as YYYY-MM-DDTHH:mm:ssZ (UTC) without quotes (e.g., 2025-05-20T14:30:00Z)
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
        
        /** Formatter for Double values (always 2 decimal places) */
        private val DOUBLE_FORMATTER = DecimalFormat("0.00")
        
        /** Formatter for LocalDateTime values (ISO format with seconds and UTC indicator) */
        private val DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        
        /** Formatter for LocalDate values (ISO format) */
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
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
            val rowData = data.joinToString(COLUMN_DELIMITER) { value ->
                formatValue(value)
            }

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
     * Formats a value based on its data type according to the official specification.
     * 
     * SUPPORTED DATA TYPES & FORMATTING:
     * - UUID: Standard string representation without quotes (e.g., 123e4567-e89b-12d3-a456-426614174000)
     * - Int: As-is without quotes (e.g., 123)
     * - Double: Exactly 2 decimal places, rounded, without quotes (e.g., 123.45)
     * - String: Always wrapped with ### qualifiers (e.g., ###Sample Text###)
     * - LocalDate: ISO format YYYY-MM-DD without quotes (e.g., 2025-05-20)
     * - LocalDateTime: ISO format YYYY-MM-DDTHH:mm:ssZ without quotes (e.g., 2025-05-20T14:30:00Z)
     *
     * @param value the value of any supported type
     * @return formatted value as a string
     */
    private fun formatValue(value: Any): String {
        return when (value) {
            // UUID type - standard string representation
            is UUID -> value.toString()
            
            // Numeric types
            is Int -> value.toString()
            is Double -> DOUBLE_FORMATTER.format(value)
            
            // Date/Time types
            is LocalDate -> value.format(DATE_FORMATTER)
            is LocalDateTime -> value.format(DATETIME_FORMATTER)
            
            // String type - always wrapped with qualifiers
            is String -> "$STRING_QUALIFIER$value$STRING_QUALIFIER"
            
            // Unsupported types - throw exception to catch programming errors
            else -> throw IllegalArgumentException(
                "Unsupported data type: ${value::class.simpleName}. " +
                "Supported types: UUID, Int, Double, String, LocalDate, LocalDateTime"
            )
        }
    }
}