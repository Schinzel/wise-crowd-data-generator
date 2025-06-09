package com.wisecrowd.data_generator.data_saver

/**
 * The purpose of this object is to provide centralized constants for file format 
 * specifications to eliminate duplication between readers and writers and ensure
 * consistency across the entire file I/O system.
 *
 * Written by Claude Sonnet 4
 */
object FileFormatConstants {
    
    /** Delimiter used between rows in output files */
    const val ROW_DELIMITER = "\n"
    
    /** Delimiter used between columns in output files */
    const val COLUMN_DELIMITER = "\t"
    
    /** String qualifier used to wrap text data in output files */
    const val STRING_QUALIFIER = "###"
    
    /** Indicates whether the first row contains column names */
    const val HAS_HEADER_ROW = true
}