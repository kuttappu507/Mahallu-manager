# Mahallu Manager - Production Ready Android Application

A complete, modern, production-ready Android application for managing Mahallu (community) operations.

## 🏗 Architecture

Built with **Clean Architecture** and **MVVM** pattern:

```
app/                    # Main application module
├── core/               # Core utilities
│   ├── ui/            # Theme, components
│   ├── database/      # Room database
│   └── security/      # Encryption, hashing
├── data/              # Data layer (repositories impl)
├── domain/            # Business logic (models, repos, use cases)
├── di/                # Dependency injection (Hilt)
└── feature-*/         # Feature modules
    ├── auth/          # Authentication
    ├── dashboard/     # Dashboard
    ├── families/      # Family management
    ├── members/       # Member management
    ├── subscriptions/ # Subscription tracking
    ├── donations/     # Donation management
    ├── finance/       # Finance module
    ├── marriage/      # Marriage register
    ├── death/         # Death register
    ├── welfare/       # Welfare management
    ├── certificates/  # Certificate generation
    ├── reports/       # Reports & exports
    └── settings/      # App settings
```

## 🎨 Design System

**Color Palette (Indigo + Coral)**
- Primary: `#4F46E5`
- Primary Dark: `#4338CA`
- Accent Coral: `#FF6B6B`
- Background: `#FFFFFF`
- Surface: `#F8FAFC`
- Text Primary: `#1F2937`
- Text Secondary: `#6B7280`
- Success: `#10B981`
- Warning: `#F59E0B`
- Error: `#EF4444`

## 🛠 Tech Stack

- **Language**: Kotlin
- **Min SDK**: 26
- **Target SDK**: Latest Stable
- **UI**: Jetpack Compose
- **Architecture**: Clean Architecture + MVVM
- **DI**: Hilt
- **Database**: Room
- **Navigation**: Navigation Compose
- **Async**: Coroutines + Flow
- **Image Loading**: Coil
- **Charts**: Vico Charts
- **PDF**: Android PdfDocument
- **Backup**: WorkManager + Google Drive

## 📱 Features

### Authentication
- Role-based access (Admin, President, Secretary, Treasurer, Imam, Staff, Auditor)
- Secure password hashing
- Remember login
- Session management

### Dashboard
- Modern fintech-style dashboard
- Total families, members, collections
- Pending dues, donations
- Welfare beneficiaries
- Recent activities
- Collection & donation trends
- Quick action buttons

### Family Management
- Add/Edit/Delete/Archive families
- Search & filter
- Family timeline
- Photo support
- House details, ward, area

### Member Management
- Complete member profiles
- Family linking
- Photo upload
- QR member card
- Advanced filters
- Search functionality

### Subscriptions
- Monthly/Yearly tracking
- Special collections
- Payment history
- Defaulter list
- PDF receipt generation

### Donations
- Multiple categories
- Donor tracking
- Receipt generation
- Donation reports

### Finance
- Income & expense tracking
- Cashbook
- Monthly/Yearly summaries
- Financial reports

### Marriage Register
- Bride & groom details
- Witnesses, Mahar
- Nikah date
- Certificate generation

### Death Register
- Death details
- Burial information
- Certificate generation

### Welfare
- Request management
- Approval workflow
- Disbursement tracking

### Certificates
- Membership certificates
- Residence certificates
- Marriage/Death certificates
- PDF export with QR verification
- Logo & seal support

### Reports
- All registers
- Collection/Donation reports
- Income/Expense reports
- Export to PDF, Excel, CSV

### Settings
- Mahallu configuration
- Backup/Restore
- Theme settings
- User management

## 🔒 Security

- AES-256 encryption for backups
- Password hashing (BCrypt)
- Secure local storage
- Role-based permissions

## 💾 Offline First

- Full offline functionality
- Room database for local storage
- Automatic sync when online
- No internet required for daily operations

## ☁️ Cloud Backup

- Daily automatic backups
- Manual backup option
- Google Drive integration
- Encrypted backup files
- Restore wizard
- Keep last 30 backups

## 📊 Performance

- Startup < 2 seconds
- Search < 300ms
- Handles 5000+ families
- Handles 25000+ members
- Paging for large lists
- Optimized database queries

## 🧪 Testing

- Unit tests for use cases
- Repository tests
- Database tests
- UI tests with Compose Testing
- ViewModel tests

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 26+

### Build Instructions

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on device/emulator

```bash
./gradlew assembleDebug
```

### Run Tests

```bash
./gradlew test
./gradlew connectedAndroidTest
```

## 📦 Project Structure Summary

- **95+ Kotlin files**
- **13 feature modules**
- **Clean Architecture**
- **Hilt DI**
- **Room Database**
- **Jetpack Compose UI**
- **Production ready**

## 📄 License

Proprietary - Mahallu Manager

---

Built with ❤️ for community management
