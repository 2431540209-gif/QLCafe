package com.example.qlcafe.models

data class AddProductRequest(
    val name: String,
    val price: Double,
    val description: String
)

data class UpdateProductRequest(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String
)

data class DeleteProductRequest(
    val id: Int
)

data class ProductResponse(
    val success: Boolean,
    val message: String,
    val product_id: Int? = null
)
