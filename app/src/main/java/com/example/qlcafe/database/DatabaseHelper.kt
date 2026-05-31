package com.example.qlcafe.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "QLCafe.db"
        // TĂNG VERSION LÊN 3: Để SQLite tự kích hoạt hàm onUpgrade cập nhật thêm cột mới
        private const val DATABASE_VERSION = 3

        // Cấu hình bảng Sản Phẩm
        const val TABLE_SAN_PHAM = "SanPham"
        const val COL_ID = "id"
        const val COL_TEN_MON = "tenMon"
        const val COL_GIA = "gia"
        const val COL_MO_TA = "moTa"       // Thêm cột Mô tả mới
        const val COL_HINH_ANH = "hinhAnh" // Thêm cột Hình ảnh mới (Lưu đường dẫn URI dạng String)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Câu lệnh tạo bảng tài khoản
        val createTableUser = "CREATE TABLE User (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, role TEXT)"
        db.execSQL(createTableUser)

        // TẠO BẢNG SẢN PHẨM (Đã cập nhật đầy đủ cấu trúc mới)
        val createTableSanPham = ("CREATE TABLE $TABLE_SAN_PHAM ("
                + "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COL_TEN_MON TEXT, "
                + "$COL_GIA REAL, "
                + "$COL_MO_TA TEXT, "      // Cột mô tả mới
                + "$COL_HINH_ANH TEXT)")   // Cột hình ảnh mới
        db.execSQL(createTableSanPham)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Nếu người dùng nâng cấp từ phiên bản cũ lên phiên bản 3
        if (oldVersion < 3) {
            // Cách an toàn nhất để nâng cấp: Thêm cột mới trực tiếp vào bảng hiện tại mà không làm mất dữ liệu cũ
            try {
                db.execSQL("ALTER TABLE $TABLE_SAN_PHAM ADD COLUMN $COL_MO_TA TEXT")
                db.execSQL("ALTER TABLE $TABLE_SAN_PHAM ADD COLUMN $COL_HINH_ANH TEXT")
            } catch (e: Exception) {
                // Trường hợp bảng bị lỗi cấu trúc nặng, ta sẽ xóa đi tạo lại sạch sẽ
                db.execSQL("DROP TABLE IF EXISTS $TABLE_SAN_PHAM")
                onCreate(db)
            }
        }
    }

    // --- CÁC HÀM XỬ LÝ NGHIỆP VỤ SẢN PHẨM (CRUD) ---

    // 1. Thêm sản phẩm (Cập nhật: Nhận thêm MoTa và HinhAnh)
    fun insertSanPham(tenMon: String, gia: Double, moTa: String, hinhAnh: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_TEN_MON, tenMon)
        values.put(COL_GIA, gia)
        values.put(COL_MO_TA, moTa)       // Đưa mô tả vào cấu trúc lưu trữ
        values.put(COL_HINH_ANH, hinhAnh) // Đưa chuỗi đường dẫn ảnh vào cấu trúc lưu trữ
        return db.insert(TABLE_SAN_PHAM, null, values)
    }

    // 2. Lấy toàn bộ danh sách sản phẩm (Cập nhật: Truy vấn thêm cột moTa và hinhAnh)
    fun getAllSanPham(): Cursor {
        val db = this.readableDatabase
        val query = "SELECT $COL_ID AS _id, $COL_TEN_MON, $COL_GIA, $COL_MO_TA, $COL_HINH_ANH FROM $TABLE_SAN_PHAM"
        return db.rawQuery(query, null)
    }

    // 3. Sửa sản phẩm (Cập nhật: Cho phép sửa cả MoTa và HinhAnh)
    fun updateSanPham(id: Int, tenMon: String, gia: Double, moTa: String, hinhAnh: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_TEN_MON, tenMon)
        values.put(COL_GIA, gia)
        values.put(COL_MO_TA, moTa)
        values.put(COL_HINH_ANH, hinhAnh)
        return db.update(TABLE_SAN_PHAM, values, "$COL_ID=?", arrayOf(id.toString()))
    }

    // 4. Xóa sản phẩm (Giữ nguyên gốc vì chỉ cần ID là đủ xóa)
    fun deleteSanPham(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_SAN_PHAM, "$COL_ID=?", arrayOf(id.toString()))
    }
}