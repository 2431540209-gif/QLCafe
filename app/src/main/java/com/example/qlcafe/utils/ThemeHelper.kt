package com.example.qlcafe.utils

import android.view.View
import android.util.TypedValue

object ThemeHelper {
    
    private fun isColorLight(color: Int): Boolean {
        val darkness = 1 - (0.299 * android.graphics.Color.red(color) + 0.587 * android.graphics.Color.green(color) + 0.114 * android.graphics.Color.blue(color)) / 255
        return darkness < 0.35 // Lower than 0.35 means light
    }

    /**
     * Hàm áp dụng giao diện đồng nhất cho các View (Card, Item, v.v.)
     * Hỗ trợ tự động đổi màu cho chữ, nền thẻ, nền ô nhập liệu, v.v.
     */
    fun applyTheme(view: View) {
        val isNight = (view.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
        
        val bgColor = if (isNight) android.graphics.Color.parseColor("#121212") else android.graphics.Color.parseColor("#F2F5F9")
        val cardBgColor = if (isNight) android.graphics.Color.parseColor("#1E1E1E") else android.graphics.Color.WHITE
        val primaryTextColor = if (isNight) android.graphics.Color.parseColor("#E0E0E0") else android.graphics.Color.parseColor("#1A1A1A")
        val secondaryTextColor = if (isNight) android.graphics.Color.parseColor("#A0A0A0") else android.graphics.Color.parseColor("#666666")
        val accentColor = if (isNight) android.graphics.Color.parseColor("#FFB74D") else android.graphics.Color.parseColor("#4A2C11") // Màu thương hiệu tông ấm / nâu cafe

        when (view) {
            is androidx.cardview.widget.CardView -> {
                view.setCardBackgroundColor(cardBgColor)
            }
            is android.widget.EditText -> {
                view.setTextColor(primaryTextColor)
                view.setHintTextColor(secondaryTextColor)
                
                // Nền bo góc tinh tế cho ô nhập mật khẩu/văn bản
                val etBgColor = if (isNight) android.graphics.Color.parseColor("#2A2A2A") else android.graphics.Color.parseColor("#F5F5F5")
                val etStrokeColor = if (isNight) android.graphics.Color.parseColor("#424242") else android.graphics.Color.parseColor("#E0E0E0")
                val radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, view.resources.displayMetrics)
                val strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, view.resources.displayMetrics).toInt()
                
                view.background = android.graphics.drawable.GradientDrawable().apply {
                    setColor(etBgColor)
                    cornerRadius = radius
                    setStroke(strokeWidth, etStrokeColor)
                }
            }
            is android.widget.CompoundButton -> { // Switch, CheckBox, RadioButton
                view.setTextColor(primaryTextColor)
                if (isNight) {
                    if (view is android.widget.Switch) {
                        view.thumbTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFB74D"))
                        view.trackTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#555555"))
                    }
                } else {
                    if (view is android.widget.Switch) {
                        view.thumbTintList = null
                        view.trackTintList = null
                    }
                }
            }
            is android.widget.TextView -> {
                val currentTextColor = view.currentTextColor
                val text = view.text.toString().trim()
                
                if (isNight) {
                    // Đổi các chữ màu tối thành chữ màu sáng
                    if (currentTextColor == android.graphics.Color.parseColor("#1A1A1A") || currentTextColor == android.graphics.Color.BLACK) {
                        view.setTextColor(primaryTextColor)
                    } else if (currentTextColor == android.graphics.Color.parseColor("#4A2C11")) {
                        // Tiêu đề thương hiệu
                        view.setTextColor(accentColor)
                    } else if (currentTextColor == android.graphics.Color.parseColor("#666666") || currentTextColor == android.graphics.Color.parseColor("#888888") || currentTextColor == android.graphics.Color.parseColor("#555555")) {
                        view.setTextColor(secondaryTextColor)
                    }
                    
                    // Đồng bộ thêm nếu text có các từ khóa tiêu đề hoặc nhãn phụ
                    if (text == "Hồ sơ cá nhân" || text == "Thao tác nhanh" || text == "Cài đặt" || text == "Đổi mật khẩu") {
                        if (text == "Hồ sơ cá nhân") {
                            view.setTextColor(android.graphics.Color.WHITE)
                        } else if (text == "Thao tác nhanh" || text == "Cài đặt") {
                            view.setTextColor(secondaryTextColor)
                        }
                    }
                } else {
                    // Trả lại màu sáng khi khôi phục hoặc dùng lại đối tượng
                    if (currentTextColor == android.graphics.Color.parseColor("#E0E0E0") || currentTextColor == android.graphics.Color.WHITE) {
                        view.setTextColor(android.graphics.Color.parseColor("#1A1A1A"))
                    } else if (currentTextColor == android.graphics.Color.parseColor("#FFB74D")) {
                        view.setTextColor(android.graphics.Color.parseColor("#4A2C11"))
                    } else if (currentTextColor == android.graphics.Color.parseColor("#A0A0A0")) {
                        view.setTextColor(android.graphics.Color.parseColor("#666666"))
                    }
                }
            }
            is android.view.ViewGroup -> {
                val background = view.background
                if (background is android.graphics.drawable.ColorDrawable) {
                    val color = background.color
                    if (isNight) {
                        if (isColorLight(color)) {
                            view.setBackgroundColor(bgColor)
                        }
                    } else {
                        if (color == android.graphics.Color.parseColor("#121212") || color == android.graphics.Color.parseColor("#1A1A1A")) {
                            view.setBackgroundColor(android.graphics.Color.parseColor("#F2F5F9"))
                        }
                    }
                }
            }
        }

        // Đệ quy đổi màu các View con bên trong
        if (view is android.view.ViewGroup) {
            for (i in 0 until view.childCount) {
                applyTheme(view.getChildAt(i))
            }
        }
    }
}
