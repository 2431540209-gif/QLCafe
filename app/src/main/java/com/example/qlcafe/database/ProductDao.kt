package com.example.qlcafe.database

import android.content.ContentValues
import android.database.Cursor
import com.example.qlcafe.models.Product

class ProductDao(private val dbHelper: DatabaseHelper) {

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
                    put(DatabaseHelper.COL_MO_TA, p.description ?: "")
                    put(DatabaseHelper.COL_HINH_ANH, "")
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
                val desc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MO_TA))
                list.add(Product(id, name, price, desc))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}
