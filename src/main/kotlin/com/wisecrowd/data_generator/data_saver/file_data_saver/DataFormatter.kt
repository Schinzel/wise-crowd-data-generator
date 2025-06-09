package com.wisecrowd.data_generator.data_saver.file_data_saver

import com.wisecrowd.data_generator.data_saver.FileFormatConstants
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * The purpose of this class is to format data values according to their type
 * for output to delimited text files with consistent formatting rules.
 *
 * SUPPORTED DATA TYPES & FORMATTING:
 * - UUID: Standard string representation without quotes (e.g., 123e4567-e89b-12d3-a456-426614174000)
 * - Int: As-is without quotes (e.g., 123)
 * - Double: Exactly 2 decimal places, rounded, without quotes (e.g., 123.45)
 * - String: Always wrapped with ### qualifiers (e.g., ###Sample Text###)
 * - LocalDate: ISO format YYYY-MM-DD without quotes (e.g., 2025-05-20)
 * - LocalDateTime: ISO format YYYY-MM-DDTHH:mm:ssZ without quotes (e.g., 2025-05-20T14:30:00Z)
 */
class DataFormatter {
    companion object {
        /** Formatter for Double values (always 2 decimal places) */
        private val DOUBLE_FORMATTER = DecimalFormat("0.00")

        /** Formatter for LocalDateTime values (ISO format with seconds and UTC indicator) */
        private val DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

        /** Formatter for LocalDate values (ISO format) */
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }

    /**
     * Formats a value based on its data type according to the official specification.
     *
     * @param value the value of any supported type
     * @return formatted value as a string
     * @throws IllegalArgumentException if the data type is not supported
     */
    fun formatValue(value: Any): String =
        when (value) {
            // UUID type - standard string representation
            is UUID -> value.toString()

            // Numeric types
            is Int -> value.toString()
            is Double -> DOUBLE_FORMATTER.format(value)

            // Date/Time types
            is LocalDate -> value.format(DATE_FORMATTER)
            is LocalDateTime -> value.format(DATETIME_FORMATTER)

            // String type - always wrapped with qualifiers
            is String -> "${FileFormatConstants.STRING_QUALIFIER}$value${FileFormatConstants.STRING_QUALIFIER}"

            // Unsupported types - throw exception to catch programming errors
            else -> throw IllegalArgumentException(
                "Unsupported data type: ${value::class.simpleName}. " +
                    "Supported types: UUID, Int, Double, String, LocalDate, LocalDateTime",
            )
        }
}
