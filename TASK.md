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
Task 0 done - Created FileNameEnum to store output file names in one place

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

### Task Summary (to be completed by AI)


## Task 2 - MarketTrend data
Generate a class that holds the market trend data. Create the class MarketTrend that holds
a row of data. The class that holds the rows is called MarketTrendCollection. 

### Task Summary (to be completed by AI)

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