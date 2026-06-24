package com.example.qlcafe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.R
import com.example.qlcafe.adapter.OrderListAdapter
import com.example.qlcafe.models.OrderStatus
import com.example.qlcafe.viewmodel.OrderViewModel

class DanhSachDonHangFragment : Fragment() {

    private val viewModel: OrderViewModel by activityViewModels()

    private lateinit var tvDisplayCount: TextView
    private lateinit var tvDisplayLabel: TextView
    private lateinit var rvOrders: RecyclerView

    private lateinit var btnTabAll: TextView
    private lateinit var btnTabPending: TextView
    private lateinit var btnTabProcessed: TextView
    private lateinit var btnTabCancelled: TextView

    private var currentFilter = "ALL"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_danh_sach_don_hang, container, false)

        tvDisplayCount = view.findViewById(R.id.tvDisplayStatsCount)
        tvDisplayLabel = view.findViewById(R.id.tvDisplayStatsLabel)
        rvOrders = view.findViewById(R.id.rvOrders)

        btnTabAll = view.findViewById(R.id.btnTabAll)
        btnTabPending = view.findViewById(R.id.btnTabPending)
        btnTabProcessed = view.findViewById(R.id.btnTabProcessed)
        btnTabCancelled = view.findViewById(R.id.btnTabCancelled)

        rvOrders.layoutManager = LinearLayoutManager(requireContext())

        setupTabs()
        observeViewModel()

        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            requireActivity().finish()
        }

        return view
    }

    private fun setupTabs() {
        btnTabAll.setOnClickListener {
            updateTabUI("ALL")
            refreshList()
        }
        btnTabPending.setOnClickListener {
            updateTabUI("PENDING")
            refreshList()
        }
        btnTabProcessed.setOnClickListener {
            updateTabUI("PROCESSED")
            refreshList()
        }
        btnTabCancelled.setOnClickListener {
            updateTabUI("CANCELLED")
            refreshList()
        }
    }

    private fun updateTabUI(filter: String) {
        currentFilter = filter
        
        btnTabAll.setBackgroundResource(if (filter == "ALL") R.drawable.bg_tab_selected else R.drawable.bg_tab_unselected)
        btnTabAll.setTextColor(if (filter == "ALL") requireContext().getColor(android.R.color.white) else requireContext().getColor(R.color.text_grey))

        btnTabPending.setBackgroundResource(if (filter == "PENDING") R.drawable.bg_tab_selected else R.drawable.bg_tab_unselected)
        btnTabPending.setTextColor(if (filter == "PENDING") requireContext().getColor(android.R.color.white) else requireContext().getColor(R.color.text_grey))

        btnTabProcessed.setBackgroundResource(if (filter == "PROCESSED") R.drawable.bg_tab_selected else R.drawable.bg_tab_unselected)
        btnTabProcessed.setTextColor(if (filter == "PROCESSED") requireContext().getColor(android.R.color.white) else requireContext().getColor(R.color.text_grey))
        
        btnTabCancelled.setBackgroundResource(if (filter == "CANCELLED") R.drawable.bg_tab_selected else R.drawable.bg_tab_unselected)
        btnTabCancelled.setTextColor(if (filter == "CANCELLED") requireContext().getColor(android.R.color.white) else requireContext().getColor(R.color.text_grey))
        
        updateStatsDisplay()
    }

    private fun observeViewModel() {
        viewModel.orders.observe(viewLifecycleOwner) {
            updateStatsDisplay()
            refreshList()
        }
    }

    private fun updateStatsDisplay() {
        when (currentFilter) {
            "ALL" -> {
                tvDisplayCount.text = viewModel.getTotalCount().toString()
                tvDisplayLabel.text = "Tổng đơn"
            }
            "PENDING" -> {
                tvDisplayCount.text = viewModel.getPendingCount().toString()
                tvDisplayLabel.text = "Chờ xử lý"
            }
            "PROCESSED" -> {
                tvDisplayCount.text = viewModel.getProcessedCount().toString()
                tvDisplayLabel.text = "Đã xử lý"
            }
            "CANCELLED" -> {
                tvDisplayCount.text = viewModel.getCancelledCount().toString()
                tvDisplayLabel.text = "Đã hủy"
            }
        }
    }

    private fun refreshList() {
        val allOrders = viewModel.orders.value ?: emptyList()
        val filteredList = when (currentFilter) {
            "PENDING" -> allOrders.filter { it.status == OrderStatus.PENDING }
            "PROCESSED" -> allOrders.filter { it.status == OrderStatus.PROCESSED }
            "CANCELLED" -> allOrders.filter { it.status == OrderStatus.CANCELLED }
            else -> allOrders
        }

        rvOrders.adapter = OrderListAdapter(
            filteredList,
            onProcessClick = { order -> viewModel.processOrder(order.id) },
            onCancelClick = { order -> viewModel.cancelOrder(order.id) }
        )
    }
}
