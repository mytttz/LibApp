package com.example.libapp.bookcatalog


import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.setFragmentResult
import com.example.libapp.R
import com.example.libapp.database.AppDatabase
import com.example.libapp.basket.BasketRepository
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FullDescriptionBookBottomSheet : BottomSheetDialogFragment() {
    private lateinit var bookImage: ImageView
    private lateinit var bookName: TextView
    private lateinit var bookType: TextView
    private lateinit var bookYear: TextView
    private lateinit var bookAuthors: TextView
    private lateinit var bookDescription: TextView
    private lateinit var bookAdd: MaterialButton
    private lateinit var userIdPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.full_description_book_bottom_sheet, container, false)
        context?.let {
            userIdPreferences = it.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        }
        bookImage = view.findViewById(R.id.bookPoster)
        bookName = view.findViewById(R.id.book_name)
        bookType = view.findViewById(R.id.book_type)
        bookYear = view.findViewById(R.id.book_year)
        bookAuthors = view.findViewById(R.id.book_authors)
        bookDescription = view.findViewById(R.id.book_description)
        bookAdd = view.findViewById(R.id.add_button)

        val bookId = arguments?.getLong("ID")
        CoroutineScope(Dispatchers.IO).launch {
            val dish = bookId?.let {
                context?.let { it1 ->
                    AppDatabase.getDatabase(it1).bookDao().getBookById(it)
                }
            }
            withContext(Dispatchers.Main) {
                bookImage.setImageDrawable(byteArrayToDrawable(dish?.image))
                bookName.text = dish?.title
                bookType.text = dish?.type.toString()
                bookYear.text = dish?.year
                bookAuthors.text = dish?.authors
                bookDescription.text = dish?.description
            }
        }

        bookAdd.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val basketRepository = context?.let { it1 ->
                    AppDatabase.getDatabase(
                        it1
                    ).basketDao()
                }?.let { it2 -> BasketRepository(it2) }
                val dish = bookId?.let {
                    context?.let { it1 ->
                        AppDatabase.getDatabase(it1).bookDao().getBookById(it)
                    }
                }
                if (dish != null) {
                    basketRepository?.manageBookInBasket(
                        userIdPreferences.getLong("user_id", -1L),
                        dish, 1
                    )
                }
                withContext(Dispatchers.Main) {
                    setFragmentResult("bookAdd", Bundle())
                    dismiss()

                }
            }

        }

        return view
    }

    companion object {
        const val TAG = "FullDescriptionBookBottomSheet"
    }

    private fun byteArrayToDrawable(byteArray: ByteArray?): Drawable? {
        if (byteArray == null) return null

        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        return BitmapDrawable(Resources.getSystem(), bitmap)
    }
}