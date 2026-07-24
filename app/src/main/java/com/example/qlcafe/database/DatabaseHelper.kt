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

    // Khai báo các DAO (Từ Phiên bản 2)
    val productDao = ProductDao(this)
    val notificationDao = NotificationDao(this)
    val nguyenLieuDao = NguyenLieuDao(this)
    val dashboardStatsDao = DashboardStatsDao(this)

    companion object {
        private const val DATABASE_NAME = "coffee_shop_db"
        // NÂNG CẤP VERSION LÊN 7: Để đảm bảo thêm cột phone và dac_quyen vào bảng User nếu bị thiếu
        private const val DATABASE_VERSION = 7

        //bảng User
        const val TABLE_USER = "User"
        const val COL_USER_ID = "id"
        const val COL_USER_NAME = "username"
        const val COL_USER_PHONE = "phone"
        const val COL_USER_PASSWORD = "password"
        const val COL_USER_ROLE = "role"
        const val COL_USER_DAC_QUYEN = "dac_quyen"

        //bảng Sản Phẩm
        const val TABLE_SAN_PHAM = "SanPham"
        const val COL_ID = "id"
        const val COL_TEN_MON = "tenMon"
        const val COL_GIA = "gia"
        const val COL_MO_TA = "moTa"
        const val COL_HINH_ANH = "hinhAnh"

        //bảng Nguyên Liệu
        const val TABLE_NGUYEN_LIEU = "NguyenLieu"
        const val COL_NL_ID = "id"
        const val COL_NL_TEN = "tenNguyenLieu"
        const val COL_NL_SO_LUONG = "soLuong"

        //bảng Thông báo
        const val TABLE_THONG_BAO = "ThongBao"
        const val COL_TB_ID = "id"
        const val COL_TB_TYPE = "type"
        const val COL_TB_TITLE = "title"
        const val COL_TB_SHORT_CONTENT = "short_content"
        const val COL_TB_DETAILS = "details"
        const val COL_TB_CREATED_AT = "created_at"

        //bảng Thống kê Dashboard
        const val TABLE_DASHBOARD_STATS = "DashboardStats"
        const val COL_STATS_REVENUE = "total_revenue"
        const val COL_STATS_ORDERS = "total_orders"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableUser = ("CREATE TABLE $TABLE_USER ("
                + "$COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COL_USER_NAME TEXT, "
                + "$COL_USER_PHONE TEXT UNIQUE, "
                + "$COL_USER_PASSWORD TEXT, "
                + "$COL_USER_ROLE TEXT, "
                + "$COL_USER_DAC_QUYEN TEXT)")
        db.execSQL(createTableUser)

        val createTableSanPham = ("CREATE TABLE $TABLE_SAN_PHAM ("
                + "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COL_TEN_MON TEXT, "
                + "$COL_GIA REAL, "
                + "$COL_MO_TA TEXT, "
                + "$COL_HINH_ANH TEXT)")
        db.execSQL(createTableSanPham)

        val createTableNguyenLieu = ("CREATE TABLE $TABLE_NGUYEN_LIEU ("
                + "$COL_NL_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COL_NL_TEN TEXT, "
                + "$COL_NL_SO_LUONG INTEGER)")
        db.execSQL(createTableNguyenLieu)

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

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Cập nhật tuần tự để không làm mất dữ liệu của app bản cũ
        if (oldVersion < 3) {
            try {
                db.execSQL("ALTER TABLE $TABLE_SAN_PHAM ADD COLUMN $COL_MO_TA TEXT")
                db.execSQL("ALTER TABLE $TABLE_SAN_PHAM ADD COLUMN $COL_HINH_ANH TEXT")
            } catch (e: Exception) {
                // Bỏ qua lỗi nếu cột đã tồn tại
            }
        }
        if (oldVersion < 4) {
            try {
                db.execSQL("ALTER TABLE $TABLE_USER ADD COLUMN $COL_USER_PHONE TEXT")
                db.execSQL("ALTER TABLE $TABLE_USER ADD COLUMN $COL_USER_DAC_QUYEN TEXT")
            } catch (e: Exception) {
                // Bỏ qua lỗi nếu cột đã tồn tại
            }
        }
        if (oldVersion < 5) {
            val createTableNguyenLieu = ("CREATE TABLE IF NOT EXISTS $TABLE_NGUYEN_LIEU ("
                    + "$COL_NL_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "$COL_NL_TEN TEXT, "
                    + "$COL_NL_SO_LUONG INTEGER)")
            db.execSQL(createTableNguyenLieu)
        }
        if (oldVersion < 6) {
            val createTableThongBao = ("CREATE TABLE IF NOT EXISTS $TABLE_THONG_BAO ("
                    + "$COL_TB_ID INTEGER PRIMARY KEY, "
                    + "$COL_TB_TYPE TEXT, "
                    + "$COL_TB_TITLE TEXT, "
                    + "$COL_TB_SHORT_CONTENT TEXT, "
                    + "$COL_TB_DETAILS TEXT, "
                    + "$COL_TB_CREATED_AT TEXT)")
            db.execSQL(createTableThongBao)

            val createTableStats = ("CREATE TABLE IF NOT EXISTS $TABLE_DASHBOARD_STATS ("
                    + "$COL_STATS_REVENUE REAL, "
                    + "$COL_STATS_ORDERS INTEGER)")
            db.execSQL(createTableStats)
        }
        if (oldVersion < 7) {
            try {
                db.execSQL("ALTER TABLE $TABLE_USER ADD COLUMN $COL_USER_PHONE TEXT")
            } catch (e: Exception) {
                // Bỏ qua nếu cột đã tồn tại
            }
            try {
                db.execSQL("ALTER TABLE $TABLE_USER ADD COLUMN $COL_USER_DAC_QUYEN TEXT")
            } catch (e: Exception) {
                // Bỏ qua nếu cột đã tồn tại
            }
        }
    }
}