package com.example.qlcafe.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.qlcafe.api.RetrofitClient
import com.example.qlcafe.models.*
import com.example.qlcafe.repository.OrderRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderViewModel : ViewModel() {

    // Sử dụng Repository để lấy dữ liệu thay vì lưu cục bộ
    val orders: LiveData<MutableList<Order>> = OrderRepository.orders

    fun addOrder(order: Order, userId: Int, items: List<OrderItemRequest>, onResult: (Boolean, String) -> Unit) {
        val request = OrderRequest(
            user_id = userId,
            total_amount = order.total_amount,
            payment_type = order.payment_type,
            items = items
        )

        RetrofitClient.instance.createOrder(request).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    OrderRepository.addOrder(order)
                    onResult(true, "Tạo đơn hàng thành công!")
                } else {
                    onResult(false, "Lỗi: " + (response.body()?.message ?: "Không xác định"))
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                onResult(false, "Lỗi kết nối: " + t.message)
            }
        })
    }

    fun processOrder(orderId: String) {
        OrderRepository.updateOrderStatus(orderId, "PROCESSED")
    }

    fun cancelOrder(orderId: String) {
        OrderRepository.updateOrderStatus(orderId, "CANCELLED")
    }

    fun getTotalCount(): Int = OrderRepository.getTotalCount()
    fun getPendingCount(): Int = OrderRepository.getPendingCount()
    fun getProcessedCount(): Int = OrderRepository.getProcessedCount()
    fun getCancelledCount(): Int = OrderRepository.getCancelledCount()
}
