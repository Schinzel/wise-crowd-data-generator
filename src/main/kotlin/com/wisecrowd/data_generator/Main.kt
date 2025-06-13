package com.wisecrowd.data_generator

import java.time.LocalDate

/**
 * The purpose of this com.wisecrowd.data_generator.main function is to provide a simple entry point
 * for generating WiseCrowd mock data from command line or IDE.
 *
 * Written by Claude Sonnet 4
 */
fun main() {
    println("WiseCrowd Data Generator")
    println("======================")

    // Create configuration with default values
    val config = DataGenerationConfig(
        numberOfAssets = 100,
        numberOfUsers = 1_000,
        startDate = LocalDate.of(2020, 1, 1),
        endDate = LocalDate.of(2022, 1, 1)
    )

    // Create and run orchestrator
    val orchestrator = WiseCrowdDataOrchestrator(config)

    try {
        orchestrator.generate()
        println("\nData generation completed successfully!")
        println("Generated files:")
        println("- asset_data.txt")
        println("- price_series.txt")
        println("- users.txt")
        println("- transactions.txt")
        println("- user_holdings.txt")
    } catch (e: Exception) {
        println("\nData generation failed: ${e.message}")
        e.printStackTrace()
    }
}
