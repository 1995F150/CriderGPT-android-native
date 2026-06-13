package app.cridergpt.android.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cridergpt.android.CriderGPTApplication
import app.cridergpt.android.BuildConfig
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class AuthViewModel : ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _signedIn = MutableLiveData<Boolean>(false)
    val signedIn: LiveData<Boolean> = _signedIn

    private val client = OkHttpClient()
    private val gson = Gson()

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _error.value = "Email and password are required"
            return
        }

        performSupabaseAuth(
            requestUrl = BuildConfig.SUPABASE_URL.trimEnd('/') + "/auth/v1/token?grant_type=password",
            requestBody = mapOf("email" to email, "password" to password)
        )
    }

    fun signInWithGoogleIdToken(idToken: String) {
        if (idToken.isBlank()) {
            _error.value = "Google ID token is required"
            return
        }

        performSupabaseAuth(
            requestUrl = BuildConfig.SUPABASE_URL.trimEnd('/') + "/auth/v1/token?grant_type=id_token&provider=google",
            requestBody = mapOf("id_token" to idToken)
        )
    }

    private fun performSupabaseAuth(requestUrl: String, requestBody: Map<String, String>) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val bodyJson = gson.toJson(requestBody)
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val request = Request.Builder()
                    .url(requestUrl)
                    .post(bodyJson.toRequestBody(mediaType))
                    .addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
                val respBody = response.body?.string()
                if (!response.isSuccessful) {
                    Log.e("AuthViewModel", "Sign-in failed: ${response.code} ${respBody}")
                    _error.postValue("Sign-in failed: ${response.code}")
                    _signedIn.postValue(false)
                } else {
                    val tokenResponse = gson.fromJson(respBody, SupabaseTokenResponse::class.java)
                    if (tokenResponse.access_token.isNullOrBlank()) {
                        _error.postValue("Sign-in failed: no access token returned")
                        _signedIn.postValue(false)
                    } else {
                        persistSession(tokenResponse)
                        _signedIn.postValue(true)
                    }
                }
            } catch (e: Throwable) {
                Log.e("AuthViewModel", "Sign-in failed", e)
                _error.postValue(e.message ?: "Sign-in failed")
                _signedIn.postValue(false)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private fun persistSession(tokenResponse: SupabaseTokenResponse) {
        try {
            val ctx: Context = CriderGPTApplication.instance.applicationContext
            val prefs = ctx.getSharedPreferences("cridergpt_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("supabase_access_token", tokenResponse.access_token)
                .putString("supabase_refresh_token", tokenResponse.refresh_token)
                .putLong("supabase_token_expires_at", System.currentTimeMillis() + ((tokenResponse.expires_in ?: 0) * 1000))
                .apply()
        } catch (e: Throwable) {
            Log.w("AuthViewModel", "Failed to persist session", e)
        }
    }

    private data class SupabaseTokenResponse(
        val access_token: String?,
        val refresh_token: String?,
        val expires_in: Int?
    )
}
