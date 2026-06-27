package com.example.qlcafe.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // NẾU CHẠY BẰNG MÁY ẢO ANDROID STUDIO: Dùng 10.0.2.2 thay cho localhost
    // NẾU CHẠY BẰNG ĐIỆN THOẠI THẬT CẮM CÁP: Đổi thành địa chỉ IP WiFi của máy tính (VD: 192.168.1.x)
    private const val BASE_URL = "http://10.0.2.2/qlcafe_api/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        retrofit.create(ApiService::class.java)
    }
}
