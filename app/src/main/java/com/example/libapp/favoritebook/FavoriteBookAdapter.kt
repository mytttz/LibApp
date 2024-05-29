package com.example.libapp.favoritebook

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
import com.example.libapp.database.Book
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox

class FavoriteBookAdapter(
    private val context: Context,
    private val viewModel: FavoriteBookViewModel,
    private val showBook: (
        id: Long
    ) -> Unit
) :
    ListAdapter<Book, FavoriteBookAdapter.BookViewHolder>(BookDiffCallback()) {

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookPoster: ImageView = itemView.findViewById(R.id.bookPoster)
        val name: TextView = itemView.findViewById(R.id.book_name)
        val type: TextView = itemView.findViewById(R.id.type_book)
        val year: TextView = itemView.findViewById(R.id.book_year)
        val description: TextView = itemView.findViewById(R.id.book_description)
        val authors: TextView = itemView.findViewById(R.id.book_authors)
        val addButton: MaterialButton = itemView.findViewById(R.id.add_button)
        private val favoriteButton: CheckBox = itemView.findViewById(R.id.favorite_button)

        init {
            addButton.visibility = View.GONE
            favoriteButton.isChecked = true
            favoriteButton.setOnCheckedChangeListener { checkBox, isChecked ->
                if (!isChecked) {
                    getItem(adapterPosition)?.let { viewModel.removeFav(context, it) }
                }
            }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.book_list_item, parent, false)
        return BookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.addButton.visibility = View.GONE
        holder.name.text = getItem(position)?.title
        holder.year.text = getItem(position)?.year.toString()
        holder.type.text = getItem(position)?.type.toString()
        holder.authors.text = getItem(position)?.authors.toString()
        holder.description.text = getItem(position)?.description
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