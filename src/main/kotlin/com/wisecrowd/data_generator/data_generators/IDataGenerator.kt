package com.wisecrowd.data_generator.data_generators

/**
 * The purpose of this interface is to define the contract for generating data
 * using an iterator pattern that allows consumers to control the flow of data generation.
 *
 * This interface provides a pull-based approach where the consumer calls hasNext()
 * to check availability and getNext() to retrieve the next generated item.
 * This design enables better memory management through lazy generation and gives
 * the consumer full control over the generation timing and flow.
 *
 * Written by Claude Sonnet 4
 */
interface IDataGenerator<T> {
    
    /**
     * Checks if there are more items available to generate
     * 
     * This method should be safe to call multiple times without side effects.
     * It should return true if getNext() will successfully return an item,
     * and false if no more items are available for generation.
     * 
     * @return true if more items are available, false otherwise
     */
    fun hasNext(): Boolean
    
    /**
     * Retrieves the next generated item
     * 
     * This method should only be called after hasNext() returns true.
     * Calling getNext() when hasNext() returns false should result in
     * a NoSuchElementException being thrown.
     * 
     * The implementation may perform the actual data generation lazily
     * when this method is called, allowing for efficient memory usage.
     * 
     * @return the next generated item of type T
     * @throws NoSuchElementException if no more items are available
     * @throws IllegalStateException if the generator is in an invalid state
     */
    fun getNext(): T
}
