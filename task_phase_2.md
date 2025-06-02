# Phase 2 - Foundation Completion

# Current Implementation Status (to be completed by AI)

# Tasks

## Phase 2 - Task 1 - Update FileNameEnum
Update the existing FileNameEnum to include user_holdings.txt file name.

### Description
Add the missing USER_HOLDINGS entry to FileNameEnum to support the complete set of 5 output files as specified in design.md.

### Deliverables
- Updated FileNameEnum with USER_HOLDINGS entry
- Updated unit tests to cover the new enum entry

### Acceptance Criteria
1. Add USER_HOLDINGS("user_holdings.txt") to FileNameEnum
2. Maintain existing entries unchanged
3. Update corresponding unit tests
4. Verify all tests still pass

### Task Summary (to be completed by AI)

## Phase 2 - Task 2 - Customer Countries Collection
Create CustomerCountriesCollection to support realistic Nordic customer distribution for banking simulation.

### Description
Implement a data collection that represents the Nordic countries where bank customers are located. This provides realistic customer demographics for the Swedish bank simulation, following the same patterns as other collection classes.

### Collection Data (from design.md)
| ID | Name | Country Code | Description | Distribution (%) |
|----|------|-------------|-------------|------------------|
| 1  | Sweden | SE | Home market with largest customer base | 60 |
| 2  | Norway | NO | Oil wealth economy with active investors | 15 |
| 3  | Denmark | DK | Strong financial sector and banking ties | 12 |
| 4  | Finland | FI | Tech-savvy market with Nordic connections | 8 |
| 5  | Iceland | IS | Small but wealthy per capita customer base | 5 |

### Deliverables
- CustomerCountry data class
- CustomerCountriesCollection class with predefined Nordic data
- Integration with existing collection patterns

### Acceptance Criteria
1. Follow existing collection patterns (similar to CurrencyCollection and ActivityLevelCollection)
2. Place in `data_collections/customer_country/` package
3. CustomerCountry data class with id, name, countryCode, description, distributionPercentage
4. CustomerCountriesCollection with filtering and lookup methods:
   - getAll() method
   - getByDistribution() method for weighted selection
   - getByCountryCode() method for lookup
5. Predefined Nordic countries data matching design.md table

### Task Summary (to be completed by AI)


