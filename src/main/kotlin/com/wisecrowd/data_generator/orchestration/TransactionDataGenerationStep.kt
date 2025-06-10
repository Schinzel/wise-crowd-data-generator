package com.wisecrowd.data_generator.orchestration

import com.wisecrowd.data_generator.DataGenerationService
import com.wisecrowd.data_generator.FileNameEnum
import com.wisecrowd.data_generator.ILog
import com.wisecrowd.data_generator.data_generators.TransactionDataGenerator
import com.wisecrowd.data_generator.data_saver.IDataSaver
import com.wisecrowd.data_generator.data_saver.SaveError
import kotlin.system.measureTimeMillis

/**
 * The purpose of this class is to handle the transaction data generation step
 * of the WiseCrowd data generation pipeline.
 *
 * Written by Claude Sonnet 4
 */
class TransactionDataGenerationStep(
    private val log: ILog,
    private val saverFactory: (String) -> IDataSaver,
) {
    fun execute(
        priceSeriesData: List<List<Any>>,
        userData: List<List<Any>>,
    ): List<SaveError> {
        log.writeToLog("Step 4/5: Generating transactions based on price series and users...")

        val errors = mutableListOf<SaveError>()
        var rowsGenerated = 0
        val stepTime =
            measureTimeMillis {
                val generator = TransactionDataGenerator(priceSeriesData, userData)
                val saver = saverFactory(FileNameEnum.TRANSACTIONS.fileName)
                val service = DataGenerationService(generator, saver)

                service.generateAndSave()
                errors.addAll(service.getErrors())
                rowsGenerated = service.getRowCount()
            }

        val warningText = if (errors.isEmpty()) "" else " (${errors.size} warnings)"
        log.writeToLog("Step 4 completed in ${stepTime}ms - $rowsGenerated rows generated$warningText")
        return errors
    }
}
