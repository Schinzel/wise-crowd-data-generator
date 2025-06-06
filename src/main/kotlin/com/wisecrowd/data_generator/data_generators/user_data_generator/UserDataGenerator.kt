package com.wisecrowd.data_generator.data_generators.user_data_generator

import com.wisecrowd.data_generator.data_collections.activity_level.ActivityLevelCollection
import com.wisecrowd.data_generator.data_collections.customer_country.CustomerCountriesCollection
import com.wisecrowd.data_generator.data_collections.investor_profile.InvestorProfileCollection
import com.wisecrowd.data_generator.data_generators.IDataGenerator
import com.wisecrowd.data_generator.utils.WeightedItem
import com.wisecrowd.data_generator.utils.WeightedRandomSelector
import java.time.LocalDate
import java.util.*
import kotlin.random.Random

/**
 * The purpose of this class is to generate realistic user data distributed
 * according to investor profile, activity level, and customer country percentages.
 *
 * This generator implements IDataGenerator to produce users.txt file contents with
 * proper customer lifecycle management including join/departure dates and status tracking.
 * Users are distributed based on the design specification percentages and follow
 * realistic behavioral patterns for a Nordic financial platform.
 *
 * Customer Lifecycle Logic:
 * - 30% of users have join_date after simulationStartDate (spread across simulation range)
 * - 20% of users have departure_date before simulationEndDate with DEPARTED status
 * - Remaining users start at simulationStartDate and remain ACTIVE throughout
 * - Uses sentinel date (9999-12-31) for users who never departed
 *
 * Written by Claude Sonnet 4
 */
class UserDataGenerator(
    private val userCount: Int,
    private val simulationStartDate: LocalDate,
    private val simulationEndDate: LocalDate,
    private val customerJoinDistribution: Double = 30.0,
    private val customerDepartureRate: Double = 20.0,
    investorProfileCollection: InvestorProfileCollection = 
        InvestorProfileCollection.createDefaultCollection(),
    activityLevelCollection: ActivityLevelCollection = 
        ActivityLevelCollection.createDefaultCollection(),
    customerCountriesCollection: CustomerCountriesCollection = 
        CustomerCountriesCollection.createDefaultCollection(),
    private val random: Random = Random.Default
) : IDataGenerator {

    companion object {
        // Sentinel date representing "never departed" 
        val SENTINEL_DATE: LocalDate = LocalDate.of(9999, 12, 31)
    }

    private var currentIndex = 0
    private val investorProfileSelector: WeightedRandomSelector<Int>
    private val activityLevelSelector: WeightedRandomSelector<Int>
    private val countrySelector: WeightedRandomSelector<Int>

    init {
        require(userCount >= 0) { "User count must be non-negative, but was: $userCount" }
        require(simulationEndDate >= simulationStartDate) { 
            "End date ($simulationEndDate) cannot be before start date ($simulationStartDate)" 
        }
        require(customerJoinDistribution >= 0.0 && customerJoinDistribution <= 100.0) {
            "Customer join distribution must be between 0 and 100, but was: $customerJoinDistribution"
        }
        require(customerDepartureRate >= 0.0 && customerDepartureRate <= 100.0) {
            "Customer departure rate must be between 0 and 100, but was: $customerDepartureRate"
        }

        // Only initialize selectors if we have users to generate
        if (userCount > 0) {
            require(!investorProfileCollection.getAllInvestorProfiles().isEmpty()) { 
                "Investor profile collection cannot be empty" 
            }
            require(!activityLevelCollection.getAllActivityLevels().isEmpty()) { 
                "Activity level collection cannot be empty" 
            }
            require(!customerCountriesCollection.getAll().isEmpty()) { 
                "Customer countries collection cannot be empty" 
            }

            // Create weighted selectors using distribution percentages
            val investorProfileItems = investorProfileCollection.getAllInvestorProfiles().map { profile ->
                WeightedItem(profile.id, profile.distributionPercentage)
            }
            investorProfileSelector = WeightedRandomSelector(investorProfileItems, random)

            val activityLevelItems = activityLevelCollection.getAllActivityLevels().map { level ->
                WeightedItem(level.id, level.distributionPercentage)
            }
            activityLevelSelector = WeightedRandomSelector(activityLevelItems, random)

            val countryItems = customerCountriesCollection.getAll().map { country ->
                WeightedItem(country.id, country.distributionPercentage)
            }
            countrySelector = WeightedRandomSelector(countryItems, random)
        } else {
            // For empty generators, create dummy selectors that will never be used
            investorProfileSelector = WeightedRandomSelector(listOf(WeightedItem(1, 100.0)), random)
            activityLevelSelector = WeightedRandomSelector(listOf(WeightedItem(1, 100.0)), random)
            countrySelector = WeightedRandomSelector(listOf(WeightedItem(1, 100.0)), random)
        }
    }

    override fun getColumnNames(): List<String> {
        return listOf(
            "user_id", 
            "investor_profile_id", 
            "activity_level_id", 
            "country_id", 
            "join_date", 
            "departure_date", 
            "customer_status"
        )
    }

    override fun hasMoreRows(): Boolean {
        return currentIndex < userCount
    }

    override fun getNextRow(): List<Any> {
        if (!hasMoreRows()) {
            throw NoSuchElementException("No more rows available in generator")
        }

        currentIndex++

        // Generate user attributes using weighted selection
        val investorProfileId = investorProfileSelector.getRandomItem()
        val activityLevelId = activityLevelSelector.getRandomItem()
        val countryId = countrySelector.getRandomItem()

        // Generate customer lifecycle attributes
        val customerLifecycle = generateCustomerLifecycle()

        return arrayListOf<Any>(
            UUID.randomUUID(),                  // user_id (UUID)
            investorProfileId,                  // investor_profile_id (Int)
            activityLevelId,                    // activity_level_id (Int)
            countryId,                          // country_id (Int)
            customerLifecycle.joinDate,         // join_date (LocalDate)
            customerLifecycle.departureDate,    // departure_date (LocalDate - sentinel date for active users)
            customerLifecycle.status.name       // customer_status (String)
        )
    }

    /**
     * Generates customer lifecycle attributes following the design specification:
     * - 30% join after simulation start (spread across simulation range)
     * - 20% depart before simulation end (DEPARTED status)
     * - Remaining are active throughout (start at simulation start, ACTIVE status)
     * - Uses sentinel date (9999-12-31) for users who never departed
     */
    private fun generateCustomerLifecycle(): CustomerLifecycle {
        val joinPercentage = random.nextDouble() * 100.0
        val departurePercentage = random.nextDouble() * 100.0

        val joinDate = if (joinPercentage <= customerJoinDistribution && simulationEndDate > simulationStartDate) {
            // 30% join after simulation start - only if simulation period has multiple days
            generateRandomDateBetween(simulationStartDate.plusDays(1), simulationEndDate)
        } else {
            // 70% start at simulation beginning (or all if single day period)
            simulationStartDate
        }

        val (departureDate, customerStatus) = if (departurePercentage <= customerDepartureRate && simulationEndDate > joinDate) {
            // 20% depart before simulation end - only if there are days after join date
            val departure = generateRandomDateBetween(joinDate.plusDays(1), simulationEndDate)
            Pair(departure, CustomerStatus.DEPARTED)
        } else {
            // 80% remain active throughout simulation - use sentinel date
            Pair(SENTINEL_DATE, CustomerStatus.ACTIVE)
        }

        return CustomerLifecycle(joinDate, departureDate, customerStatus)
    }

    /**
     * Generates a random date between the given start and end dates (inclusive)
     */
    private fun generateRandomDateBetween(startDate: LocalDate, endDate: LocalDate): LocalDate {
        val daysBetween = startDate.until(endDate).days
        if (daysBetween <= 0) {
            return startDate
        }
        val randomDays = random.nextInt(daysBetween + 1)
        return startDate.plusDays(randomDays.toLong())
    }
}
