package com.wisecrowd.data_generator.data_saver.file_data_saver

import com.wisecrowd.data_generator.data_saver.FileFormatConstants
import com.wisecrowd.data_generator.data_saver.IDataSaver
import com.wisecrowd.data_generator.data_saver.SaveError

/**
 * The purpose of this class is to orchestrate data saving workflow
 * with validation, formatting, and error management.
 */
class FileDataSaver(
    filePath: String,
    private val dataFormatter: DataFormatter = DataFormatter(),
    private val fileWriter: FileWriter = FileWriter(filePath)
) : IDataSaver {


    // Collection of errors that occurred during operations
    private val errors = mutableListOf<SaveError>()
    // Names of the columns being saved
    private var columnNames: List<String> = emptyList()

    init {
        createDirectoryStructure()
    }

    override fun prepare(columnNames: List<String>) {
        validateColumnNames(columnNames)
        if (hasErrors()) return
        
        this.columnNames = columnNames
        openFile()
        writeHeaderRow()
    }

    override fun saveItem(data: List<Any>) {
        if (!validateWriterState(data)) return
        if (!validateDataSize(data)) return

        writeDataRow(data)
    }

    override fun complete() {
        closeFile()
    }

    override fun getErrors(): List<SaveError> = errors.toList()

    override fun hasErrors(): Boolean = errors.isNotEmpty()

    private fun createDirectoryStructure() {
        try {
            fileWriter.createDirectoryStructure()
        } catch (e: Exception) {
            errors.add(
                SaveError(
                    message = "Failed to create directory structure",
                    exception = e
                )
            )
        }
    }

    private fun validateColumnNames(columnNames: List<String>) {
        if (columnNames.isEmpty()) {
            errors.add(SaveError(message = "Column names cannot be empty"))
        }
    }

    private fun openFile() {
        try {
            fileWriter.open()
        } catch (e: Exception) {
            errors.add(
                SaveError(
                    message = "Failed to open file for writing",
                    exception = e
                )
            )
        }
    }

    private fun writeHeaderRow() {
        try {
            val headerRow = buildHeaderRow()
            fileWriter.writeLine(headerRow)
        } catch (e: Exception) {
            errors.add(
                SaveError(
                    message = "Failed to write header row",
                    exception = e
                )
            )
        }
    }

    private fun validateWriterState(data: List<Any>): Boolean {
        if (!fileWriter.isOpen()) {
            errors.add(SaveError(
                message = "Cannot save item: prepare() must be called first",
                rowData = data
            ))
            return false
        }
        return true
    }

    private fun validateDataSize(data: List<Any>): Boolean {
        if (data.size != columnNames.size) {
            errors.add(SaveError(
                message = "Data size (${data.size}) does not match column count (${columnNames.size})",
                rowData = data
            ))
            return false
        }
        return true
    }

    private fun writeDataRow(data: List<Any>) {
        try {
            val rowData = buildDataRow(data)
            fileWriter.writeLine(rowData)
        } catch (e: Exception) {
            errors.add(SaveError(
                message = "Failed to write data row",
                rowData = data,
                exception = e
            ))
        }
    }

    private fun closeFile() {
        try {
            fileWriter.close()
        } catch (e: Exception) {
            errors.add(SaveError(
                message = "Failed to close file",
                exception = e
            ))
        }
    }

    private fun buildHeaderRow(): String {
        return columnNames.joinToString(FileFormatConstants.COLUMN_DELIMITER) + FileFormatConstants.ROW_DELIMITER
    }

    private fun buildDataRow(data: List<Any>): String {
        val formattedData = data.joinToString(FileFormatConstants.COLUMN_DELIMITER) { value ->
            dataFormatter.formatValue(value)
        }
        return formattedData + FileFormatConstants.ROW_DELIMITER
    }
}
