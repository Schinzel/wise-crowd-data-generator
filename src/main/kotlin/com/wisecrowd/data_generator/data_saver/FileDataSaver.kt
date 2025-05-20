package com.wisecrowd.data_generator.data_saver

import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

/**
 * The purpose of this class is to save data to a text file in a delimited format
 * with proper handling of different data types and robust error management.
 *
 * Written by Claude 3.7 with Code Standard 1.0
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

    private var writer: BufferedWriter? = null
    private val errors = mutableListOf<SaveError>()
    private var columnData: List<ColumnData> = emptyList()

    init {
        try {
            // Create directory structure if it doesn't exist
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

    override fun prepare(columnData: List<ColumnData>) {
        if (columnData.isEmpty()) {
            errors.add(SaveError(message = "Column data cannot be empty"))
            return
        }

        this.columnData = columnData
        
        try {
            writer = BufferedWriter(
                OutputStreamWriter(
                    FileOutputStream(filePath),
                    StandardCharsets.UTF_8
                )
            )
            
            // Write header row
            val headerRow = columnData.joinToString(COLUMN_DELIMITER) { it.name }
            writer?.write(headerRow)
            writer?.write(ROW_DELIMITER)
        } catch (e: Exception) {
            errors.add(
                SaveError(
                    message = "Failed to create file or write header: $filePath",
                    exception = e
                )
            )
        }
    }

    override fun saveItem(data: List<String>) {
        if (writer == null) {
            errors.add(SaveError(
                message = "Cannot save item: prepare() must be called first",
                rowData = data
            ))
            return
        }

        if (data.size != columnData.size) {
            errors.add(SaveError(
                message = "Data size (${data.size}) does not match column count (${columnData.size})",
                rowData = data
            ))
            return
        }

        try {
            val rowData = data.mapIndexed { i, value -> 
                formatValue(value, columnData[i].dataType)
            }.joinToString(COLUMN_DELIMITER)
            
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

    override fun complete() {
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

    override fun getErrors(): List<SaveError> = errors.toList()

    override fun hasErrors(): Boolean = errors.isNotEmpty()

    /**
     * Formats a value based on its data type.
     * Strings are wrapped with qualifiers, other types are passed through.
     *
     * @param value the string representation of the value
     * @param dataType the type of data as defined in DataTypeEnum
     * @return formatted value as a string
     */
    private fun formatValue(value: String, dataType: DataTypeEnum): String {
        return when (dataType) {
            DataTypeEnum.STRING -> "$STRING_QUALIFIER$value$STRING_QUALIFIER"
            // Other types don't need qualifiers
            else -> value
        }
    }
}