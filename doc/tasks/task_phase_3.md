# Phase 3 - Asset pipeline

# Current Implementation Status (to be completed by AI)
- Task 1 done - 2025-06-03
- Task 2 done - 2025-06-03

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
interface IDataGenerator<T> {
    fun hasNext(): Boolean
    fun getNext(): T
}
```

### Deliverables
- IDataGenerator interface with generic type parameter
- Proper documentation and examples
- Integration with DataGenerationService pattern

### Acceptance Criteria
1. Uses iterator pattern with hasNext() and getNext() methods
2. Generic interface supporting different data types (Asset, PriceSeries, etc.)
3. Pull-based approach allowing consumer to control generation flow
4. Includes proper error handling mechanisms (getNext() can throw exceptions)
5. Provides clear method contracts and documentation
6. Designed for use with DataGenerationService and IDataSaver

### Task Summary (to be completed by AI)
**Completed 2025-06-03** ✅

**Major Changes Made:**
- Created `IDataGenerator<T>` interface using iterator pattern with `hasNext()` and `getNext()` methods
- Implemented comprehensive contract testing framework `IDataGeneratorContractTest<T>` for interface compliance
- Created `TestStringDataGenerator` as concrete implementation for testing purposes
- Added extensive unit tests covering all edge cases and behaviors

**Files Created:**
- `src/main/kotlin/com/wisecrowd/data_generator/data_generators/IDataGenerator.kt`
- `src/test/kotlin/com/wisecrowd/data_generator/data_generators/IDataGeneratorContractTest.kt`
- `src/test/kotlin/com/wisecrowd/data_generator/data_generators/TestStringDataGenerator.kt`
- `src/test/kotlin/com/wisecrowd/data_generator/data_generators/TestStringDataGeneratorTest.kt`

**Key Decisions:**
- Used iterator pattern (`hasNext()`/`getNext()`) instead of lifecycle methods for better control flow
- Made interface generic `IDataGenerator<T>` to support different data types
- Implemented contract testing to ensure all future implementations follow the same behavior
- Added comprehensive error handling with `NoSuchElementException` for consistency with Iterator pattern

**Impact on Future Tasks:**
- All data generators (Asset, PriceSeries, etc.) can now implement this clean interface
- Contract testing ensures consistent behavior across all implementations
- Pull-based iteration enables better memory management and consumer control
- Ready for integration with DataGenerationService in next task

## Phase 3 - Task 3 - Data Generation Service
Create DataGenerationService that orchestrates IDataGenerator and IDataSaver to test the complete workflow.

### Description
Implement the orchestration service that combines data generation and file saving using the iterator pattern. The service pulls data from IDataGenerator and pushes it to IDataSaver.

### Design Pattern
```kotlin
class DataGenerationService<T>(
    private val generator: IDataGenerator<T>,
    private val saver: IDataSaver
) {
    fun generateAndSave(columnData: List<ColumnData>, itemConverter: (T) -> List<String>) {
        saver.prepare(columnData)
        while (generator.hasNext()) {
            val item = generator.getNext()
            val stringData = itemConverter(item)
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
3. Uses converter function to transform generated objects to string lists for saving
4. Handles errors from both generator and saver components
5. Includes comprehensive unit tests with mock IDataGenerator implementations
6. Includes integration tests with FileDataSaver

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
