# Journey Progress App

This app is designed to provide users with information about a journey route, including stops, distances between stops, and the progress of the journey. It follows the guidelines outlined below:

## Guidelines

1. *Distance Units*: The app displays distances in both kilometers and miles, allowing users to switch between the two units using a toggle button.
2. *Next Stop Button*: It allows users to mark the next stop reached by clicking a button.
3. *Load Progress Button*: It allows users to load or hide the progress of the journey, visualized through a TextBox showing the current stop, next stop, distance between the stops, total distance covered, total distance left, and a ProgressBar.
4. *Lazy List*: If the route has more than 10 stops, a lazy list is used to efficiently handle the display of stops. Two hardcoded entries of stops are provided in the code, one shown as a normal list and one with a lazy list.
5. *Compatibility*: The app is designed to run on both Android devices and the Android emulator.

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
2. Use the provided buttons to switch between distance units, load progress and indicate reaching the next stop.