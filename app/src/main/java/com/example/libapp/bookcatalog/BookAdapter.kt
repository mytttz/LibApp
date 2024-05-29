package com.example.libapp.bookcatalog

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.libapp.R
import com.example.libapp.database.AppDatabase
import com.example.libapp.database.Book
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookAdapter(
    private val userId: Long,
    private val context: Context,
    private val viewModel: BookCatalogViewModel,
    private val showBook: (id: Long) -> Unit
) : ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookPoster: ImageView = itemView.findViewById(R.id.bookPoster)
        val name: TextView = itemView.findViewById(R.id.book_name)
        val type: TextView = itemView.findViewById(R.id.type_book)
        val year: TextView = itemView.findViewById(R.id.book_year)
        val description: TextView = itemView.findViewById(R.id.book_description)
        val authors: TextView = itemView.findViewById(R.id.book_authors)
        val addButton: MaterialButton = itemView.findViewById(R.id.add_button)
        val favoriteButton: CheckBox = itemView.findViewById(R.id.favorite_button)

        init {
            addButton.setOnClickListener {
                if (userId == -100L) {
                    viewModel.editBook(getItem(adapterPosition).id, context)
                } else {
                    viewModel.addToBasket(context, userId, getItem(adapterPosition))
                }
            }
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val id = getItem(position).id
                    showBook(id)
                }
            }

            favoriteButton.setOnCheckedChangeListener { checkBox, isChecked ->
                if (userId == -100L) {
                    favoriteButton.visibility = View.GONE
                } else {
                    if (isChecked) {
                        viewModel.addToFavorite(getItem(adapterPosition), context)
                    } else {
                        viewModel.removeToFavorite(getItem(adapterPosition), context)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.book_list_item, parent, false)
        return BookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = getItem(position)
        if (userId == -100L) {
            holder.addButton.icon = context.getDrawable(R.drawable.edit_icon)
            holder.favoriteButton.visibility = View.GONE
        }
        holder.name.text = book?.title
        holder.year.text = book?.year.toString()
        holder.type.text = book?.type.toString()
        holder.authors.text = book?.authors.toString()
        holder.description.text = book?.description
        holder.bookPoster.setImageDrawable(byteArrayToDrawable(book?.image))

        // Set the favorite button state in onBindViewHolder
        CoroutineScope(Dispatchers.IO).launch {
            val isFavorite = AppDatabase.getDatabase(context)
                .favoriteBookListDao()
                .getFavoriteListByUserId(userId)
                ?.composition
                ?.any { it.id == book?.id } == true

            withContext(Dispatchers.Main) {
                holder.favoriteButton.setOnCheckedChangeListener(null) // Remove the listener temporarily
                holder.favoriteButton.isChecked = isFavorite
                holder.favoriteButton.setOnCheckedChangeListener { checkBox, isChecked ->
                    if (isChecked) {
                        viewModel.addToFavorite(book, context)
                    } else {
                        viewModel.removeToFavorite(book, context)
                    }
                }
            }
        }
    }

    class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }

    private fun byteArrayToDrawable(byteArray: ByteArray?): Drawable? {
        if (byteArray == null) return null

        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        return BitmapDrawable(Resources.getSystem(), bitmap)
    }
}
