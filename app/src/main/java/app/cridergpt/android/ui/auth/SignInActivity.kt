package app.cridergpt.android.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import app.cridergpt.android.BuildConfig
import app.cridergpt.android.R
import app.cridergpt.android.viewmodels.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class SignInActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel
    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken.isNullOrBlank()) {
                    Toast.makeText(this, "Google sign-in did not return an ID token.", Toast.LENGTH_LONG).show()
                } else {
                    viewModel.signInWithGoogleIdToken(idToken)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign-in failed: ${e.statusCode}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Google sign-in canceled.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val signInButton = findViewById<Button>(R.id.signInButton)
        val googleSignInButton = findViewById<Button>(R.id.googleSignInButton)
        val progress = findViewById<ProgressBar>(R.id.signInProgress)
        val signUpLink = findViewById<TextView>(R.id.signUpLink)

        signUpLink.setOnClickListener {
            Toast.makeText(this, "Sign-up flow not implemented", Toast.LENGTH_SHORT).show()
        }

        signInButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            viewModel.signIn(email, password)
        }

        googleSignInButton.setOnClickListener {
            startGoogleSignIn()
        }

        viewModel.isLoading.observe(this) { loading ->
            progress.visibility = if (loading) android.view.View.VISIBLE else android.view.View.GONE
            signInButton.isEnabled = !loading
            googleSignInButton.isEnabled = !loading
        }

        viewModel.signedIn.observe(this) { signedIn ->
            if (signedIn == true) {
                Toast.makeText(this, "Signed in", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.error.observe(this) { err ->
            err?.let { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
        }
    }

    private fun startGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(BuildConfig.SUPABASE_GOOGLE_WEB_CLIENT_ID)
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInLauncher.launch(googleSignInClient.signInIntent)
    }
}
