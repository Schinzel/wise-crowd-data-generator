# Phase 4 - User pipeline

# Current Implementation Status (to be completed by AI)

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
