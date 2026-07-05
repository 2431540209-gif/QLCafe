package com.example.qlcafe.models

data class Order(
    val id: String,
    val user_id: Int?,
    val customer_name: String?, // Tên khách
    val table_name: String?,   // Số bàn
    val items: String?,        // Tổng hợp món
    val total_amount: Double,
    val payment_type: String,
    val created_at: String?,
    val updated_at: String?,
    var status: String = "pending"
)

data class OrderRequest(
    val user_id: Int,
    val customer_name: String,
    val table_name: String,
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

object OrderStatus {
    const val PENDING = "pending"
    const val PROCESSED = "done"
    const val CANCELLED = "cancel"
}
