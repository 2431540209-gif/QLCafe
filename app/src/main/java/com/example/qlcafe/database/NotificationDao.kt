package com.example.qlcafe.database

import android.content.ContentValues
import com.example.qlcafe.models.ThongBao

class NotificationDao(private val dbHelper: DatabaseHelper) {

    fun cacheNotifications(list: List<ThongBao>) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            db.delete(DatabaseHelper.TABLE_THONG_BAO, null, null)
            for (tb in list) {
                val values = ContentValues().apply {
                    put(DatabaseHelper.COL_TB_ID, tb.id)
                    put(DatabaseHelper.COL_TB_TYPE, tb.type)
                    put(DatabaseHelper.COL_TB_TITLE, tb.title)
                    put(DatabaseHelper.COL_TB_SHORT_CONTENT, tb.short_content)
                    put(DatabaseHelper.COL_TB_DETAILS, tb.details)
                    put(DatabaseHelper.COL_TB_CREATED_AT, tb.created_at)
                }
                db.insert(DatabaseHelper.TABLE_THONG_BAO, null, values)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getCachedNotifications(): List<ThongBao> {
        val list = ArrayList<ThongBao>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_THONG_BAO} ORDER BY ${DatabaseHelper.COL_TB_ID} DESC", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TB_ID))
                val type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TB_TYPE))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TB_TITLE))
                val shortContent = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TB_SHORT_CONTENT))
                val details = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TB_DETAILS))
                val createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TB_CREATED_AT))
                list.add(ThongBao(id, type, title, shortContent, details, createdAt))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}
