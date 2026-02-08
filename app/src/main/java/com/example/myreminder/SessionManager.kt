package com.example.myreminder

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val pref: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun login(email: String) {
        pref.edit()
            .putString("email", email)
            .apply()
    }

    fun getEmail(): String? {
        return pref.getString("email", null)
    }

    fun logout() {
        pref.edit().clear().apply()
    }

    fun isLogin(): Boolean {
        return getEmail() != null
    }
}
