package com.wisecrowd.data_generator

import java.nio.file.Paths
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * The purpose of this data class is to manage all configuration parameters
 * for the data generation process with sensible defaults and automatic
 * output directory creation.
 *
 * Written by Claude Sonnet 4
 */
data class DataGenerationConfig(
    val startDate: LocalDate = LocalDate.of(2020, 1, 1),
    val endDate: LocalDate = LocalDate.now(),
    val numberOfAssets: Int = 100,
    val numberOfUsers: Int = 1_000,
    val outputDirectory: String = generateDefaultOutputDirectory(),
) {
    init {
        require(endDate >= startDate) {
            "End date ($endDate) must be greater than or equal to start date ($startDate)"
        }
        require(numberOfAssets > 0) {
            "Number of assets ($numberOfAssets) must be greater than 0"
        }
        require(numberOfUsers > 0) {
            "Number of users ($numberOfUsers) must be greater than 0"
        }
    }

    companion object {
        /**
         * The purpose of this function is to generate a default output directory
         * path with timestamp format: wise_crowd_data_{month}_{day}_{hour}:{minute}
         * using CET timezone and lowercase month names.
         */
        private fun generateDefaultOutputDirectory(): String {
            // Get current time in CET timezone
            val cetZone = ZoneId.of("CET")
            val now = ZonedDateTime.now(cetZone)

            // Create formatter for lowercase month abbreviation
            val monthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH)
            val month = now.format(monthFormatter).lowercase()

            // Format day, hour, and minute
            val day = String.format("%02d", now.dayOfMonth)
            val hour = String.format("%02d", now.hour)
            val minute = String.format("%02d", now.minute)

            // Create directory name
            val directoryName = "wise_crowd_data_${month}_${day}_$hour:$minute"

            // Get desktop path
            val desktopPath = getDesktopPath()

            return Paths.get(desktopPath, directoryName).toString()
        }

        /**
         * The purpose of this function is to detect the desktop path
         * across different operating systems.
         */
        private fun getDesktopPath(): String {
            val userHome = System.getProperty("user.home")

            // Try different desktop locations based on OS
            val osName = System.getProperty("os.name").lowercase()

            return when {
                osName.contains("windows") -> Paths.get(userHome, "Desktop").toString()
                osName.contains("mac") -> Paths.get(userHome, "Desktop").toString()
                osName.contains("linux") -> Paths.get(userHome, "Desktop").toString()
                else -> Paths.get(userHome, "Desktop").toString() // Default fallback
            }
        }
    }
}
