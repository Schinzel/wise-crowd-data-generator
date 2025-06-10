package com.wisecrowd.data_generator.orchestration

import com.wisecrowd.data_generator.DataGenerationService
import com.wisecrowd.data_generator.FileNameEnum
import com.wisecrowd.data_generator.data_generators.UserDataGenerator
import com.wisecrowd.data_generator.data_saver.IDataSaver
import com.wisecrowd.data_generator.data_saver.SaveError
import com.wisecrowd.data_generator.log.ILog
import java.time.LocalDate
import kotlin.system.measureTimeMillis

/**
 * The purpose of this class is to handle the user data generation step
 * of the WiseCrowd data generation pipeline.
 *
 * Written by Claude Sonnet 4
 */
class UserDataGenerationStep(
    private val log: ILog,
    private val saverFactory: (String) -> IDataSaver,
) {
    fun execute(
        numberOfUsers: Int,
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<SaveError> {
        log.writeToLog("Step 3/5: Generating $numberOfUsers users...")

        val errors = mutableListOf<SaveError>()
        var rowsGenerated = 0
        val stepTime =
            measureTimeMillis {
                val generator =
                    UserDataGenerator(
                        userCount = numberOfUsers,
                        simulationStartDate = startDate,
                        simulationEndDate = endDate,
                    )
                val saver = saverFactory(FileNameEnum.USERS.fileName)
                val service = DataGenerationService(generator, saver)

                service.generateAndSave()
                errors.addAll(service.getErrors())
                rowsGenerated = service.getRowCount()
            }

        val warningText = if (errors.isEmpty()) "" else " (${errors.size} warnings)"
        log.writeToLog("Step 3 completed in ${stepTime}ms - $rowsGenerated rows generated$warningText")
        return errors
    }
}
