package com.example.qlcafe.auth // Sửa lại package nếu của bạn khác

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.example.qlcafe.auth.LoginActivity // Chú ý import đúng đường dẫn LoginActivity của bạn

class SessionManager(var context: Context) {
    private val pref: SharedPreferences = context.getSharedPreferences("QLCafeSession", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()

    // 🕒 Cài đặt thời gian hết hạn: 30 phút (Tính bằng mili-giây: 30 * 60 * 1000)
    private val SESSION_EXPIRATION_TIME = 30 * 60 * 1000L

    // Lưu thông tin khi Đăng nhập thành công
    fun createLoginSession(id: Int, username: String, phone: String, role: String) {
        editor.putBoolean("IS_LOGGED_IN", true)
        editor.putInt("USER_ID", id)
        editor.putString("USERNAME", username)
        editor.putString("USER_PHONE", phone)
        editor.putString("USER_ROLE", role)

        // Bắt đầu bấm giờ: Lưu lại thời điểm hiện tại
        editor.putLong("LAST_ACTIVE_TIME", System.currentTimeMillis())

        editor.apply()
    }

    // Kiểm tra xem còn hạn hay không (Thường gọi lúc mới mở app)
    fun isLoggedIn(): Boolean {
        val isLoggedIn = pref.getBoolean("IS_LOGGED_IN", false)
        if (!isLoggedIn) return false // Chưa đăng nhập thì cút luôn

        // Lấy thời gian lần cuối truy cập ra
        val lastActiveTime = pref.getLong("LAST_ACTIVE_TIME", 0)
        val currentTime = System.currentTimeMillis()

        // Phép tính: Hiện tại - Quá khứ > 30 phút ?
        if (currentTime - lastActiveTime > SESSION_EXPIRATION_TIME) {
            // Đã quá 30 phút -> Hết hạn! Xóa sạch dữ liệu và trả về false
            logoutUser()
            return false
        } else {
            // Chưa tới 30 phút -> Vẫn còn hạn.
            // Cập nhật lại mốc thời gian mới để gia hạn thêm 30 phút nữa (Tính từ lúc này)
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

    // Hàm Đăng xuất (Dùng cho cả khi hết hạn hoặc khi người dùng tự bấm nút)
    fun logoutUser() {
        // Xóa sạch bách sành sanh bộ nhớ
        editor.clear()
        editor.apply()

        // Đá người dùng về thẳng trang Login
        val intent = Intent(context, LoginActivity::class.java)
        // Dòng cờ này giúp xóa hết các trang cũ đang mở, ngăn người dùng bấm nút "Back" quay lại
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }
}