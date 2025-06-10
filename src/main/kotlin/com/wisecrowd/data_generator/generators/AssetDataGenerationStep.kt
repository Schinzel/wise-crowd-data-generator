package com.wisecrowd.data_generator.generators

import com.wisecrowd.data_generator.DataGenerationService
import com.wisecrowd.data_generator.FileNameEnum
import com.wisecrowd.data_generator.ILog
import com.wisecrowd.data_generator.data_generators.AssetDataGenerator
import com.wisecrowd.data_generator.data_saver.IDataSaver
import com.wisecrowd.data_generator.data_saver.SaveError
import kotlin.system.measureTimeMillis

/**
 * The purpose of this class is to handle the asset data generation step
 * of the WiseCrowd data generation pipeline.
 *
 * Written by Claude Sonnet 4
 */
class AssetDataGenerationStep(
    private val log: ILog,
    private val saverFactory: (String) -> IDataSaver,
) {
    fun execute(numberOfAssets: Int): List<SaveError> {
        log.writeToLog("Step 1/5: Generating $numberOfAssets assets...")

        val errors = mutableListOf<SaveError>()
        var rowsGenerated = 0
        val stepTime =
            measureTimeMillis {
                val generator = AssetDataGenerator(numberOfAssets)
                val saver = saverFactory(FileNameEnum.ASSET_DATA.fileName)
                val service = DataGenerationService(generator, saver)

                service.generateAndSave()
                errors.addAll(service.getErrors())
                rowsGenerated = service.getRowCount()
            }

        val warningText = if (errors.isEmpty()) "" else " (${errors.size} warnings)"
        log.writeToLog("Step 1 completed in ${stepTime}ms - $rowsGenerated rows generated$warningText")
        return errors
    }
}
