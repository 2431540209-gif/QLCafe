package com.example.qlcafe.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.qlcafe.R
import com.example.qlcafe.api.RetrofitClient
import com.example.qlcafe.models.StatictisResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

class FragmentStatistic : Fragment(R.layout.fragment_statistic) {

    private lateinit var tvThongKeNgay: TextView
    private lateinit var tvThongKeThang: TextView
    private lateinit var tvThongKeNam: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ánh xạ các TextView từ layout mới
        tvThongKeNgay = view.findViewById(R.id.tvThongKeNgay)
        tvThongKeThang = view.findViewById(R.id.tvThongKeThang)
        tvThongKeNam = view.findViewById(R.id.tvThongKeNam)

        // Cập nhật tiêu đề Top Bar
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = "Báo Cáo Doanh Thu"

        val btnBack = view.findViewById<android.widget.ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        layDuLieuThongKeDoanhThu()
    }

    private fun layDuLieuThongKeDoanhThu() {
        RetrofitClient.instance.getDoanhThu().enqueue(object : Callback<StatictisResponse> {
            override fun onResponse(call: Call<StatictisResponse>, response: Response<StatictisResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    if (data.success) {
                        val formatter = DecimalFormat("#,###đ")

                        // Cập nhật số tiền thật lên màn hình
                        tvThongKeNgay.text = formatter.format(data.doanhThuNgay)
                        tvThongKeThang.text = formatter.format(data.doanhThuThang)
                        tvThongKeNam.text = formatter.format(data.doanhThuNam)
                    }
                }
            }

            override fun onFailure(call: Call<StatictisResponse>, t: Throwable) {
                // Xử lý khi xảy ra lỗi kết nối
            }
        })
    }
}