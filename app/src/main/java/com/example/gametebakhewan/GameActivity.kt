package com.example.gametebakhewan

import android.content.Intent
import android.graphics.Color
import android.os.*
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private lateinit var imgAnimal: ImageView
    private lateinit var tvScore: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvLevel: TextView

    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button

    private var score = 0
    private var level = 1
    private lateinit var correctAnswer: String
    private lateinit var timer: CountDownTimer

    data class Animal(val image: Int, val name: String)

    private val animals = listOf(
        Animal(R.drawable.kucing, "Kucing"),
        Animal(R.drawable.gajah, "Gajah"),
        Animal(R.drawable.harimau, "Harimau"),
        Animal(R.drawable.panda, "Panda"),
        Animal(R.drawable.singa, "Singa")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        imgAnimal = findViewById(R.id.imgAnimal)
        tvScore = findViewById(R.id.tvScore)
        tvTimer = findViewById(R.id.tvTimer)
        tvLevel = findViewById(R.id.tvLevel)

        btn1 = findViewById(R.id.btnOption1)
        btn2 = findViewById(R.id.btnOption2)
        btn3 = findViewById(R.id.btnOption3)
        btn4 = findViewById(R.id.btnOption4)

        val clickListener = View.OnClickListener { checkAnswer(it as Button) }
        btn1.setOnClickListener(clickListener)
        btn2.setOnClickListener(clickListener)
        btn3.setOnClickListener(clickListener)
        btn4.setOnClickListener(clickListener)

        // Ambil level dari DashboardActivity
        val selectedLevel = intent.getStringExtra("level") ?: "Easy"
        tvLevel.text = "Level: $selectedLevel"

        // Tentukan waktu berdasarkan level
        level = when(selectedLevel) {
            "Easy" -> 1
            "Medium" -> 2
            "Hard" -> 3
            else -> 1
        }

        startTimer()
        loadQuestion()
    }

    private fun loadQuestion() {
        resetButtonColor()

        val animal = animals.random()
        imgAnimal.setImageResource(animal.image)
        correctAnswer = animal.name

        val optionCount = 4 // tetap 4 tombol
        val wrongAnswers = animals.filter { it.name != correctAnswer }
            .shuffled()
            .take(optionCount - 1)
            .map { it.name }

        val options = mutableListOf(correctAnswer)
        options.addAll(wrongAnswers)
        options.shuffle()

        val buttons = listOf(btn1, btn2, btn3, btn4)
        buttons.forEach { it.visibility = View.GONE }

        for (i in options.indices) {
            buttons[i].visibility = View.VISIBLE
            buttons[i].text = options[i]
        }
    }

    private fun checkAnswer(button: Button) {
        disableButtons()

        if (button.text == correctAnswer) {
            score += 10
            tvScore.text = "Skor: $score"
            button.setBackgroundColor(Color.parseColor("#4CAF50"))
            imgAnimal.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_up))
            Toast.makeText(this, "Benar 🎉", Toast.LENGTH_SHORT).show()
        } else {
            button.setBackgroundColor(Color.parseColor("#F44336"))
            imgAnimal.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
            Toast.makeText(this, "Salah 😢", Toast.LENGTH_SHORT).show()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            enableButtons()
            loadQuestion()
        }, 900)
    }

    private fun startTimer() {
        val totalTime = when(level) {
            1 -> 40000L // Mudah 40 detik
            2 -> 30000L // Sedang 30 detik
            else -> 20000L // Sulit 20 detik
        }

        timer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvTimer.text = "Waktu: ${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                startActivity(Intent(this@GameActivity, ScoreActivity::class.java)
                    .putExtra("score", score))
                finish()
            }
        }.start()
    }

    private fun disableButtons() {
        btn1.isEnabled = false
        btn2.isEnabled = false
        btn3.isEnabled = false
        btn4.isEnabled = false
    }

    private fun enableButtons() {
        btn1.isEnabled = true
        btn2.isEnabled = true
        btn3.isEnabled = true
        btn4.isEnabled = true
    }

    private fun resetButtonColor() {
        listOf(btn1, btn2, btn3, btn4).forEach {
            it.setBackgroundColor(Color.parseColor("#2196F3"))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}
