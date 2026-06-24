package com.example.qlcafe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.R
import com.example.qlcafe.adapter.ProductSelectionAdapter
import com.example.qlcafe.models.Order
import com.example.qlcafe.models.ProductOrder
import com.example.qlcafe.viewmodel.OrderViewModel
import java.text.SimpleDateFormat
import java.util.*

class TaoDonHangFragment : Fragment() {

    private val viewModel: OrderViewModel by activityViewModels()
    
    private lateinit var etCustomerName: EditText
    private lateinit var spinnerTable: Spinner
    private lateinit var rvProducts: RecyclerView
    private lateinit var tvTotalPrice: TextView
    private lateinit var btnSubmit: Button
    
    private val products = listOf(
        ProductOrder("Cà phê sữa", 25000.0),
        ProductOrder("Bạc xỉu", 30000.0),
        ProductOrder("Cappuccino", 45000.0),
        ProductOrder("Trà đào", 30000.0),
        ProductOrder("Sinh tố bơ", 35000.0),
        ProductOrder("Bánh mì", 20000.0)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tao_don_hang, container, false)
        
        etCustomerName = view.findViewById(R.id.etCustomerName)
        spinnerTable = view.findViewById(R.id.spinnerTable)
        rvProducts = view.findViewById(R.id.rvProductSelection)
        tvTotalPrice = view.findViewById(R.id.tvTotalOrderPrice)
        btnSubmit = view.findViewById(R.id.btnCreateOrderSubmit)

        setupSpinner()
        setupRecyclerView()
        
        btnSubmit.setOnClickListener {
            createOrder()
        }

        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            requireActivity().finish()
        }

        return view
    }

    private fun setupSpinner() {
        val tables = arrayOf("Bàn 1", "Bàn 2", "Bàn 3", "Bàn 4", "Bàn 5", "Mang về")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tables)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTable.adapter = adapter
    }

    private fun setupRecyclerView() {
        rvProducts.layoutManager = LinearLayoutManager(requireContext())
        rvProducts.adapter = ProductSelectionAdapter(products) {
            updateTotalPrice()
        }
    }

    private fun updateTotalPrice() {
        val total = products.sumOf { it.price * it.quantity }
        tvTotalPrice.text = String.format(Locale.getDefault(), "%,.0fđ", total)
    }

    private fun createOrder() {
        val name = etCustomerName.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập tên khách!", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedItems = products.filter { it.quantity > 0 }
        if (selectedItems.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng chọn ít nhất 1 món!", Toast.LENGTH_SHORT).show()
            return
        }

        val table = spinnerTable.selectedItem.toString()
        val itemsSummary = selectedItems.joinToString(", ") { "${it.name} x${it.quantity}" }
        val totalPrice = selectedItems.sumOf { it.price * it.quantity }
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val orderId = "DH${String.format(Locale.getDefault(), "%03d", viewModel.getTotalCount() + 1)}"

        val order = Order(orderId, name, table, itemsSummary, totalPrice, time)
        viewModel.addOrder(order)

        Toast.makeText(requireContext(), "Tạo đơn hàng thành công!", Toast.LENGTH_SHORT).show()
        
        // Chuyển sang tab Danh sách
        val bottomNav = requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_danh_sach
    }
}
