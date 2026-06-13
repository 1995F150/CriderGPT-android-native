Debug notes and developer instructions

1) Supabase configuration
   - Do NOT commit service-role or secret keys.
   - `app/build.gradle` already includes `SUPABASE_URL` and `SUPABASE_ANON_KEY` build config fields.
   - `SUPABASE_GOOGLE_WEB_CLIENT_ID` is present but must be replaced with the actual Google OAuth web client ID.
   - The app currently uses direct Supabase REST auth for password and Google ID token grants.
   - The reference web app uses `supabase.auth.signInWithOAuth({ provider: 'google' })`, which is not the same native flow as the current Android implementation.

2) Google Sign-In and native OAuth
   - `google-services.json` is not sufficient by itself for Supabase native Google auth.
   - The Android native flow requires a valid Google OAuth client and matching Supabase provider configuration.
   - The current code uses `GoogleSignInOptions.requestIdToken(...)` and then calls `/auth/v1/token?grant_type=id_token&provider=google`.
   - Verify the OAuth client is configured in the Google Cloud console and allowed by Supabase.

3) Billing and subscription flow
   - Reference web payment flow uses Supabase Edge Functions and Stripe customer portal integration.
   - Android currently includes a Play Billing skeleton but no Stripe or backend portal integration.
   - `PaymentManager.kt` contains a duplicate `PaymentManager` object; remove the placeholder duplicate and keep a single billing manager.

4) Build and verification
   - Use the Gradle wrapper if present or install Gradle locally.
   - Fix the duplicate `PaymentManager` compilation issue before running the build.
   - Run `./gradlew assembleDebug` and inspect warnings/errors for missing resources, manifest declarations, or invalid build config values.

5) Feature parity notes
   - The web app’s auth, chat, payment, profile, and settings flows are not yet mirrored in Android.
   - The Android project currently provides packaging and dependency scaffolding, but most feature logic is absent or incomplete.
