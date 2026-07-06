package com.example.qlcafe.models

import com.google.gson.annotations.SerializedName

data class PermissionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("phone") val phone: String?,
    @SerializedName("username") val username: String?,
    @SerializedName("role") val role: String?,
    @SerializedName("dac_quyen") val dacQuyen: String?,
    @SerializedName("message") val message: String?
)