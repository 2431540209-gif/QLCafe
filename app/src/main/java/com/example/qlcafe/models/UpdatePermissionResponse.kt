package com.example.qlcafe.models

import com.google.gson.annotations.SerializedName

data class UpdatePermissionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)