package com.example.qlcafe.models

data class NhapXuatKhoRequest(
    val name: String,
    val quantity: Int
)

data class InventoryResponse(
    val success: Boolean,
    val message: String,
    val status: Int? = null
)
