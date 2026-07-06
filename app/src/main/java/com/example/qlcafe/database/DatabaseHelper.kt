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

    val productDao = ProductDao(this)
    val notificationDao = NotificationDao(this)
    val nguyenLieuDao = NguyenLieuDao(this)
    val dashboardStatsDao = DashboardStatsDao(this)

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
}