# AGENTS.md — Codebase Index for Mahallu Manager

Fast-navigation index so agents can locate code with minimal reads. User-facing docs live in `README.md`.

## Build & Verify
- Build (only reliable gate; no emulator/adb): `./gradlew --no-daemon assembleDebug`
- Long builds MUST run via a background terminal, not blocking `bash`.
- Lint cannot gate CI: pre-existing `CAMERA` error in `app/src/main/AndroidManifest.xml:8`.
- Modules build order: `core-ui` → features (KSP/Hilt) → `app`.

## Architecture
Multi-module, offline-first Room + Hilt + Compose, MVVM. Single-Activity, single-Activity NavGraph.

| Module | Responsibility | Key locations |
|---|---|---|
| `app` | Entry, DI modules, nav graph, backup worker, app-level PDF | `navigation/MainShell.kt`, `di/DatabaseModule.kt`, `MainActivity.kt`, `MahalluApplication.kt` |
| `core` | Pure utilities | `util/IdGenerator.kt` |
| `core-ui` | Design system + shared composables + `Formatters` | `components/`, `theme/`, `util/Formatters.kt` |
| `core-database` | Room: entities, DAOs, repositories, seed, audit, language/theme controllers | `entity/Entities.kt`, `dao/Daos.kt`, `repository/` |
| `core-security` | Auth hashing, encrypted prefs | `PasswordHasher.kt` |
| `core-network` | (Google Drive upload etc.) | — |
| `feature-*` | Screens + ViewModels per domain | `src/main/java/com/mahallu/manager/feature/<x>/` |

## Navigation Routes (`app/.../navigation/MainShell.kt`)
`dashboard`, `families`, `members`, `finance`, `more` (bottom tabs).
- `family_detail/{familyId}`, `family_edit?id={familyId}`
- `member_detail/{memberId}`, `member_edit?id={memberId}`, `collection_entry?memberId={memberId}`
- `subscriptions`, `donations`, `donation_entry`, `finance`, `finance_entry`
- `marriages`, `marriage_edit?id={id}`, `deaths`, `death_edit?id={id}`
- `welfare`, `welfare_edit?id={id}`, `certificates`, `certificate/{type}`
- `reports`, `settings`, `backup`, `search`, `announcements`

Bottom tabs defined in `BottomNavigationBar.kt` (Icons + labels; `nav_*` core-ui strings).

## Key Files Map
- **MainActivity.kt**: locale wrapping via `attachBaseContext` (language toggle), reads prefs `app_language_prefs`/`app_language`, calls `applyLanguage()` → writes prefs + `recreate()`.
- **DatabaseModule.kt** (`app/.../di/`): provides DB, DAOs, `SharedPreferences` (language + theme prefs).
- **LanguageController.kt** (`core-database/repository/`): StateFlow + SharedPreferences sync, `KEY=app_language`, `DEFAULT=en`. NO Hilt qualifiers (core-database has no Hilt).
- **ThemeModeController.kt**: dark/light toggle, same pattern.
- **Entities.kt** (`core-database/.../entity/`): all Room entities in ONE file (MemberEntity, FamilyEntity, MarriageEntity[has brideId/groomId], DeathEntity[has familyId/memberId], SubscriptionEntity, DonationEntity, FinanceEntryEntity, CertificateEntity, WelfareEntity, ...).
- **Daos.kt**: all DAOs in ONE file. MemberDao: `observeAll`, `search(q)`, `getById`, `observeById`, `observeByFamily`.
- **Repositories** (`core-database/repository/`): one per domain + `DashboardRepository`, `GlobalSearchRepository`, `AuditLogRepository`, `SeedData.kt` (currency_symbol = `₹`), `CurrentActor`.
- **SearchableSelectField.kt** (`core-ui/components/`): generic searchable picker, `options: List<Pair<String,String>>`, `onSelect(id)`. Used by marriage (bride/groom) + death (deceased) entry screens.
- **AppCard.kt**: default elevation `4.dp`, shadow ambient 0.09/spot 0.07.
- **InfoGrid.kt**: `InfoCell`, `InfoGridCard`, `DetailAction`, `DetailActionsRow` (equal-height action boxes), `DetailSectionTitle`, `SectionHeader`.

## Design System (`core-ui/.../theme/`)
- `LocalMahalluColors` (CompositionLocal) → `MahalluColors` in `MahalluColors.kt` (light) + `Color.kt` (palette). Keys: `primaryIndigo`, `primaryDark`, `accentCoral`, `surface`, `surfaceVariant`, `border`, `textPrimary`, `textSecondary`, `textTertiary`, `muted`, `success`, `successDark`, `successTint`, `rose`, `roseTint`, `purple`, `warning`, `chartIncome`, `chartExpense`, `chartGrid`, `error`, `background`.
- `Type.kt`: Sora (display/headline/title) + Manrope (body/label) with **Gayathri as per-weight fallback** — Gayathri is the only font with the `₹` (U+20B9) glyph. Do NOT remove Gayathri from families.
- Shapes in `Shape.kt`: `RadiusMd`, `RadiusLg`.
- Theme.kt: non-AppCompat `android:Theme.Material.Light.NoActionBar` / `Material.NoActionBar`.

## Shared Composables (`core-ui/.../components/`)
`AppCard` (shadow container), `AppButton`, `AppTextField` (+`PasswordTextField`), `ChipPill`, `SearchableSelectField`, `TopAppBar`, `BottomNavigationBar`, `DashboardHeader`, `StatTile`, `SmallActionButton`, `BarChart`/`LineChart`/`ChartPoint`, `SectionHeader`/`DetailSectionTitle`/`InfoGridCard`, `AnimatedReveal`, `statusBarInsetDp()`.

## Formatters (`core-ui/.../util/Formatters.kt`)
`currency()` → `₹` + Indian grouping; `currencyShort()` (K/L/Cr); `date()`, `dateTime()`, `isoDate()`, `calculateAge(dob)`, `initials(name)`. Package is `com.mahallu.manager.core.ui.util.Formatters` (NOT `core.util`).

## Conventions & Gotchas
- **Strings**: every feature module has `res/values/strings.xml` (en) + `values-ml/strings.xml` (Malayalam). New strings need BOTH. `cd_*` = content-description keys.
- **R class**: feature R = `feature.<x>.feature.<x>.R` (e.g. `feature.members.feature.members.R`); core-ui R = `com.mahallu.manager.core.ui.R`.
- **Icons**: `androidx.compose.material.icons.extended` (`Icons.Rounded.*`) — available everywhere core-ui is.
- **DI**: `@HiltViewModel` with constructor injection; `@ApplicationContext` OK in feature/app modules but NOT in `core-database`.
- **Hardcoded colors in screens**: replace `Color.White`/`Color(0xFF...)` backgrounds with `colors.surface`/theme values for dark-mode correctness (many already converted).
- **All entities in `Entities.kt`, all DAOs in `Daos.kt`** — a grep there beats file hunting.
- App-level PDF generator in `app/.../pdf/PdfGenerator.kt`; per-feature PDFs in `feature-*/.../pdf/`. On-device fonts have no `₹` → PDFs use `Rs.`.

## Recent Work (do not regress)
- Language toggle (en/ml): prefs-backed `attachBaseContext` in MainActivity.
- Dashboard: adaptive header/stat overlap (`onSizeChanged` halves grid height); quick actions = Add Member / Record Donation / Add Collection / New Certificate; chart cards clickable → `donations` / `finance`.
- Marriage & death entry: member pickers via `SearchableSelectField` (bride/groom/deceased), prefill name+age(+gender), persist `brideId`/`groomId` / `memberId`/`familyId`.
- Currency: `₹` everywhere in UI; `$4,120` in MoreScreen was replaced with `Formatters.currency()`.
- Card shadows raised (AppCard 4.dp) so cards separate from white background.
- Malayalam truncation pass: every `Text` with `maxLines` now also sets `overflow = TextOverflow.Ellipsis`. StatTile label allows 2 lines (center); AppButton height is `heightIn(min=52.dp)` + 2-line centered text; SmallActionButton label = 2 lines + ellipsis; bottom-nav tabs are equal `weight(1f)` with 1-line ellipsis labels; TopAppBar title = 1 line + ellipsis; long Malayalam dashboard quick-action labels shortened (e.g. `സംഭാവന രേഖപ്പെടുത്തുക` → `സംഭാവന ചേർക്കുക`).
- Logo: uploaded `ChatGPT Image ...png` (blue→cyan + orange, transparent bg) is the app logo. Source assets generated from it with Pillow: `core-ui/res/drawable-nodpi/ic_logo.png` (1024² transparent, logo at 62% safe-zone) used in-app (LoginScreen tile, branded loading gate) + splash icon; `app/res/drawable-nodpi/ic_launcher_monochrome.png` (white alpha mask); `app/res/drawable/ic_launcher_bg.xml` (indigo→deep-indigo gradient, `angle=270`) = adaptive bg; legacy `mipmap-*/ic_launcher{,_round}.png` baked with gradient bg + logo at 70%. Regenerate with the Pillow script pattern if the source image changes. Splash/login styling (user requirement): splash theme `windowSplashScreenBackground` = `@color/background` (light), login logo tile is a LIGHT gradient (`#EEF2FF`→`#E0E7FF`) with indigo dashed ring, logo 150.dp — no dark indigo tile.
- Nav transitions: `MainShell` NavHost has NO enter/exit transitions (instant tab switches — crossfade caused a flash/ghost overlap).
- Auth gate: `AuthState.isInitializing` (default true) — `MahalluNavGraph` shows a branded light loading screen until `checkExistingSession()` resolves, preventing a login-page flash for signed-in users; every `_authState` write must set `isInitializing=false`. Splash + loading gate use LIGHT backgrounds (`#F8FAFC`) with a large logo (200.dp) and indigo spinner — user requirement (no dark indigo splash).
- First-run jank: `MahalluApplication.appReady` StateFlow flips true only after `seedIfEmpty()` + backup scheduling complete; `MahalluNavGraph` ALSO holds the branded loading screen until `appReady`. First-launch PBKDF2 hashing (120k x 3 users) + Room DB creation therefore happens behind the logo/spinner, not janking the login screen / bottom nav. Custom fonts pre-warmed in `Application.onCreate` via `ResourcesCompat.getFont` (off main thread). Any new startup work must run in the `appScope` coroutine BEFORE `_appReady.value = true`.
- Finance sync (IMPORTANT): Donations & subscriptions must ALWAYS create finance entries. `DonationRepository.save/saveAll/delete` and `SubscriptionRepository.save/saveAll/delete` write/remove derived `FinanceEntryEntity` rows via `FinanceEntryFactory` (`financeEntryFromDonation`/`financeEntryFromSubscription`, deterministic id `fin-{sourceId}` with `receiptId={sourceId}` so deletes can map back) — do NOT add other donation/subscription write paths without syncing finance, or the finance page/dashboard income chart silently goes empty. `SeedData` also seeds matching finance entries.
- Tab navigation: dashboard chart/quick-action navigation to a bottom-tab route uses tab-style navigation (`popUpTo(startDestination){saveState=true}` + `launchSingleTop` + `restoreState`) so it behaves like a bottom-tab switch, not a pushed duplicate screen.
- Launcher icon: adaptive + legacy launcher backgrounds are WHITE (user requirement). `app/res/drawable/ic_launcher_bg.xml` = solid white (`<solid android:color="#FFFFFF"/>`); legacy `mipmap-*/ic_launcher{,_round}.png` baked white + logo at 70%. Re-run `/tmp/opencode/gen_launcher_white.py` to regenerate. `ic_logo.png` itself stays transparent.
- Finance entry: `FinanceScreen` shows an "Add Entry" ExtendedFloatingActionButton (bottom-end) → `finance_entry` route (`IncomeExpenseEntryScreen` supports BOTH INCOME and EXPENSE via type toggle + category chips). List bottom contentPadding is 100.dp so the FAB never covers the last row.
- Certificates: `CertificateListScreen` shows ALL generated certificates (from `CertificateRepository.observeAll`) in a "Generated Certificates" section with real issued count; tapping a row opens the PDF via `PdfShare.open`. `CertificateFormViewModel.generate()` de-duplicates via `CertificateRepository.findByTypeAndSubject(type, subjectName)` — regenerating for the same type+subject UPDATEs the existing entity (keeps id/certificateNumber), it never inserts a duplicate row. Certificate PDF stamps date+time (`dd MMM yyyy, hh:mm a`) on the "Issued on" line AND in the page footer.
- Certificate PDF layout is Design C (ornamental) — user-approved. Rules: mahallu name from `settingsRepo.getString("mahallu.name", ...)` is the header org line (NOT "Mahallu Manager"); mahallu address appears under it (no "Almighty" text). Membership/Residence/Death bodies use `cert_pdf_certify_that` + `"Janab <name>"` centered bold (the `cert_pdf_janab` honorific is used on these three ONLY). Marriage uses `cert_pdf_marriage_intro` (nikah wording), NEVER Janab, NEVER occupation; it renders Groom/Bride `PdfPanel`s (full name / father's name / age / address), a Nikah Details `PdfInfoBlock` (date, venue, Mahr — label is `cert_mahr`, never "Dower"), Witnesses block, Qazi block (from `performedBy`), and 3 signature labels (Secretary/Qazi/President). `PdfGenerator.generate()` is backward-compatible: `ornament` defaults false (donation/subscription receipts unchanged); all certificate types pass `ornament=true`, `issuedLine` (date+time stamp), footer = mahalluName + stamp. Marriage data comes from `MarriageEntity` only (groom/bride father+age direct fields, addresses via linked member records via `groomId`/`brideId`; NO mother's-name field exists). New marriage-form fields live in `CertificateFormScreen.MarriageFields`; the "Generate Certificate" button on `MarriageEditScreen` (MainShell) prefills groomFatherName/groomAge/brideFatherName/brideAge/mahar/performedBy via `CertificatePrefillData`.
