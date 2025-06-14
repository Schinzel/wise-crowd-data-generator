# Architecture

## Pattern
Feature-based modular monolith with domain-driven design principles.

## Core Components

### Data Generators
- **IDataGenerator**: Interface for all data generation logic
- **IDataSaver**: Interface for file output operations  
- **DataGenerationService**: Orchestrates generate-and-save workflow

### Key Generators
- **AssetGenerator**: Creates investment assets with asset classes and names
- **PriceSeriesGenerator**: Generates historical price data using market trends
- **UserGenerator**: Creates investor profiles with risk tolerance and activity levels
- **TransactionGenerator**: Simulates buy/sell transactions based on user profiles
- **UserHoldingsGenerator**: Calculates final portfolios from transactions

### Orchestration
- **WiseCrowdDataOrchestrator**: Main entry point, coordinates all generators
- **DataGenerationConfig**: Configuration for assets, users, dates
- **IOutputDirectory**: File output location management

## Data Flow
1. **Config** → DataGenerationConfig with parameters
2. **Assets** → AssetGenerator creates investment assets
3. **Prices** → PriceSeriesGenerator creates historical data
4. **Users** → UserGenerator creates investor profiles
5. **Transactions** → TransactionGenerator simulates trading
6. **Holdings** → UserHoldingsGenerator calculates final portfolios

## Generated File Schemas

### asset_data.txt
- `asset_id` (UUID) - Unique asset identifier
- `asset_class_id` (Int) - Reference to asset class type
- `name` (String) - Realistic Swedish/Nordic asset name

### price_series.txt  
- `asset_id` (UUID) - Asset identifier matching asset_data.txt
- `date` (LocalDate) - Trading date
- `price` (Double) - Asset price using geometric Brownian motion

### users.txt
- `user_id` (UUID) - Unique user identifier
- `investor_profile_id` (Int) - Risk tolerance profile
- `activity_level_id` (Int) - Trading frequency level
- `country_id` (Int) - Nordic customer country
- `join_date` (LocalDate) - Customer activation date
- `departure_date` (LocalDate) - Customer departure date (9999-12-31 for active)
- `customer_status` (String) - ACTIVE or DEPARTED

### transactions.txt
- `transaction_id` (UUID) - Unique transaction identifier  
- `user_id` (UUID) - User identifier matching users.txt
- `asset_id` (UUID) - Asset identifier matching asset_data.txt
- `transaction_type` (String) - "buy" or "sell"
- `amount` (Double) - Transaction amount in specified currency
- `currency_id` (Int) - Transaction currency

### user_holdings.txt
- `user_id` (UUID) - User identifier matching users.txt
- `asset_id` (UUID) - Asset identifier matching asset_data.txt  
- `amount` (Double) - Net positive holding amount
- `currency_id` (Int) - Holding currency

## Data Generation Flow Diagram

```mermaid
flowchart TD
    %% Data Collections
    MT[MarketTrend Collection]:::collection
    AC[AssetClass Collection]:::collection
    C[Currency Collection]:::collection
    IP[InvestorProfile Collection]:::collection
    AL[ActivityLevel Collection]:::collection
    CC[CustomerCountries Collection]:::collection
    
    %% Generators
    AG[Asset Generator]:::generator
    AN[Asset Namer]:::generator
    PSG[Price Series Generator]:::generator
    UG[User Generator]:::generator
    TG[Transaction Generator]:::generator
    UHG[User Holdings Generator]:::generator
    
    %% Output files
    AD[asset_data.txt]:::outputFile
    PS[price_series.txt]:::outputFile
    U[users.txt]:::outputFile
    T[transactions.txt]:::outputFile
    UH[user_holdings.txt]:::outputFile
    
    %% Relationships
    AC --> AG
    MT --> PSG
    AN --> AG
    AG --> AD
    AD --> PSG
    PSG --> PS
    IP --> UG
    AL --> UG
    CC --> UG
    UG --> U
    PS --> TG
    U --> TG
    C --> TG
    TG --> T
    T --> UHG
    UHG --> UH
    
    %% Styling
    classDef collection fill:#e1f5fe,stroke:#0277bd,stroke-width:2px,color:#000
    classDef generator fill:#e8f5e8,stroke:#2e7d32,stroke-width:2px,color:#000
    classDef outputFile fill:#fff3e0,stroke:#f57c00,stroke-width:2px,color:#000
```

## Error Handling
Centralized error handling with detailed exception messages and validation at generator boundaries.

## Logging
- **ILog**: Interface for progress tracking
- Step-by-step progress reporting during generation
- File creation confirmation and statistics