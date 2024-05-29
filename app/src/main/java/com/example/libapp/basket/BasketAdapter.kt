package com.example.libapp.basket

import android.content.Context
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


class BasketAdapter(
    private val userId: Long,
    private val context: Context,
    private val viewModel: BasketViewModel,
    private val showBook: (
        id: Long
    ) -> Unit
) :
    ListAdapter<Book, BasketAdapter.BookViewHolder>(BookDiffCallback()) {

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
            // Обработчик нажатия на кнопку "Уменьшить количество"
            removeButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val currentDish = getItem(position)
                    if (currentDish.quantity > 1) {
                        // Уменьшаем количество товара
                        viewModel.updateBasket(context, currentDish, 0)
                    } else {
                        // Удаляем товар, если количество <= 1
                        viewModel.updateBasket(context, currentDish, 0)
                    }
                }
            }

            // Обработчик нажатия на кнопку "Увеличить количество"
            addButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val currentDish = getItem(position)
                    // Увеличиваем количество товара
                    viewModel.updateBasket(context, currentDish, 1)
                }
            }

            // Обработчик нажатия на элемент списка
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val id = getItem(position).id
                    showBook(id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.basket_list_item, parent, false)
        return BookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bookName.text = getItem(position)?.title
        holder.bookType.text = getItem(position)?.type
        holder.bookYear.text = getItem(position)?.year
        holder.bookAuthors.text = getItem(position)?.authors
        holder.quantity.text = getItem(position)?.quantity.toString()
        holder.bookPoster.setImageDrawable(byteArrayToDrawable(getItem(position).image))
    }


    class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
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



