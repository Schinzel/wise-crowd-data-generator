package com.wisecrowd.data_generator.log

/**
 * The purpose of this interface is to provide a flexible logging abstraction
 * for output messages during data generation processes.
 *
 * This interface allows for different implementations of output handling
 * (console, file, testing, etc.) while keeping the orchestrator code
 * decoupled from specific logging mechanisms.
 *
 * Written by Claude Sonnet 4
 */
interface ILog {
    /**
     * Writes a message to the log output
     *
     * @param message The message to be written to the log
     */
    fun writeToLog(message: String)
}
