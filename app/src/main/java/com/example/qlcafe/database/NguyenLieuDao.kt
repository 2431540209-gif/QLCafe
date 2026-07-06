package com.example.qlcafe.database

import android.content.ContentValues
import com.example.qlcafe.models.NguyenLieu

class NguyenLieuDao(private val dbHelper: DatabaseHelper) {

    fun getAllNguyenLieu(): ArrayList<NguyenLieu> {
        val list = ArrayList<NguyenLieu>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_NGUYEN_LIEU}", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NL_ID))
                val ten = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NL_TEN))
                val soLuong = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NL_SO_LUONG))
                list.add(NguyenLieu(id, ten, soLuong))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun nhapKho(tenNL: String, soLuongNhap: Int) {
        val db = dbHelper.writableDatabase
        val cursor = db.rawQuery("SELECT ${DatabaseHelper.COL_NL_SO_LUONG} FROM ${DatabaseHelper.TABLE_NGUYEN_LIEU} WHERE ${DatabaseHelper.COL_NL_TEN} = ?", arrayOf(tenNL))

        if (cursor.moveToFirst()) {
            val currentQty = cursor.getInt(0)
            val values = ContentValues().apply {
                put(DatabaseHelper.COL_NL_SO_LUONG, currentQty + soLuongNhap)
            }
            db.update(DatabaseHelper.TABLE_NGUYEN_LIEU, values, "${DatabaseHelper.COL_NL_TEN} = ?", arrayOf(tenNL))
        } else {
            val values = ContentValues().apply {
                put(DatabaseHelper.COL_NL_TEN, tenNL)
                put(DatabaseHelper.COL_NL_SO_LUONG, soLuongNhap)
            }
            db.insert(DatabaseHelper.TABLE_NGUYEN_LIEU, null, values)
        }
        cursor.close()
    }

    fun xuatKho(tenNL: String, soLuongXuat: Int): Int {
        val db = dbHelper.writableDatabase
        val cursor = db.rawQuery("SELECT ${DatabaseHelper.COL_NL_SO_LUONG} FROM ${DatabaseHelper.TABLE_NGUYEN_LIEU} WHERE ${DatabaseHelper.COL_NL_TEN} = ?", arrayOf(tenNL))
        var result = 0

        if (cursor.moveToFirst()) {
            val currentQty = cursor.getInt(0)
            if (currentQty >= soLuongXuat) {
                val values = ContentValues().apply {
                    put(DatabaseHelper.COL_NL_SO_LUONG, currentQty - soLuongXuat)
                }
                db.update(DatabaseHelper.TABLE_NGUYEN_LIEU, values, "${DatabaseHelper.COL_NL_TEN} = ?", arrayOf(tenNL))
                result = 1
            } else {
                result = -1
            }
        }
        cursor.close()
        return result
    }
}
