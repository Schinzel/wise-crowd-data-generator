package com.wisecrowd.data_generator.data_saver

import java.io.File

/**
 * The purpose of this class is to parse generated tab-delimited files back 
 * into List<List<String>> format for generator dependencies. Uses 
 * FileFormatConstants for consistency with file writers.
 *
 * Written by Claude Sonnet 4
 */
class FileDataParser {

    /**
     * Parses a tab-delimited file and returns data as List<List<String>>.
     * Skips header row if FileFormatConstants.HAS_HEADER_ROW is true.
     * 
     * @param filePath Path to the file to parse
     * @return List<List<String>> containing the parsed data, empty list if 
     *         file is empty
     * @throws IllegalArgumentException if file doesn't exist or is not readable
     */
    fun parseFile(filePath: String): List<List<String>> {
        val file = File(filePath)
        
        // Validate file exists and is readable
        require(file.exists()) { "File does not exist: $filePath" }
        require(file.isFile()) { "Path is not a file: $filePath" }
        require(file.canRead()) { "File is not readable: $filePath" }
        
        return try {
            val lines = file.readLines()
            
            // Handle empty file
            if (lines.isEmpty()) {
                return emptyList()
            }
            
            // Skip header row if configured
            @Suppress("KotlinConstantConditions", "SimplifyBooleanWithConstants")
            val shouldSkipHeader = FileFormatConstants.HAS_HEADER_ROW &&
                                 lines.isNotEmpty()
            @Suppress("KotlinConstantConditions")
            val dataLines = if (shouldSkipHeader) {
                lines.drop(1)
            } else {
                lines
            }
            
            // Parse each line into columns
            parseLines(dataLines)
            
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to parse file: $filePath", e)
        }
    }
    
    /**
     * Parses lines of text into List<List<String>> format.
     * Handles string qualifiers and empty/malformed lines gracefully.
     */
    private fun parseLines(lines: List<String>): List<List<String>> {
        return lines.mapNotNull { line ->
            parseLine(line.trim())
        }
    }
    
    /**
     * Parses a single line into a list of string values.
     * Handles string qualifiers and empty lines gracefully.
     * 
     * @param line The line to parse
     * @return List<String> of column values, or null if line should be skipped
     */
    private fun parseLine(line: String): List<String>? {
        // Skip empty lines
        if (line.isEmpty()) {
            return null
        }
        
        // Split by column delimiter, preserving trailing empty columns
        val delimiter = FileFormatConstants.COLUMN_DELIMITER
        val columns = mutableListOf<String>()
        
        var currentIndex = 0
        while (currentIndex <= line.length) {
            val nextDelimiterIndex = line.indexOf(delimiter, currentIndex)
            
            if (nextDelimiterIndex == -1) {
                // No more delimiters, add remaining part
                columns.add(line.substring(currentIndex))
                break
            } else {
                // Add part before delimiter
                columns.add(line.substring(currentIndex, nextDelimiterIndex))
                currentIndex = nextDelimiterIndex + delimiter.length
                
                // If we're at the end of the line after a delimiter, add empty string
                if (currentIndex == line.length) {
                    columns.add("")
                    break
                }
            }
        }
        
        // Remove string qualifiers from each column
        return columns.map { column ->
            removeStringQualifiers(column)
        }
    }
    
    /**
     * Removes string qualifiers from a column value if present.
     * 
     * @param value The column value that may have string qualifiers
     * @return The value with string qualifiers removed
     */
    private fun removeStringQualifiers(value: String): String {
        val qualifier = FileFormatConstants.STRING_QUALIFIER
        
        return if (value.startsWith(qualifier) && value.endsWith(qualifier) && 
                   value.length >= qualifier.length * 2) {
            value.substring(qualifier.length, value.length - qualifier.length)
        } else {
            value
        }
    }
}