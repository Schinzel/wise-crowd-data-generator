# Main steps

## Part 1: Segmentation Strategy
The customers can be segmented on for example age, nationality or more.
Each segment is sent to the Analysis Pipeline.

## Part 2: Analysis Pipeline
1. Qualify the customers. Ensure they meet minimum requirements.
2. Rank the qualified customers.
3. Find the top-ranked customers. Select top X% of ranked customers.
4. Find asset classes the top-ranked customers hold.
5. Find the assets in each asset class.


# Example Output

# Top level interfaces
```Kotlin
typealias Description = String
interface ICustomerSegmenter {
    fun segment(customers: List<Customer>): Map<Description, List<Customer>>
}
```


```Kotlin
interface ICustomerQualifier {
  fun qualifyCustomers(customers: List<Customer>): List<Customer>
}
```

```Kotlin
interface ICustomerRanker {
  fun rankCustomers(customers: List<Customer>): List<Customer>
}
```

```Kotlin
interface ICustomerSelector {
  fun selectTopCustomers(rankedCustomers: List<Customer>, percentage: Double): List<Customer>
}
```

```Kotlin
class Percentage(val value: Int) {
  init {
    require(value in 0..100) { "Percentage must be between 0 and 100, got $value" }
  }
}

interface IAssetClassAnalyzer {
  fun findAssetClasses(topCustomers: List<Customer>): Map<AssetClass, Percentage>
}
```

```Kotlin
interface IAssetFinder {
    fun findAssets(assetClassData: Map<AssetClass, Percentage>): Map<AssetClass, List<Asset>>
}
```
