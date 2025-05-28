package com.wisecrowd.data_generator.data_collections.market_trend

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * The purpose of this class is to manage a collection of market trends and
 * provide functionality to access and filter them.
 *
 * Written by Claude 3.7 Sonnet
 */
class MarketTrendCollection {
    private val trends = mutableListOf<MarketTrend>()
    
    /**
     * Adds a market trend to the collection
     * 
     * @param trend The market trend to add
     */
    fun add(trend: MarketTrend) {
        trends.add(trend)
    }
    
    /**
     * Adds multiple market trends to the collection
     * 
     * @param trendList The list of market trends to add
     */
    fun addAll(trendList: List<MarketTrend>) {
        trends.addAll(trendList)
    }
    
    /**
     * Gets all market trends in the collection
     * 
     * @return Immutable list of all market trends
     */
    fun getAll(): List<MarketTrend> = trends.toList()
    
    /**
     * Gets the number of market trends in the collection
     * 
     * @return The count of market trends
     */
    fun size(): Int = trends.size
    
    /**
     * Checks if the collection is empty
     * 
     * @return True if there are no market trends, false otherwise
     */
    fun isEmpty(): Boolean = trends.isEmpty()
    
    /**
     * Gets market trends that include the specified date
     * 
     * @param date The date to check
     * @return List of trends that include the date
     */
    fun getTrendsOnDate(date: LocalDate): List<MarketTrend> {
        return trends.filter { trend ->
            date >= trend.startDate && date <= trend.endDate
        }
    }
    
    /**
     * Gets market trends within the specified date range
     * 
     * @param startDate The start of the date range
     * @param endDate The end of the date range
     * @return List of trends that overlap with the date range
     */
    fun getTrendsInRange(startDate: LocalDate, endDate: LocalDate): List<MarketTrend> {
        require(endDate >= startDate) { "End date cannot be before start date" }
        
        return trends.filter { trend ->
            // Trend overlaps with the date range if:
            // 1. Trend start date is within the range, or
            // 2. Trend end date is within the range, or
            // 3. Trend completely encompasses the range
            (trend.startDate >= startDate && trend.startDate <= endDate) ||
            (trend.endDate >= startDate && trend.endDate <= endDate) ||
            (trend.startDate <= startDate && trend.endDate >= endDate)
        }
    }
    
    /**
     * Creates default Swedish/Nordic market trend data from 1990-2025
     * as specified in the design document
     * 
     * @return A populated MarketTrendCollection
     */
    companion object {
        private val DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        
        fun createDefaultCollection(): MarketTrendCollection {
            val collection = MarketTrendCollection()
            
            // Add historical market trends from the design document
            collection.addAll(listOf(
                MarketTrend(
                    startDateString = "1990-01-01",
                    endDateString = "1992-11-01",
                    trendType = "Bear",
                    strength = 1.8,
                    description = "Nordic banking crisis"
                ),
                MarketTrend(
                    startDateString = "1992-11-21",
                    endDateString = "1995-01-05",
                    trendType = "Recovery",
                    strength = 1.3,
                    description = "Post-crisis restructuring"
                ),
                MarketTrend(
                    startDateString = "1995-01-06",
                    endDateString = "1997-08-19",
                    trendType = "Bull",
                    strength = 1.4,
                    description = "EU membership boost"
                ),
                MarketTrend(
                    startDateString = "1997-08-19",
                    endDateString = "1999-10-11",
                    trendType = "Correction",
                    strength = -0.7,
                    description = "Asian financial crisis impact"
                ),
                MarketTrend(
                    startDateString = "1998-10-11",
                    endDateString = "2000-03-10",
                    trendType = "Bull",
                    strength = 1.8,
                    description = "Dot-com boom"
                ),
                MarketTrend(
                    startDateString = "2000-03-11",
                    endDateString = "2002-10-10",
                    trendType = "Bear",
                    strength = 1.7,
                    description = "Tech bubble burst"
                ),
                MarketTrend(
                    startDateString = "2002-10-10",
                    endDateString = "2007-07-17",
                    trendType = "Bull",
                    strength = 1.5,
                    description = "Global expansion"
                ),
                MarketTrend(
                    startDateString = "2007-07-17",
                    endDateString = "2009-03-09",
                    trendType = "Bear",
                    strength = -2.3,
                    description = "Financial crisis"
                ),
                MarketTrend(
                    startDateString = "2009-03-10",
                    endDateString = "2011-07-21",
                    trendType = "Bull",
                    strength = 1.6,
                    description = "Recovery phase"
                ),
                MarketTrend(
                    startDateString = "2011-07-22",
                    endDateString = "2012-05-04",
                    trendType = "Correction",
                    strength = -0.8,
                    description = "Eurozone debt crisis"
                ),
                MarketTrend(
                    startDateString = "2012-06-05",
                    endDateString = "2015-04-15",
                    trendType = "Bull",
                    strength = 1.3,
                    description = "QE-driven growth"
                ),
                MarketTrend(
                    startDateString = "2015-04-16",
                    endDateString = "2016-02-11",
                    trendType = "Correction",
                    strength = -0.6,
                    description = "China slowdown fears"
                ),
                MarketTrend(
                    startDateString = "2016-02-12",
                    endDateString = "2018-01-26",
                    trendType = "Bull",
                    strength = 1.4,
                    description = "Synchronized global growth"
                ),
                MarketTrend(
                    startDateString = "2018-01-27",
                    endDateString = "2018-12-24",
                    trendType = "Correction",
                    strength = -0.7,
                    description = "Trade war concerns"
                ),
                MarketTrend(
                    startDateString = "2018-12-25",
                    endDateString = "2020-02-19",
                    trendType = "Bull",
                    strength = 1.2,
                    description = "Late-cycle growth"
                ),
                MarketTrend(
                    startDateString = "2020-02-20",
                    endDateString = "2020-03-23",
                    trendType = "Crash",
                    strength = -2.1,
                    description = "COVID-19 pandemic"
                ),
                MarketTrend(
                    startDateString = "2020-03-24",
                    endDateString = "2021-11-08",
                    trendType = "Bull",
                    strength = 1.7,
                    description = "Stimulus recovery"
                ),
                MarketTrend(
                    startDateString = "2021-11-09",
                    endDateString = "2022-09-30",
                    trendType = "Bear",
                    strength = -1.5,
                    description = "Inflationary fears"
                ),
                MarketTrend(
                    startDateString = "2022-10-01",
                    endDateString = "2023-07-31",
                    trendType = "Recovery",
                    strength = 1.2,
                    description = "Disinflation hopes"
                ),
                MarketTrend(
                    startDateString = "2023-08-01",
                    endDateString = "2024-03-25",
                    trendType = "Sideways",
                    strength = 0.2,
                    description = "Soft landing uncertainty"
                ),
                MarketTrend(
                    startDateString = "2024-03-26",
                    endDateString = "2025-06-30",
                    trendType = "Bull",
                    strength = 1.1,
                    description = "Current phase"
                )
            ))
            
            return collection
        }
    }
}