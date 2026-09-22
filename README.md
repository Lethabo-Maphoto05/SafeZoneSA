# SafeZone SA — Android Client (Kotlin / Jetpack Compose)

OPSC6312 (Open Source Coding – Intermediate), Group 8 — "Your Smart Community Safety
Network". This is the Android client described in the Planning & Design document
(`OPSC6312_GROUP8.pdf`): a Kotlin/Jetpack Compose app that talks to a central ASP.NET Core
REST API (Section 5) hosted on Azure.

## What's implemented

All 17 screens from Section 4 are built and wired together through one Compose navigation
graph, matching the Section 4.7 navigation map (bottom bar: Home, Feed, SOS, Chat, Profile;
every other screen is reachable from those five), plus an OTP verification screen for FR1.

| Screen | Requirement(s) |
|---|---|
| Splash / Login / Create Account / OTP Verify | FR1 |
| Home Dashboard | FR3, FR7, FR12, FR18 |
| Community Feed | FR2, FR15 |
| Crime Map (live Google Map + incident markers) | FR7, FR14 |
| SOS Emergency (live API call + GPS) | FR3 |
| Walk With Me (live Google Map route) | FR4 |
| Safe Route | FR13 |
| Community Chat | FR10 |
| Beware | FR8 |
| Missing Persons | FR11 |
| Emergency Directory (working call intents) | FR18 |
| Push Notifications | FR12 |
| User Reputation | (gamification, supports FR15) |
| AI Crime Prediction | FR14, FR19 |
| Volunteer Responders | FR17 |
| Profile | account hub |

The colour palette, typography, and component styling follow the Section 4.1 design system
(`ui/theme/`). Data models in `model/Models.kt` mirror the Section 7 data entities. A
`MockRepository` (`data/MockRepository.kt`) still supplies the sample data shown on most
screens so the UI demos without a live backend.

### Network layer (now wired up)

- **`network/ApiClient.kt`** — builds the Retrofit/OkHttp client against
  `BuildConfig.API_BASE_URL`, attaching the stored JWT as a Bearer token on every request
  via an interceptor.
- **`data/SessionManager.kt`** — SharedPreferences-backed storage for the JWT + userId
  returned by `/api/auth/login`. Swap for `EncryptedSharedPreferences`/DataStore before
  shipping, since NFR2/NFR3 treat this token and the user's identity as sensitive.
- **`viewmodel/AuthViewModel.kt`** — Login and Register screens now call
  `/api/auth/login`, `/api/auth/register` for real, with loading spinners and on-screen
  error messages instead of a hard-coded success path. Register routes into a new
  **OTP Verification screen** that calls `/api/auth/verify-otp` before landing on Home,
  matching FR1's "OTP confirms registration" requirement.
- **`viewmodel/SosViewModel.kt`** — the SOS button now fetches the device's last known
  location (via `FusedLocationProviderClient`, permission-gated) and POSTs to `/api/sos`.
  On failure it shows a clear on-screen message telling the person to call their contacts
  or SAPS (10111) directly — a safety feature should never fail silently.
- **`data/SafeZoneMessagingService.kt`** — FCM push now creates two notification channels
  (Alerts / General), builds a real `NotificationCompat` notification tagged by the
  `category` data field, and deep-links back into the app on tap.

None of these can succeed end-to-end yet because the ASP.NET Core API isn't deployed —
that's expected until the Section 8 Gantt milestone of 20 Sept 2026 (core endpoints live).
Every network call is wrapped in try/catch so the app degrades to a visible, honest error
rather than crashing or silently pretending to succeed.

### Maps (now wired up)

- **`CrimeMapScreen.kt`** — a live `GoogleMap` (maps-compose) seeded with the mock incident
  pins, colour-coded by severity, with an "Enable location" prompt when permission isn't
  granted yet.
- **`WalkWithMeScreen.kt`** — a live `GoogleMap` showing the start/destination markers and a
  dashed route line, plus working "Call"/"Message" buttons (via `ACTION_DIAL`/`ACTION_SENDTO`
  intents, so no `CALL_PHONE` runtime permission is needed).
- **`ui/components/LocationPermission.kt`** — a small reusable Compose helper
  (`rememberLocationPermissionState()`) used by both map screens and the SOS screen.

You must add a real key to `res/values/strings.xml` (`google_maps_key`) for the maps to
render on a device/emulator — see Section 5.4. The current value is a placeholder.

## What's still stubbed for Part 2

- **Firebase** — add `app/google-services.json` from the Firebase console and uncomment the
  `com.google.gms.google-services` plugin in `app/build.gradle.kts` and the root
  `build.gradle.kts`. Until then FCM won't actually receive pushes (the handling code is
  ready, it just has nothing to connect to).
- **Google Maps API key** — see above.
- **Poppins font** — drop `Poppins-Regular.ttf` / `Poppins-Bold.ttf` into `res/font/` and
  update `ui/theme/Type.kt` (instructions inline in that file); currently falls back to the
  system font so the project builds out of the box.
- **App icon** — `res/drawable/ic_launcher_foreground.xml` is a placeholder glyph; replace
  with the finished shield icon from Section 2.2.
- **Community Feed, Beware, Missing Persons, Safe Route, city-problem reporting** — UI is
  complete but still reads/writes `MockRepository` rather than the live endpoints in
  `network/SafeZoneApiService.kt`; follow the same pattern as `AuthViewModel`/`SosViewModel`
  (a small `ViewModel` per screen, `ApiClient.getService(context)`, map the response into
  the existing `model/Models.kt` types).
- **Device token registration** — `SafeZoneMessagingService.onNewToken` has a TODO to POST
  the FCM token to the backend once a device-token endpoint exists.
- **Gradle wrapper** — `gradle/wrapper/gradle-wrapper.properties` is included, but the
  wrapper script/jar aren't (keeps the zip small). Run `gradle wrapper` once, or just open
  the project in Android Studio, which regenerates it automatically on sync.

## Building

Open the project root in Android Studio (Koala/2024.1+) and let it sync — `settings.gradle.kts`
already points at the `app` module. Minimum SDK 26, target/compile SDK 34, Kotlin 1.9.24,
Compose BOM 2024.06.00.

```
./gradlew assembleDebug
```

## Architecture note

Per Section 5 of the Planning & Design document, all business logic (RBAC, geo-targeted
notification computation, the ten-confirmation verification rule) lives server-side in the
ASP.NET Core API — the Android client is intentionally a thin(ner) presentation layer that
never talks to Azure SQL, Google Maps' backend services directly (only the client-side Maps
SDK), or the notification dispatch service directly.
