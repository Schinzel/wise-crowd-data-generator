package com.wisecrowd.data_generator.log

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * The purpose of this class is to test the SystemOutLog implementation
 * to ensure it correctly writes messages to System.out.
 *
 * Written by Claude Sonnet 4
 */
class SystemOutLogTest {
    @Test
    fun `writeToLog _ single message _ outputs to system out`() {
        val originalOut = System.out
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        try {
            val log = SystemOutLog()
            val testMessage = "Test message for logging"

            log.writeToLog(testMessage)

            val output = outputStream.toString().trim()
            assertThat(output).isEqualTo(testMessage)
        } finally {
            System.setOut(originalOut)
        }
    }

    @Test
    fun `writeToLog _ multiple messages _ outputs all messages to system out`() {
        val originalOut = System.out
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        try {
            val log = SystemOutLog()
            val message1 = "First message"
            val message2 = "Second message"

            log.writeToLog(message1)
            log.writeToLog(message2)

            val output = outputStream.toString()
            assertThat(output).contains(message1)
            assertThat(output).contains(message2)
        } finally {
            System.setOut(originalOut)
        }
    }

    @Test
    fun `writeToLog _ empty message _ outputs empty line to system out`() {
        val originalOut = System.out
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        try {
            val log = SystemOutLog()

            log.writeToLog("")

            val output = outputStream.toString()
            assertThat(output).isEqualTo(System.lineSeparator())
        } finally {
            System.setOut(originalOut)
        }
    }
}
