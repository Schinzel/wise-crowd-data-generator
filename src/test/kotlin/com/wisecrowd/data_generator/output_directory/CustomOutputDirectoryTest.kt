package com.wisecrowd.data_generator.output_directory

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CustomOutputDirectoryTest {
    @Test
    fun `getPath _ custom path provided _ returns exact path`() {
        val customPath = "/custom/test/directory"
        val outputDirectory = CustomOutputDirectory(customPath)

        val result = outputDirectory.getPath()

        assertThat(result).isEqualTo(customPath)
    }

    @Test
    fun `getPath _ empty path _ returns empty string`() {
        val outputDirectory = CustomOutputDirectory("")

        val result = outputDirectory.getPath()

        assertThat(result).isEqualTo("")
    }

    @Test
    fun `getPath _ relative path _ returns relative path`() {
        val relativePath = "relative/path/test"
        val outputDirectory = CustomOutputDirectory(relativePath)

        val result = outputDirectory.getPath()

        assertThat(result).isEqualTo(relativePath)
    }
}
