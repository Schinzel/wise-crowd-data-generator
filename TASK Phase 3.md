# WiseCrowd Data Generator - Phase 3: Data Generators

# The purpose
The purpose of this document is to describe the problem and the solution to an AI that then can do the changes. 

# Instructions

* Implement one task at a time
* Follow the code standard. Read the file README.md in the project root and follow its links to understand the project, the stack, code standard and how testing is to be done.
* All tasks must follow project standards, use defensive programming principles, and include comprehensive unit tests unless explicitly stated otherwise.
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
## Phase 3 - Task 1 - Weighted Random Selector
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

### Acceptance Criteria
1. Takes list of weighted items and returns random selection based on weights
2. Supports both percentage weights (25.0) and raw weights
3. Injectable Random instance for deterministic testing
4. Handles edge cases (empty lists, zero weights, etc.)

### Task Summary (to be completed by AI)

## Phase 3 - Task 2 - IDataGenerator Interface
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
4. Designed for use with FileDataSaver

### Task Summary (to be completed by AI)

## Phase 3 - Task 3 - Data Generation Service
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

### Task Summary (to be completed by AI)

## Phase 3 - Task 4 - Asset Namer
Create AssetNamer utility that generates realistic asset names based on AssetClass.

### Description
Implement a utility that takes an AssetClass instance and returns realistic Swedish/Nordic asset names following the naming principles in design.md.

### Deliverables
- AssetNamer class
- Integration with AssetClass

### Acceptance Criteria
1. Takes AssetClass as input and returns realistic asset name
2. Uses naming components from design.md:
   - Providers: Swedbank, SEB, Nordea, Handelsbanken, Länsförsäkringar
   - Regions: Sweden, Nordic, Europe, Global, Asia, US
   - Sectors: Tech, Health, Finance, Energy, Real Estate
   - Fund types based on asset class (Index/Active for equity, Government/Corporate for bonds, etc.)
3. Generates names like "SEB Nordic Tech Active" or "Nordea European Corporate Bond"

### Task Summary (to be completed by AI)

## Phase 3 - Task 5 - Asset Generator → `asset_data.txt`
Create AssetDataGenerator that implements IDataGenerator to generate asset data.

### Description
Implement a generator that creates realistic asset data based on asset classes and their prevalence percentages.

### Uses
- AssetClassCollection
- AssetNamer

### Deliverables
- AssetDataGenerator class
- Integration with existing data collections and AssetNamer

### Acceptance Criteria
1. Implements IDataGenerator interface
2. Generates assets distributed according to asset class prevalence percentages
3. Uses AssetNamer to create realistic asset names

### Task Summary (to be completed by AI)

## Phase 3 - Task 6 - Price Series Generator → `price_series.txt`
Create PriceSeriesDataGenerator that implements IDataGenerator to generate historical price data.

### Description
Implement a generator that creates realistic price series data influenced by market trends and asset volatility. Takes assets generated by the Asset Generator and creates historical price movements.

### Uses
- MarketTrendCollection
- Asset Generator output

### Deliverables
- PriceSeriesDataGenerator class
- Integration with MarketTrendCollection and Asset Generator output

### Acceptance Criteria
1. Implements IDataGenerator interface
2. Generates price series that reflect market trends and asset volatility
3. Uses Asset Generator output to determine which assets need price data
4. Creates realistic price movements and historical patterns based on MarketTrendCollection

### Task Summary (to be completed by AI)
