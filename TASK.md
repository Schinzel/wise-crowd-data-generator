# WiseCrowd Data Generator - Phase 2: Data Generators

# The purpose
The purpose of this document is to describe the problem and the solution to an AI that then can do the changes. 

# Instructions

* Implement one task at a time
* Follow the code standard. Read the file README.md in the project root and follow its links to understand the project, the stack, code standard and how testing is to be done.
* Explain your approach for each task
* When you are done with a task
  * run below commands to verify that things still work
    * mvn clean install \-DskipTests
    * mvn test
  * Fill out the *Task Summary (to be completed by AI)* for the task you just completed
    * Be concise and to the point
    * Include major changes made and files affected
    * Document key decisions and their rationale
    * Highlight any items that might affect future tasks
  * Fill out *Current Implementation Status (to be completed by AI)* and add which tasks have been completed. Simply add a row with *Task X done - YYYY-MM-DD*.
* Let me know when you've completed a task so I can verify it before moving to the next one

# User story

As a developer   
I want a tool that can generate mocked data   
So that I can develop the WiseCrowd platform using data of different sizes and configurations 

# Business values
* by generating large data sets we can test the performance for different scenarios  
* we can derive asset allocation indices and verify that these are as we expect

# Solution Architecture
The system generates mocked data files for the WiseCrowd platform. Foundation components (data collections, file saver) are complete. Next phase focuses on implementing data generators that use these components to create realistic datasets.

## Component Integration Pattern
The system uses a DataGenerationService to orchestrate the interaction between IDataGenerator and IDataSaver:
- IDataGenerator: Handles data generation logic
- IDataSaver: Handles file output operations  
- DataGenerationService: Coordinates the generate-and-save workflow
This ensures single responsibility and testability.

## Package Structure
All data generators must be placed in the `data_generators` package:
- `com.wisecrowd.data_generator.data_generators` for generator implementations
- Follow the existing pattern established with `data_collections` package

# Resulting files
The result of running the code be the a set of text files:
- asset_data.txt
- users.txt
- price_series.txt
- transactions.txt
- user_holdings.txt

# Current Implementation Status (to be completed by AI)
Phase 1 Complete - Foundation built with data collections and file saving capabilities. Ready for Phase 2: Data Generators implementation.

# Tasks

## Task 8 - Update FileNameEnum
Update the existing FileNameEnum to include user_holdings.txt file name.

### Description
Add the missing USER_HOLDINGS entry to FileNameEnum to support the complete set of 5 output files as specified in design.md.

### Deliverables
- Updated FileNameEnum with USER_HOLDINGS entry
- Updated unit tests to cover the new enum entry

### Acceptance Criteria
1. Add USER_HOLDINGS("user_holdings.txt") to FileNameEnum
2. Maintain existing entries unchanged
3. Update corresponding unit tests
4. Verify all tests still pass
5. Code follows project standards

### Task Summary (to be completed by AI)

## Task 8.5 - Customer Countries Collection
Create CustomerCountriesCollection to support realistic Nordic customer distribution for banking simulation.

### Description
Implement a data collection that represents the Nordic countries where bank customers are located. This provides realistic customer demographics for the Swedish bank simulation, following the same patterns as other collection classes.

### Collection Data (from design.md)
| ID | Name | Country Code | Description | Distribution (%) |
|----|------|-------------|-------------|------------------|
| 1  | Sweden | SE | Home market with largest customer base | 60 |
| 2  | Norway | NO | Oil wealth economy with active investors | 15 |
| 3  | Denmark | DK | Strong financial sector and banking ties | 12 |
| 4  | Finland | FI | Tech-savvy market with Nordic connections | 8 |
| 5  | Iceland | IS | Small but wealthy per capita customer base | 5 |

### Deliverables
- CustomerCountry data class
- CustomerCountriesCollection class with predefined Nordic data
- Comprehensive unit tests
- Integration with existing collection patterns

### Acceptance Criteria
1. Follow existing collection patterns (similar to CurrencyCollection and ActivityLevelCollection)
2. Place in `data_collections/customer_country/` package
3. CustomerCountry data class with id, name, countryCode, description, distributionPercentage
4. CustomerCountriesCollection with filtering and lookup methods:
   - getAll() method
   - getByDistribution() method for weighted selection
   - getByCountryCode() method for lookup
5. Predefined Nordic countries data matching design.md table
6. Comprehensive unit tests covering all methods and edge cases
7. Follows defensive programming principles (validation, error handling)
8. Code follows project standards

### Task Summary (to be completed by AI)

## Task 9 - Weighted Random Selector
Create a centralized WeightedRandomSelector utility to handle randomized distribution across all collections.

### Description
Implement a reusable utility that handles weighted random selection for asset classes, investor profiles, activity levels, currencies, and other distribution-based collections. This eliminates code duplication and provides consistent randomization logic.

### Design
```kotlin
interface IWeightedRandomSelector<T> {
    fun getRandomItem(): T
}

class WeightedRandomSelector<T>(
    private val items: List<WeightedItem<T>>,
    private val random: Random = Random.Default
) : IWeightedRandomSelector<T>

data class WeightedItem<T>(
    val item: T,
    val weight: Double  // Can be percentage like 25.0 or raw weight
)
```

### Deliverables
- IWeightedRandomSelector interface
- WeightedRandomSelector implementation
- WeightedItem data class
- Comprehensive unit tests

### Acceptance Criteria
1. Takes list of weighted items and returns random selection based on weights
2. Supports both percentage weights (25.0) and raw weights
3. Injectable Random instance for deterministic testing
4. Handles edge cases (empty lists, zero weights, etc.)
5. Follows defensive programming principles
6. Includes comprehensive unit tests covering all scenarios
7. Code follows project standards

### Task Summary (to be completed by AI)

## Task 10 - IDataGenerator Interface
Create the IDataGenerator interface that all data generators will implement.

### Description
Define the contract for all data generators following the same patterns as IDataSaver with prepare, generate, and complete phases.

### Deliverables
- IDataGenerator interface
- Proper documentation and examples
- Integration with existing patterns

### Acceptance Criteria
1. Follows the same pattern as IDataSaver (prepare, generate items, complete)
2. Includes proper error handling mechanisms
3. Provides clear method contracts and documentation
4. Follows project code standards
5. Designed for use with FileDataSaver

### Task Summary (to be completed by AI)

## Task 11 - Data Generation Service
Create DataGenerationService that orchestrates IDataGenerator and IDataSaver to test the complete workflow.

### Description
Implement the orchestration service that combines data generation and file saving, allowing us to test the entire design with actual asset data generation.

### Deliverables
- DataGenerationService class
- Unit tests with mock IDataGenerator implementations
- Integration tests with FileDataSaver

### Acceptance Criteria
1. Takes IDataGenerator and IDataSaver as constructor parameters
2. Orchestrates the complete generate-and-save workflow (prepare → generate items → save items → complete)
3. Handles errors from both generator and saver components
4. Includes comprehensive unit tests with mock IDataGenerator implementations
5. Includes integration tests with FileDataSaver
6. Code follows project standards

### Task Summary (to be completed by AI)

## Task 12 - Asset Namer
Create AssetNamer utility that generates realistic asset names based on AssetClass.

### Description
Implement a utility that takes an AssetClass instance and returns realistic Swedish/Nordic asset names following the naming principles in design.md.

### Deliverables
- AssetNamer class
- Comprehensive unit tests
- Integration with AssetClass

### Acceptance Criteria
1. Takes AssetClass as input and returns realistic asset name
2. Uses naming components from design.md:
   - Providers: Swedbank, SEB, Nordea, Handelsbanken, Länsförsäkringar
   - Regions: Sweden, Nordic, Europe, Global, Asia, US
   - Sectors: Tech, Health, Finance, Energy, Real Estate
   - Fund types based on asset class (Index/Active for equity, Government/Corporate for bonds, etc.)
3. Generates names like "SEB Nordic Tech Active" or "Nordea European Corporate Bond"
4. Includes comprehensive unit tests covering all asset classes
5. Code follows project standards

### Task Summary (to be completed by AI)

## Task 13 - Asset Generator
Create AssetDataGenerator that implements IDataGenerator to generate asset data using AssetClassCollection and AssetNamer.

### Description
Implement a generator that creates realistic asset data based on asset classes and their prevalence percentages.

### Deliverables
- AssetDataGenerator class
- Comprehensive unit tests
- Integration with existing data collections and AssetNamer

### Acceptance Criteria
1. Implements IDataGenerator interface
2. Generates assets distributed according to asset class prevalence percentages
3. Uses AssetNamer to create realistic asset names
4. Follows defensive programming principles
5. Includes comprehensive unit tests
6. Code follows project standards

### Task Summary (to be completed by AI)

## Task 14 - User Generator
Create UserDataGenerator that implements IDataGenerator to generate user data using InvestorProfileCollection, ActivityLevelCollection, and CustomerCountriesCollection.

### Description
Implement a generator that creates realistic user data with proper distribution of investor profiles, activity levels, and customer countries. Includes customer lifecycle management with join/departure dates and customer status tracking.

### Parameters
- Number of users
- customerJoinDistribution (e.g., 30% join after simulation start date)
- customerDepartureRate (e.g., 20% leave before simulation end date)
- simulationStartDate and simulationEndDate for determining join/departure date ranges

### Output
- user_id
- investor_profile_id
- activity_level_id
- country_id
- join_date (when customer became active)
- departure_date (when customer left, null if still active)
- customer_status (ACTIVE, DEPARTED)

### Customer Lifecycle Logic
- 30% of users have join_date after simulationStartDate (spread across entire simulation range)
- 20% of users have departure_date before simulationEndDate with customer_status = DEPARTED
- Remaining users start at simulationStartDate and remain ACTIVE throughout
- Departed customers trigger instant sell-off transactions in Transaction Generator

### Deliverables
- UserDataGenerator class
- Comprehensive unit tests
- Integration with existing data collections

### Acceptance Criteria
1. Implements IDataGenerator interface
2. Generates users distributed according to investor profile, activity level, and customer country percentages
3. Implements customer lifecycle logic with join/departure dates and status tracking
4. Handles customerJoinDistribution and customerDepartureRate parameters
5. Creates realistic user demographics with proper date ranges
6. Follows defensive programming principles
7. Includes comprehensive unit tests covering all lifecycle scenarios
8. Code follows project standards

### Task Summary (to be completed by AI)

## Task 15 - Price Series Generator
Create PriceSeriesDataGenerator that implements IDataGenerator to generate historical price data using MarketTrendCollection.

### Description
Implement a generator that creates realistic price series data influenced by market trends and asset volatility.

### Deliverables
- PriceSeriesDataGenerator class
- Comprehensive unit tests
- Integration with market trends and asset data

### Acceptance Criteria
1. Implements IDataGenerator interface
2. Generates price series that reflect market trends and asset volatility
3. Creates realistic price movements and historical patterns
4. Follows defensive programming principles
5. Includes comprehensive unit tests
6. Code follows project standards

### Task Summary (to be completed by AI)

## Task 16 - Transaction Generator
Create TransactionDataGenerator that implements IDataGenerator to generate transaction data using price series, users, and CurrencyCollection.

### Description
Implement a generator that creates realistic transaction data with proper currency distribution and user activity patterns. Includes special handling for departed customers who trigger instant sell-off transactions.

### Customer Lifecycle Integration
- Process users with DEPARTED status to generate sell-off transactions
- Departed customers sell all holdings on their departure_date
- Regular ACTIVE customers follow normal activity patterns based on their activity_level

### Deliverables
- TransactionDataGenerator class
- Comprehensive unit tests
- Integration with price series, users, and currencies

### Acceptance Criteria
1. Implements IDataGenerator interface
2. Generates transactions distributed according to currency and user activity patterns
3. Handles departed customer sell-off logic using customer_status and departure_date
4. Creates realistic transaction amounts, dates, and types based on price series
5. Processes both ACTIVE and DEPARTED customer scenarios
6. Follows defensive programming principles
7. Includes comprehensive unit tests covering all customer lifecycle scenarios
8. Code follows project standards

### Task Summary (to be completed by AI)

## Task 17 - User Holdings Generator
Create UserHoldingsDataGenerator that implements IDataGenerator to generate user holdings data from transaction history.

### Description
Implement a generator that calculates current user portfolio positions by processing all transactions and computing net holdings for each user-asset combination.

### Deliverables
- UserHoldingsDataGenerator class
- Comprehensive unit tests
- Integration with transaction data

### Acceptance Criteria
1. Implements IDataGenerator interface
2. Takes transaction data as input and calculates net positions
3. Aggregates buy/sell transactions to determine current holdings
4. Handles multiple currencies properly
5. Generates realistic user_holdings.txt output
6. Follows defensive programming principles
7. Includes comprehensive unit tests
8. Code follows project standards

### Task Summary (to be completed by AI)
