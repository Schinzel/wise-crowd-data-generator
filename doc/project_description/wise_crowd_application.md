# Main steps

## Part 1: Segmentation Strategy
The retail investors can be segmented on for example age, nationality or more.
Each segment is sent to the Analysis Pipeline.

## Part 2: Analysis Pipeline
1. Qualify the retail investors. Ensure they meet minimum requirements.
2. Rank the qualified retail investors.
3. Find the top-ranked retail investors. Select top X% of ranked retail investors.
4. Find asset classes the top-ranked retail investors hold.
5. Find the assets in each asset class.


# Example Output

# Top level interfaces
```Kotlin
typealias Description = String
interface IRetailInvestorSegmenter {
    fun segment(retailInvestors: List<RetailInvestor>): Map<Description, List<RetailInvestor>>
}
```


```Kotlin
interface IRetailInvestorQualifier {
  fun qualifyRetailInvestors(retailInvestors: List<RetailInvestor>): List<RetailInvestor>
}
```

```Kotlin
interface IRetailInvestorRanker {
  fun rankRetailInvestors(retailInvestors: List<RetailInvestor>): List<RetailInvestor>
}
```

```Kotlin
interface IRetailInvestorSelector {
  fun selectTopRetailInvestors(rankedRetailInvestors: List<RetailInvestor>, percentage: Double): List<RetailInvestor>
}
```

```Kotlin
class Percentage(val value: Int) {
  init {
    require(value in 0..100) { "Percentage must be between 0 and 100, got $value" }
  }
}

interface IAssetClassAnalyzer {
  fun findAssetClasses(topRetailInvestors: List<RetailInvestor>): Map<AssetClass, Percentage>
}
```

```Kotlin
interface IAssetFinder {
    fun findAssets(assetClassData: Map<AssetClass, Percentage>): Map<AssetClass, List<Asset>>
}
```
