package com.example.myreminder

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.max
import kotlin.math.min

class QuizActivity : AppCompatActivity() {

    private var correctCount = 0
    private var wrongCount = 0
    private var currentStep = 1

    private lateinit var audioManager: AudioManager
    private var maxVolume = 0
    private var startVolume = 0
    private var lockedVolume = 0

    private lateinit var tvQuestion: TextView
    private lateinit var tvProgress: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var radioGroup: RadioGroup
    private lateinit var btnNext: Button

    private lateinit var currentAnswer: String

    private val questions = listOf(
        Triple("Sholat Subuh ada berapa rakaat?", listOf("2", "3", "4"), "2"),
        Triple("Warna langit di siang hari adalah?", listOf("Biru", "Merah", "Hitam"), "Biru"),
        Triple("1 hari ada berapa jam?", listOf("12", "24", "30"), "24"),
        Triple("Huruf pertama dalam abjad adalah?", listOf("A", "B", "C"), "A"),
        Triple("Matahari terbit dari arah?", listOf("Timur", "Barat", "Utara"), "Timur")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        setContentView(R.layout.activity_quiz)

        tvQuestion = findViewById(R.id.tvQuestion)
        tvProgress = findViewById(R.id.tvProgress)
        progressBar = findViewById(R.id.progressQuiz)
        radioGroup = findViewById(R.id.radioGroup)
        btnNext = findViewById(R.id.btnNext)

        // 🔔 Nama alarm
        val alarmTitle = intent.getStringExtra("alarm_title") ?: "Alarm"
        findViewById<TextView>(R.id.tvAlarmTitle).text = alarmTitle

        // 🔊 Audio
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
        startVolume = max(audioManager.getStreamVolume(AudioManager.STREAM_ALARM), maxVolume / 4)
        lockedVolume = startVolume
        forceVolume()

        updateProgressUI()
        loadQuestion()

        btnNext.setOnClickListener {
            val checkedId = radioGroup.checkedRadioButtonId
            if (checkedId == -1) {
                Toast.makeText(this, "Pilih jawaban dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selected = findViewById<RadioButton>(checkedId).text.toString()

            if (selected == currentAnswer) {
                correctCount++
                currentStep++

                if (correctCount == 3) {
                    stopService(Intent(this, AlarmService::class.java))
                    Toast.makeText(this, "Alarm dimatikan!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    updateProgressUI()
                    loadQuestion()
                }

            } else {
                wrongCount++
                val step = (maxVolume - startVolume) / 3
                lockedVolume = min(startVolume + (step * wrongCount), maxVolume)
                forceVolume()
                Toast.makeText(this, "Salah! Volume naik", Toast.LENGTH_SHORT).show()
                loadQuestion()
            }
        }
    }

    private fun updateProgressUI() {
        tvProgress.text = "Pertanyaan $currentStep dari 3"

        val targetProgress = (currentStep - 1) * 33
        ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, targetProgress).apply {
            duration = 400
            start()
        }
    }

    private fun loadQuestion() {
        radioGroup.removeAllViews()
        val q = questions.random()
        tvQuestion.text = q.first
        currentAnswer = q.third

        q.second.shuffled().forEach {
            val rb = RadioButton(this)
            rb.text = it
            rb.textSize = 16f
            rb.setPadding(32, 24, 32, 24)
            rb.background = getDrawable(R.drawable.bg_option)
            rb.buttonDrawable = null
            rb.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 16
            }
            radioGroup.addView(rb)
        }
    }

    private fun forceVolume() {
        audioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            lockedVolume,
            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
        )
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
            event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
        ) {
            forceVolume()
            return true
        }
        return super.dispatchKeyEvent(event)
    }
}
