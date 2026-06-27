package com.example.qlcafe.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("register.php")
    fun registerUser(@Body request: RegisterRequest): Call<AuthResponse>

    @POST("login.php")
    fun loginUser(@Body request: LoginRequest): Call<AuthResponse>
}
