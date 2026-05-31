package com.example.qlcafe.auth

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val pref: SharedPreferences = context.getSharedPreferences("QLCafePrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()

    fun createLoginSession(name: String, role: String) {
        editor.putBoolean("isLoggedIn", true)
        editor.putString("userName", name)
        editor.putString("userRole", role) // Phân quyền: manager, cashier, barista
        editor.apply()
    }

    fun getUserRole(): String {
        return pref.getString("userRole", "manager") ?: "manager"
        // Để mặc định tạm thời là "manager" để khi test bạn sẽ nhìn thấy đầy đủ toàn bộ menu tác vụ
    }
}