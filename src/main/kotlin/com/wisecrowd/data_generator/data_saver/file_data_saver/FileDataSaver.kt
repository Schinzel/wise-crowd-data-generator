package com.wisecrowd.data_generator.data_saver.file_data_saver

import com.wisecrowd.data_generator.data_saver.FileFormatConstants
import com.wisecrowd.data_generator.data_saver.IDataSaver

/**
 * The purpose of this class is to orchestrate data saving workflow
 * with validation and formatting.
 *
 * Written by Claude Sonnet 4
 */
class FileDataSaver(
    filePath: String,
    private val dataFormatter: DataFormatter = DataFormatter(),
    private val fileWriter: FileWriter = FileWriter(filePath),
) : IDataSaver {
    // Names of the columns being saved
    private var columnNames: List<String> = emptyList()

    init {
        // Ensure parent directories exist before any file operations
        fileWriter.createDirectoryStructure()
    }

    override fun prepare(columnNames: List<String>) {
        // Validate column names before proceeding
        require(columnNames.isNotEmpty()) {
            "Column names cannot be empty"
        }

        // Store column names for later validation
        this.columnNames = columnNames

        // Open file for writing
        fileWriter.open()

        // Write header row with column names
        val headerRow = buildHeaderRow()
        fileWriter.writeLine(headerRow)
    }

    override fun saveItem(data: List<Any>) {
        // Ensure prepare() was called first
        check(fileWriter.isOpen()) {
            "Cannot save item: prepare() must be called first"
        }

        // Validate data matches expected column count
        require(data.size == columnNames.size) {
            "Data size (${data.size}) does not match column count " +
                "(${columnNames.size})"
        }

        // Format and write data row
        val rowData = buildDataRow(data)
        fileWriter.writeLine(rowData)
    }

    override fun complete() {
        // Close file and release resources
        fileWriter.close()
    }

    private fun buildHeaderRow(): String {
        // Join column names with tab delimiter and add row delimiter
        return columnNames.joinToString(FileFormatConstants.COLUMN_DELIMITER) +
            FileFormatConstants.ROW_DELIMITER
    }

    private fun buildDataRow(data: List<Any>): String {
        // Format each value using dataFormatter
        val formattedData =
            data.joinToString(
                FileFormatConstants.COLUMN_DELIMITER,
            ) { value ->
                dataFormatter.formatValue(value)
            }
        // Add row delimiter to complete the line
        return formattedData + FileFormatConstants.ROW_DELIMITER
    }
}
