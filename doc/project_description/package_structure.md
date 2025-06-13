# Package Structure

## Overview
Feature-based organization with clear domain boundaries and minimal coupling between packages.

## Package Hierarchy

### `com.wisecrowd.data_generator`
Root package containing main entry points.

**Files:**
- `Main.kt` - Command-line entry point
- `WiseCrowdDataOrchestrator.kt` - Primary orchestrator
- `DataGenerationConfig.kt` - Configuration data class

### `com.wisecrowd.data_generator.data_collections`
Static reference data and domain collections.

**Responsibilities:**
- Market trend definitions (bull/bear markets)
- Asset class specifications (stocks, bonds, REITs)
- Currency definitions and exchange rates
- Investor profile types (conservative, aggressive)
- Activity level classifications
- Customer country distributions

**Key Files:**
- `MarketTrendCollection.kt`
- `AssetClassCollection.kt`
- `CurrencyCollection.kt`
- `InvestorProfileCollection.kt`

### `com.wisecrowd.data_generator.data_generators`
All data generation implementations.

**Responsibilities:**
- Generate investment assets
- Create historical price series
- Generate user profiles
- Simulate transactions
- Calculate portfolio holdings

**Key Files:**
- `AssetGenerator.kt`
- `PriceSeriesGenerator.kt`
- `UserGenerator.kt`
- `TransactionGenerator.kt`
- `UserHoldingsGenerator.kt`
- `IDataGenerator.kt` (interface)

### `com.wisecrowd.data_generator.data_savers`
File output and persistence layer.

**Responsibilities:**
- Save data to text files
- Handle file formatting
- Manage output directories

**Key Files:**
- `IDataSaver.kt` (interface)
- Asset, User, Transaction, Price data savers

### `com.wisecrowd.data_generator.orchestrators`
Coordination and workflow management.

**Responsibilities:**
- Orchestrate generation workflow
- Coordinate data flow between generators
- Handle dependencies between generation steps

### `com.wisecrowd.data_generator.utils`
Cross-cutting utilities and shared functionality.

**Responsibilities:**
- Logging interfaces and implementations
- Output directory management
- Common utilities

**Key Files:**
- `ILog.kt`
- `ConsoleLog.kt`
- `IOutputDirectory.kt`

## Dependencies

### Allowed Dependencies
- `data_generators` → `data_collections` (read static data)
- `data_savers` → `utils` (logging, output directory)
- `orchestrators` → `data_generators` + `data_savers` (coordination)
- `main` → `orchestrators` (entry point)

### Prohibited Dependencies
- `data_collections` → any other package (pure data)
- `data_generators` → `data_savers` (separation of concerns)
- `utils` → domain packages (utility independence)

## Naming Conventions
- Generators: `[Domain]Generator.kt`
- Savers: `[Domain]DataSaver.kt`
- Collections: `[Domain]Collection.kt`
- Interfaces: `I[Name].kt`