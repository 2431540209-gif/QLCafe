package com.example.qlcafe

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.models.ThongBao
import androidx.recyclerview.widget.LinearLayoutManager
// Tí nữa có xài class nào thêm thì cứ Alt + Enter nhé!

class FragmentThongBao : Fragment(R.layout.fragment_thong_bao) {
    // R.layout.fragment_thong_bao là cái file giao diện XML của bạn

    // Hàm này sẽ tự động chạy sau khi giao diện XML đã được tạo xong
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Ánh xạ cái danh sách từ XML sang Kotlin
        val rvThongBao = view.findViewById<RecyclerView>(R.id.rvThongBao)
        val layoutEmpty = view.findViewById<View>(R.id.layoutEmpty)

        val listDuLieu = mutableListOf<ThongBao>()

        // 3. KIỂM TRA ĐỂ HIỆN GIAO DIỆN PHÙ HỢP
        if (listDuLieu.isEmpty()) {
            // Nếu danh sách trống -> Giấu cái kệ đi, Hiện cái hình báo trống lên
            rvThongBao.visibility = View.GONE
            layoutEmpty.visibility = View.VISIBLE
        } else {
            // Nếu có thông báo -> Giấu cái hình báo trống đi, Hiện cái kệ lên
            layoutEmpty.visibility = View.GONE
            rvThongBao.visibility = View.VISIBLE

            // Cài đặt nhân viên Adapter bưng hàng lên kệ...
            val adapter = ThongBaoAdapter(listDuLieu)
            rvThongBao.layoutManager = LinearLayoutManager(requireContext())

            rvThongBao.adapter = adapter
        }
        // ==========================================
        // TÍNH NĂNG VUỐT ĐỂ XÓA CHÍNH THỨC NẰM Ở ĐÂY
        // ==========================================
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Lấy vị trí của item vừa bị vuốt
                val position = viewHolder.adapterPosition

                // Khi nào bạn có Adapter thật thì mở comment 2 dòng dưới ra để nó xóa dữ liệu nhé:
                // listThongBao.removeAt(position)
                // rvThongBao.adapter?.notifyItemRemoved(position)
            }
        }

        // Gắn tính năng vuốt vào cái danh sách rvThongBao
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(rvThongBao)
    }
}