package com.example.qlcafe.database

import android.content.ContentValues
import android.database.Cursor
import com.example.qlcafe.models.Product

class ProductDao(private val dbHelper: DatabaseHelper) {

    fun insertSanPham(tenMon: String, gia: Double, moTa: String, hinhAnh: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_TEN_MON, tenMon)
            put(DatabaseHelper.COL_GIA, gia)
            put(DatabaseHelper.COL_MO_TA, moTa)
            put(DatabaseHelper.COL_HINH_ANH, hinhAnh)
        }
        return db.insert(DatabaseHelper.TABLE_SAN_PHAM, null, values)
    }

    fun getAllSanPham(): Cursor {
        val db = dbHelper.readableDatabase
        return db.rawQuery("SELECT ${DatabaseHelper.COL_ID} AS _id, ${DatabaseHelper.COL_TEN_MON}, ${DatabaseHelper.COL_GIA}, ${DatabaseHelper.COL_MO_TA}, ${DatabaseHelper.COL_HINH_ANH} FROM ${DatabaseHelper.TABLE_SAN_PHAM}", null)
    }

    fun updateSanPham(id: Int, tenMon: String, gia: Double, moTa: String, hinhAnh: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_TEN_MON, tenMon)
            put(DatabaseHelper.COL_GIA, gia)
            put(DatabaseHelper.COL_MO_TA, moTa)
            put(DatabaseHelper.COL_HINH_ANH, hinhAnh)
        }
        return db.update(DatabaseHelper.TABLE_SAN_PHAM, values, "${DatabaseHelper.COL_ID}=?", arrayOf(id.toString()))
    }

    fun deleteSanPham(id: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(DatabaseHelper.TABLE_SAN_PHAM, "${DatabaseHelper.COL_ID}=?", arrayOf(id.toString()))
    }

    fun cacheProducts(list: List<Product>) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            db.delete(DatabaseHelper.TABLE_SAN_PHAM, null, null)
            for (p in list) {
                val values = ContentValues().apply {
                    put(DatabaseHelper.COL_ID, p.id)
                    put(DatabaseHelper.COL_TEN_MON, p.name)
                    put(DatabaseHelper.COL_GIA, p.price)
                    put(DatabaseHelper.COL_MO_TA, "")
                    put(DatabaseHelper.COL_HINH_ANH, p.image_url ?: "")
                }
                db.insert(DatabaseHelper.TABLE_SAN_PHAM, null, values)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getCachedProducts(): List<Product> {
        val list = ArrayList<Product>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_SAN_PHAM}", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TEN_MON))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GIA))
                val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_HINH_ANH))
                list.add(Product(id, name, price, imageUrl, null))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}
