package com.example.myreminder

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddAlarmActivity : AppCompatActivity() {

    private val cal: Calendar = Calendar.getInstance()
    private var isDateSet = false
    private var isTimeSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alarm)

        // 🔐 IZIN EXACT ALARM (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            }
        }

        val etTitle = findViewById<EditText>(R.id.etTitle)
        val btnDate = findViewById<Button>(R.id.btnDate)
        val btnTime = findViewById<Button>(R.id.btnTime)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // 📅 PILIH TANGGAL
        btnDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    cal.set(Calendar.YEAR, y)
                    cal.set(Calendar.MONTH, m)
                    cal.set(Calendar.DAY_OF_MONTH, d)
                    isDateSet = true
                    btnDate.text = "Tanggal: $d/${m + 1}/$y"
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // ⏰ PILIH WAKTU
        btnTime.setOnClickListener {
            TimePickerDialog(
                this,
                { _, h, min ->
                    cal.set(Calendar.HOUR_OF_DAY, h)
                    cal.set(Calendar.MINUTE, min)
                    cal.set(Calendar.SECOND, 0)
                    isTimeSet = true
                    btnTime.text = String.format("Waktu: %02d:%02d", h, min)
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }

        // 💾 SIMPAN ALARM
        btnSave.setOnClickListener {

            val title = etTitle.text.toString().trim()

            if (title.isEmpty() || !isDateSet || !isTimeSet) {
                Toast.makeText(this, "Lengkapi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val alarmText = "$title - ${sdf.format(cal.time)}"

            try {
                val intent = Intent(this, AlarmReceiver::class.java)

                // ✅ INI KUNCI UTAMA (KIRIM JUDUL ALARM)
                intent.putExtra("alarm_title", title)

                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        cal.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        cal.timeInMillis,
                        pendingIntent
                    )
                }

            } catch (e: Exception) {
                Toast.makeText(this, "Gagal set alarm: ${e.message}", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // 👤 SIMPAN KE LIST (TETAP SAMA)
            val session = SessionManager(this)
            val email = session.getEmail() ?: return@setOnClickListener

            val pref = getSharedPreferences("alarm_data", MODE_PRIVATE)
            val key = "alarm_list_$email"

            val oldData = pref.getString(key, "") ?: ""
            val newData = if (oldData.isEmpty()) {
                alarmText
            } else {
                "$oldData||$alarmText"
            }

            pref.edit().putString(key, newData).apply()

            finish()
        }
    }
}
