package com.example.qlcafe.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.qlcafe.R
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

class FragmentDangKyLich : Fragment() {

    private lateinit var sessionManager: SessionManager
    private var dateForDatabase = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Tái sử dụng layout activity_add_attendance_request.xml
        return inflater.inflate(R.layout.activity_add_attendance_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        // 1. Ánh xạ các thành phần giao diện
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        
        val spnBranch = view.findViewById<AutoCompleteTextView>(R.id.spnBranch)
        val spnShift = view.findViewById<AutoCompleteTextView>(R.id.spnShift)
        val edtDate = view.findViewById<TextInputEditText>(R.id.edtDate)
        val edtStartTime = view.findViewById<TextInputEditText>(R.id.edtStartTime)
        val edtEndTime = view.findViewById<TextInputEditText>(R.id.edtEndTime)
        val edtNote = view.findViewById<TextInputEditText>(R.id.edtNote)
        val btnSubmitRequest = view.findViewById<MaterialButton>(R.id.btnSubmitRequest)

        // Thiết lập tiêu đề thanh điều hướng
        tvTitle?.text = "Đăng ký lịch làm việc"
        btnBack?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Cập nhật text nút gửi thành "Đăng ký"
        btnSubmitRequest?.text = "Đăng ký lịch"

        // 2. Thiết lập dữ liệu cho Spinner chọn chi nhánh
        val branches = arrayOf("VAA CAFE Cơ sở 1", "VAA CAFE Cơ sở 2")
        val branchAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, branches)
        spnBranch.setAdapter(branchAdapter)
        spnBranch.setText(branches[0], false) // Chọn sẵn cơ sở 1

        // 3. Thiết lập dữ liệu cho Spinner chọn ca làm
        val shifts = arrayOf(
            "Ca sáng (07:00 - 12:00)", 
            "Ca chiều (12:00 - 17:00)", 
            "Ca tối (17:00 - 22:00)"
        )
        val shiftAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, shifts)
        spnShift.setAdapter(shiftAdapter)

        // Tự động điền giờ khi chọn ca làm việc chuẩn
        spnShift.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    edtStartTime.setText("07:00")
                    edtEndTime.setText("12:00")
                }
                1 -> {
                    edtStartTime.setText("12:00")
                    edtEndTime.setText("17:00")
                }
                2 -> {
                    edtStartTime.setText("17:00")
                    edtEndTime.setText("22:00")
                }
                3 -> {
                    edtStartTime.setText("")
                    edtEndTime.setText("")
                }
            }
        }

        // 4. Bật chọn ngày (DatePickerDialog)
        edtDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                edtDate.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year))
                dateForDatabase = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        // 5. Bật chọn giờ (TimePickerDialog)
        edtStartTime.setOnClickListener { openTimePicker(edtStartTime) }
        edtEndTime.setOnClickListener { openTimePicker(edtEndTime) }

        // 6. Xử lý sự kiện gửi yêu cầu đăng ký lịch
        btnSubmitRequest.setOnClickListener {
            val branch = spnBranch.text.toString().trim()
            val shift = spnShift.text.toString().trim()
            val startTime = edtStartTime.text.toString().trim()
            val endTime = edtEndTime.text.toString().trim()
            val note = edtNote.text.toString().trim()
            val userId = sessionManager.getUserId()

            // Kiểm tra tính hợp lệ dữ liệu đầu vào
            if (branch.isEmpty() || shift.isEmpty() || dateForDatabase.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin đăng ký lịch!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId == -1) {
                Toast.makeText(requireContext(), "Lỗi: Không tìm thấy ID tài khoản để đăng ký!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Gói dữ liệu gửi lên API PHP
            val request = AddAttendanceRequest(userId, branch, dateForDatabase, shift, startTime, endTime, note)

            btnSubmitRequest.isEnabled = false
            btnSubmitRequest.text = "Đang đăng ký..."

            RetrofitClient.instance.addAttendanceRequest(request).enqueue(object : Callback<AddAttendanceResponse> {
                override fun onResponse(call: Call<AddAttendanceResponse>, response: Response<AddAttendanceResponse>) {
                    btnSubmitRequest.isEnabled = true
                    btnSubmitRequest.text = "Đăng ký lịch"

                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        if (body.success) {
                            Toast.makeText(requireContext(), "Đăng ký lịch làm việc thành công!", Toast.LENGTH_LONG).show()
                            requireActivity().onBackPressedDispatcher.onBackPressed() // Quay lại màn hình chính
                        } else {
                            Toast.makeText(requireContext(), "Lỗi: ${body.message}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Lỗi từ phía máy chủ!", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<AddAttendanceResponse>, t: Throwable) {
                    btnSubmitRequest.isEnabled = true
                    btnSubmitRequest.text = "Đăng ký lịch"
                    Toast.makeText(requireContext(), "Mất kết nối server: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun openTimePicker(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
            editText.setText(String.format("%02d:%02d", hourOfDay, minute))
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }
}
