# WiseCrowd Data Generator - Phase 3: Data Generators

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

## Phase 4 - Task 1 - User Generator → `users.txt`
Create UserDataGenerator that implements IDataGenerator to generate user data.

### Description
Implement a generator that creates realistic user data with proper distribution of investor profiles, activity levels, and customer countries. Includes customer lifecycle management with join/departure dates and customer status tracking.

### Uses
- InvestorProfileCollection
- ActivityLevelCollection
- CustomerCountriesCollection

### Parameters
- Number of users
- customerJoinDistribution (e.g., 30% join after simulation start date)
- customerDepartureRate (e.g., 20% leave before simulation end date)
- simulationStartDate and simulationEndDate for determining join/departure date ranges

### Output
- user_id
- investor_profile_id
- activity_level_id
- country_id
- join_date (when customer became active)
- departure_date (when customer left, null if still active)
- customer_status (ACTIVE, DEPARTED)

### Customer Lifecycle Logic
- 30% of users have join_date after simulationStartDate (spread across entire simulation range)
- 20% of users have departure_date before simulationEndDate with customer_status = DEPARTED
- Remaining users start at simulationStartDate and remain ACTIVE throughout
- Departed customers trigger instant sell-off transactions in Transaction Generator

### Deliverables
- UserDataGenerator class
- Integration with existing data collections

### Acceptance Criteria
1. Implements IDataGenerator interface
2. Generates users distributed according to investor profile, activity level, and customer country percentages
3. Implements customer lifecycle logic with join/departure dates and status tracking
4. Handles customerJoinDistribution and customerDepartureRate parameters
5. Creates realistic user demographics with proper date ranges

### Task Summary (to be completed by AI)

## Phase 4 - Task 2 - Transaction Generator → `transactions.txt`
Create TransactionDataGenerator that implements IDataGenerator to generate transaction data.

### Description
Implement a generator that creates realistic transaction data with proper currency distribution and user activity patterns. Includes special handling for departed customers who trigger instant sell-off transactions.

### Uses
- Price series data
- User data  
- CurrencyCollection

### Customer Lifecycle Integration
- Process users with DEPARTED status to generate sell-off transactions
- Departed customers sell all holdings on their departure_date
- Regular ACTIVE customers follow normal activity patterns based on their activity_level

### Deliverables
- TransactionDataGenerator class
- Integration with price series, users, and currencies

### Acceptance Criteria
1. Implements IDataGenerator interface
2. Generates transactions distributed according to currency and user activity patterns
3. Handles departed customer sell-off logic using customer_status and departure_date
4. Creates realistic transaction amounts, dates, and types based on price series
5. Processes both ACTIVE and DEPARTED customer scenarios

### Task Summary (to be completed by AI)

## Phase 4 - Task 3 - User Holdings Generator → `user_holdings.txt`
Create UserHoldingsDataGenerator that implements IDataGenerator to generate user holdings data.

### Description
Implement a generator that calculates current user portfolio positions by processing all transactions and computing net holdings for each user-asset combination.

### Uses
- Transaction data

### Deliverables
- UserHoldingsDataGenerator class
- Integration with transaction data

### Acceptance Criteria
1. Implements IDataGenerator interface
2. Takes transaction data as input and calculates net positions
3. Aggregates buy/sell transactions to determine current holdings
4. Handles multiple currencies properly
5. Generates realistic user_holdings.txt output

### Task Summary (to be completed by AI)
