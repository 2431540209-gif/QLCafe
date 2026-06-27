package com.example.qlcafe.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.qlcafe.models.Order
import com.example.qlcafe.models.OrderStatus

/**
 * Singleton Repository để lưu trữ đơn hàng trong bộ nhớ.
 * Dữ liệu sẽ tồn tại xuyên suốt phiên làm việc của ứng dụng, ngay cả khi đóng Activity.
 */
object OrderRepository {
    private val orderList = mutableListOf<Order>()
    private val _orders = MutableLiveData<MutableList<Order>>(orderList)
    val orders: LiveData<MutableList<Order>> = _orders

    fun addOrder(order: Order) {
        orderList.add(0, order)
        _orders.value = orderList
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        val order = orderList.find { it.id == orderId }
        order?.status = newStatus
        _orders.value = orderList
    }

    fun getTotalCount() = orderList.size
    fun getPendingCount() = orderList.count { it.status == OrderStatus.PENDING }
    fun getProcessedCount() = orderList.count { it.status == OrderStatus.PROCESSED }
    fun getCancelledCount() = orderList.count { it.status == OrderStatus.CANCELLED }
}
