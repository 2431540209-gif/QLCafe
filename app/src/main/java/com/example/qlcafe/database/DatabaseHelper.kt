package com.example.qlcafe.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "QLCafe.db"
        private const val DATABASE_VERSION = 2 // Nâng version lên 2 để cập nhật bảng mới

        // Cấu hình bảng Sản Phẩm
        const val TABLE_SAN_PHAM = "SanPham"
        const val COL_ID = "id"
        const val COL_TEN_MON = "tenMon"
        const val COL_GIA = "gia"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Câu lệnh tạo bảng tài khoản
        val createTableUser = "CREATE TABLE User (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, role TEXT)"
        db.execSQL(createTableUser)

        // TẠO BẢNG SẢN PHẨM MỚI
        val createTableSanPham = ("CREATE TABLE $TABLE_SAN_PHAM ("
                + "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COL_TEN_MON TEXT, "
                + "$COL_GIA REAL)")
        db.execSQL(createTableSanPham)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_SAN_PHAM")
            onCreate(db)
        }
    }

    // --- CÁC HÀM XỬ LÝ NGHIỆP VỤ SẢN PHẨM (CRUD) ---

    // 1. Thêm sản phẩm
    fun insertSanPham(tenMon: String, gia: Double): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_TEN_MON, tenMon)
        values.put(COL_GIA, gia)
        return db.insert(TABLE_SAN_PHAM, null, values)
    }

    // 2. Lấy toàn bộ danh sách sản phẩm (ĐÃ SỬA: Đổi tên id thành _id để tránh crash)
    fun getAllSanPham(): Cursor {
        val db = this.readableDatabase
        val query = "SELECT $COL_ID AS _id, $COL_TEN_MON, $COL_GIA FROM $TABLE_SAN_PHAM"
        return db.rawQuery(query, null)
    }

    // 3. Sửa sản phẩm
    fun updateSanPham(id: Int, tenMon: String, gia: Double): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_TEN_MON, tenMon)
        values.put(COL_GIA, gia)
        return db.update(TABLE_SAN_PHAM, values, "$COL_ID=?", arrayOf(id.toString()))
    }

    // 4. Xóa sản phẩm
    fun deleteSanPham(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_SAN_PHAM, "$COL_ID=?", arrayOf(id.toString()))
    }
}