# WiseCrowd Data Generator
The purpose of this project is generate mocked data for WiseCrowd

## Usage

This data generator creates mock data for the WiseCrowd platform to enable development and testing
with datasets of various sizes and configurations.

### Generated Files

The generator produces five output files:

- **asset_data.txt** - Investment assets with asset classes and names
- **price_series.txt** - Historical price data for all assets
- **users.txt** - Investor profiles with risk tolerance and activity levels
- **transactions.txt** - Buy/sell transactions by users over time
- **user_holdings.txt** - Current portfolio holdings for each user

### Basic Usage

```kotlin
val config = DataGenerationConfig(
    numberOfAssets = 100,
    numberOfUsers = 1000,
    startDate = LocalDate.of(2020, 1, 1),
    endDate = LocalDate.of(2025, 1, 1)
)

val orchestrator = WiseCrowdDataOrchestrator(config)
orchestrator.generate()
```

### Running the Generator

**Command Line:**
```bash
mvn exec:java -Dexec.mainClass="com.wisecrowd.data_generator.MainKt"
```

**IDE:**
Run the `main` function in `Main.kt`

**Kotlin:**
```bash
kotlin -cp target/classes com.wisecrowd.data_generator.MainKt
```

AIs must read and adhere to the code standards.
Read the document below and follow the links in the document.
- [Code Standards](doc/code_standards/code_standards_index.md)

AIs must read and understand the project description.
Read the document below and follow the links in the document.
- [Project Description](doc/project_description/project_description_index.md)

For humans and AIs there is a suggested workflow for AI assisted development
- [Workflow](doc/workflow/workflow.md)

