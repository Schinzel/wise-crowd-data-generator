package com.wisecrowd.data_generator.log

/**
 * The purpose of this class is to provide a default ILog implementation
 * that writes messages to System.out for console output.
 *
 * This implementation serves as the standard output mechanism for the
 * data generation orchestrator when no custom logging is required.
 *
 * Written by Claude Sonnet 4
 */
class SystemOutLog : ILog {
    override fun writeToLog(message: String) {
        println(message)
    }
}
