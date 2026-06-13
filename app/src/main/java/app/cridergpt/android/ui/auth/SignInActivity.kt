package app.cridergpt.android.ui.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import app.cridergpt.android.R
import app.cridergpt.android.viewmodels.AuthViewModel

class SignInActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val signInButton = findViewById<Button>(R.id.signInButton)
        val progress = findViewById<ProgressBar>(R.id.signInProgress)
        val signUpLink = findViewById<TextView>(R.id.signUpLink)

        signInButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            viewModel.signIn(email, password)
        }

        signUpLink.setOnClickListener {
            Toast.makeText(this, "Sign-up flow not implemented", Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(this) { loading ->
            progress.visibility = if (loading) android.view.View.VISIBLE else android.view.View.GONE
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
}
