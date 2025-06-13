package com.wisecrowd.data_generator.output_directory

import java.nio.file.Paths
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * The purpose of this class is to generate desktop output directory paths
 * with timestamp format: wise_crowd_data_{month}_{day}_{hour}_{minute}
 * using CET timezone and lowercase month names.
 *
 * Written by Claude Sonnet 4
 */
class DesktopOutputDirectory : IOutputDirectory {
    /**
     * Generates a timestamped directory path on the user's desktop
     * @return Full path to desktop directory with timestamp
     */
    override fun getPath(): String {
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

        // Create directory name with underscores to avoid path separators
        val directoryName = "wise_crowd_data_${month}_${day}_${hour}_$minute"

        // Get desktop path
        val desktopPath = getDesktopPath()

        return Paths.get(desktopPath, directoryName).toString()
    }

    /**
     * Detects the desktop path across different operating systems
     * @return Path to user's desktop directory
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
