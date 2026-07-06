package com.example.qlcafe.models

import com.google.gson.annotations.SerializedName

data class UpdatePermissionRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("dac_quyen") val dacQuyen: String
)