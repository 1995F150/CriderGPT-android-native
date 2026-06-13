package app.cridergpt.android.data

import app.cridergpt.android.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    // IMPORTANT: Do NOT hardcode keys here. Provide the URL and anon key via
    // BuildConfig or secure storage. This file uses BuildConfig fields expected
    // to be supplied by the build system (see ANDROID_DEBUG_NOTES.md).
    private val url: String = try { BuildConfig.SUPABASE_URL } catch (e: Throwable) { "" }
    private val key: String = try { BuildConfig.SUPABASE_ANON_KEY } catch (e: Throwable) { "" }

    val client: SupabaseClient? = if (url.isNotBlank() && key.isNotBlank()) {
        createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = key
        ) {
            install(Auth)
            install(Postgrest)
        }
    } else {
        null
    }
}
