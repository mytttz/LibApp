package com.example.libapp.account

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.libapp.R
import com.example.libapp.database.Order


class CompletedOrdersAdapter(
    private val showBook: (
        id: Long
    ) -> Unit
) :
    ListAdapter<Order, CompletedOrdersAdapter.OrderViewHolder>(OrderDiffCallback()) {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderName: TextView = itemView.findViewById(R.id.order_name)
        val orderState: TextView = itemView.findViewById(R.id.order_state)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val id = getItem(position).id
                    showBook(
                        id
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.orderName.text = getItem(position)?.name
        holder.orderState.text = "Book has been returned"
    }


    class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(
            oldItem: Order,
            newItem: Order
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Order,
            newItem: Order
        ): Boolean {
            return oldItem == newItem
        }
    }
}