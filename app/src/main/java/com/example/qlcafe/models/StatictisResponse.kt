package com.example.qlcafe.models

import com.google.gson.annotations.SerializedName

data class StatictisResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("doanh_thu_ngay") val doanhThuNgay: Double,
    @SerializedName("doanh_thu_thang") val doanhThuThang: Double,
    @SerializedName("doanh_thu_nam") val doanhThuNam: Double
)