# Mahallu Manager — Android

A complete, modern, offline-first Android application for managing a Mahallu (Islamic community center).
Built with Kotlin, Jetpack Compose, Hilt, Room, and modern Android architecture.

## Highlights

- **Modern UI** — Jetpack Compose, Indigo + Coral design system, no Material Theme look.
- **Offline-first** — All data stored locally in Room; works without internet.
- **Encrypted backup** — AES-256-GCM + ZIP, optional Google Drive upload, scheduled daily via WorkManager.
- **Role-based access** — Administrator, President, Secretary, Treasurer, Imam, Staff, Auditor.
- **Modular architecture** — 14 feature modules + 5 core modules, Clean Architecture + MVVM.
- **Production-ready** — Hilt DI, secure session (EncryptedSharedPreferences), PDF generation, charts.

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Kotlin 2.0.21 |
| UI | Jetpack Compose (BOM 2024.10.00), Material 3, custom design system |
| Architecture | Clean Architecture + MVVM |
| DI | Hilt 2.51.1 |
| Database | Room 2.6.1 |
| Async | Kotlin Coroutines + Flow |
| Navigation | Navigation Compose 2.8.2 |
| Networking | Ktor 2.3.12 (offline-first, server sync optional) |
| Charts | Custom Compose path-based Line + Bar charts |
| PDF | Android PdfDocument |
| Backup | AES-256-GCM (custom), ZIP compression, Google Drive REST API |
| Background | WorkManager 2.9.1 |
| Preferences | DataStore + EncryptedSharedPreferences |
| Logging | Timber 5.0.1 |
| Testing | JUnit4, MockK, Turbine, Truth, Hilt testing, Room testing |

## Modules

```
MahalluManager/
├── app/                     # Application, MainActivity, navigation host, backup manager
├── core/                    # Common types, utilities, result types
├── core-ui/                 # Theme, design tokens, reusable Compose components
├── core-database/           # Room database, entities, DAOs, repositories, seed data
├── core-security/           # Password hashing, AES cipher, secure session
├── core-network/            # Ktor HTTP client setup (for future cloud sync)
├── feature-auth/            # Login + role-based session
├── feature-dashboard/       # Modern fintech-style dashboard with charts
├── feature-families/        # Family CRUD + detail screen
├── feature-members/         # Member CRUD + profile + family linking
├── feature-subscriptions/   # Monthly/quarterly/yearly collection entry
├── feature-donations/       # Donation tracking with categories
├── feature-finance/         # Income/expense ledger + cashbook
├── feature-marriage/        # Nikah register + certificate
├── feature-death/           # Death register + certificate
├── feature-welfare/         # Welfare requests + approval workflow
├── feature-certificates/    # Membership / residence / marriage / death certificates
├── feature-reports/         # PDF reports (family, member, collection, donation, etc.)
├── feature-settings/        # Mahallu settings, theme, user info, backup & restore
└── feature-search/          # Global cross-entity search
```

## Setup

1. **Clone and open** in Android Studio Koala (2024.1.1) or newer.
2. **JDK 17** required (Gradle 8.9).
3. **Sync Gradle** — Android Studio will download all dependencies.
4. **Build & run** on an Android 8.0+ (API 26) device or emulator.
5. **Demo credentials**:
   - `admin` / `admin123` (Administrator)
   - `secretary` / `secretary123` (Secretary)
   - `treasurer` / `treasurer123` (Treasurer)

## Design System

- **Primary**: `#4F46E5` (Indigo)
- **Primary Dark**: `#4338CA`
- **Accent Coral**: `#FF6B6B`
- **Background**: `#FFFFFF` / `#0F172A` (dark)
- **Surface**: `#F8FAFC` / `#1E293B` (dark)
- **Text Primary**: `#1F2937` / `#F8FAFC` (dark)
- **Text Secondary**: `#6B7280` / `#CBD5E1` (dark)
- **Success**: `#10B981`, **Warning**: `#F59E0B`, **Error**: `#EF4444`

Flat colors only. Soft shadows. Rounded corners. No gradients. No Material Theme look.

## Architecture

```
┌──────────────────────────────────────────────────────────┐
│  UI (Compose Screens) — feature-* modules                 │
├──────────────────────────────────────────────────────────┤
│  ViewModels (HiltViewModel + StateFlow)                  │
├──────────────────────────────────────────────────────────┤
│  Use cases (business logic — co-located in feature VMs)  │
├──────────────────────────────────────────────────────────┤
│  Repositories — core-database                             │
├──────────────────────────────────────────────────────────┤
│  Room DAO + Entities                                      │
├──────────────────────────────────────────────────────────┤
│  SQLite                                                   │
└──────────────────────────────────────────────────────────┘
```

- All UI state via `StateFlow` collected with `collectAsStateWithLifecycle`.
- One-shot operations via `viewModelScope.launch`.
- Hilt wires everything via constructor injection.
- No business logic inside Composables.

## Database

- 14 entities (users, families, members, subscriptions, donations, finance, marriages, deaths, welfare, certificates, audit logs, settings, backups, notifications).
- Indexed on searchable and join columns.
- Foreign keys with cascade where appropriate (e.g. deleting a family deletes its members).
- Migrations defined for non-destructive schema evolution.
- Seed data on first launch (5 families, 17 members, 5 donations, 5 subscriptions).

## Backup

- **Manual**: Trigger from Backup screen.
- **Automatic**: WorkManager periodic, daily, only on network.
- **Encryption**: AES-256-GCM with 256-bit key stored in encrypted prefs.
- **Compression**: ZIP.
- **Storage**: Local + Google Drive (folder auto-created).
- **Retention**: Last 30 backups kept; older ones pruned.
- **Restore**: Wizard in Backup screen, decrypts and applies to Room.

## Security

- Password hashing: PBKDF2-HMAC-SHA256, 120k iterations, 256-bit key, random 16-byte salt.
- Session token: EncryptedSharedPreferences (AES-256-SIV keys, AES-256-GCM values) backed by Android Keystore.
- Backup master key: AES-256-GCM key persisted in encrypted preferences.
- All `BuildConfig.DEBUG`-aware Timber logging.

## Performance

- All list screens backed by `Flow` so DB writes reflect in UI instantly.
- Paging 3 available for large lists (Members).
- Search debounced 250ms.
- Charts drawn with Compose `Canvas` (no extra chart library).
- LazyColumn / LazyRow everywhere.

## Build Commands

```bash
# Debug
./gradlew :app:assembleDebug

# Release (unsigned)
./gradlew :app:assembleRelease

# Tests
./gradlew test
./gradlew connectedAndroidTest

# Install
./gradlew :app:installDebug
```

## Testing

The repository includes unit-test hooks for:
- `UserRepository` (login + password verify)
- `FamilyRepository`, `MemberRepository`, `SubscriptionRepository`
- `BackupManager` (round-trip encrypt/decrypt)
- ViewModels for Dashboard, Families, Members, Donations, Welfare, Subscriptions

## Roadmap

- Multi-Mahallu support
- Cloud sync via Ktor (REST API)
- WhatsApp notifications for receipts
- QR member cards (built into ZXing dep)
- Advanced reports (date-range custom queries)
- Multi-language (Malayalam, Arabic, English)

## License

Copyright © 2025 Mahallu Manager. All rights reserved.