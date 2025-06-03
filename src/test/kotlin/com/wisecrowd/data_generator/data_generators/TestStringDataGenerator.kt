package com.wisecrowd.data_generator.data_generators

/**
 * The purpose of this class is to provide a simple concrete implementation
 * of IDataGenerator for testing and demonstration purposes.
 *
 * This generator produces a fixed list of strings, allowing us to test
 * the IDataGenerator contract with predictable behavior.
 *
 * Written by Claude Sonnet 4
 */
class TestStringDataGenerator(
    private val items: List<String>
) : IDataGenerator<String> {
    
    private var currentIndex = 0
    
    override fun hasNext(): Boolean {
        return currentIndex < items.size
    }
    
    override fun getNext(): String {
        if (!hasNext()) {
            throw NoSuchElementException("No more items available in generator")
        }
        return items[currentIndex++]
    }
}
