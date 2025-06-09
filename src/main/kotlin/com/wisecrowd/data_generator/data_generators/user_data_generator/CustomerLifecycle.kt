package com.wisecrowd.data_generator.data_generators.user_data_generator

import java.time.LocalDate

/**
 * The purpose of this data class is to hold customer lifecycle information
 * with clear, self-documenting property names instead of using Triple.
 *
 * Written by Claude Sonnet 4
 */
data class CustomerLifecycle(
    val joinDate: LocalDate,
    val departureDate: LocalDate,
    val status: CustomerStatus,
)
