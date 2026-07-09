package com.example.qlcafe.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

class SessionManager(var context: Context) {
    private val pref: SharedPreferences = context.getSharedPreferences("QLCafeSession", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()

    // Cài đặt thời gian
    private val SESSION_EXPIRATION_TIME = 30 * 60 * 1000L

    // Lưu thông tin khi Đăng nhập thành công (Đã bao gồm dacQuyen)
    fun createLoginSession(id: Int, username: String, phone: String, role: String, dacQuyen: String) {
        editor.putBoolean("IS_LOGGED_IN", true)
        editor.putInt("USER_ID", id)
        editor.putString("USERNAME", username)
        editor.putString("USER_PHONE", phone)
        editor.putString("USER_ROLE", role)
        editor.putString("USER_DAC_QUYEN", dacQuyen)

        // Bắt đầu bấm giờ: Lưu lại thời điểm hiện tại
        editor.putLong("LAST_ACTIVE_TIME", System.currentTimeMillis())

        editor.apply()
    }

    // Kiểm tra xem còn hạn hay không
    fun isLoggedIn(): Boolean {
        val isLoggedIn = pref.getBoolean("IS_LOGGED_IN", false)
        if (!isLoggedIn) return false // Chưa đăng nhập thì cút luôn

        val lastActiveTime = pref.getLong("LAST_ACTIVE_TIME", 0)
        val currentTime = System.currentTimeMillis()

        // công thức
        if (currentTime - lastActiveTime > SESSION_EXPIRATION_TIME) {
            logoutUser()
            return false
        } else {
            // Cập nhật lại mốc thời gian mới để gia hạn thêm 30 phút nữa
            editor.putLong("LAST_ACTIVE_TIME", currentTime)
            editor.apply()
            return true
        }
    }

    fun getUserName(): String {
        return pref.getString("USERNAME", "Nhân Viên") ?: "Nhân Viên"
    }

    fun getUserRole(): String {
        return pref.getString("USER_ROLE", "employee") ?: "employee"
    }

    fun getUserId(): Int {
        return pref.getInt("USER_ID", -1)
    }

    fun getUserPhone(): String {
        return pref.getString("USER_PHONE", "") ?: ""
    }

    fun saveUserExtraPermissions(dacQuyen: String) {
        editor.putString("USER_DAC_QUYEN", dacQuyen)
        editor.apply()
    }

    fun getUserExtraPermissions(): String {
        return pref.getString("USER_DAC_QUYEN", "") ?: ""
    }

    fun logoutUser() {
        editor.clear()
        editor.apply()

        val intent = Intent(context, LoginActivity::class.java)
        // Dòng này giúp xóa hết các trang cũ đang mở, ngăn người dùng bấm nút "Back" quay lại
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }

    fun addHiddenNotification(notifId: Int) {
        val hiddenIds = getHiddenNotifications().toMutableSet()
        hiddenIds.add(notifId)
        val setString = hiddenIds.joinToString(",")
        editor.putString("hidden_notifications", setString)
        editor.apply()
    }

    fun getHiddenNotifications(): Set<Int> {
        val setString = pref.getString("hidden_notifications", "") ?: ""
        if (setString.isEmpty()) return emptySet()
        return setString.split(",").map { it.toInt() }.toSet()
    }
}