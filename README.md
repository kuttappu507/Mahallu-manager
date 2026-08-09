# Mahallu Manager — Android

A complete, modern, offline-first Android application for managing a Mahallu (Islamic community center).
Built with Kotlin, Jetpack Compose, Hilt, Room, and modern Android architecture.

[![Build APK](https://github.com/kuttappu507/Mahallu-manager/actions/workflows/build-apk.yml/badge.svg)](https://github.com/kuttappu507/Mahallu-manager/actions/workflows/build-apk.yml)
[![Latest Release](https://img.shields.io/github/v/release/kuttappu507/Mahallu-manager)](https://github.com/kuttappu507/Mahallu-manager/releases/latest)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin)](https://kotlinlang.org)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26-3DDC84?logo=android)](https://developer.android.com)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-34-3DDC84?logo=android)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

**[📥 Download v1.0.0 APK](https://github.com/kuttappu507/Mahallu-manager/releases/tag/v1.0.0)**

## Highlights

- 🕌 **Built for Mahallu** — families, members, finance, marriage, death, donations, certificates, welfare, reports
- 🎨 **Modern UI** — Jetpack Compose, custom Indigo + Coral design system (no Material Theme look)
- 📴 **Offline-first** — All data stored locally in Room; works without internet
- 🔐 **Secure auth** — PBKDF2-HMAC-SHA256 password hashing (120k iterations), EncryptedSharedPreferences session
- 🛡️ **Role-based access** — Administrator, President, Secretary, Treasurer, Imam, Staff, Auditor
- 💾 **Encrypted backup** — AES-256-GCM + ZIP, optional Google Drive upload, scheduled daily via WorkManager
- 📊 **Admin dashboard** — Real-time charts, KPIs, recent activity
- 📄 **PDF certificates** — Birth, marriage, death, income — generated on-device
- 🏗️ **Modular architecture** — 14 feature modules + 5 core modules, Clean Architecture + MVVM
- 🚀 **CI/CD** — GitHub Actions builds the APK on every push

## Quick Start

### 1. Install the APK
Download from the [latest release](https://github.com/kuttappu507/Mahallu-manager/releases/latest), transfer to your phone, and install. You may need to enable "Install from unknown sources" in Android settings.

### 2. Log in
The app seeds three default users on first launch:

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | Administrator (full access) |
| `secretary` | `secretary123` | Secretary |
| `treasurer` | `treasurer123` | Treasurer |

### 3. Build from source
```bash
git clone https://github.com/kuttappu507/Mahallu-manager.git
cd MahalluManager
./gradlew :app:assembleDebug
# APK at app/build/outputs/apk/debug/app-debug.apk
```

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

> Agents / AI contributors: see [`AGENTS.md`](AGENTS.md) for the codebase index (module map, nav routes, key files, design system, conventions) — read that instead of scanning source files.

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
├── feature-finance/         # Income / expense / category / account
├── feature-marriage/        # Marriage registration
├── feature-death/           # Death records + condolence tracking
├── feature-welfare/         # Welfare schemes + beneficiaries
├── feature-certificates/    # PDF certificate generation
├── feature-reports/         # Monthly / annual reports with filters
├── feature-settings/        # User prefs, theme, backup config
└── feature-search/          # Global search across all entities
```

## Design System

The app uses a custom **Indigo + Coral** design system defined in `core-ui/theme/`:

- **Primary**: Indigo (`#3B4FB8`) — for actions, links, primary buttons
- **Accent**: Coral (`#FF6B6B`) — for highlights, notifications, warm moments
- **Background**: Off-white (`#FAFAFA`) / Dark slate (`#0F1419`)
- **Type**: Inter (system) — no Material defaults

No Material Theme look. The design is intentional and minimalist.

## Security

- **Password storage**: PBKDF2-HMAC-SHA256, 120,000 iterations, 16-byte salt, 256-bit output
- **Session storage**: EncryptedSharedPreferences with AES-256-GCM
- **Backup encryption**: AES-256-GCM with key derived from user passphrase
- **No plain text anywhere** — credentials, sessions, and backup keys are all encrypted at rest

## Default Admin Functions

After logging in as `admin`, you have access to:

- **Dashboard** — Total families, members, monthly income, expense trends
- **Families** — Create / edit / archive families, view member list per family
- **Members** — Full CRUD, search, filter by role
- **Subscriptions** — Track monthly contributions, generate receipts
- **Donations** — Categorize donations, generate thank-you receipts
- **Finance** — Accounts, categories, income/expense tracking, charts
- **Marriage / Death** — Register events, generate certificates
- **Welfare** — Manage schemes, beneficiaries, disbursements
- **Reports** — Monthly, annual, custom date range
- **Backup** — Manual backup, restore, schedule
- **Settings** — User profile, app preferences, role management

## Building

### Prerequisites
- JDK 17
- Android SDK 34
- Gradle 8.9 (wrapper included)

### Build
```bash
./gradlew :app:assembleDebug          # Debug APK
./gradlew :app:assembleRelease        # Release APK (unsigned)
```

### CI/CD
Pushes to `main` or `master` trigger a GitHub Actions build that:
1. Compiles the debug APK on Ubuntu with 4 GB heap
2. Uploads the APK as a workflow artifact
3. Creates a draft release tag

See `.github/workflows/build-apk.yml`.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/my-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin feature/my-feature`)
5. Open a pull request

## License

MIT — see [LICENSE](LICENSE) for details.

## Support

Open an [issue](https://github.com/kuttappu507/Mahallu-manager/issues) for bug reports or feature requests.

---

Made with ❤️ for the Mahallu community
