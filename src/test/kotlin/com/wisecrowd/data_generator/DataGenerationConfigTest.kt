package com.wisecrowd.data_generator

import com.wisecrowd.data_generator.output_directory.CustomOutputDirectory
import com.wisecrowd.data_generator.output_directory.DesktopOutputDirectory
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DataGenerationConfigTest {
    @Nested
    inner class Constructor {
        @Test
        fun `constructor _ default parameters _ creates config with correct defaults`() {
            val config = DataGenerationConfig()

            val expectedStartDate = LocalDate.of(2020, 1, 1)
            assertThat(config.startDate).isEqualTo(expectedStartDate)

            val expectedEndDate = LocalDate.now()
            assertThat(config.endDate).isEqualTo(expectedEndDate)

            assertThat(config.numberOfAssets).isEqualTo(100)
            assertThat(config.numberOfUsers).isEqualTo(1_000)
            assertThat(config.outputDirectory).isInstanceOf(DesktopOutputDirectory::class.java)
        }

        @Test
        fun `constructor _ custom parameters _ creates config with specified values`() {
            val startDate = LocalDate.of(2021, 6, 15)
            val endDate = LocalDate.of(2022, 12, 31)
            val numberOfAssets = 50
            val numberOfUsers = 500
            val customOutputDirectory = CustomOutputDirectory("/custom/path")

            val config =
                DataGenerationConfig(
                    startDate = startDate,
                    endDate = endDate,
                    numberOfAssets = numberOfAssets,
                    numberOfUsers = numberOfUsers,
                    outputDirectory = customOutputDirectory,
                )

            assertThat(config.startDate).isEqualTo(startDate)
            assertThat(config.endDate).isEqualTo(endDate)
            assertThat(config.numberOfAssets).isEqualTo(numberOfAssets)
            assertThat(config.numberOfUsers).isEqualTo(numberOfUsers)
            assertThat(config.outputDirectory).isEqualTo(customOutputDirectory)
        }

        @Test
        fun `constructor _ same start and end date _ creates valid config`() {
            val date = LocalDate.of(2023, 5, 10)

            val config =
                DataGenerationConfig(
                    startDate = date,
                    endDate = date,
                )

            assertThat(config.startDate).isEqualTo(date)
            assertThat(config.endDate).isEqualTo(date)
        }
    }

    @Nested
    inner class Validation {
        @Test
        fun `constructor _ end date before start date _ throws IllegalArgumentException`() {
            val startDate = LocalDate.of(2023, 6, 15)
            val endDate = LocalDate.of(2023, 6, 14)

            assertThatThrownBy {
                DataGenerationConfig(
                    startDate = startDate,
                    endDate = endDate,
                )
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("End date")
                .hasMessageContaining("must be greater than or equal to start date")
        }

        @Test
        fun `constructor _ zero assets _ throws IllegalArgumentException`() {
            assertThatThrownBy {
                DataGenerationConfig(numberOfAssets = 0)
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Number of assets")
                .hasMessageContaining("must be greater than 0")
        }

        @Test
        fun `constructor _ negative assets _ throws IllegalArgumentException`() {
            assertThatThrownBy {
                DataGenerationConfig(numberOfAssets = -5)
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Number of assets")
                .hasMessageContaining("must be greater than 0")
        }

        @Test
        fun `constructor _ zero users _ throws IllegalArgumentException`() {
            assertThatThrownBy {
                DataGenerationConfig(numberOfUsers = 0)
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Number of users")
                .hasMessageContaining("must be greater than 0")
        }

        @Test
        fun `constructor _ negative users _ throws IllegalArgumentException`() {
            assertThatThrownBy {
                DataGenerationConfig(numberOfUsers = -10)
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Number of users")
                .hasMessageContaining("must be greater than 0")
        }
    }

    @Nested
    inner class OutputDirectory {
        @Test
        fun `outputDirectory _ default desktop directory _ returns valid path`() {
            val config = DataGenerationConfig()

            val path = config.outputDirectory.getPath()

            assertThat(path).contains("wise_crowd_data_")
        }

        @Test
        fun `outputDirectory _ custom directory _ returns specified path`() {
            val customPath = "/custom/test/path"
            val customOutputDirectory = CustomOutputDirectory(customPath)
            val config = DataGenerationConfig(outputDirectory = customOutputDirectory)

            val path = config.outputDirectory.getPath()

            assertThat(path).isEqualTo(customPath)
        }

        @Test
        fun `outputDirectory _ desktop directory contains month _ returns path with month`() {
            val config = DataGenerationConfig()

            val path = config.outputDirectory.getPath()

            // Should contain a lowercase 3-letter month abbreviation
            val monthPattern = "_(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)_"
            assertThat(path).containsPattern(monthPattern)
        }

        @Test
        fun `outputDirectory _ desktop directory contains time _ returns path with time format`() {
            val config = DataGenerationConfig()

            val path = config.outputDirectory.getPath()

            // Should contain day (01-31) and time in format HH:MM
            val dayTimePattern = "_\\d{2}_\\d{2}:\\d{2}$"
            assertThat(path).containsPattern(dayTimePattern)
        }

        @Test
        fun `outputDirectory _ desktop directory points to desktop _ returns desktop path`() {
            val config = DataGenerationConfig()

            val path = config.outputDirectory.getPath()
            val userHome = System.getProperty("user.home")

            assertThat(path).startsWith(userHome)
            assertThat(path).contains("Desktop")
        }
    }

    @Nested
    inner class EdgeCases {
        @Test
        fun `constructor _ very large numbers _ creates valid config`() {
            val config =
                DataGenerationConfig(
                    numberOfAssets = 1_000_000,
                    numberOfUsers = 10_000_000,
                )

            assertThat(config.numberOfAssets).isEqualTo(1_000_000)
            assertThat(config.numberOfUsers).isEqualTo(10_000_000)
        }

        @Test
        fun `constructor _ minimum valid values _ creates valid config`() {
            val config =
                DataGenerationConfig(
                    numberOfAssets = 1,
                    numberOfUsers = 1,
                )

            assertThat(config.numberOfAssets).isEqualTo(1)
            assertThat(config.numberOfUsers).isEqualTo(1)
        }

        @Test
        fun `constructor _ far future dates _ creates valid config`() {
            val startDate = LocalDate.of(2050, 1, 1)
            val endDate = LocalDate.of(2099, 12, 31)

            val config =
                DataGenerationConfig(
                    startDate = startDate,
                    endDate = endDate,
                )

            assertThat(config.startDate).isEqualTo(startDate)
            assertThat(config.endDate).isEqualTo(endDate)
        }

        @Test
        fun `constructor _ historical dates _ creates valid config`() {
            val startDate = LocalDate.of(1990, 1, 1)
            val endDate = LocalDate.of(1999, 12, 31)

            val config =
                DataGenerationConfig(
                    startDate = startDate,
                    endDate = endDate,
                )

            assertThat(config.startDate).isEqualTo(startDate)
            assertThat(config.endDate).isEqualTo(endDate)
        }
    }
}
