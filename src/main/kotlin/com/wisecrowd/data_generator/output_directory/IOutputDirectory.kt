package com.wisecrowd.data_generator.output_directory

/**
 * The purpose of this interface is to define the contract for
 * providing output directory paths for data generation.
 *
 * Written by Claude Sonnet 4
 */
interface IOutputDirectory {
    /**
     * Returns the full path where generated data files should be saved
     * @return Full directory path as a string
     */
    fun getPath(): String
}
