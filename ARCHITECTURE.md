# Mahallu Manager - Architecture Guide

## Overview

Mahallu Manager follows **Clean Architecture** principles with a modular structure designed for scalability, maintainability, and testability.

## Architecture Layers

### 1. Presentation Layer (UI)
- **Location**: `feature-*/src/main/java/com/mahallu/feature/*/ui/`
- **Components**:
  - Compose Screens
  - ViewModels
  - UI States
  - Navigation

**Example Structure**:
```
feature-members/
├── ui/
│   ├── screens/
│   │   ├── MemberListScreen.kt
│   │   ├── MemberDetailScreen.kt
│   │   └── MemberFormScreen.kt
│   ├── viewmodel/
│   │   └── MemberViewModel.kt
│   └── navigation/
│       └── MemberNavigation.kt
```

### 2. Domain Layer (Business Logic)
- **Location**: `domain/src/main/java/com/mahallu/domain/`
- **Components**:
  - Models (data classes)
  - Repository Interfaces
  - Use Cases

**Key Principles**:
- Pure Kotlin module (no Android dependencies)
- Contains all business rules
- Independent of UI and data sources

### 3. Data Layer
- **Location**: `data/src/main/java/com/mahallu/data/`
- **Components**:
  - Repository Implementations
  - Local Data Sources (Room)
  - Remote Data Sources (if needed)

### 4. Core Modules
- **Location**: `core/*/`
- **Modules**:
  - `core-ui`: Theme, colors, typography, components
  - `core-database`: Room database setup, entities, DAOs
  - `core-security`: Encryption, hashing, secure storage

## Dependency Flow

```
app → feature-* → domain ← data ← core-*
                    ↑
                 (depends only on interfaces)
```

**Rules**:
- Outer layers depend on inner layers
- Domain layer has no dependencies on other layers
- Use dependency injection to invert dependencies

## Module Responsibilities

### app
- Application class
- Main DI setup
- App-wide configuration

### feature-auth
- Login screen
- Authentication flow
- Session management

### feature-dashboard
- Dashboard UI
- Statistics display
- Quick actions

### feature-families
- Family CRUD operations
- Family search & filter
- Family detail view

### feature-members
- Member CRUD operations
- Member search & filter
- Member profiles
- QR card generation

### feature-subscriptions
- Subscription tracking
- Payment entry
- Receipt generation
- Defaulter list

### feature-donations
- Donation entry
- Category management
- Donor tracking
- Reports

### feature-finance
- Income/Expense tracking
- Cashbook
- Financial reports

### feature-marriage
- Marriage registration
- Certificate generation
- Search & reports

### feature-death
- Death registration
- Certificate generation
- Reports

### feature-welfare
- Welfare requests
- Approval workflow
- Disbursement tracking

### feature-certificates
- PDF generation
- Template management
- QR verification

### feature-reports
- Report generation
- Export functionality (PDF, Excel, CSV)

### feature-settings
- App configuration
- Backup/Restore
- User management

### di
- Hilt modules
- Dependency provision

### domain
- Business models
- Repository interfaces
- Use cases

### data
- Repository implementations
- Local data sources

### core-ui
- Theme system
- Color palette
- Typography
- Reusable components

### core-database
- Room database
- Entities
- DAOs
- Type converters

### core-security
- AES encryption
- Password hashing
- Secure preferences

## State Management

All ViewModels use **StateFlow** for state management:

```kotlin
sealed class UiState {
    object Loading : UiState()
    data class Success<T>(val data: T) : UiState()
    data class Error(val message: String) : UiState()
}

class MyViewModel @Inject constructor(
    private val myUseCase: MyUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
}
```

## Navigation

Uses **Navigation Compose** with type-safe routes:

```kotlin
// Navigation graph
NavHost(navController, startDestination = "login") {
    loginScreen(onLoginSuccess = { navController.navigate("dashboard") })
    dashboardScreen(onNavigateToMembers = { navController.navigate("members") })
    memberListScreen(
        onMemberClick = { id -> navController.navigate("member/$id") },
        onAddMember = { navController.navigate("member/add") }
    )
}
```

## Database

**Room Database** with offline-first approach:

- All data stored locally
- No internet required for daily operations
- Automatic backup to cloud

**Entities**: Family, Member, Subscription, Donation, Finance, Marriage, Death, Welfare, User, Settings

## Security

1. **Password Hashing**: BCrypt for password storage
2. **Data Encryption**: AES-256 for backup files
3. **Secure Storage**: Encrypted SharedPreferences for sensitive data

## Testing Strategy

1. **Unit Tests**: Use cases, repositories, ViewModels
2. **Database Tests**: DAO operations, migrations
3. **UI Tests**: Compose testing for screens
4. **Integration Tests**: Full feature flows

## Performance Optimizations

1. **Paging**: For large lists (members, families)
2. **Coroutines**: Async operations on IO dispatcher
3. **Lazy Loading**: Images with Coil
4. **Database Indexes**: On frequently queried columns
5. **State Hoisting**: Minimal recomposition in Compose

## Backup System

**WorkManager** scheduled daily backups:

1. Export Room database
2. AES-256 encryption
3. ZIP compression
4. Upload to Google Drive
5. Keep last 30 backups

## Build Configuration

- **Min SDK**: 26
- **Target SDK**: Latest stable
- **Build Types**: Debug, Release
- **Flavors**: Production, Staging (optional)

## Code Style

- **Kotlin Coding Conventions**
- **ktlint** for formatting
- **detekt** for static analysis

---

This architecture ensures:
✅ Separation of concerns
✅ Testability
✅ Maintainability
✅ Scalability
✅ Offline-first capability
✅ Security
