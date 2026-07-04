package com.example.qlcafe.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.R
import com.example.qlcafe.api.RetrofitClient
import com.example.qlcafe.utils.setupTopBar
import com.example.qlcafe.auth.SessionManager
import com.example.qlcafe.models.AttendanceRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentLichChung : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var rvLich: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lich_chung, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sessionManager = SessionManager(requireContext())
        
        // Ánh xạ các View từ XML
        rvLich = view.findViewById(R.id.rvLich)
        tvEmpty = view.findViewById(R.id.tvEmpty)
        progressBar = view.findViewById(R.id.progressBar)
        
        rvLich.layoutManager = LinearLayoutManager(requireContext())
        
        // Thiết lập tiêu đề thanh điều hướng Top Bar
        val appCompatActivity = requireActivity() as? AppCompatActivity
        appCompatActivity?.setupTopBar("Lịch Làm Việc")
        
        loadLichLamViec()
    }

    private fun loadLichLamViec() {
        val userId = sessionManager.getUserId()
        if (userId == -1) {
            progressBar.visibility = View.GONE
            tvEmpty.visibility = View.VISIBLE
            return
        }

        RetrofitClient.instance.getAttendanceRequests(userId).enqueue(object : Callback<List<AttendanceRequest>> {
            override fun onResponse(call: Call<List<AttendanceRequest>>, response: Response<List<AttendanceRequest>>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    // Lọc chỉ lấy các lịch làm việc đã được QUẢN LÝ PHÊ DUYỆT (approved)
                    val approvedLich = response.body()!!.filter { it.status == "approved" }
                    if (approvedLich.isEmpty()) {
                        tvEmpty.visibility = View.VISIBLE
                        rvLich.visibility = View.GONE
                    } else {
                        tvEmpty.visibility = View.GONE
                        rvLich.visibility = View.VISIBLE
                        rvLich.adapter = LichAdapter(approvedLich)
                    }
                } else {
                    tvEmpty.text = "Lỗi phản hồi từ server!"
                    tvEmpty.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<List<AttendanceRequest>>, t: Throwable) {
                progressBar.visibility = View.GONE
                tvEmpty.text = "Mất kết nối server!"
                tvEmpty.visibility = View.VISIBLE
            }
        })
    }

    private fun dpToPx(dp: Float): Int {
        return android.util.TypedValue.applyDimension(
            android.util.TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }

    private inner class LichAdapter(private val list: List<AttendanceRequest>) : RecyclerView.Adapter<LichViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LichViewHolder {
            val context = parent.context
            val isNight = (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES

            val card = CardView(context).apply {
                layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT).apply {
                    setMargins(0, 0, 0, dpToPx(12f))
                }
                radius = dpToPx(12f).toFloat()
                cardElevation = dpToPx(2f).toFloat()
                setCardBackgroundColor(if (isNight) Color.parseColor("#1E1E1E") else Color.WHITE)
            }

            val layout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dpToPx(16f), dpToPx(16f), dpToPx(16f), dpToPx(16f))
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }

            val rowHeader = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            }

            val tvDate = TextView(context).apply {
                textSize = 16f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(if (isNight) Color.parseColor("#E0E0E0") else Color.parseColor("#1A1A1A"))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            rowHeader.addView(tvDate)

            val tvStatus = TextView(context).apply {
                text = "ĐÃ DUYỆT"
                textSize = 12f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#4CAF50"))
                setPadding(dpToPx(8f), dpToPx(4f), dpToPx(8f), dpToPx(4f))
                background = android.graphics.drawable.GradientDrawable().apply {
                    setColor(Color.parseColor("#E8F5E9"))
                    cornerRadius = dpToPx(6f).toFloat()
                }
            }
            rowHeader.addView(tvStatus)
            layout.addView(rowHeader)

            val tvInfo = TextView(context).apply {
                textSize = 14f
                setTextColor(if (isNight) Color.parseColor("#B0B0B0") else Color.parseColor("#555555"))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    topMargin = dpToPx(8f)
                }
            }
            layout.addView(tvInfo)

            card.addView(layout)
            return LichViewHolder(card, tvDate, tvInfo)
        }

        override fun onBindViewHolder(holder: LichViewHolder, position: Int) {
            com.example.qlcafe.utils.ThemeHelper.applyTheme(holder.itemView)
            val item = list[position]
            holder.tvDate.text = "Ngày: ${item.request_date}"
            // Định dạng chuỗi hiển thị
            val start = if (item.start_time.length >= 5) item.start_time.substring(0, 5) else item.start_time
            val end = if (item.end_time.length >= 5) item.end_time.substring(0, 5) else item.end_time
            holder.tvInfo.text = "Ca làm: ${item.shift_name}\nThời gian: $start - $end\nChi nhánh: ${item.branch}"
        }

        override fun getItemCount() = list.size
    }

    private class LichViewHolder(
        itemView: View,
        val tvDate: TextView,
        val tvInfo: TextView
    ) : RecyclerView.ViewHolder(itemView)
}
