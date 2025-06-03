package com.wisecrowd.data_generator.data_saver

/**
 * Represents an error that occurred during data saving
 */
data class SaveError(
    val message: String,
    val identifier: String = "",
    val rowData: List<Any>? = null,
    val exception: Exception? = null
)