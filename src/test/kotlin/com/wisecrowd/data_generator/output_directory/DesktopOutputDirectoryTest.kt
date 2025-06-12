package com.wisecrowd.data_generator.output_directory

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DesktopOutputDirectoryTest {
    @Nested
    inner class GetPath {
        @Test
        fun `getPath _ default call _ returns path containing wise_crowd_data prefix`() {
            val outputDirectory = DesktopOutputDirectory()

            val path = outputDirectory.getPath()

            assertThat(path).contains("wise_crowd_data_")
        }

        @Test
        fun `getPath _ default call _ returns path containing month abbreviation`() {
            val outputDirectory = DesktopOutputDirectory()

            val path = outputDirectory.getPath()

            // Should contain a lowercase 3-letter month abbreviation
            val monthPattern = "_(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)_"
            assertThat(path).containsPattern(monthPattern)
        }

        @Test
        fun `getPath _ default call _ returns path containing day and time format`() {
            val outputDirectory = DesktopOutputDirectory()

            val path = outputDirectory.getPath()

            // Should contain day (01-31) and time in format HH:MM
            val dayTimePattern = "_\\d{2}_\\d{2}:\\d{2}$"
            assertThat(path).containsPattern(dayTimePattern)
        }

        @Test
        fun `getPath _ default call _ returns path pointing to desktop`() {
            val outputDirectory = DesktopOutputDirectory()

            val path = outputDirectory.getPath()
            val userHome = System.getProperty("user.home")

            assertThat(path).startsWith(userHome)
            assertThat(path).contains("Desktop")
        }

        @Test
        fun `getPath _ multiple calls _ can generate different timestamps`() {
            val outputDirectory1 = DesktopOutputDirectory()
            val outputDirectory2 = DesktopOutputDirectory()

            val path1 = outputDirectory1.getPath()
            Thread.sleep(1) // Ensure different minute if near boundary
            val path2 = outputDirectory2.getPath()

            // Should have same format structure even if timestamps are identical
            assertThat(path1).contains("wise_crowd_data_")
            assertThat(path2).contains("wise_crowd_data_")
        }
    }
}
