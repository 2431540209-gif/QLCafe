package com.example.qlcafe.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.qlcafe.models.NguyenLieu
import com.example.qlcafe.models.Product
import com.example.qlcafe.models.DashboardStats
import com.example.qlcafe.models.ThongBao

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "coffee_shop_db"
        // TĂNG VERSION LÊN 5: Để tạo bảng Thông báo và Stats
        private const val DATABASE_VERSION = 5

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

        // Cấu hình bảng Thông báo
        const val TABLE_THONG_BAO = "ThongBao"
        const val COL_TB_ID = "id"
        const val COL_TB_TYPE = "type"
        const val COL_TB_TITLE = "title"
        const val COL_TB_SHORT_CONTENT = "short_content"
        const val COL_TB_DETAILS = "details"
        const val COL_TB_CREATED_AT = "created_at"

        // Cấu hình bảng Thống kê Dashboard
        const val TABLE_DASHBOARD_STATS = "DashboardStats"
        const val COL_STATS_REVENUE = "total_revenue"
        const val COL_STATS_ORDERS = "total_orders"
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

        // TẠO BẢNG THÔNG BÁO
        val createTableThongBao = ("CREATE TABLE $TABLE_THONG_BAO ("
                + "$COL_TB_ID INTEGER PRIMARY KEY, "
                + "$COL_TB_TYPE TEXT, "
                + "$COL_TB_TITLE TEXT, "
                + "$COL_TB_SHORT_CONTENT TEXT, "
                + "$COL_TB_DETAILS TEXT, "
                + "$COL_TB_CREATED_AT TEXT)")
        db.execSQL(createTableThongBao)

        // TẠO BẢNG DASHBOARD STATS
        val createTableStats = ("CREATE TABLE $TABLE_DASHBOARD_STATS ("
                + "$COL_STATS_REVENUE REAL, "
                + "$COL_STATS_ORDERS INTEGER)")
        db.execSQL(createTableStats)
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
        if (oldVersion < 5) {
            val createTableThongBao = ("CREATE TABLE $TABLE_THONG_BAO ("
                    + "$COL_TB_ID INTEGER PRIMARY KEY, "
                    + "$COL_TB_TYPE TEXT, "
                    + "$COL_TB_TITLE TEXT, "
                    + "$COL_TB_SHORT_CONTENT TEXT, "
                    + "$COL_TB_DETAILS TEXT, "
                    + "$COL_TB_CREATED_AT TEXT)")
            db.execSQL(createTableThongBao)

            val createTableStats = ("CREATE TABLE $TABLE_DASHBOARD_STATS ("
                    + "$COL_STATS_REVENUE REAL, "
                    + "$COL_STATS_ORDERS INTEGER)")
            db.execSQL(createTableStats)
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

    // --- CÁC HÀM CACHE CHO THÔNG BÁO ---
    fun cacheNotifications(list: List<ThongBao>) {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_THONG_BAO, null, null)
            for (tb in list) {
                val values = ContentValues().apply {
                    put(COL_TB_ID, tb.id)
                    put(COL_TB_TYPE, tb.type)
                    put(COL_TB_TITLE, tb.title)
                    put(COL_TB_SHORT_CONTENT, tb.short_content)
                    put(COL_TB_DETAILS, tb.details)
                    put(COL_TB_CREATED_AT, tb.created_at)
                }
                db.insert(TABLE_THONG_BAO, null, values)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getCachedNotifications(): List<ThongBao> {
        val list = ArrayList<ThongBao>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_THONG_BAO ORDER BY $COL_TB_ID DESC", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TB_ID))
                val type = cursor.getString(cursor.getColumnIndexOrThrow(COL_TB_TYPE))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TB_TITLE))
                val shortContent = cursor.getString(cursor.getColumnIndexOrThrow(COL_TB_SHORT_CONTENT))
                val details = cursor.getString(cursor.getColumnIndexOrThrow(COL_TB_DETAILS))
                val createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COL_TB_CREATED_AT))
                list.add(ThongBao(id, type, title, shortContent, details, createdAt))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // --- CÁC HÀM CACHE CHO SẢN PHẨM ---
    fun cacheProducts(list: List<Product>) {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_SAN_PHAM, null, null)
            for (p in list) {
                val values = ContentValues().apply {
                    put(COL_ID, p.id)
                    put(COL_TEN_MON, p.name)
                    put(COL_GIA, p.price)
                    put(COL_MO_TA, "")
                    put(COL_HINH_ANH, p.image_url ?: "")
                }
                db.insert(TABLE_SAN_PHAM, null, values)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getCachedProducts(): List<Product> {
        val list = ArrayList<Product>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_SAN_PHAM", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COL_TEN_MON))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_GIA))
                val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COL_HINH_ANH))
                list.add(Product(id, name, price, imageUrl, null))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // --- CÁC HÀM CACHE CHO DASHBOARD STATS ---
    fun cacheDashboardStats(stats: DashboardStats) {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_DASHBOARD_STATS, null, null)
            val values = ContentValues().apply {
                put(COL_STATS_REVENUE, stats.total_revenue)
                put(COL_STATS_ORDERS, stats.total_orders)
            }
            db.insert(TABLE_DASHBOARD_STATS, null, values)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getCachedDashboardStats(): DashboardStats? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_DASHBOARD_STATS LIMIT 1", null)
        var stats: DashboardStats? = null
        if (cursor.moveToFirst()) {
            val revenue = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_STATS_REVENUE))
            val orders = cursor.getInt(cursor.getColumnIndexOrThrow(COL_STATS_ORDERS))
            stats = DashboardStats(revenue, orders)
        }
        cursor.close()
        return stats
    }
}