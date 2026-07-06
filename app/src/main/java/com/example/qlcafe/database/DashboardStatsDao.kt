package com.example.qlcafe.database

import android.content.ContentValues
import com.example.qlcafe.models.DashboardStats

class DashboardStatsDao(private val dbHelper: DatabaseHelper) {

    fun cacheDashboardStats(stats: DashboardStats) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            db.delete(DatabaseHelper.TABLE_DASHBOARD_STATS, null, null)
            val values = ContentValues().apply {
                put(DatabaseHelper.COL_STATS_REVENUE, stats.total_revenue)
                put(DatabaseHelper.COL_STATS_ORDERS, stats.total_orders)
            }
            db.insert(DatabaseHelper.TABLE_DASHBOARD_STATS, null, values)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getCachedDashboardStats(): DashboardStats? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_DASHBOARD_STATS} LIMIT 1", null)
        var stats: DashboardStats? = null
        if (cursor.moveToFirst()) {
            val revenue = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STATS_REVENUE))
            val orders = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STATS_ORDERS))
            stats = DashboardStats(revenue, orders)
        }
        cursor.close()
        return stats
    }
}
