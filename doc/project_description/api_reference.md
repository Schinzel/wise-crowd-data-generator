# API Reference

## Core Interfaces

### IDataGenerator
```kotlin
interface IDataGenerator<T> {
    fun generate(): T
}
```
Contract for all data generation logic. Implementations handle specific data creation (assets, users, transactions).

### IDataSaver
```kotlin
interface IDataSaver<T> {
    fun save(data: T)
}
```
Contract for file output operations. Implementations handle writing data to specific file formats.

### ILog
```kotlin
interface ILog {
    fun info(message: String)
    fun error(message: String)
}
```
Progress tracking and error reporting interface.

## Main Entry Points

### WiseCrowdDataOrchestrator
Primary orchestrator for data generation workflow.

```kotlin
class WiseCrowdDataOrchestrator(
    private val config: DataGenerationConfig,
    private val outputDirectory: IOutputDirectory = OutputDirectory(),
    private val log: ILog = ConsoleLog()
) {
    fun generate()
}
```

### DataGenerationConfig
Configuration parameters for data generation.

```kotlin
data class DataGenerationConfig(
    val numberOfAssets: Int,
    val numberOfUsers: Int, 
    val startDate: LocalDate,
    val endDate: LocalDate
)
```

### Main Entry Point
```kotlin
// Main.kt
fun main() {
    val config = DataGenerationConfig(
        numberOfAssets = 100,
        numberOfUsers = 1000,
        startDate = LocalDate.of(2020, 1, 1),
        endDate = LocalDate.of(2025, 1, 1)
    )
    
    val orchestrator = WiseCrowdDataOrchestrator(config)
    orchestrator.generate()
}
```

## Key Generator APIs

### AssetGenerator
```kotlin
class AssetGenerator(numberOfAssets: Int) : IDataGenerator<List<Asset>>
```

### UserGenerator  
```kotlin
class UserGenerator(numberOfUsers: Int, startDate: LocalDate, endDate: LocalDate) : IDataGenerator<List<User>>
```

### TransactionGenerator
```kotlin
class TransactionGenerator(users: List<User>, priceData: List<PriceData>) : IDataGenerator<List<Transaction>>
```

## Usage Patterns

### Basic Generation
```kotlin
val config = DataGenerationConfig(100, 1000, startDate, endDate)
val orchestrator = WiseCrowdDataOrchestrator(config)
orchestrator.generate()
```

### Custom Output Directory
```kotlin
val customDir = CustomOutputDirectory("/custom/path")
val orchestrator = WiseCrowdDataOrchestrator(config, customDir)
orchestrator.generate()
```

### Custom Logging
```kotlin
val customLog = FileLog("generation.log")
val orchestrator = WiseCrowdDataOrchestrator(config, outputDir, customLog)
orchestrator.generate()
```