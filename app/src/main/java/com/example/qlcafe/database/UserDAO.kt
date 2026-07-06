package com.example.qlcafe.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class UserDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insertUser(username: String, phone: String, role: String, dacQuyen: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put(DatabaseHelper.COL_USER_NAME, username)
        values.put(DatabaseHelper.COL_USER_PHONE, phone)
        values.put(DatabaseHelper.COL_USER_ROLE, role)
        values.put(DatabaseHelper.COL_USER_DAC_QUYEN, dacQuyen)
        return db.insert(DatabaseHelper.TABLE_USER, null, values)
    }

    fun getAllUsers(): Cursor {
        val db = dbHelper.readableDatabase
        val query = "SELECT ${DatabaseHelper.COL_USER_ID} AS _id, ${DatabaseHelper.COL_USER_NAME}, ${DatabaseHelper.COL_USER_PHONE}, ${DatabaseHelper.COL_USER_ROLE}, ${DatabaseHelper.COL_USER_DAC_QUYEN} FROM ${DatabaseHelper.TABLE_USER}"
        return db.rawQuery(query, null)
    }

    fun updateUser(phone: String, username: String, role: String, dacQuyen: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put(DatabaseHelper.COL_USER_NAME, username)
        values.put(DatabaseHelper.COL_USER_ROLE, role)
        values.put(DatabaseHelper.COL_USER_DAC_QUYEN, dacQuyen)
        return db.update(DatabaseHelper.TABLE_USER, values, "${DatabaseHelper.COL_USER_PHONE} = ?", arrayOf(phone))
    }

    fun changePassword(phone: String, passwordNew: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put(DatabaseHelper.COL_USER_PASSWORD, passwordNew)
        return db.update(DatabaseHelper.TABLE_USER, values, "${DatabaseHelper.COL_USER_PHONE} = ?", arrayOf(phone))
    }

    fun deleteUser(phone: String): Int {
        val db = dbHelper.writableDatabase
        return db.delete(DatabaseHelper.TABLE_USER, "${DatabaseHelper.COL_USER_PHONE} = ?", arrayOf(phone))
    }
}