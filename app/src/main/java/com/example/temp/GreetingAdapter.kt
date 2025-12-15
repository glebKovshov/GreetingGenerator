package com.example.temp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class GreetingAdapter(
    private val list: List<GreetingEntity>,
    private val onClick: (GreetingEntity) -> Unit
) : RecyclerView.Adapter<GreetingAdapter.GreetingViewHolder>() {

    class GreetingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvGreetingTitle)
        val tvDate: TextView = itemView.findViewById(R.id.tvGreetingDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GreetingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_greeting, parent, false)
        return GreetingViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: GreetingViewHolder, position: Int) {
        val item = list[position]

        holder.tvTitle.text = "${item.name} — ${item.occasion}"

        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        holder.tvDate.text = sdf.format(item.createdAt)

        // КЛИК по элементу
        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }
}
