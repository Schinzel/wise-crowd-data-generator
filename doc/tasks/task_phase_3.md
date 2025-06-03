# Phase 3 - Asset pipeline

# Current Implementation Status (to be completed by AI)
- Task 1 done - 2025-06-03
- Task 2 done - 2025-06-03
- Task 3 done - 2025-06-03

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
**Completed 2025-06-03** ✅

**Major Changes Made:**
- Created `IWeightedRandomSelector<T>` interface in `/utils/` package
- Implemented `WeightedRandomSelector<T>` class with binary search algorithm for O(log n) performance
- Created `WeightedItem<T>` data class using `percent: Double` (changed from `weight` to be more precise)
- Added comprehensive unit tests for both classes with edge case coverage

**Files Created:**
- `src/main/kotlin/com/wisecrowd/data_generator/utils/IWeightedRandomSelector.kt`
- `src/main/kotlin/com/wisecrowd/data_generator/utils/WeightedRandomSelector.kt`
- `src/main/kotlin/com/wisecrowd/data_generator/utils/WeightedItem.kt`
- `src/test/kotlin/com/wisecrowd/data_generator/utils/WeightedItemTest.kt`
- `src/test/kotlin/com/wisecrowd/data_generator/utils/WeightedRandomSelectorTest.kt`

**Key Decisions:**
- Used `percent: Double` instead of `weight: Double` for clarity (represents percentage like 25.0 for 25%)
- Implemented binary search for efficient O(log n) item selection
- Added defensive programming with comprehensive input validation
- Injectable Random instance enables deterministic testing

**Impact on Future Tasks:**
- This utility can now be used by all data generators requiring weighted random selection
- Eliminates code duplication across asset classes, investor profiles, activity levels, etc.
- Provides consistent randomization behavior throughout the system

## Phase 3 - Task 2 - IDataGenerator Interface
Create the IDataGenerator interface that all data generators will implement.

### Description
Define the contract for all data generators using an iterator pattern with hasNext() and getNext() methods. This provides a pull-based approach where consumers control the flow of data generation.

### Design
```kotlin
interface IDataGenerator {
    fun getColumnData(): List<ColumnData>
    fun hasMoreRows(): Boolean
    fun getNextRow(): List<Any>
}
```

### Deliverables
- IDataGenerator interface with self-describing column structure
- Proper documentation and examples
- Integration with DataGenerationService pattern

### Acceptance Criteria
1. Uses iterator pattern with hasMoreRows() and getNextRow() methods
2. Includes getColumnData() method for self-describing schema
3. Returns List<Any> supporting mixed data types (String, Double, Int, Boolean, etc.)
4. Pull-based approach allowing consumer to control generation flow
5. Includes proper error handling mechanisms (getNextRow() can throw exceptions)
6. Provides clear method contracts and documentation
7. Designed for use with DataGenerationService and IDataSaver

### Task Summary (to be completed by AI)
**Completed 2025-06-03** ✅

**Major Changes Made:**
- Created `IDataGenerator` interface with self-describing schema via `getColumnData(): List<ColumnData>`
- Implemented iterator pattern with `hasMoreRows()` and `getNextRow(): List<Any>` methods
- Implemented comprehensive contract testing framework `IDataGeneratorContractTest` for interface compliance
- Created `TestStringDataGenerator` as concrete implementation demonstrating mixed data types and column structure
- Added extensive unit tests covering all edge cases and schema consistency verification

**Files Created:**
- `src/main/kotlin/com/wisecrowd/data_generator/data_generators/IDataGenerator.kt`
- `src/test/kotlin/com/wisecrowd/data_generator/data_generators/IDataGeneratorContractTest.kt`
- `src/test/kotlin/com/wisecrowd/data_generator/data_generators/TestStringDataGenerator.kt`
- `src/test/kotlin/com/wisecrowd/data_generator/data_generators/TestStringDataGeneratorTest.kt`

**Key Decisions:**
- Added `getColumnData()` method for self-describing generators (single source of truth for schema)
- Used iterator pattern (`hasMoreRows()`/`getNextRow()`) for better control flow and clearer naming
- Returns `List<Any>` to support mixed data types (String, Double, Int, Boolean) in each row
- Implemented contract testing to ensure all future implementations follow the same behavior
- Added comprehensive error handling with `NoSuchElementException` for consistency

**Impact on Future Tasks:**
- All data generators are now self-describing with consistent column structure
- DataGenerationService no longer needs external column configuration
- Contract testing ensures consistent behavior across all implementations
- Pull-based iteration enables better memory management and consumer control
- Ready for integration with DataGenerationService in next task

## Phase 3 - Task 3 - Data Generation Service
Create DataGenerationService that orchestrates IDataGenerator and IDataSaver to test the complete workflow.

### Description
Implement the orchestration service that combines data generation and file saving using the iterator pattern. The service pulls data from IDataGenerator and pushes it to IDataSaver.

### Design Pattern
```kotlin
class DataGenerationService(
    private val generator: IDataGenerator,
    private val saver: IDataSaver
) {
    fun generateAndSave() {
        val columnData = generator.getColumnData()  // Self-describing!
        saver.prepare(columnData)
        
        while (generator.hasMoreRows()) {
            val dataRow = generator.getNextRow()  // List<Any>
            val stringData = dataRow.map { it.toString() }
            saver.saveItem(stringData)
        }
        saver.complete()
    }
}
```

### Deliverables
- DataGenerationService class
- Unit tests with mock IDataGenerator implementations
- Integration tests with FileDataSaver

### Acceptance Criteria
1. Takes IDataGenerator and IDataSaver as constructor parameters
2. Orchestrates the complete generate-and-save workflow using iterator pattern
3. Uses generator.getColumnData() to obtain schema (no external column configuration needed)
4. Converts List<Any> data rows to List<String> for file output using toString()
5. Handles errors from both generator and saver components
6. Includes comprehensive unit tests with mock IDataGenerator implementations
7. Includes integration tests with FileDataSaver

### Task Summary (to be completed by AI)
**Completed 2025-06-03** ✅

**Major Changes Made:**
- Created `DataGenerationService` class that orchestrates IDataGenerator and IDataSaver components
- Implemented pull-based iterator pattern workflow using generator's self-describing schema
- Added toString() conversion from generator's List<Any> to saver's List<String> format
- Created comprehensive unit tests with mock implementations for both generator and saver
- Created integration tests with FileDataSaver demonstrating real file operations

**Files Created:**
- `src/main/kotlin/com/wisecrowd/data_generator/DataGenerationService.kt`
- `src/test/kotlin/com/wisecrowd/data_generator/DataGenerationServiceTest.kt`  
- `src/test/kotlin/com/wisecrowd/data_generator/DataGenerationServiceIntegrationTest.kt`

**Key Decisions:**
- Service uses generator.getColumnData() for self-describing schema (no external column configuration needed)
- Generator exceptions propagate to caller for visibility while saver errors are handled internally
- Pull-based iteration pattern allows consumer control over data generation flow
- Integration tests use temporary files and TestStringDataGenerator for realistic testing scenarios

**Impact on Future Tasks:**
- Complete generate-and-save workflow now tested and ready for production use
- Asset generators can now use this service to generate data files directly
- Clear separation of concerns between generation logic and file I/O operations
- Foundation established for generating asset_data.txt and price_series.txt files

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
