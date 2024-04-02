# Weather History App

This Weather History App is designed to allow users to retrieve historical weather data for a specified date and year. The app utilizes a free weather API to download the weather data in the form of JSON files.

## Features

1. *Retrieve Historical Weather Data:* Users can input a date and year to retrieve historical weather data for that specific date.
2. *Display Maximum and Minimum Temperature:* The app displays the maximum and minimum temperature for the specified date.
3. *Database Integration:* Allows users to store weather data in a local database for offline access and faster retrieval.
4. *Create Database and Schema:* Provides functionality to create the relevant database and schema.
5. *Insert Data into Database:* Enables users to insert weather data into the database.
6. *Display Temperature from Database:* Shows the maximum and minimum temperature from the database for the given date. If the date is in the future, the app calculates the average of the last 10 available years' temperatures.

## How to Use

1. Clone the repository.
2. Open the project in Android Studio.
3. Connect an Android device or start the Android emulator.
4. Build and run the app.

## Code Structure

- *Kotlin*: Contains the Kotlin source code for the app.
- *Gradle*: Contains the Gradle configuration files.
- *XML*: Contains XML files for layout designs (if applicable).
- *README.md*: It providing an overview of the project and instructions for use.

## Usage

1. Open the app on your Android device or emulator.
2. The app will display the maximum and minimum temperature for the specified date.