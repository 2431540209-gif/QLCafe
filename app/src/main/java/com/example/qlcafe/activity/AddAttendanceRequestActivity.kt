package com.example.qlcafe.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qlcafe.R
import com.example.qlcafe.utils.setupTopBar
import com.example.qlcafe.api.RetrofitClient
import com.example.qlcafe.auth.SessionManager
import com.example.qlcafe.models.AddAttendanceRequest
import com.example.qlcafe.models.AddAttendanceResponse
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class AddAttendanceRequestActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    // Biến dùng để lưu ngày tháng chuẩn MySQL (YYYY-MM-DD) gửi lên Server
    private var dateForDatabase = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_attendance_request)
        setupTopBar("Thêm yêu cầu bổ sung")

        sessionManager = SessionManager(this)

        // 1. Ánh xạ giao diện
        val spnBranch = findViewById<AutoCompleteTextView>(R.id.spnBranch)
        val spnShift = findViewById<AutoCompleteTextView>(R.id.spnShift)
        val edtDate = findViewById<TextInputEditText>(R.id.edtDate)
        val edtStartTime = findViewById<TextInputEditText>(R.id.edtStartTime)
        val edtEndTime = findViewById<TextInputEditText>(R.id.edtEndTime)
        val edtNote = findViewById<TextInputEditText>(R.id.edtNote)
        val btnSubmitRequest = findViewById<MaterialButton>(R.id.btnSubmitRequest)

        // 2. Nạp dữ liệu cho 2 cái Spinner
        val branches = arrayOf("VAA CAFE Cơ sở 1", "VAA CAFE Cơ sở 2")
        val branchAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, branches)
        spnBranch.setAdapter(branchAdapter)

        val shifts = arrayOf("ca phát sinh")
        val shiftAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, shifts)
        spnShift.setAdapter(shiftAdapter)

        // 3. Xử lý chọn Ngày (Bật cuốn lịch lên)
        edtDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                // Hiển thị ra màn hình cho đẹp: DD/MM/YYYY
                edtDate.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year))
                // Lưu ngầm chuẩn MySQL: YYYY-MM-DD
                dateForDatabase = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        // 4. Xử lý chọn Giờ (Bật đồng hồ lên)
        edtStartTime.setOnClickListener { chonGio(edtStartTime) }
        edtEndTime.setOnClickListener { chonGio(edtEndTime) }

        // 5. Xử lý nút THÊM CÔNG
        btnSubmitRequest.setOnClickListener {
            val branch = spnBranch.text.toString()
            val shift = spnShift.text.toString()
            val startTime = edtStartTime.text.toString()
            val endTime = edtEndTime.text.toString()
            val note = edtNote.text.toString().trim()
            val userId = sessionManager.getUserId()

            if (branch.isEmpty() || shift.isEmpty() || dateForDatabase.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin có dấu thời gian!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId == -1) {
                Toast.makeText(this, "Lỗi: Không tìm thấy ID nhân viên!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Gói dữ liệu
            val request = AddAttendanceRequest(userId, branch, dateForDatabase, shift, startTime, endTime, note)

            // Bắn API
            btnSubmitRequest.isEnabled = false // Khóa nút tránh bấm 2 lần
            btnSubmitRequest.text = "Đang gửi..."

            RetrofitClient.instance.addAttendanceRequest(request).enqueue(object : Callback<AddAttendanceResponse> {
                override fun onResponse(call: Call<AddAttendanceResponse>, response: Response<AddAttendanceResponse>) {
                    btnSubmitRequest.isEnabled = true
                    btnSubmitRequest.text = "Thêm công"

                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        if (body.success) {
                            Toast.makeText(this@AddAttendanceRequestActivity, body.message, Toast.LENGTH_LONG).show()
                            finish() // Đóng trang này lại, quay về trang danh sách
                        } else {
                            Toast.makeText(this@AddAttendanceRequestActivity, "Lỗi: ${body.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<AddAttendanceResponse>, t: Throwable) {
                    btnSubmitRequest.isEnabled = true
                    btnSubmitRequest.text = "Thêm công"
                    Toast.makeText(this@AddAttendanceRequestActivity, "Lỗi thật sự: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    // Hàm phụ trợ: Bật đồng hồ chọn giờ (HH:mm)
    private fun chonGio(edt: TextInputEditText) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(this, { _, hourOfDay, minute ->
            edt.setText(String.format("%02d:%02d", hourOfDay, minute))
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }
}