package com.wisecrowd.data_generator.utils

/**
 * The purpose of this interface is to define the contract for weighted random selection
 * across all collections requiring distribution-based randomization.
 *
 * This interface enables consistent randomized distribution logic throughout the system
 * while supporting dependency injection for deterministic testing.
 *
 * Written by Claude Sonnet 4
 */
interface IWeightedRandomSelector<T> {
    /**
     * Returns a randomly selected item based on weighted distribution.
     *
     * @return A randomly selected item from the weighted collection
     * @throws IllegalStateException if no items are available for selection
     */
    fun getRandomItem(): T
}
