# Mahallu Manager

A modern, production-ready Android application for managing Mahallu (community) operations.

## Features

- **Family Management**: Complete family registration and tracking
- **Member Management**: Individual member profiles with QR cards
- **Subscription Management**: Monthly/yearly collection tracking
- **Donation Management**: Multiple donation categories
- **Finance Module**: Income and expense tracking
- **Marriage Register**: Marriage records and certificates
- **Death Register**: Death records and certificates
- **Welfare Management**: Aid request processing
- **Certificate Generation**: PDF certificates with QR verification
- **Reports**: Comprehensive reporting with PDF/Excel export
- **Cloud Backup**: Encrypted Google Drive backup
- **Offline First**: Works completely without internet

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: Clean Architecture + MVVM
- **DI**: Hilt
- **Database**: Room
- **Navigation**: Navigation Compose
- **Security**: AES-256 encryption

## Design System

- **Primary Color**: #4F46E5 (Indigo)
- **Accent Color**: #FF6B6B (Coral)
- **Theme**: Modern, premium productivity app style

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17
- Android SDK 26+

### Build Instructions

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on device or emulator

### Default Login

- Username: `admin`
- Password: `password123`

## Project Structure

```
app/
├── data/
│   ├── local/          # Room database, DAOs, Entities
│   └── repository/     # Repository implementations
├── domain/
│   ├── model/          # Domain models
│   ├── repository/     # Repository interfaces
│   └── usecase/        # Business logic use cases
├── ui/
│   ├── screen/         # Compose screens
│   ├── viewmodel/      # ViewModels
│   ├── navigation/     # Navigation graph
│   └── theme/          # Theme and colors
├── di/                 # Dependency injection modules
└── core/               # Security, utils
```

## License

Proprietary - All rights reserved
