package com.example.gametebakhewan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val btnEasy = findViewById<Button>(R.id.btnEasy)
        val btnMedium = findViewById<Button>(R.id.btnMedium)
        val btnHard = findViewById<Button>(R.id.btnHard)

        val btnRanking = findViewById<Button>(R.id.btnRanking)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnEasy.setOnClickListener { startGame("Easy") }
        btnMedium.setOnClickListener { startGame("Medium") }
        btnHard.setOnClickListener { startGame("Hard") }

        btnRanking.setOnClickListener {
            startActivity(Intent(this, RankingActivity::class.java))
        }

        btnLogout.setOnClickListener {
            // Kembali ke LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun startGame(level: String) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("level", level)
        startActivity(intent)
    }
}
