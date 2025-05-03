package com.example.petfeeder

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Button: Login with Google (redirects to MainActivity after logic is handled)
        val btnGoogleSignIn = findViewById<Button>(R.id.btnGoogleSignIn)
        btnGoogleSignIn.setOnClickListener {
            // TODO: Replace this with actual Google Sign-In logic later
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Optional: finish login activity so it doesn't stay in back stack
        }
    }
}