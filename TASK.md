# WiseCrowd Data Generator - Phase 2: Complete foundation gaps

# The purpose
The purpose of this document is to describe the problem and the solution to an AI that then can do the changes. 

# Instructions

* Implement one task at a time
* Follow the code standard. Read the file README.md in the project root and follow its links to understand the project, the stack, code standard and how testing is to be done.
* All tasks must follow project standards, use defensive programming principles, and include comprehensive unit tests unless explicitly stated otherwise.
* Explain your approach for each task
* When you are done with a task
  * run below commands to verify that things still work
    * mvn clean install \-DskipTests
    * mvn test
  * Fill out the *Task Summary (to be completed by AI)* for the task you just completed
    * Be concise and to the point
    * Include major changes made and files affected
    * Document key decisions and their rationale
    * Highlight any items that might affect future tasks
  * Fill out *Current Implementation Status (to be completed by AI)* and add which tasks have been completed. Simply add a row with *Task X done - YYYY-MM-DD*.
* Let me know when you've completed a task so I can verify it before moving to the next one

# User story

As a developer   
I want a tool that can generate mocked data   
So that I can develop the WiseCrowd platform using data of different sizes and configurations 

# Business values
* by generating large data sets we can test the performance for different scenarios  
* we can derive asset allocation indices and verify that these are as we expect

# Solution Architecture
The system generates mocked data files for the WiseCrowd platform. Foundation components (data collections, file saver) are complete. Next phase focuses on implementing data generators that use these components to create realistic datasets.

## Component Integration Pattern
The system uses a DataGenerationService to orchestrate the interaction between IDataGenerator and IDataSaver:
- IDataGenerator: Handles data generation logic
- IDataSaver: Handles file output operations  
- DataGenerationService: Coordinates the generate-and-save workflow
This ensures single responsibility and testability.

## Package Structure
All data generators must be placed in the `data_generators` package:
- `com.wisecrowd.data_generator.data_generators` for generator implementations
- Follow the existing pattern established with `data_collections` package

# Resulting files
The result of running the code be the a set of text files:
- asset_data.txt
- users.txt
- price_series.txt
- transactions.txt
- user_holdings.txt

# Current Implementation Status (to be completed by AI)
Phase 1 Complete - Foundation built with data collections and file saving capabilities. Ready for Phase 2: Data Generators implementation.

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


