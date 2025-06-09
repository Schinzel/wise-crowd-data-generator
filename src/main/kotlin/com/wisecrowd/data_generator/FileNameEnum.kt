package com.wisecrowd.data_generator

/**
 * The purpose of this enum is to store the names of output files in one place
 * for consistent reference throughout the application.
 *
 * Written by Claude 3.7 with Code Standard 1.0
 */
enum class FileNameEnum(
    val fileName: String,
) {
    /** Asset data file containing asset metadata */
    ASSET_DATA("asset_data.txt"),

    /** Users data file containing user profiles */
    USERS("users.txt"),

    /** Price series data file containing historical prices */
    PRICE_SERIES("price_series.txt"),

    /** Transactions data file containing user transactions */
    TRANSACTIONS("transactions.txt"),

    /** User holdings data file containing user asset holdings */
    USER_HOLDINGS("user_holdings.txt"),
}
