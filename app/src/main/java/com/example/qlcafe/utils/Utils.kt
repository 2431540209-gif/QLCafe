package com.example.qlcafe.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.qlcafe.R

// Tạo một "hàm mở rộng" gắn thẳng vào tất cả các Activity
fun AppCompatActivity.setupTopBar(tieuDe: String) {

    val tvTitle = findViewById<TextView>(R.id.tvTitle)
    tvTitle?.text = tieuDe

    val btnBack = findViewById<ImageView>(R.id.btnBack)
    btnBack?.setOnClickListener {
        onBackPressedDispatcher.onBackPressed()
    }
}