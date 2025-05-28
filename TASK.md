# WiseCrowd Data Generator

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
    * Include major changes made and files affected  
    * Document key decisions and their rationale  
    * Note any challenges encountered and how they were resolved  
    * Highlight any items that might affect future tasks  
  * Fill out *Current Implementation Status (to be completed by AI)* and add which tasks have been completed. Simply add a row with *Task X done*.   
* Let me know when you've completed a task so I can verify it before moving to the next one

# User story

As a developer   
I want a tool that can generate mocked data   
So that I can develop the WiseCrowd platform using data of different sizes and configurations 

# Business values
* by generating large data sets we can test the performance for different scenarios  
* we can derive asset allocation indices and verify that these are as we expect

# Resulting files
The result of running the code be the a set of text files:
- asset_data.txt
- users.txt
- price_series.txt
- transactions.txt

The encoding of the files is UTF-8. 
The generated files will be stored in a directory on my desktop of the user ~/Desktop.
The name of the directory is wise_crowd_data_2025_09_30_14_45. 
This directory will be created when the project runs.


# Current Implementation Status (to be completed by AI)
- Task 0 done - Created FileNameEnum to store output file names in one place
- Task 1 done - Created FileDataSaver to save data to text files with proper formatting and error handling
- Task 2 done - Created MarketTrend and MarketTrendCollection classes to manage market trend data

# Tasks

## Task 0 - File names in an enum
The names of the resulting files should be in an enum so that they are
in one place. The enum should be in the data_generator package. 

### Task Summary (to be completed by AI)
- **Changes Made**: Created a new enum class `FileNameEnum` in the `com.wisecrowd.data_generator` package to store all output file names in one centralized location.

- **Files Created**: 
  - `/src/main/kotlin/com/wisecrowd/data_generator/FileNameEnum.kt`

- **Implementation Details**:
  - Implemented an enum with four entries corresponding to the required output files: ASSET_DATA, USERS, PRICE_SERIES, and TRANSACTIONS
  - Each enum constant has a `fileName` property with the corresponding file name string (e.g., "asset_data.txt")
  - Added proper documentation including the purpose of the enum and the Claude signature as required by the code standards
  - Used clear naming conventions according to the project's code standards

- **Verification**: Successfully built and tested the project to ensure the changes didn't break any existing functionality.


## Task 1 - File Data Save
Based on IDataSaver implement a FileDataSaver. 
- The FileDataSaver should:
  - Save data to the specified file path
  - Use "\n" as row delimiter (stored as a constant)
  - Use "\t" as column delimiter (stored as a constant)
  - Use "###" as string qualifier (stored as a constant)
  - Include a constructor parameter for the file path
  - Create the output directory if it doesn't exist
  - Create a header row based on the column names in the prepare() method
  - Use defensive programming principles (validate inputs, handle exceptions properly)
  - Close file resources properly in the complete() method

### Acceptance Criteria
1. FileDataSaver correctly writes data to files with proper delimiters
2. All errors during the save process are captured and available via getErrors()
3. The class includes proper resource management (files are closed after use)
4. Unit tests cover normal operation and error cases
5. Code follows the project's code standards
6. The implementation can handle all data types defined in DataTypeEnum
7. Unit tests test edge cases such as cyrillic and polish chars, long and short strings. Use the Class io.schinzel.basicutils.FunnyChars

### Task Summary (to be completed by AI)
- **Changes Made**: Implemented a new `FileDataSaver` class that fulfills the IDataSaver interface to write data to files with proper formatting and error handling.

- **Files Created**: 
  - `/src/main/kotlin/com/wisecrowd/data_generator/data_saver/FileDataSaver.kt`
  - `/src/test/kotlin/com/wisecrowd/data_generator/data_saver/FileDataSaverTest.kt`

- **Implementation Details**:
  - Created a class that saves data to a specified file path with:
    - "\n" as row delimiter (stored as a constant ROW_DELIMITER)
    - "\t" as column delimiter (stored as a constant COLUMN_DELIMITER)
    - "###" as string qualifier (stored as a constant STRING_QUALIFIER)
  - Implemented constructor to create parent directories if they don't exist
  - Implemented prepare() method to create the file and header row
  - Implemented saveItem() to save data with proper formatting based on data type
  - Implemented complete() method to properly close resources
  - Implemented error handling with getErrors() and hasErrors() methods
  - Used defensive programming principles throughout:
    - Validation of inputs (empty column data, data/column size mismatch)
    - Proper exception handling with informative error messages
    - Proper resource management for file handling

- **Testing**:
  - Created comprehensive test suite covering all requirements and edge cases:
    - Testing file creation and directory structure creation
    - Testing header row generation
    - Testing data formatting for all data types
    - Testing special character handling (Cyrillic, Polish, etc.)
    - Testing long string handling
    - Testing empty string handling
    - Testing error conditions and proper error reporting
    - Testing proper resource cleanup

- **Verification**: All tests pass successfully, demonstrating the robustness of the implementation.

- **Future Considerations**: The FileDataSaver will be used by various data generators to save their output to the specified files.


## Task 2 - MarketTrend data
Generate a class that holds the market trend data. Create the class MarketTrend that holds
a row of data. The class that holds the rows is called MarketTrendCollection. 

### Task Summary (to be completed by AI)
- **Changes Made**: Implemented MarketTrend and MarketTrendCollection classes to represent and manage market trend data.

- **Files Created**: 
  - `/src/main/kotlin/com/wisecrowd/data_generator/market_trend/MarketTrend.kt`
  - `/src/main/kotlin/com/wisecrowd/data_generator/market_trend/MarketTrendCollection.kt`
  - `/src/test/kotlin/com/wisecrowd/data_generator/market_trend/MarketTrendTest.kt`
  - `/src/test/kotlin/com/wisecrowd/data_generator/market_trend/MarketTrendCollectionTest.kt`

- **Implementation Details**:
  - **MarketTrend class**: 
    - Implemented as a data class to represent a single market trend with startDate, endDate, trendType, strength, and description
    - Added validation in the init block to ensure end date is not before start date, strength is within a reasonable range, and trend type is not empty

  - **MarketTrendCollection class**:
    - Implemented methods to add, retrieve, and filter market trends
    - Created methods to filter trends by date (getTrendsOnDate) and date range (getTrendsInRange)
    - Added a companion object with a static createDefaultCollection() method that creates the predefined Swedish/Nordic market trends from 1990-2025 as specified in the design document

- **Testing**:
  - Created comprehensive tests for both classes
  - Tested validation rules for MarketTrend
  - Tested collection operations and filtering capabilities for MarketTrendCollection
  - Verified that the default collection contains the expected number of trends and that specific historical events are correctly represented

- **Verification**: Successfully built and tested the implementation, with all tests passing.

- **Future Considerations**: The MarketTrendCollection will be used by the Price Series Generator to create historical price data for assets based on market trends.

## Task 3 - Asset class data
Generate a class that holds the asset class data. Create the class AssetClass that holds
a row of data. AssetClassCollection holds the rows. 

### Task Summary (to be completed by AI)

## Task 4 - Currencies
Generate a class that holds the currency data. Create the class Currency that holds
a row of data. CurrencyCollection holds the rows.

### Task Summary (to be completed by AI)

## Task 5 - Investor Profiles
Generate a class that holds the investor profile data. Create the class InvestorProfile that holds
a row of data. InvestorProfileCollection holds the rows.

### Task Summary (to be completed by AI)

## Task 6 - Activity Levels
Generate a class that holds the activity level data. Create the class ActivityLevel that holds
a row of data. ActivityLevelCollection holds the rows.

### Task Summary (to be completed by AI)