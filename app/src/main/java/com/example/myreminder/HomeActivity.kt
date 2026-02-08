package com.example.myreminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var alarmContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ CEK SESSION (AUTO LOGIN)
        val session = SessionManager(this)
        if (!session.isLogin()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_home)

        alarmContainer = findViewById(R.id.alarmContainer)

        // ================= HEADER =================
        val tvGreeting = findViewById<TextView>(R.id.tvGreeting)
        val tvDate = findViewById<TextView>(R.id.tvDate)

        // 👋 Hallo, username
        val username = session.getEmail()
        tvGreeting.text = "Hallo, $username"

        // 📅 Tanggal hari ini
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        tvDate.text = dateFormat.format(Date())

        // ================= BUTTON =================
        findViewById<Button>(R.id.btnAdd).setOnClickListener {
            startActivity(Intent(this, AddAlarmActivity::class.java))
        }

        // ✅ FIX DI SINI (ImageView / View, BUKAN Button)
        findViewById<View>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadAlarmData()
    }

    private fun loadAlarmData() {
        alarmContainer.removeAllViews()

        val session = SessionManager(this)
        val email = session.getEmail() ?: return

        val pref = getSharedPreferences("alarm_data", MODE_PRIVATE)
        val key = "alarm_list_$email"
        val data = pref.getString(key, "") ?: ""

        if (data.isEmpty()) {
            val tv = TextView(this)
            tv.text = "Belum ada alarm"
            alarmContainer.addView(tv)
            return
        }

        val alarmList = data.split("||").toMutableList()

        alarmList.forEachIndexed { index, alarmText ->

            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL
            row.setPadding(0, 8, 0, 8)

            val tv = TextView(this)
            tv.text = alarmText
            tv.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )

            val btnEdit = Button(this)
            btnEdit.text = "Edit"

            val btnDelete = Button(this)
            btnDelete.text = "Hapus"

            // ✏️ EDIT
            btnEdit.setOnClickListener {
                startActivity(Intent(this, AddAlarmActivity::class.java))
            }

            // 🗑️ HAPUS + BATALKAN ALARM
            btnDelete.setOnClickListener {

                val intent = Intent(this, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(pendingIntent)

                alarmList.removeAt(index)
                val newData = alarmList.joinToString("||")
                pref.edit().putString(key, newData).apply()

                Toast.makeText(this, "Alarm dihapus", Toast.LENGTH_SHORT).show()
                loadAlarmData()
            }

            row.addView(tv)
            row.addView(btnEdit)
            row.addView(btnDelete)

            alarmContainer.addView(row)
        }
    }
}
