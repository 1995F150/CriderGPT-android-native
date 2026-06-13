This file lists missing or partial features and current gaps compared to the reference repo.

- Native OAuth and session handling
  - Google sign-in is stubbed, but not wired to a complete native OAuth redirect or deep-link flow.
  - `SUPABASE_GOOGLE_WEB_CLIENT_ID` is still a placeholder.
  - Session refresh and auto-login are not implemented.

- Chat and conversation flows
  - `ChatViewModel.kt` and `ChatActivity.kt` are placeholders only.
  - No chat UI, no message rendering, no conversation list, and no Supabase function integration.
  - Key reference flows such as `chat-operations` and chat table queries are absent.

- Payments and subscription management
  - Billing code is not integrated with the reference Stripe customer portal flow.
  - `PaymentManager.kt` has a duplicate object definition and will fail to compile.
  - No backend verification of purchase tokens or Stripe subscription state.

- App navigation and shell
  - `MainActivity.kt` is static and does not route users to authentication, chat, profile, or payment screens.
  - No navigation drawer, sidebar, or mobile shell equivalent.

- Profile / account / settings
  - No screens or data binding for profile, account settings, tokens/credits, usage stats, or AI settings.

- Backend alignment
  - Reference repo uses Supabase functions, session persistence, and database tables; Android currently lacks those data integrations.

- Build and CI gaps
  - No Android Gradle wrapper in repo; if missing, add it or use local Gradle.
  - No tests, no instrumentation, no static analysis in place.
