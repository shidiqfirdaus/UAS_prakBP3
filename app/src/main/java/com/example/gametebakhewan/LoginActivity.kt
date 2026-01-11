package com.example.gametebakhewan

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = getSharedPreferences("USER_PREF", MODE_PRIVATE)
        if (pref.getBoolean("isLogin", false)) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            if (etUsername.text.isEmpty() || etPassword.text.isEmpty()) {
                Toast.makeText(this, "Isi semua data!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            pref.edit()
                .putString("username", etUsername.text.toString())
                .putBoolean("isLogin", true)
                .apply()

            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }
}
