package com.example.petfeeder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import androidx.activity.result.IntentSenderRequest

class LoginActivity : AppCompatActivity() {

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var firebaseAuth: FirebaseAuth

    // Replace this with your Cloud Run API endpoint
    private val BACKEND_URL = "https://spf-backend-566730574934.us-central1.run.app/login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id)) // from google-services.json
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        val btnGoogleSignIn = findViewById<Button>(R.id.btnGoogleSignIn)
        btnGoogleSignIn.setOnClickListener {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener { result ->
                    launcher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
                    Log.e("LoginActivity", "Google Sign-In failed", e)
                }
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken
            val googleCredentials = GoogleAuthProvider.getCredential(idToken, null)

            firebaseAuth.signInWithCredential(googleCredentials)
                .addOnSuccessListener {
                    firebaseAuth.currentUser?.getIdToken(true)
                        ?.addOnSuccessListener { firebaseToken ->
                            val token = firebaseToken.token
                            sendTokenToCloudRun(token)
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Firebase Auth failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun sendTokenToCloudRun(idToken: String?) {
        if (idToken == null) {
            Toast.makeText(this, "ID Token is null", Toast.LENGTH_SHORT).show()
            return
        }

        val client = OkHttpClient()
        val json = JSONObject().apply {
            put("token", idToken) // Match FastAPI's TokenModel
        }

        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())

        val request = Request.Builder()
            .url(BACKEND_URL)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Network error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("LoginActivity", "Response from Cloud Run: $responseBody")
                if (response.isSuccessful) {
                    runOnUiThread {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
