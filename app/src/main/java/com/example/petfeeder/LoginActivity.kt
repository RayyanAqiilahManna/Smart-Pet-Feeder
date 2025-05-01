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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Tombol MASUK -> ke halaman utama (atau halaman lain yang kamu inginkan)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            // TODO: Tambahkan logika autentikasi di sini jika perlu
            startActivity(Intent(this, MainActivity::class.java)) // ganti ke halaman tujuan
        }

        // Teks "Daftar Sekarang" bisa diklik dan berwarna kuning
        val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)
        val fullText = "Belum punya akun? Daftar Sekarang"
        val spannable = SpannableString(fullText)

        val start = fullText.indexOf("Daftar Sekarang")
        val end = start + "Daftar Sekarang".length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }

        spannable.setSpan(
            ForegroundColorSpan("#F4A300".toColorInt()), // kuning
            start, end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            clickableSpan,
            start, end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tvGoToRegister.text = spannable
        tvGoToRegister.movementMethod = LinkMovementMethod.getInstance()
    }
}