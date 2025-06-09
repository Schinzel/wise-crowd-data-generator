package com.wisecrowd.data_generator.data_generators.transaction_data_generator

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class PriceDataTest {
    @Nested
    inner class Constructor {
        @Test
        fun constructor_validData_createsObjectWithCorrectProperties() {
            val assetId = UUID.randomUUID()
            val date = LocalDate.of(2024, 1, 1)
            val price = 100.0

            val priceData = PriceData(assetId, date, price)

            assertThat(priceData.assetId).isEqualTo(assetId)
            assertThat(priceData.date).isEqualTo(date)
            assertThat(priceData.price).isEqualTo(price)
        }

        @Test
        fun constructor_negativePrice_throwsIllegalArgumentException() {
            val assetId = UUID.randomUUID()
            val date = LocalDate.of(2024, 1, 1)
            val negativePrice = -10.0

            assertThatThrownBy {
                PriceData(assetId, date, negativePrice)
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Price must be positive, but was: -10.0")
        }

        @Test
        fun constructor_zeroPrice_throwsIllegalArgumentException() {
            val assetId = UUID.randomUUID()
            val date = LocalDate.of(2024, 1, 1)
            val zeroPrice = 0.0

            assertThatThrownBy {
                PriceData(assetId, date, zeroPrice)
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Price must be positive, but was: 0.0")
        }
    }
}
