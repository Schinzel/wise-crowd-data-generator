package com.wisecrowd.data_generator

import com.wisecrowd.data_generator.log.ILog

/**
 * The purpose of this class is to provide a test implementation of ILog
 * that captures log messages for verification in unit tests.
 *
 * Written by Claude Sonnet 4
 */
class TestLog : ILog {
    private val messages = mutableListOf<String>()

    override fun writeToLog(message: String) {
        messages.add(message)
    }

    fun getMessages(): List<String> = messages.toList()

    fun clear() {
        messages.clear()
    }

    fun hasMessage(message: String): Boolean = messages.contains(message)

    fun getMessageCount(): Int = messages.size
}
