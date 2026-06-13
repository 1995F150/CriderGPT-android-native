# Android Reconciliation Checklist

## Summary
This checklist compares the current Android native app with the reference web app repository.
It documents what is implemented, what is partially implemented, and what is missing.

> Current state: the Android app has scaffolding, a password auth path, and billing skeletons, but it does not yet match the web app's auth flow, chat/backend integration, or subscription management.

## 1. Project Setup

- [x] Android Gradle project exists (`settings.gradle`, `build.gradle`, `app/build.gradle`).
- [x] Package name is consistent: `app.cridergpt.android`.
- [x] Supabase configuration fields exist in `app/build.gradle`.
- [ ] `SUPABASE_GOOGLE_WEB_CLIENT_ID` is still a placeholder and must be replaced with the real OAuth web client ID.
- [ ] `google-services.json` is present in app, but native Supabase Google auth requires a valid OAuth client configuration beyond Firebase services.

## 2. Authentication

### Reference web app behavior
- `src/integrations/supabase/client.ts`: creates Supabase client with auth storage and session persistence.
- `src/contexts/AuthContext.tsx`: calls `supabase.auth.getSession()` and `supabase.auth.onAuthStateChange(...)`.
- `src/components/GoogleSignInButton.tsx`: uses `supabase.auth.signInWithOAuth({ provider: 'google' })` and browser popup/redirect.

### Android implementation
- [x] `AuthViewModel.kt` implements password auth via Supabase `/auth/v1/token?grant_type=password`.
- [x] `SignInActivity.kt` implements native Google sign-in using `GoogleSignInOptions.requestIdToken(...)`.
- [ ] `AuthViewModel.kt` does not implement `getSession()`, `refreshSession()`, sign-out, or automatic session restoration.
- [ ] `SignInActivity.kt` does not implement sign-up, password reset, or user creation.
- [ ] Native Google auth is partial: it uses ID token grant, but the web app uses a browser OAuth redirect flow. This needs validation against Supabase provider settings.
- [ ] No Supabase auth client is used for session lifecycle; the current implementation uses manual OkHttp REST calls.

## 3. Chat and AI Interaction

### Reference web app behavior
- `src/components/ChatInterface.tsx`: loads conversations and messages, calls Supabase tables and functions.
- Uses Supabase `chat_conversations`, `chat_messages`, and `chat-operations` edge function.
- Supports creating conversations, sending messages, deleting conversations, and loading chat history.

### Android implementation
- [ ] `ChatViewModel.kt` is a placeholder with no chat logic.
- [ ] `ChatActivity.kt` is a placeholder with no UI and no Supabase integration.
- [ ] No support for conversations, messages, or chat function invocation.
- [ ] No AI response handling, streaming, or conversation state exists.

## 4. Subscription / Payments

### Reference web app behavior
- `src/components/ManageSubscription.tsx`: invokes Supabase edge function `customer-portal` with the user session token.
- Payment management is handled through Stripe customer portal and Supabase functions.

### Android implementation
- [x] `PaymentManager.kt` contains a Google Play Billing skeleton.
- [x] `PaymentActivity.kt` presents subscription buttons and calls `PaymentManager.purchase(...)`.
- [ ] `PaymentManager.kt` does not validate purchases with backend or Stripe.
- [ ] There is no Stripe customer portal or `customer-portal` function integration.
- [ ] The Android payment flow does not match the web app's subscription management architecture.

## 5. Profile and Settings

### Reference web app behavior
- Has profile/account screens and settings components: `AccountSettings.tsx`, `AISettings.tsx`, `ProfileMenu.tsx`, `UsageStats.tsx`, `TokensCredits.tsx`.

### Android implementation
- [ ] No profile, account settings, usage stats, or tokens/credits screens are implemented.
- [ ] No user preference or AI settings module is present.

## 6. Navigation and App Shell

### Reference web app behavior
- Uses mobile navigation and sidebar components: `MobileNavigation.tsx`, `NavigationSidebar.tsx`, `ProtectedRoute.tsx`.
- The web app has a full shell for switching between chat, dashboard, subscription, and profile.

### Android implementation
- [ ] `MainActivity.kt` is a placeholder static screen and does not implement navigation.
- [ ] No bottom navigation, drawer, or multi-screen app shell exists.
- [ ] `DashboardActivity.kt` is empty.

## 7. Backend and Data Layer

### Reference web app behavior
- Uses `supabase.auth`, table queries, and edge functions with full session context.
- Supabase client is created with `persistSession: true` and `autoRefreshToken: true`.

### Android implementation
- [x] `SupabaseClient.kt` creates a Supabase client from BuildConfig values.
- [ ] The client is not used in auth/session code, and no tables are queried.
- [ ] No edge-function integration is implemented.
- [ ] No local data models, persistence beyond shared preferences, or offline behavior are implemented.

## 8. Current Blocking Issues

- [ ] Android auth does not fully match the reference web app flow.
- [ ] Google sign-in client ID is not set and likely not configured correctly for Supabase.
- [ ] Missing chat conversation/message flows.
- [ ] Missing subscription backend integration and billing validation.
- [ ] Missing navigation shell, profile/settings, and dashboard screens.
- [ ] No sign-out or session lifecycle management.

## 9. Recommended Next Steps

1. Replace `SUPABASE_GOOGLE_WEB_CLIENT_ID` with the actual Google OAuth client ID.
2. Implement Supabase session lifecycle: `getSession()`, `refreshSession()`, `onAuthStateChange()`, and sign-out.
3. Build the native chat UI and integrate with Supabase tables/functions used by the web app.
4. Implement subscription management using the same backend edge functions/Stripe portal model as the web app or document a native equivalent.
5. Add a real Android app shell and navigation flow connecting auth, chat, dashboard, payment, and profile.
6. Validate the app with `./gradlew assembleDebug` and fix any compile errors or missing manifest entries.
