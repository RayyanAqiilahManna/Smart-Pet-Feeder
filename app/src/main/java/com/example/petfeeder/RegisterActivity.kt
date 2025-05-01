package com.example.petfeeder

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnRegister.setOnClickListener {
            val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Selamat!")
                .setMessage("Anda Baru Saja Terdaftar")
                .setPositiveButton("OK") { _, _ ->
                    // Navigate to LoginActivity
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish() // Optional: close RegisterActivity so user can’t go back with back button
                }
                .create()

            dialog.show()
        }

        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)
        val fullText = "Sudah punya akun? Masuk ke aplikasi"
        val spannable = SpannableString(fullText)

        val start = fullText.indexOf("Masuk ke aplikasi")
        val end = start + "Masuk ke aplikasi".length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            }
        }

        spannable.setSpan(
            ForegroundColorSpan("#F4A300".toColorInt()),
            start, end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvGoToLogin.text = spannable
        tvGoToLogin.movementMethod = LinkMovementMethod.getInstance()
    }
}
