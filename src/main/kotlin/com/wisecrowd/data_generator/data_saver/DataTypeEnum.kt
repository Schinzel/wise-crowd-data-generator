package com.wisecrowd.data_generator.data_saver

/**
 * Possible data types for columns
 */
enum class DataTypeEnum {
    /** Text data */
    STRING,

    /** Integer numbers */
    INTEGER,

    /** Decimal/floating point numbers */
    DECIMAL,

    /** Date values */
    DATE,

    /** Date and time values */
    DATETIME,

    /** Boolean values */
    BOOLEAN,

    /** A unique identifier */
    IDENTIFIER,
}