package com.example.qlcafe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.R
import com.example.qlcafe.models.TaskItem

class TaskChildAdapter(
    private val taskItems: List<TaskItem>,
    private val listener: OnTaskClickListener
) : RecyclerView.Adapter<TaskChildAdapter.ChildViewHolder>() {

    interface OnTaskClickListener {
        fun onTaskClick(item: TaskItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task_child, parent, false)
        return ChildViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val item = taskItems[position]
        holder.tvTitle.text = item.title
        holder.imgIcon.setImageResource(item.iconResId)

        holder.itemView.setOnClickListener { listener.onTaskClick(item) }
    }

    override fun getItemCount(): Int = taskItems.size

    class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgIcon: ImageView = itemView.findViewById(R.id.imgTaskIcon)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)
    }
}