# Main steps

## Part 1: Segmentation Strategy
The retail investors can be segmented on for example age, nationality, wealth level, or risk profile.
Each segment is sent to the Analysis Pipeline.

## Part 2: Analysis Pipeline
1. Qualify the retail investors. Ensure they meet minimum requirements.
2. Rank the qualified retail investors.
3. Find the top-ranked retail investors. Select top X% of ranked retail investors.
4. Find asset classes the top-ranked retail investors hold.
5. Find the specific assets and their allocation percentages within each asset class.

## Data Sources

### Data from Bank
- **Users/Customers** - Demographics (age, country), risk profiles, account information, customer status (active/departed), join/departure dates
- **Transactions** - Complete transaction history (buy/sell, amounts, dates, currencies) for all customers
- **Holdings** - Current portfolio positions and quantities for all customers

### Data from Services  
- **Asset Prices** - Current market values and historical price data (from services like Morningstar)
- **Asset Information** - Basic asset details like names, tickers, descriptions (from financial data providers)
- **Asset Classification** - Asset class categorization (stocks, bonds, REITs, etc.) - initially from Morningstar, later enhanced with custom classification rules

# Example Output

## Common Types
```Kotlin
class Percentage(val value: BigDecimal) {
  init {
    require(value >= BigDecimal.ZERO && value <= BigDecimal(100)) { 
      "Percentage must be between 0.0 and 100.0, got $value" 
    }
  }
}

class PercentageInt(val value: Int) {
  init {
    require(value in 0..100) { 
      "Percentage must be between 0 and 100, got $value" 
    }
  }
}
```

## Data Provider Interfaces
```Kotlin
interface IRetailInvestorProvider {
    fun getRetailInvestors(): List<RetailInvestor>
}

interface ITransactionProvider {
    fun getTransactionsForUser(userId: UserId): List<Transaction>
}

interface IHoldingsProvider {
    fun getCurrentHoldings(userId: UserId): List<Holding>
}

interface IAssetPriceProvider {
    fun getCurrentPrice(assetId: AssetId): Price
}

interface IAssetProvider {
    fun getAsset(assetId: AssetId): Asset
}

interface IAssetClassProvider {
    fun getAssetClass(assetId: AssetId): AssetClass
}
```

## Top Level Interfaces
```Kotlin
typealias Description = String
interface IRetailInvestorSegmenter {
    fun segment(
        retailInvestorProvider: IRetailInvestorProvider,
        holdingsProvider: IHoldingsProvider,
        assetPriceProvider: IAssetPriceProvider
    ): Map<Description, List<RetailInvestor>>
}
```


```Kotlin
interface IRetailInvestorQualifier {
  fun qualifyRetailInvestors(
      retailInvestors: List<RetailInvestor>,
      transactionProvider: ITransactionProvider,
      holdingsProvider: IHoldingsProvider,
      assetPriceProvider: IAssetPriceProvider
  ): List<RetailInvestor>
}
```

```Kotlin
interface IRetailInvestorRanker {
  fun rankRetailInvestors(
      retailInvestors: List<RetailInvestor>,
      transactionProvider: ITransactionProvider,
      holdingsProvider: IHoldingsProvider,
      assetPriceProvider: IAssetPriceProvider
  ): List<RetailInvestor>
}
```

```Kotlin
interface IRetailInvestorSelector {
  fun selectTopRetailInvestors(
      rankedRetailInvestors: List<RetailInvestor>, 
      percentage: Percentage
  ): List<RetailInvestor>
}
```

```Kotlin
interface IAssetClassDistributionDeriver {
  fun deriveAssetClassDistribution(
      topRetailInvestors: List<RetailInvestor>,
      holdingsProvider: IHoldingsProvider,
      assetClassProvider: IAssetClassProvider
  ): Map<AssetClass, PercentageInt>
}
```

```Kotlin
interface IAssetAllocationDeriver {
    fun deriveAssetAllocation(
        assetClassData: Map<AssetClass, PercentageInt>,
        topRetailInvestors: List<RetailInvestor>,
        holdingsProvider: IHoldingsProvider,
        assetClassProvider: IAssetClassProvider,
        assetProvider: IAssetProvider
    ): Map<AssetClass, Map<Asset, PercentageInt>>
}
```
