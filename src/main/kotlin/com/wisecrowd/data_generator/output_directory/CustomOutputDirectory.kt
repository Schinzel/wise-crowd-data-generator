package com.wisecrowd.data_generator.output_directory

/**
 * The purpose of this class is to provide a custom output directory
 * path for testing and specific use cases.
 *
 * Written by Claude Sonnet 4
 */
class CustomOutputDirectory(
    private val path: String,
) : IOutputDirectory {
    /**
     * Returns the custom path provided in constructor
     * @return Custom directory path as specified
     */
    override fun getPath(): String = path
}
