package com.example.petfeeder

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.petfeeder.network.RetrofitClient
import com.example.petfeeder.network.TriggerRequest
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SettingsActivity : AppCompatActivity() {
    private val api = RetrofitClient.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // 1) Grab the TextView
        val tvUsername = findViewById<TextView>(R.id.tvUsername)

        // 2) Pull the signed-in user's name (or fallback to email prefix)
        FirebaseAuth.getInstance().currentUser?.let { user ->
            val display = user.displayName
                ?: user.email?.substringBefore("@")
                ?: "User"
            tvUsername.text = display
        }

        // 3) Wire up your buttons
        val btnLogout   = findViewById<Button>(R.id.btnLogout)
        val btnShutdown = findViewById<ImageButton>(R.id.btnShutdown)
        val btnRestart  = findViewById<ImageButton>(R.id.btnRestart)

        btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }

        btnShutdown.setOnClickListener {
            confirmAndSendCommand("shutdown")
        }

        btnRestart.setOnClickListener {
            confirmAndSendCommand("reboot")
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin logout?")
            .setPositiveButton("Ya") { _, _ ->
                // ✅ 1. Sign out from Firebase (clear session)
                FirebaseAuth.getInstance().signOut()

                // ✅ 2. Clear saved token from SharedPreferences
                val prefs = getSharedPreferences("auth", MODE_PRIVATE)
                prefs.edit().clear().apply()

                // ✅ 3. Redirect to SplashScreen
                val intent = Intent(this, SplashScreen::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun confirmAndSendCommand(command: String) {
        val (title, message) = if (command == "shutdown") {
            "Matikan Smart Pet Feeder" to "Apakah Anda yakin ingin mematikan Smart Pet Feeder?"
        } else {
            "Restart Raspberry Pi" to "Apakah Anda yakin ingin me-restart Smart Pet Feeder?"
        }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ya") { _, _ -> sendCommand(command) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun sendCommand(command: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = FirebaseAuth.getInstance()
                    .currentUser
                    ?.getIdToken(true)
                    ?.await()
                    ?.token

                if (token == null) {
                    showToast("Gagal mendapatkan token auth.")
                    return@launch
                }

                val request = TriggerRequest(token)
                val response = when (command) {
                    "shutdown" -> api.shutdown(request)
                    "reboot"   -> api.reboot(request)
                    else       -> return@launch
                }

                showToast("✅ ${response.message}")
            } catch (e: Exception) {
                showToast("❌ Gagal mengirim perintah: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this@SettingsActivity, message, Toast.LENGTH_LONG).show()
        }
    }
}
