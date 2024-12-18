﻿# polling-project

Polling System
  A scalable polling system implemented using JDBC, with a focus on Low-Level Design (LLD) principles and a well-structured schema design.

Table of Contents
  1) Overview
  2) Features
  3) Schema Design
  4) Database Tables
  5) Technologies Used
  6) Setup Instructions
  7) How to Use

1) Overview
    The WhatsApp LIKE Polling System allows users to create, vote in, and view the results of polls. It ensures a seamless user experience with robust data consistency using JDBC and efficient database queries.

2) Features
    User Accounts: Users can create an account to access the Polling platform.
    Poll Creation: Create a poll with a question and multiple options.
    Vote in Polls: Users can cast their vote for an active poll.
    View Results: Real-time results displaying vote counts and percentages.
    List Active Polls: Fetch and display currently active polls.
    Data Integrity: Ensures no duplicate votes and maintains ACID properties.

3) System Design
    Schema Design :- refer to the image mentioned in resources

4) Database Tables
     The database schema consists of four key tables:
        Users: Stores user details.
        Polls: Stores poll metadata.
        choices: Stores options associated with each poll.
        responces: Tracks user votes for specific poll options.

5) Technologies Used
  Java: Core language for implementing the system.
  JDBC: For database connectivity and query execution.
  MySQL: Relational database management system.

6) Setup Instructions
   Prerequisites
    Install Java Development Kit (JDK) (Version 8 or above).
    Install MySQL and create the necessary database.
    Configure a MySQL user with access permissions.

    Clone the repository
    Set up the database
    Import the schema using the SQL commands provided in the Schema Design section.
    Configure the project
    Update the database credentials in the DBConnection file
    Build and run the project

7) How to use 
  Create an account with user then you have the access to create the polls and manage even you can list the live polls and respond to ther polls.

