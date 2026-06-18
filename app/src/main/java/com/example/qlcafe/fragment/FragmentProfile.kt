package com.example.qlcafe.fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.qlcafe.R

class FragmentProfile : Fragment(R.layout.activity_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ánh xạ đầy đủ 6 CardView
        val cardPassword = view.findViewById<CardView>(R.id.cardPassword)
        val cardStaff = view.findViewById<CardView>(R.id.cardStaff)
        val cardInventory = view.findViewById<CardView>(R.id.cardInventory)
        val cardReceipt = view.findViewById<CardView>(R.id.cardReceipt)
        val cardNotification = view.findViewById<CardView>(R.id.cardNotification)
        val cardSetting = view.findViewById<CardView>(R.id.cardSetting)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // Thiết lập sự kiện Click
        val cards = listOf(cardPassword, cardStaff, cardInventory, cardReceipt, cardNotification, cardSetting)
        val names = listOf("Đổi mật khẩu", "Nhân viên", "Kho vật tư", "Hóa đơn", "Thông báo", "Cài đặt")

        cards.forEachIndexed { index, card ->
            card?.setOnClickListener {
                Toast.makeText(context, "Mở: ${names[index]}", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogout?.setOnClickListener {
            Toast.makeText(context, "Đã đăng xuất hệ thống", Toast.LENGTH_SHORT).show()
        }
    }
}