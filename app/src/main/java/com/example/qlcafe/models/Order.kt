package com.example.qlcafe.models

data class Order(
    val id: String,
    val user_id: Int?,
    val customerName: String, // Trong DB thực tế có thể map từ user_id hoặc thêm cột
    val table: String,
    val items: String, // Summary for local display
    val total_amount: Double,
    val payment_type: String,
    val time: String,
    var status: String = "pending"
)

data class OrderRequest(
    val user_id: Int,
    val total_amount: Double,
    val payment_type: String,
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    val product_id: Int,
    val quantity: Int,
    val unit_price: Double
)

data class OrderResponse(
    val success: Boolean,
    val message: String,
    val order_id: Int? = null
)

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val image_url: String?,
    val category_id: Int?
)

data class ProductOrder(
    val name: String,
    val price: Double,
    var quantity: Int = 0
)
