package com.wisecrowd.data_generator.data_saver.file_data_saver

import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

/**
 * The purpose of this class is to handle pure file I/O operations
 * including file creation, writing text content, and resource management.
 * 
 * This class provides a simple interface for writing text files with:
 * - Automatic directory creation for parent directories
 * - UTF-8 encoding by default
 * - Buffered writing for performance
 * - Proper resource management with explicit open/close lifecycle
 * 
 * USAGE PATTERN:
 * 1. Call createDirectoryStructure() to ensure parent directories exist
 * 2. Call open() to initialize the file writer
 * 3. Call writeLine() multiple times to write content
 * 4. Call close() to flush and release resources
 */
class FileWriter(private val filePath: String) {

    // Buffered writer for efficient file output operations
    // Null when file is not open, non-null when ready for writing
    private var writer: BufferedWriter? = null

    /**
     * Creates the directory structure if it doesn't exist
     * 
     * Extracts the parent directory from the file path and creates
     * all necessary directories in the hierarchy. Safe to call
     * multiple times - will not fail if directories already exist.
     * 
     * @throws Exception if directory creation fails due to permissions or I/O errors
     */
    fun createDirectoryStructure() {
        // Extract the file object from the provided path
        val file = File(filePath)
        // Get the parent directory (may be null if no parent)
        val parentDir = file.parentFile
        // Create all necessary parent directories if they don't exist
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs()
        }
    }

    /**
     * Opens the file for writing with UTF-8 encoding
     * 
     * Creates a buffered writer that outputs to the specified file
     * using UTF-8 character encoding. The file will be created if
     * it doesn't exist, or overwritten if it does exist.
     * 
     * Must be called before any writeLine() operations.
     * 
     * @throws Exception if file cannot be opened due to permissions, 
     *                  invalid path, or other I/O errors
     */
    fun open() {
        // Create a buffered writer with UTF-8 encoding for the output file
        writer = BufferedWriter(
            OutputStreamWriter(
                FileOutputStream(filePath),
                StandardCharsets.UTF_8
            )
        )
    }

    /**
     * Writes a line of text to the file
     * 
     * Writes the provided content directly to the file. The content
     * is written exactly as provided - no automatic line breaks or
     * formatting are added. Caller is responsible for including
     * newlines or other formatting as needed.
     * 
     * @param content the text content to write (including any newlines)
     * @throws IllegalStateException if the file writer is not open
     * @throws Exception if writing fails due to I/O errors
     */
    fun writeLine(content: String) {
        // Get the writer (fails fast if not open) and write content
        getWriter().write(content)
    }

    /**
     * Flushes and closes the file writer
     * 
     * Ensures all buffered content is written to disk and releases
     * all file system resources. After calling this method, the
     * writer is no longer usable and isOpen() will return false.
     * 
     * Safe to call multiple times - subsequent calls will have no effect.
     * 
     * @throws Exception if flushing or closing fails due to I/O errors
     */
    fun close() {
        // Only attempt to close if writer exists
        writer?.let { w ->
            // Flush any remaining buffered content to disk
            w.flush()
            // Close the writer and release file system resources
            w.close()
        }
        // Reset the writer reference to indicate file is no longer open
        writer = null
    }

    /**
     * Gets the current BufferedWriter instance
     * 
     * Returns the active writer for file operations. This method
     * implements fail-fast behavior by throwing an exception
     * immediately if the writer hasn't been opened yet.
     * 
     * @return the active BufferedWriter instance
     * @throws IllegalStateException if open() has not been called yet
     */
    private fun getWriter(): BufferedWriter {
        // Return writer or fail fast with clear error message
        return writer ?: throw IllegalStateException(
            "File writer must be opened before writing. Call open() first."
        )
    }

    /**
     * Checks if the writer is currently open and ready for writing
     * 
     * Returns true if open() has been called and the file is ready
     * to accept writeLine() operations. Returns false if the writer
     * has not been opened yet or has been closed.
     * 
     * @return true if writer is open and ready, false otherwise
     */
    fun isOpen(): Boolean = writer != null
}
