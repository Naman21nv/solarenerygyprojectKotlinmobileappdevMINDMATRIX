# Surya-Shakti Solar Monitor

Surya-Shakti is a modern Android application designed to help users track and optimize their solar energy usage. Built with Jetpack Compose and Material 3, it provides an intuitive interface to monitor energy generation, consumption, and overall independence.

## 🚀 Features

- **Energy Dashboard**: Real-time visualization of solar energy metrics including generation (kWh), consumption (kWh), and net energy.
- **Independence Score**: Automatically calculates your energy independence percentage to help you understand how much of your power comes from the sun.
- **30-Day Savings Report**: Comprehensive reports showing total savings, average independence, and total energy stats over the last month.
- **Local Logging**: Easily log daily energy data, battery levels, and weather conditions.
- **Smart Suggestions**: Get actionable insights based on your energy generation and weather patterns.
- **Local Persistence**: All data is stored securely on-device using Room database.

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Design System**: Material Design 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room Persistence Library
- **Concurrency**: Kotlin Coroutines & Flow
- **Dependency Injection**: Manual injection via ViewModel Factories

## 📂 Project Structure

- `ui/`: Contains Compose screens (`DashboardScreen`), components, and themes.
- `viewmodel/`: Business logic and UI state management (`EnergyViewModel`).
- `data/`: Data layer containing Room database, DAOs, and the Repository pattern.

## 🛠️ Setup & Installation

1. Clone the repository.
2. Open the project in **Android Studio (Ladybug or newer)**.
3. Sync Gradle and ensure all dependencies are downloaded.
4. Run the app on an Android device or emulator (API 24+).

---
Developed for the MINDMATRIX Solar Project.
