# WiseCrowd Data Generator - Phase 2: Data Generators

# The purpose
The purpose of this document is to describe the problem and the solution to an AI that then can do the changes. 

# Instructions

* Implement one task at a time
* Follow the code standard. Read the file README.md in the project root and follow its links to understand the project, the stack, code standard and how testing is to be done.
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

# Current Implementation Status (to be completed by AI)
Phase 1 Complete - Foundation built with data collections and file saving capabilities. Ready for Phase 2: Data Generators implementation.

# Tasks

## Task 8 - Asset Generator
Create AssetDataGenerator that implements IDataGenerator to generate asset data using AssetClassCollection.

### Description
Implement a generator that creates realistic asset data based on asset classes and their prevalence percentages.

### Deliverables
- AssetDataGenerator class
- Comprehensive unit tests
- Integration with existing data collections

### Acceptance Criteria
1. Generates assets distributed according to asset class prevalence percentages
2. Creates realistic asset names, descriptions, and properties
3. Follows defensive programming principles
4. Includes comprehensive unit tests
5. Code follows project standards

### Task Summary (to be completed by AI)

## Task 9 - User Generator
Create UserDataGenerator that implements IDataGenerator to generate user data using InvestorProfileCollection and ActivityLevelCollection.

### Description
Implement a generator that creates realistic user data with proper distribution of investor profiles and activity levels.

### Deliverables
- UserDataGenerator class
- Comprehensive unit tests
- Integration with existing data collections

### Acceptance Criteria
1. Generates users distributed according to investor profile and activity level percentages
2. Creates realistic user demographics and preferences
3. Follows defensive programming principles
4. Includes comprehensive unit tests
5. Code follows project standards

### Task Summary (to be completed by AI)

## Task 10 - Price Series Generator
Create PriceSeriesDataGenerator that implements IDataGenerator to generate historical price data using MarketTrendCollection.

### Description
Implement a generator that creates realistic price series data influenced by market trends and asset volatility.

### Deliverables
- PriceSeriesDataGenerator class
- Comprehensive unit tests
- Integration with market trends and asset data

### Acceptance Criteria
1. Generates price series that reflect market trends and asset volatility
2. Creates realistic price movements and historical patterns
3. Follows defensive programming principles
4. Includes comprehensive unit tests
5. Code follows project standards

### Task Summary (to be completed by AI)

## Task 11 - Transaction Generator
Create TransactionDataGenerator that implements IDataGenerator to generate transaction data using CurrencyCollection and user/asset data.

### Description
Implement a generator that creates realistic transaction data with proper currency distribution and user activity patterns.

### Deliverables
- TransactionDataGenerator class
- Comprehensive unit tests
- Integration with currencies, users, and assets

### Acceptance Criteria
1. Generates transactions distributed according to currency and user activity patterns
2. Creates realistic transaction amounts, dates, and types
3. Follows defensive programming principles
4. Includes comprehensive unit tests
5. Code follows project standards

### Task Summary (to be completed by AI)
