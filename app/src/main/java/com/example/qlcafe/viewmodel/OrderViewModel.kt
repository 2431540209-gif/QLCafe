package com.example.qlcafe.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.qlcafe.models.Order
import com.example.qlcafe.repository.OrderRepository
import com.example.qlcafe.models.OrderStatus

class OrderViewModel : ViewModel() {

    // Sử dụng Repository để lấy dữ liệu thay vì lưu cục bộ
    val orders: LiveData<MutableList<Order>> = OrderRepository.orders

    fun addOrder(order: Order) {
        OrderRepository.addOrder(order)
    }

    fun processOrder(orderId: String) {
        OrderRepository.updateOrderStatus(orderId, OrderStatus.PROCESSED)
    }

    fun cancelOrder(orderId: String) {
        OrderRepository.updateOrderStatus(orderId, OrderStatus.CANCELLED)
    }

    fun getTotalCount(): Int = OrderRepository.getTotalCount()
    fun getPendingCount(): Int = OrderRepository.getPendingCount()
    fun getProcessedCount(): Int = OrderRepository.getProcessedCount()
    fun getCancelledCount(): Int = OrderRepository.getCancelledCount()
}
