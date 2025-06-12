package com.wisecrowd.data_generator

import com.wisecrowd.data_generator.output_directory.DesktopOutputDirectory
import com.wisecrowd.data_generator.output_directory.IOutputDirectory
import java.time.LocalDate

/**
 * The purpose of this data class is to hold all configuration parameters
 * for the data generation process with sensible defaults.
 *
 * Written by Claude Sonnet 4
 */
data class DataGenerationConfig(
    val startDate: LocalDate = LocalDate.of(2020, 1, 1),
    val endDate: LocalDate = LocalDate.now(),
    val numberOfAssets: Int = 100,
    val numberOfUsers: Int = 1_000,
    val outputDirectory: IOutputDirectory = DesktopOutputDirectory(),
)
