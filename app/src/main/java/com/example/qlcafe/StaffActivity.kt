package com.example.qlcafe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView // Dòng này cực quan trọng

class StaffActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff)

        // Tìm RecyclerView theo ID
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        // Dữ liệu mẫu
        val data = listOf("Nguyễn Văn A - Quản lý", "Trần Thị B - Thu ngân", "Lê Văn C - Phục vụ")

        // Thiết lập hiển thị
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = StaffAdapter(data)
    }
}