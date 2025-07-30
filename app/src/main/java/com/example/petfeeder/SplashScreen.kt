package com.example.petfeeder

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Show splash screen layout first
        setContentView(R.layout.activity_splash_screen)

        // ✅ Check if the user is already logged in
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            // ✅ User is already logged in → skip login and go to MainActivity (Monitoring)
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Close Splash so user can't go back to it
        } else {
            // ❌ User is NOT logged in → wait for “Masuk” button press
            val btnMasuk = findViewById<Button>(R.id.btnMasuk)
            btnMasuk.setOnClickListener {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()  // ✅ Close Splash so user can’t go back with back button
            }
        }
    }
}
