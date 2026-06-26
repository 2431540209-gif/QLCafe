package com.example.qlcafe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.R
import com.example.qlcafe.models.TaskCategory

class TaskCategoryAdapter(
    private val categories: List<TaskCategory>,
    private val childClickListener: TaskChildAdapter.OnTaskClickListener
) : RecyclerView.Adapter<TaskCategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.tvCategoryName.text = category.categoryName

        holder.rvChildTasks.layoutManager = GridLayoutManager(holder.itemView.context, 3)

        val childAdapter = TaskChildAdapter(category.taskItems, childClickListener)
        holder.rvChildTasks.adapter = childAdapter
    }

    override fun getItemCount(): Int = categories.size

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        val rvChildTasks: RecyclerView = itemView.findViewById(R.id.rvChildTasks)
    }
}