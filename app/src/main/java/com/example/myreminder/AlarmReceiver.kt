package com.example.myreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        // 🔥 WAKELOCK (KUNCI AGAR ALARM BUNYI DI HP)
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = pm.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "MyReminder::AlarmWakeLock"
        )
        wakeLock.acquire(5 * 60 * 1000L) // 5 menit (AMAN)

        // 🔔 AMBIL JUDUL ALARM
        val alarmTitle = intent.getStringExtra("alarm_title") ?: "Alarm"

        // 🔊 START SERVICE
        val serviceIntent = Intent(context, AlarmService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        // 🧠 BUKA QUIZ
        val quizIntent = Intent(context, QuizActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("alarm_title", alarmTitle)
        }

        context.startActivity(quizIntent)
    }
}
