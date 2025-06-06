package com.wisecrowd.data_generator.data_saver.file_data_saver

import com.wisecrowd.data_generator.data_saver.IDataSaver
import com.wisecrowd.data_generator.data_saver.SaveError
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

/**
 * The purpose of this class is to save data to a text file in a delimited format
 * with proper file handling and robust error management.
 */
class FileDataSaver(
    private val filePath: String,
    private val dataFormatter: DataFormatter = DataFormatter()
) : IDataSaver {

    companion object {
        /** Delimiter used between rows */
        const val ROW_DELIMITER = "\n"
        
        /** Delimiter used between columns */
        const val COLUMN_DELIMITER = "\t"
    }

    // Writer used to output data to the file
    private var writer: BufferedWriter? = null
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
        initializeWriter()
        writeHeader()
    }

    override fun saveItem(data: List<Any>) {
        if (!isWriterReady(data)) return
        if (!isDataValid(data)) return

        writeDataRow(data)
    }

    override fun complete() {
        closeWriter()
    }

    override fun getErrors(): List<SaveError> = errors.toList()

    override fun hasErrors(): Boolean = errors.isNotEmpty()

    private fun createDirectoryStructure() {
        try {
            val file = File(filePath)
            val parentDir = file.parentFile
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs()
            }
        } catch (e: Exception) {
            errors.add(
                SaveError(
                    message = "Failed to create directory structure for file: $filePath",
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

    private fun initializeWriter() {
        try {
            writer = BufferedWriter(
                OutputStreamWriter(
                    FileOutputStream(filePath),
                    StandardCharsets.UTF_8
                )
            )
        } catch (e: Exception) {
            errors.add(
                SaveError(
                    message = "Failed to create file: $filePath",
                    exception = e
                )
            )
        }
    }

    private fun writeHeader() {
        try {
            val headerRow = columnNames.joinToString(COLUMN_DELIMITER)
            writer?.write(headerRow)
            writer?.write(ROW_DELIMITER)
        } catch (e: Exception) {
            errors.add(
                SaveError(
                    message = "Failed to write header to file: $filePath",
                    exception = e
                )
            )
        }
    }

    private fun isWriterReady(data: List<Any>): Boolean {
        if (writer == null) {
            errors.add(SaveError(
                message = "Cannot save item: prepare() must be called first",
                rowData = data
            ))
            return false
        }
        return true
    }

    private fun isDataValid(data: List<Any>): Boolean {
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
            val rowData = data.joinToString(COLUMN_DELIMITER) { value ->
                dataFormatter.formatValue(value)
            }
            writer?.write(rowData)
            writer?.write(ROW_DELIMITER)
        } catch (e: Exception) {
            errors.add(SaveError(
                message = "Failed to write data row",
                rowData = data,
                exception = e
            ))
        }
    }

    private fun closeWriter() {
        try {
            writer?.flush()
            writer?.close()
            writer = null
        } catch (e: Exception) {
            errors.add(SaveError(
                message = "Failed to close file: $filePath",
                exception = e
            ))
        }
    }
}
