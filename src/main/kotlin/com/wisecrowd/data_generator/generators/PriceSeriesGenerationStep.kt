package com.wisecrowd.data_generator.generators

import com.wisecrowd.data_generator.DataGenerationService
import com.wisecrowd.data_generator.FileNameEnum
import com.wisecrowd.data_generator.ILog
import com.wisecrowd.data_generator.data_generators.PriceSeriesDataGenerator
import com.wisecrowd.data_generator.data_saver.IDataSaver
import com.wisecrowd.data_generator.data_saver.SaveError
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.system.measureTimeMillis

/**
 * The purpose of this class is to handle the price series data generation step
 * of the WiseCrowd data generation pipeline.
 *
 * Written by Claude Sonnet 4
 */
class PriceSeriesGenerationStep(
    private val log: ILog,
    private val saverFactory: (String) -> IDataSaver,
) {
    fun execute(
        assetIds: List<UUID>,
        startDate: LocalDate,
        endDate: LocalDate,
        numberOfAssets: Int,
    ): List<SaveError> {
        val dayCount = ChronoUnit.DAYS.between(startDate, endDate) + 1
        log.writeToLog(
            "Step 2/5: Generating price series for $numberOfAssets assets " +
                "over $dayCount days...",
        )

        val errors = mutableListOf<SaveError>()
        var rowsGenerated = 0
        val stepTime =
            measureTimeMillis {
                val generator =
                    PriceSeriesDataGenerator(
                        assetIds = assetIds,
                        startDate = startDate,
                        endDate = endDate,
                    )
                val saver = saverFactory(FileNameEnum.PRICE_SERIES.fileName)
                val service = DataGenerationService(generator, saver)

                service.generateAndSave()
                errors.addAll(service.getErrors())
                rowsGenerated = service.getRowCount()
            }

        val warningText = if (errors.isEmpty()) "" else " (${errors.size} warnings)"
        log.writeToLog("Step 2 completed in ${stepTime}ms - $rowsGenerated rows generated$warningText")
        return errors
    }
}
