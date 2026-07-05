package com.example.qlcafe.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.qlcafe.api.RetrofitClient
import com.example.qlcafe.repository.OrderRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderViewModel : ViewModel() {

    // Sử dụng Repository để lấy dữ liệu thay vì lưu cục bộ
    val orders: LiveData<MutableList<Order>> = OrderRepository.orders

    fun fetchOrders(userId: Int) {
        RetrofitClient.instance.getOrders(userId).enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        OrderRepository.setOrders(it)
                    }
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                // Có thể thêm LiveData xử lý lỗi nếu cần
            }
        })
    }

    fun addOrder(request: OrderRequest, order: Order, onResult: (Boolean, String) -> Unit) {
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
        updateStatusOnServer(orderId, OrderStatus.PROCESSED)
    }

    fun cancelOrder(orderId: String) {
        updateStatusOnServer(orderId, OrderStatus.CANCELLED)
    }

    private fun updateStatusOnServer(orderId: String, status: String) {
        val request = mapOf("order_id" to orderId, "status" to status)
        RetrofitClient.instance.updateOrderStatus(request).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    OrderRepository.updateOrderStatus(orderId, status)
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                // Xử lý lỗi nếu cần
            }
        })
    }

    fun getTotalCount(): Int = OrderRepository.getTotalCount()
    fun getPendingCount(): Int = OrderRepository.getPendingCount()
    fun getProcessedCount(): Int = OrderRepository.getProcessedCount()
    fun getCancelledCount(): Int = OrderRepository.getCancelledCount()
}
