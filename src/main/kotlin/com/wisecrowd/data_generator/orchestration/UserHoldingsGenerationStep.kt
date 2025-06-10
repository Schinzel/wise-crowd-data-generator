package com.wisecrowd.data_generator.orchestration

import com.wisecrowd.data_generator.DataGenerationService
import com.wisecrowd.data_generator.FileNameEnum
import com.wisecrowd.data_generator.data_generators.UserHoldingsDataGenerator
import com.wisecrowd.data_generator.data_saver.IDataSaver
import com.wisecrowd.data_generator.data_saver.SaveError
import com.wisecrowd.data_generator.log.ILog
import kotlin.system.measureTimeMillis

/**
 * The purpose of this class is to handle the user holdings data generation step
 * of the WiseCrowd data generation pipeline.
 *
 * Written by Claude Sonnet 4
 */
class UserHoldingsGenerationStep(
    private val log: ILog,
    private val saverFactory: (String) -> IDataSaver,
) {
    fun execute(transactionData: List<List<Any>>): List<SaveError> {
        log.writeToLog("Step 5/5: Generating user holdings based on transactions...")

        val errors = mutableListOf<SaveError>()
        var rowsGenerated = 0
        val stepTime =
            measureTimeMillis {
                val generator = UserHoldingsDataGenerator(transactionData)
                val saver = saverFactory(FileNameEnum.USER_HOLDINGS.fileName)
                val service = DataGenerationService(generator, saver)

                service.generateAndSave()
                errors.addAll(service.getErrors())
                rowsGenerated = service.getRowCount()
            }

        val warningText = if (errors.isEmpty()) "" else " (${errors.size} warnings)"
        log.writeToLog("Step 5 completed in ${stepTime}ms - $rowsGenerated rows generated$warningText")
        return errors
    }
}
