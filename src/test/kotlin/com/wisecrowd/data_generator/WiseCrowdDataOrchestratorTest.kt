package com.wisecrowd.data_generator

import com.wisecrowd.data_generator.output_directory.CustomOutputDirectory
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import java.time.LocalDate

/**
 * The purpose of this class is to test the WiseCrowdDataOrchestrator
 * with various configuration scenarios and error conditions.
 *
 * Written by Claude Sonnet 4
 */
class WiseCrowdDataOrchestratorTest {
    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `generate _ small dataset _ creates all five files`() {
        val config =
            DataGenerationConfig(
                startDate = LocalDate.of(2024, 1, 1),
                endDate = LocalDate.of(2024, 1, 3),
                numberOfAssets = 5,
                numberOfUsers = 10,
                outputDirectory = CustomOutputDirectory(tempDir.toString()),
            )
        val testLog = TestLog()
        val generator = WiseCrowdDataOrchestrator(config, testLog)

        generator.generate()

        // Verify all files were created
        val assetFile = File(tempDir.toFile(), FileNameEnum.ASSET_DATA.fileName)
        val priceFile = File(tempDir.toFile(), FileNameEnum.PRICE_SERIES.fileName)
        val usersFile = File(tempDir.toFile(), FileNameEnum.USERS.fileName)
        val transactionsFile = File(tempDir.toFile(), FileNameEnum.TRANSACTIONS.fileName)
        val holdingsFile = File(tempDir.toFile(), FileNameEnum.USER_HOLDINGS.fileName)

        assertThat(assetFile.exists()).isTrue()
        assertThat(priceFile.exists()).isTrue()
        assertThat(usersFile.exists()).isTrue()
        assertThat(transactionsFile.exists()).isTrue()
        assertThat(holdingsFile.exists()).isTrue()
    }

    @Test
    fun `generate _ small dataset _ logs progress for all steps`() {
        val config =
            DataGenerationConfig(
                startDate = LocalDate.of(2024, 1, 1),
                endDate = LocalDate.of(2024, 1, 2),
                numberOfAssets = 3,
                numberOfUsers = 5,
                outputDirectory = CustomOutputDirectory(tempDir.toString()),
            )
        val testLog = TestLog()
        val generator = WiseCrowdDataOrchestrator(config, testLog)

        generator.generate()

        val messages = testLog.getMessages()
        assertThat(messages).anyMatch { it.contains("Step 1/5: Generating 3 assets") }
        assertThat(messages).anyMatch { it.contains("Step 2/5: Generating price series for 3 assets") }
        assertThat(messages).anyMatch { it.contains("Step 3/5: Generating 5 users") }
        assertThat(messages).anyMatch { it.contains("Step 4/5: Generating transactions") }
        assertThat(messages).anyMatch { it.contains("Step 5/5: Generating user holdings") }
        assertThat(messages).anyMatch { it.contains("All steps completed in") }
    }

    @Test
    fun `generate _ small dataset _ logs completion timing for all steps`() {
        val config =
            DataGenerationConfig(
                startDate = LocalDate.of(2024, 1, 1),
                endDate = LocalDate.of(2024, 1, 1),
                numberOfAssets = 2,
                numberOfUsers = 3,
                outputDirectory = CustomOutputDirectory(tempDir.toString()),
            )
        val testLog = TestLog()
        val generator = WiseCrowdDataOrchestrator(config, testLog)

        generator.generate()

        val messages = testLog.getMessages()
        assertThat(messages).anyMatch { it.contains("Step 1 completed in") && it.contains("ms") }
        assertThat(messages).anyMatch { it.contains("Step 2 completed in") && it.contains("ms") }
        assertThat(messages).anyMatch { it.contains("Step 3 completed in") && it.contains("ms") }
        assertThat(messages).anyMatch { it.contains("Step 4 completed in") && it.contains("ms") }
        assertThat(messages).anyMatch { it.contains("Step 5 completed in") && it.contains("ms") }
    }

    @Test
    fun `generate _ output directory does not exist _ creates directory`() {
        val nonExistentDir = File(tempDir.toFile(), "new_directory")
        val config =
            DataGenerationConfig(
                startDate = LocalDate.of(2024, 1, 1),
                endDate = LocalDate.of(2024, 1, 1),
                numberOfAssets = 2,
                numberOfUsers = 2,
                outputDirectory = CustomOutputDirectory(nonExistentDir.absolutePath),
            )
        val generator = WiseCrowdDataOrchestrator(config)

        generator.generate()

        assertThat(nonExistentDir.exists()).isTrue()
        assertThat(nonExistentDir.isDirectory()).isTrue()
    }

    @Test
    fun `generate _ invalid output directory path _ throws exception`() {
        // Use an invalid path that cannot be created (root permission required)
        val invalidPath = CustomOutputDirectory("/root/cannot_create_this_directory")
        val config =
            DataGenerationConfig(
                numberOfAssets = 1,
                numberOfUsers = 1,
                outputDirectory = invalidPath,
            )
        val generator = WiseCrowdDataOrchestrator(config)

        assertThatThrownBy {
            generator.generate()
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Failed to create output directory")
    }

    @Test
    fun `generate _ default log implementation _ uses SystemOutLog`() {
        val config =
            DataGenerationConfig(
                startDate = LocalDate.of(2024, 1, 1),
                endDate = LocalDate.of(2024, 1, 1),
                numberOfAssets = 1,
                numberOfUsers = 1,
                outputDirectory = CustomOutputDirectory(tempDir.toString()),
            )

        // Test that constructor works with default log (no exception thrown)
        val generator = WiseCrowdDataOrchestrator(config)

        // Should complete without error
        generator.generate()

        // Verify files are created (proving the generator worked)
        val assetFile = File(tempDir.toFile(), FileNameEnum.ASSET_DATA.fileName)
        assertThat(assetFile.exists()).isTrue()
    }
}
