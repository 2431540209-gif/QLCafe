package com.example.qlcafe.models

data class Order(
    val id: String,
    val customerName: String,
    val table: String,
    val items: String,
    val price: Double,
    val time: String,
    var status: OrderStatus = OrderStatus.PENDING
)

enum class OrderStatus {
    PENDING,    // Chờ xử lý
    PROCESSED,  // Đã xử lý
    CANCELLED   // Đã hủy
}

data class ProductOrder(
    val name: String,
    val price: Double,
    var quantity: Int = 0
)
