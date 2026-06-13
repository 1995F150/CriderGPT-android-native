Status: Scaffolded Android project from reference repo

- Gradle project structure: created `settings.gradle`, `build.gradle`, and `app/build.gradle`.
- Package name: `com.cridergpt.android` (from reference Android app)
- Supabase auth: `SupabaseClient.kt` placeholder added; keys are NOT included.
- Google Sign-In: dependency added to `app/build.gradle` (implementation only).
- Session persistence: placeholder `AuthViewModel` and `ChatViewModel` created.
- Dashboard & Chat: placeholder activities added.
- Payments/subscriptions: `PaymentManager` placeholder added.

Next steps to verify build:
1. Add Android Gradle wrapper or use local Gradle installation.
2. Provide `SUPABASE_URL` and `SUPABASE_ANON_KEY` via `build.gradle` `buildConfigField` or secure env.
3. Optionally add `google-services.json` if using Firebase/Google services (do NOT commit it).
4. Run `./gradlew assembleDebug` to verify.
