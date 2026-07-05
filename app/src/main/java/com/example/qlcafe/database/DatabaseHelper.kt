package com.example.qlcafe.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.qlcafe.models.NguyenLieu

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "coffee_shop_db"
        // TĂNG VERSION LÊN 4: Để tạo bảng Nguyên Liệu
        private const val DATABASE_VERSION = 4

        // Cấu hình bảng Sản Phẩm
        const val TABLE_SAN_PHAM = "SanPham"
        const val COL_ID = "id"
        const val COL_TEN_MON = "tenMon"
        const val COL_GIA = "gia"
        const val COL_MO_TA = "moTa"
        const val COL_HINH_ANH = "hinhAnh"

        // Cấu hình bảng Nguyên Liệu
        const val TABLE_NGUYEN_LIEU = "NguyenLieu"
        const val COL_NL_ID = "id"
        const val COL_NL_TEN = "tenNguyenLieu"
        const val COL_NL_SO_LUONG = "soLuong"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableUser = "CREATE TABLE User (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, role TEXT)"
        db.execSQL(createTableUser)

        val createTableSanPham = ("CREATE TABLE $TABLE_SAN_PHAM ("
                + "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COL_TEN_MON TEXT, "
                + "$COL_GIA REAL, "
                + "$COL_MO_TA TEXT, "
                + "$COL_HINH_ANH TEXT)")
        db.execSQL(createTableSanPham)

        // TẠO BẢNG NGUYÊN LIỆU
        val createTableNguyenLieu = ("CREATE TABLE $TABLE_NGUYEN_LIEU ("
                + "$COL_NL_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COL_NL_TEN TEXT, "
                + "$COL_NL_SO_LUONG INTEGER)")
        db.execSQL(createTableNguyenLieu)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            try {
                db.execSQL("ALTER TABLE $TABLE_SAN_PHAM ADD COLUMN $COL_MO_TA TEXT")
                db.execSQL("ALTER TABLE $TABLE_SAN_PHAM ADD COLUMN $COL_HINH_ANH TEXT")
            } catch (e: Exception) {
                db.execSQL("DROP TABLE IF EXISTS $TABLE_SAN_PHAM")
                onCreate(db)
            }
        }
        if (oldVersion < 4) {
            val createTableNguyenLieu = ("CREATE TABLE $TABLE_NGUYEN_LIEU ("
                    + "$COL_NL_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "$COL_NL_TEN TEXT, "
                    + "$COL_NL_SO_LUONG INTEGER)")
            db.execSQL(createTableNguyenLieu)
        }
    }

    // --- CÁC HÀM XỬ LÝ SẢN PHẨM ---
    fun insertSanPham(tenMon: String, gia: Double, moTa: String, hinhAnh: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_TEN_MON, tenMon)
        values.put(COL_GIA, gia)
        values.put(COL_MO_TA, moTa)
        values.put(COL_HINH_ANH, hinhAnh)
        return db.insert(TABLE_SAN_PHAM, null, values)
    }

    fun getAllSanPham(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT $COL_ID AS _id, $COL_TEN_MON, $COL_GIA, $COL_MO_TA, $COL_HINH_ANH FROM $TABLE_SAN_PHAM", null)
    }

    fun updateSanPham(id: Int, tenMon: String, gia: Double, moTa: String, hinhAnh: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_TEN_MON, tenMon)
        values.put(COL_GIA, gia)
        values.put(COL_MO_TA, moTa)
        values.put(COL_HINH_ANH, hinhAnh)
        return db.update(TABLE_SAN_PHAM, values, "$COL_ID=?", arrayOf(id.toString()))
    }

    fun deleteSanPham(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_SAN_PHAM, "$COL_ID=?", arrayOf(id.toString()))
    }

    // --- CÁC HÀM XỬ LÝ NGUYÊN LIỆU (QUẢN LÝ KHO) ---

    // Lấy toàn bộ danh sách nguyên liệu
    fun getAllNguyenLieu(): ArrayList<NguyenLieu> {
        val list = ArrayList<NguyenLieu>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NGUYEN_LIEU", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_NL_ID))
                val ten = cursor.getString(cursor.getColumnIndexOrThrow(COL_NL_TEN))
                val soLuong = cursor.getInt(cursor.getColumnIndexOrThrow(COL_NL_SO_LUONG))
                list.add(NguyenLieu(id, ten, soLuong))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // Nhập kho: Có thì cộng thêm, chưa có thì tạo mới
    fun nhapKho(tenNL: String, soLuongNhap: Int) {
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT $COL_NL_SO_LUONG FROM $TABLE_NGUYEN_LIEU WHERE $COL_NL_TEN = ?", arrayOf(tenNL))

        if (cursor.moveToFirst()) {
            // Nguyên liệu đã tồn tại -> Cộng dồn số lượng
            val currentQty = cursor.getInt(0)
            val values = ContentValues()
            values.put(COL_NL_SO_LUONG, currentQty + soLuongNhap)
            db.update(TABLE_NGUYEN_LIEU, values, "$COL_NL_TEN = ?", arrayOf(tenNL))
        } else {
            // Nguyên liệu chưa tồn tại -> Thêm mới
            val values = ContentValues()
            values.put(COL_NL_TEN, tenNL)
            values.put(COL_NL_SO_LUONG, soLuongNhap)
            db.insert(TABLE_NGUYEN_LIEU, null, values)
        }
        cursor.close()
    }

    // Xuất kho: Trả về 1 (Thành công), -1 (Không đủ hàng), 0 (Không tìm thấy)
    fun xuatKho(tenNL: String, soLuongXuat: Int): Int {
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT $COL_NL_SO_LUONG FROM $TABLE_NGUYEN_LIEU WHERE $COL_NL_TEN = ?", arrayOf(tenNL))
        var result = 0

        if (cursor.moveToFirst()) {
            val currentQty = cursor.getInt(0)
            if (currentQty >= soLuongXuat) {
                // Đủ số lượng -> Trừ đi
                val values = ContentValues()
                values.put(COL_NL_SO_LUONG, currentQty - soLuongXuat)
                db.update(TABLE_NGUYEN_LIEU, values, "$COL_NL_TEN = ?", arrayOf(tenNL))
                result = 1
            } else {
                result = -1 // Không đủ số lượng trong kho
            }
        }
        cursor.close()
        return result
    }
}