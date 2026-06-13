Debug notes and developer instructions

1) Supabase configuration
   - Do NOT commit service-role or secret keys.
   - Add the public anon key and URL to `app` by setting `buildConfigField("String", "SUPABASE_URL", '"https://..."')` and `buildConfigField("String", "SUPABASE_ANON_KEY", '"key"')` in `app/build.gradle`.

2) Google Sign-In
   - Add `google-services.json` to `app/` locally if using Firebase/Google services.
   - Keep `google-services.json` out of source control.

3) Building
   - Use the Gradle wrapper or local Gradle. If wrapper missing, run `gradle wrapper`.
   - Recommended command to build: `./gradlew assembleDebug`.

4) Reference repo usage
   - The original web repo was cloned temporarily to `_reference_repos/cridergpt` for analysis.
   - That folder will be removed; it's listed in `.gitignore`.
