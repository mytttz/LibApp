package com.example.libapp.account

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.libapp.R
import com.example.libapp.database.Book
import com.google.android.material.button.MaterialButtonToggleGroup


class OrderAdapter(
) :
    ListAdapter<Book, OrderAdapter.DishViewHolder>(DishDiffCallback()) {

    inner class DishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookPoster: ImageView = itemView.findViewById(R.id.bookPoster)
        val bookName: TextView = itemView.findViewById(R.id.book_name)
        val bookType: TextView = itemView.findViewById(R.id.book_type)
        val bookYear: TextView = itemView.findViewById(R.id.book_year)
        val bookAuthors: TextView = itemView.findViewById(R.id.book_authors)
        val quantityGroup: MaterialButtonToggleGroup = itemView.findViewById(R.id.quantity_group)
        val removeButton: Button = itemView.findViewById(R.id.remove)
        val quantity: Button = itemView.findViewById(R.id.quantity)
        val addButton: Button = itemView.findViewById(R.id.add)

        init {
            removeButton.visibility = View.GONE
            addButton.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.basket_list_item, parent, false)
        return DishViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        holder.bookName.text = getItem(position)?.title
        holder.bookType.text = getItem(position)?.type
        holder.bookYear.text = getItem(position)?.year
        holder.bookAuthors.text = getItem(position)?.authors
        holder.quantity.text = getItem(position)?.quantity.toString()
        holder.bookPoster.setImageDrawable(byteArrayToDrawable(getItem(position).image))
    }


    class DishDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(
            oldItem: Book,
            newItem: Book
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Book,
            newItem: Book
        ): Boolean {
            return oldItem == newItem
        }
    }

    private fun byteArrayToDrawable(byteArray: ByteArray?): Drawable? {
        if (byteArray == null) return null

        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        return BitmapDrawable(Resources.getSystem(), bitmap)
    }
}



