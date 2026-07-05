package com.example.qlcafe.activity

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.database.DatabaseHelper

class StaffActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private val staffList = mutableListOf<StaffMember>()
    private lateinit var adapter: StaffMemberAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = DatabaseHelper(this)

        // 1. Tạo Giao diện động (Programmatic UI) bằng Kotlin
        val context = this
        val mainLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#F8F6F4"))
        }

        // --- TOOLBAR ---
        val toolbar = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(56f)
            )
            setBackgroundColor(Color.parseColor("#4A2C11")) // Màu nâu đặc trưng của quán
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dpToPx(16f), 0, dpToPx(16f), 0)
        }

        val btnBack = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(dpToPx(24f), dpToPx(24f))
            setImageResource(android.R.drawable.ic_menu_revert) // Icon back mặc định của hệ thống
            setColorFilter(Color.WHITE)
            setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        val tvTitle = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                marginStart = dpToPx(16f)
            }
            text = "Quản lý nhân viên"
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
        }

        toolbar.addView(btnBack)
        toolbar.addView(tvTitle)
        mainLayout.addView(toolbar)

        // --- NÚT THÊM NHÂN VIÊN ---
        val btnAddLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(dpToPx(16f), dpToPx(16f), dpToPx(16f), dpToPx(8f))
        }

        val btnAddStaff = Button(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(48f)
            )
            text = "+ Thêm nhân viên mới"
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#D0770B")) // Màu cam vàng thương hiệu
            setOnClickListener {
                showAddEditDialog()
            }
        }

        btnAddLayout.addView(btnAddStaff)
        mainLayout.addView(btnAddLayout)

        // --- RECYCLER VIEW DANH SÁCH ---
        recyclerView = RecyclerView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            ).apply {
                setPadding(dpToPx(16f), dpToPx(8f), dpToPx(16f), dpToPx(16f))
            }
            clipToPadding = false
        }

        mainLayout.addView(recyclerView)
        setContentView(mainLayout)

        // 2. Thiết lập dữ liệu và hiển thị danh sách
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StaffMemberAdapter(staffList, 
            onEditClick = { member -> showAddEditDialog(member) },
            onDeleteClick = { member -> showDeleteConfirmDialog(member) }
        )
        recyclerView.adapter = adapter

        refreshList()
    }

    private fun refreshList() {
        staffList.clear()
        staffList.addAll(loadStaffMembers())
        adapter.notifyDataSetChanged()
    }

    // --- CƠ SỞ DỮ LIỆU SQLITE ---
    private fun loadStaffMembers(): List<StaffMember> {
        val list = mutableListOf<StaffMember>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, username, role, password FROM User", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val username = cursor.getString(1)
                val role = cursor.getString(2)
                val pass = cursor.getString(3)
                list.add(StaffMember(id, username, role, pass))
            } while (cursor.moveToNext())
        }
        cursor.close()

        // Nếu DB chưa có dữ liệu, thêm dữ liệu mẫu ban đầu
        if (list.isEmpty()) {
            val dbWrite = dbHelper.writableDatabase
            val defaultUsers = listOf(
                ContentValues().apply {
                    put("username", "Nguyễn Văn A")
                    put("role", "manager")
                    put("password", "123456")
                },
                ContentValues().apply {
                    put("username", "Trần Thị B")
                    put("role", "cashier")
                    put("password", "123456")
                },
                ContentValues().apply {
                    put("username", "Lê Văn C")
                    put("role", "staff")
                    put("password", "123456")
                }
            )
            for (user in defaultUsers) {
                dbWrite.insert("User", null, user)
            }
            return loadStaffMembers() // Đọc lại danh sách mới chèn
        }
        return list
    }

    // --- HỘP THOẠI THÊM / SỬA NHÂN VIÊN ---
    private fun showAddEditDialog(member: StaffMember? = null) {
        val builder = AlertDialog.Builder(this)
        val isEdit = member != null
        builder.setTitle(if (isEdit) "Cập nhật nhân viên" else "Thêm nhân viên mới")

        // Tạo giao diện nhập liệu động cho Dialog
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(24f), dpToPx(16f), dpToPx(24f), dpToPx(16f))
        }

        val edtName = EditText(this).apply {
            hint = "Tên nhân viên"
            inputType = InputType.TYPE_CLASS_TEXT
            setText(member?.name ?: "")
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(12f)
            }
        }

        val edtPassword = EditText(this).apply {
            hint = "Mật khẩu đăng nhập"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            setText(member?.pass ?: "")
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(16f)
            }
        }

        val tvRoleLabel = TextView(this).apply {
            text = "Vai trò chức vụ:"
            textSize = 14f
            setTextColor(Color.GRAY)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(6f)
            }
        }

        // Chọn vai trò bằng Spinner
        val spinnerRole = Spinner(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(12f)
            }
        }

        val roleNames = arrayOf("Quản lý (manager)", "Thu ngân (cashier)", "Nhân viên (staff)")
        val roleValues = arrayOf("manager", "cashier", "staff")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roleNames)
        spinnerRole.adapter = spinnerAdapter

        // Chọn sẵn vị trí của nhân viên nếu đang ở chế độ Sửa
        if (isEdit) {
            val index = roleValues.indexOf(member?.role)
            if (index >= 0) {
                spinnerRole.setSelection(index)
            }
        } else {
            spinnerRole.setSelection(2) // Mặc định là Nhân viên
        }

        container.addView(edtName)
        container.addView(edtPassword)
        container.addView(tvRoleLabel)
        container.addView(spinnerRole)
        builder.setView(container)

        builder.setPositiveButton(if (isEdit) "Cập nhật" else "Lưu") { dialog, _ ->
            val name = edtName.text.toString().trim()
            val pass = edtPassword.text.toString().trim()
            val role = roleValues[spinnerRole.selectedItemPosition]

            if (name.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ Tên và Mật khẩu!", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("username", name)
                put("password", pass)
                put("role", role)
            }

            val result = if (isEdit) {
                db.update("User", values, "id=?", arrayOf(member!!.id.toString()))
            } else {
                db.insert("User", null, values).toInt()
            }

            if (result != -1) {
                Toast.makeText(this, if (isEdit) "Cập nhật thành công!" else "Thêm nhân viên thành công!", Toast.LENGTH_SHORT).show()
                refreshList()
            } else {
                Toast.makeText(this, "Đã xảy ra lỗi, vui lòng thử lại!", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Hủy") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    // --- HỘP THOẠI XÁC NHẬN XÓA ---
    private fun showDeleteConfirmDialog(member: StaffMember) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa nhân viên '${member.name}' khỏi hệ thống?")
            .setPositiveButton("Xóa") { dialog, _ ->
                val db = dbHelper.writableDatabase
                val count = db.delete("User", "id=?", arrayOf(member.id.toString()))
                if (count > 0) {
                    Toast.makeText(this, "Đã xóa nhân viên thành công!", Toast.LENGTH_SHORT).show()
                    refreshList()
                } else {
                    Toast.makeText(this, "Không thể xóa nhân viên này!", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    // --- TIỆN ÍCH CHUYỂN DP SANG PX ---
    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }

    // --- LỚP MÔ HÌNH DỮ LIỆU NHÂN VIÊN ---
    data class StaffMember(val id: Int, var name: String, var role: String, var pass: String)

    // --- ADAPTER RECYCLERVIEW TỰ THIẾT KẾ ---
    inner class StaffMemberAdapter(
        private val list: List<StaffMember>,
        private val onEditClick: (StaffMember) -> Unit,
        private val onDeleteClick: (StaffMember) -> Unit
    ) : RecyclerView.Adapter<StaffMemberAdapter.ViewHolder>() {

        inner class ViewHolder(
            view: View,
            val tvName: TextView,
            val tvRole: TextView,
            val btnEdit: Button,
            val btnDelete: Button
        ) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val context = parent.context
            
            // Layout thẻ nhân viên
            val container = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(dpToPx(16f), dpToPx(12f), dpToPx(16f), dpToPx(12f))
                gravity = Gravity.CENTER_VERTICAL
                
                // Tạo nền trắng bo góc nhẹ cho mỗi item giống cardview
                val outValue = TypedValue()
                context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
                setBackgroundResource(outValue.resourceId)
            }

            val infoContainer = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvName = TextView(context).apply {
                textSize = 16f
                setTextColor(Color.parseColor("#222222"))
                typeface = Typeface.DEFAULT_BOLD
            }

            val tvRole = TextView(context).apply {
                textSize = 13f
                setTextColor(Color.parseColor("#777777"))
                setPadding(0, dpToPx(2f), 0, 0)
            }

            infoContainer.addView(tvName)
            infoContainer.addView(tvRole)
            container.addView(infoContainer)

            val btnEdit = Button(context).apply {
                text = "Sửa"
                textSize = 12f
                setTextColor(Color.parseColor("#FFA000"))
                setBackgroundColor(Color.TRANSPARENT)
                layoutParams = LinearLayout.LayoutParams(dpToPx(60f), dpToPx(36f)).apply {
                    marginEnd = dpToPx(8f)
                }
            }

            val btnDelete = Button(context).apply {
                text = "Xóa"
                textSize = 12f
                setTextColor(Color.parseColor("#D32F2F"))
                setBackgroundColor(Color.TRANSPARENT)
                layoutParams = LinearLayout.LayoutParams(dpToPx(60f), dpToPx(36f))
            }

            container.addView(btnEdit)
            container.addView(btnDelete)

            return ViewHolder(container, tvName, tvRole, btnEdit, btnDelete)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            com.example.qlcafe.utils.ThemeHelper.applyTheme(holder.itemView)
            val member = list[position]
            holder.tvName.text = member.name
            
            // Format vai trò hiển thị thân thiện
            val displayRole = when (member.role) {
                "manager" -> "Quản lý"
                "cashier" -> "Thu ngân"
                "staff" -> "Nhân viên"
                else -> member.role
            }
            holder.tvRole.text = "Chức vụ: $displayRole"

            holder.btnEdit.setOnClickListener { onEditClick(member) }
            holder.btnDelete.setOnClickListener { onDeleteClick(member) }
        }

        override fun getItemCount(): Int = list.size
    }
}