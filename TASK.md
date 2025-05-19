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

# Tasks

## Task 1 - Delimiters
Implement a file that holds the row and column delimiters.
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