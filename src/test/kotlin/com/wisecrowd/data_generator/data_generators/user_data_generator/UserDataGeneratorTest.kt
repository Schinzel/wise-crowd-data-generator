package com.wisecrowd.data_generator.data_generators.user_data_generator

import com.wisecrowd.data_generator.data_collections.activity_level.ActivityLevel
import com.wisecrowd.data_generator.data_collections.activity_level.ActivityLevelCollection
import com.wisecrowd.data_generator.data_collections.customer_country.CustomerCountriesCollection
import com.wisecrowd.data_generator.data_collections.customer_country.CustomerCountry
import com.wisecrowd.data_generator.data_collections.investor_profile.InvestorProfile
import com.wisecrowd.data_generator.data_collections.investor_profile.InvestorProfileCollection
import com.wisecrowd.data_generator.data_generators.IDataGenerator
import com.wisecrowd.data_generator.data_generators.IDataGeneratorContractTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*
import kotlin.random.Random

class UserDataGeneratorTest : IDataGeneratorContractTest() {

    private val testStartDate = LocalDate.of(2020, 1, 1)
    private val testEndDate = LocalDate.of(2024, 12, 31)

    override fun createGenerator(): IDataGenerator {
        return UserDataGenerator(
            userCount = 5,
            simulationStartDate = testStartDate,
            simulationEndDate = testEndDate
        )
    }

    override fun createEmptyGenerator(): IDataGenerator {
        return UserDataGenerator(
            userCount = 0,  // This will cause failure but matches contract expectation
            simulationStartDate = testStartDate,
            simulationEndDate = testEndDate
        )
    }

    @Test
    fun getColumnNames_defaultConfiguration_returnsCorrectColumnNames() {
        val generator = UserDataGenerator(
            userCount = 1,
            simulationStartDate = testStartDate,
            simulationEndDate = testEndDate
        )
        
        val columnNames = generator.getColumnNames()
        
        val expectedColumns = listOf(
            "user_id", 
            "investor_profile_id", 
            "activity_level_id", 
            "country_id", 
            "join_date", 
            "departure_date", 
            "customer_status"
        )
        assertThat(columnNames).isEqualTo(expectedColumns)
    }

    @Test
    fun hasMoreRows_newGenerator_returnsTrue() {
        val generator = UserDataGenerator(
            userCount = 2,
            simulationStartDate = testStartDate,
            simulationEndDate = testEndDate
        )
        
        val hasMore = generator.hasMoreRows()
        
        assertThat(hasMore).isTrue()
    }

    @Test
    fun hasMoreRows_allUsersGenerated_returnsFalse() {
        val generator = UserDataGenerator(
            userCount = 1,
            simulationStartDate = testStartDate,
            simulationEndDate = testEndDate
        )
        
        generator.getNextRow()
        val hasMore = generator.hasMoreRows()
        
        assertThat(hasMore).isFalse()
    }

    @Test
    fun getNextRow_allUsersGenerated_throwsNoSuchElementException() {
        val generator = UserDataGenerator(
            userCount = 1,
            simulationStartDate = testStartDate,
            simulationEndDate = testEndDate
        )
        generator.getNextRow()
        
        assertThatThrownBy { generator.getNextRow() }
            .isInstanceOf(NoSuchElementException::class.java)
            .hasMessageContaining("No more rows available in generator")
    }

    @Test
    fun getNextRow_validGenerator_returnsCorrectDataTypes() {
        val generator = UserDataGenerator(
            userCount = 1,
            simulationStartDate = testStartDate,
            simulationEndDate = testEndDate
        )
        
        val row = generator.getNextRow()
        
        assertThat(row).hasSize(7)
        assertThat(row[0]).isInstanceOf(UUID::class.java)
        assertThat(row[1]).isInstanceOf(Integer::class.java)
        assertThat(row[2]).isInstanceOf(Integer::class.java)
        assertThat(row[3]).isInstanceOf(Integer::class.java)
        assertThat(row[4]).isInstanceOf(LocalDate::class.java)
        assertThat(row[5]).isInstanceOf(LocalDate::class.java)  // Now always LocalDate (sentinel or real)
        assertThat(row[6]).isInstanceOf(String::class.java)
    }

    @Test
    fun getNextRow_validGenerator_returnsValidUuidFormat() {
        val generator = UserDataGenerator(
            userCount = 1,
            simulationStartDate = testStartDate,
            simulationEndDate = testEndDate
        )
        
        val row = generator.getNextRow()
        val userId = row[0] as UUID
        
        assertThat(userId.toString()).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")
    }

    @Test
    fun getNextRow_sentinelDate_correctValue() {
        val generator = UserDataGenerator(
            userCount = 1,
            simulationStartDate = testStartDate,
            simulationEndDate = testEndDate
        )
        
        val sentinelDate = UserDataGenerator.SENTINEL_DATE
        val expectedSentinelDate = LocalDate.of(9999, 12, 31)
        
        assertThat(sentinelDate).isEqualTo(expectedSentinelDate)
    }

    @Test
    fun getNextRow_defaultCollections_returnsValidIds() {
        val generator = UserDataGenerator(
            userCount = 50,
            simulationStartDate = testStartDate,
            simulationEndDate = testEndDate
        )
        
        val investorProfileIds = mutableSetOf<Int>()
        val activityLevelIds = mutableSetOf<Int>()
        val countryIds = mutableSetOf<Int>()
        
        repeat(50) {
            val row = generator.getNextRow()
            investorProfileIds.add(row[1] as Int)
            activityLevelIds.add(row[2] as Int)
            countryIds.add(row[3] as Int)
        }
        
        assertThat(investorProfileIds).allMatch { it in 1..5 }
        assertThat(activityLevelIds).allMatch { it in 1..5 }
        assertThat(countryIds).allMatch { it in 1..5 }
    }

    @Test
    fun getNextRow_validGenerator_returnsValidCustomerStatus() {
        val generator = UserDataGenerator(
            userCount = 100,
            simulationStartDate = testStartDate,
            simulationEndDate = testEndDate
        )
        
        val statuses = mutableSetOf<String>()
        repeat(100) {
            val row = generator.getNextRow()
            val status = row[6] as String
            statuses.add(status)
        }
        
        val validStatuses = setOf("ACTIVE", "DEPARTED")
        assertThat(statuses).allMatch { it in validStatuses }
    }

    @Test
    fun getNextRow_validGenerator_joinDateWithinRange() {
        val generator = UserDataGenerator(
            userCount = 100,
            simulationStartDate = testStartDate,
            simulationEndDate = testEndDate
        )
        
        repeat(100) {
            val row = generator.getNextRow()
            val joinDate = row[4] as LocalDate
            
            assertThat(joinDate).isBetween(testStartDate, testEndDate)
        }
    }

    @Test
    fun getNextRow_validGenerator_departureDateLogic() {
        val generator = UserDataGenerator(
            userCount = 100,
            simulationStartDate = testStartDate,
            simulationEndDate = testEndDate
        )
        
        repeat(100) {
            val row = generator.getNextRow()
            val joinDate = row[4] as LocalDate
            val departureDate = row[5] as LocalDate
            val status = row[6] as String
            
            if (status == "DEPARTED") {
                // Departed users have real departure dates
                assertThat(departureDate).isNotEqualTo(UserDataGenerator.SENTINEL_DATE)
                assertThat(departureDate).isAfter(joinDate)
                assertThat(departureDate).isBetween(joinDate.plusDays(1), testEndDate)
            } else {
                // Active users have sentinel date
                assertThat(departureDate).isEqualTo(UserDataGenerator.SENTINEL_DATE)
                assertThat(status).isEqualTo("ACTIVE")
            }
        }
    }

    @Nested
    inner class CustomerLifecycleDistribution {
        
        @Test
        fun `getNextRow _ large sample _ approximately 30 percent join after start`() {
            val generator = UserDataGenerator(
                userCount = 1000,
                simulationStartDate = testStartDate,
                simulationEndDate = testEndDate,
                customerJoinDistribution = 30.0,
                random = Random(42)
            )
            
            var joinedAfterStart = 0
            repeat(1000) {
                val row = generator.getNextRow()
                val joinDate = row[4] as LocalDate
                if (joinDate.isAfter(testStartDate)) {
                    joinedAfterStart++
                }
            }
            
            val joinRatio = joinedAfterStart.toDouble() / 1000
            assertThat(joinRatio).isBetween(0.25, 0.35)
        }
        
        @Test
        fun `getNextRow _ large sample _ approximately 20 percent departed`() {
            val generator = UserDataGenerator(
                userCount = 1000,
                simulationStartDate = testStartDate,
                simulationEndDate = testEndDate,
                customerDepartureRate = 20.0,
                random = Random(42)
            )
            
            var departedCount = 0
            repeat(1000) {
                val row = generator.getNextRow()
                val status = row[6] as String
                if (status == "DEPARTED") {
                    departedCount++
                }
            }
            
            val departureRatio = departedCount.toDouble() / 1000
            assertThat(departureRatio).isBetween(0.15, 0.25)
        }
    }

    @Nested
    inner class ConstructorValidation {
        
        @Test
        fun `constructor _ negative user count _ throws IllegalArgumentException`() {
            assertThatThrownBy {
                UserDataGenerator(
                    userCount = -5,
                    simulationStartDate = testStartDate,
                    simulationEndDate = testEndDate
                )
            }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("User count must be non-negative, but was: -5")
        }
        
        @Test
        fun `constructor _ end date before start date _ throws IllegalArgumentException`() {
            val endBeforeStart = testStartDate.minusDays(1)
            
            assertThatThrownBy {
                UserDataGenerator(
                    userCount = 1,
                    simulationStartDate = testStartDate,
                    simulationEndDate = endBeforeStart
                )
            }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("End date ($endBeforeStart) cannot be before start date ($testStartDate)")
        }
        
        @Test
        fun `constructor _ invalid customer join distribution _ throws IllegalArgumentException`() {
            assertThatThrownBy {
                UserDataGenerator(
                    userCount = 1,
                    simulationStartDate = testStartDate,
                    simulationEndDate = testEndDate,
                    customerJoinDistribution = 150.0
                )
            }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Customer join distribution must be between 0 and 100, but was: 150.0")
        }
        
        @Test
        fun `constructor _ invalid customer departure rate _ throws IllegalArgumentException`() {
            assertThatThrownBy {
                UserDataGenerator(
                    userCount = 1,
                    simulationStartDate = testStartDate,
                    simulationEndDate = testEndDate,
                    customerDepartureRate = -10.0
                )
            }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Customer departure rate must be between 0 and 100, but was: -10.0")
        }
        
        @Test
        fun `constructor _ empty investor profile collection _ throws IllegalArgumentException`() {
            val emptyInvestorProfiles = InvestorProfileCollection()
            
            assertThatThrownBy {
                UserDataGenerator(
                    userCount = 1,
                    simulationStartDate = testStartDate,
                    simulationEndDate = testEndDate,
                    investorProfileCollection = emptyInvestorProfiles
                )
            }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Investor profile collection cannot be empty")
        }
    }

    @Test
    fun getNextRow_customCollections_returnsValidCustomIds() {
        val customInvestorProfiles = InvestorProfileCollection()
        customInvestorProfiles.addInvestorProfile(
            InvestorProfile(10, "Custom Profile", "Test description", 100.0)
        )
        
        val customActivityLevels = ActivityLevelCollection()
        customActivityLevels.addActivityLevel(
            ActivityLevel(20, "Custom Activity", "Test description", 100.0)
        )
        
        val customCountries = CustomerCountriesCollection()
        customCountries.addCountry(
            CustomerCountry(30, "Custom Country", "CC", "Test description", 100.0)
        )
        
        val generator = UserDataGenerator(
            userCount = 5,
            simulationStartDate = testStartDate,
            simulationEndDate = testEndDate,
            investorProfileCollection = customInvestorProfiles,
            activityLevelCollection = customActivityLevels,
            customerCountriesCollection = customCountries
        )
        
        repeat(5) {
            val row = generator.getNextRow()
            val investorProfileId = row[1] as Int
            val activityLevelId = row[2] as Int
            val countryId = row[3] as Int
            
            assertThat(investorProfileId).isEqualTo(10)
            assertThat(activityLevelId).isEqualTo(20)
            assertThat(countryId).isEqualTo(30)
        }
    }

    @Test
    fun getNextRow_multipleGenerations_generatesUniqueUserIds() {
        val generator = UserDataGenerator(
            userCount = 20,
            simulationStartDate = testStartDate,
            simulationEndDate = testEndDate
        )
        
        val userIds = mutableSetOf<UUID>()
        repeat(20) {
            val row = generator.getNextRow()
            val userId = row[0] as UUID
            userIds.add(userId)
        }
        
        assertThat(userIds).hasSize(20)
    }

    @Test
    fun getNextRow_specifiedUserCount_generatesExactCount() {
        val userCount = 15
        val generator = UserDataGenerator(
            userCount = userCount,
            simulationStartDate = testStartDate,
            simulationEndDate = testEndDate
        )
        
        var generatedCount = 0
        while (generator.hasMoreRows()) {
            generator.getNextRow()
            generatedCount++
        }
        
        assertThat(generatedCount).isEqualTo(userCount)
    }

    @Test
    fun getNextRow_singleDayPeriod_validDates() {
        val singleDay = LocalDate.of(2023, 6, 15)
        val generator = UserDataGenerator(
            userCount = 10,
            simulationStartDate = singleDay,
            simulationEndDate = singleDay
        )
        
        repeat(10) {
            val row = generator.getNextRow()
            val joinDate = row[4] as LocalDate
            val departureDate = row[5] as LocalDate
            val status = row[6] as String
            
            assertThat(joinDate).isEqualTo(singleDay)
            if (status == "DEPARTED") {
                assertThat(departureDate).isEqualTo(singleDay)
            } else {
                assertThat(departureDate).isEqualTo(UserDataGenerator.SENTINEL_DATE)
            }
        }
    }
}
