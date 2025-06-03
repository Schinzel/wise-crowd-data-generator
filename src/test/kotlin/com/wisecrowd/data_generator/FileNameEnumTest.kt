package com.wisecrowd.data_generator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FileNameEnumTest {

    @Test
    fun assetData_hasCorrectFileName() {
        assertThat(FileNameEnum.ASSET_DATA.fileName).isEqualTo("asset_data.txt")
    }

    @Test
    fun users_hasCorrectFileName() {
        assertThat(FileNameEnum.USERS.fileName).isEqualTo("users.txt")
    }

    @Test
    fun priceSeries_hasCorrectFileName() {
        assertThat(FileNameEnum.PRICE_SERIES.fileName).isEqualTo("price_series.txt")
    }

    @Test
    fun transactions_hasCorrectFileName() {
        assertThat(FileNameEnum.TRANSACTIONS.fileName).isEqualTo("transactions.txt")
    }

    @Test
    fun userHoldings_hasCorrectFileName() {
        assertThat(FileNameEnum.USER_HOLDINGS.fileName).isEqualTo("user_holdings.txt")
    }

    @Test
    fun allEnumValues_countIsCorrect() {
        assertThat(FileNameEnum.entries).hasSize(5)
    }

    @Test
    fun allEnumValues_haveUniqueFileNames() {
        val fileNames = FileNameEnum.entries.map { it.fileName }.toSet()
        assertThat(fileNames).hasSize(FileNameEnum.entries.size)
    }

    @Test
    fun allEnumValues_haveNonEmptyFileNames() {
        FileNameEnum.entries.forEach { enum ->
            assertThat(enum.fileName).isNotBlank()
        }
    }

    @Test
    fun allEnumValues_fileNamesEndWithTxt() {
        FileNameEnum.entries.forEach { enum ->
            assertThat(enum.fileName).endsWith(".txt")
        }
    }
}