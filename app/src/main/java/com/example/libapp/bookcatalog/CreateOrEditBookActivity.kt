package com.example.libapp.bookcatalog

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.libapp.R
import com.example.libapp.database.AppDatabase
import com.example.libapp.database.Book
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class CreateOrEditBookActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var bookNameLayout: TextInputLayout
    private lateinit var bookNameEditText: TextInputEditText
    private lateinit var bookDescriptionLayout: TextInputLayout
    private lateinit var bookDescriptionEditText: TextInputEditText
    private lateinit var bookTypeLayout: TextInputLayout
    private lateinit var bookTypeEditText: TextInputEditText
    private lateinit var bookYearLayout: TextInputLayout
    private lateinit var bookYearEditText: TextInputEditText
    private lateinit var bookAuthors: TextInputLayout
    private lateinit var bookAuthorsEditText: TextInputEditText
    private lateinit var bookImageView: ImageView
    private lateinit var saveEditButton: MaterialButton
    private lateinit var preferences: SharedPreferences
    private lateinit var userIdPreferences: SharedPreferences

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                bookImageView.setImageBitmap(bitmap)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_or_edit_book_activity)
        val action = intent.getStringExtra("CreateOrEdit")
        val bookId = intent.getLongExtra("ID", -1L)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        userIdPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = userIdPreferences.getLong("user_id", -1L)
        toolbar = findViewById(R.id.topAppBar)
        bookNameLayout = findViewById(R.id.book_name)
        bookNameEditText = findViewById(R.id.book_name_text)
        bookDescriptionLayout = findViewById(R.id.book_description)
        bookDescriptionEditText = findViewById(R.id.book_description_text)
        bookTypeLayout = findViewById(R.id.book_type)
        bookTypeEditText = findViewById(R.id.dish_type_text)
        bookYearLayout = findViewById(R.id.book_year)
        bookYearEditText = findViewById(R.id.book_year_text)
        bookAuthors = findViewById(R.id.book_authors)
        bookAuthorsEditText = findViewById(R.id.book_authors_text)
        bookImageView = findViewById(R.id.image_book)
        saveEditButton = findViewById(R.id.save_dish_button)


        if (action == "Edit") {
            CoroutineScope(Dispatchers.IO).launch {
                val book = AppDatabase.getDatabase(this@CreateOrEditBookActivity).bookDao()
                    .getBookById(bookId)
                withContext(Dispatchers.Main) {
                    saveEditButton.text = "Save changes"
                    bookNameEditText.setText(book.title)
                    bookDescriptionEditText.setText(book.description)
                    bookTypeEditText.setText(book.type)
                    bookYearEditText.setText(book.year)
                    bookAuthorsEditText.setText(book.authors)
                    bookImageView.setImageDrawable(byteArrayToDrawable(book.image))
                }
            }
        }

        bookImageView.setOnClickListener {
            pickImageFromGallery()
        }

        saveEditButton.setOnClickListener {
            if (isFieldsNotEmpty()) {
                val book = Book(
                    title = bookNameEditText.text.toString(),
                    description = bookDescriptionEditText.text.toString(),
                    type = bookTypeEditText.text.toString(),
                    year = bookYearEditText.text.toString(),
                    authors = bookAuthorsEditText.text.toString(),
                    image = drawableToByteArray(bookImageView.drawable)
                )
                CoroutineScope(Dispatchers.IO).launch {
                    if (action == "Edit") {
                        AppDatabase.getDatabase(this@CreateOrEditBookActivity).bookDao()
                            .updateBook(book.copy(id = bookId))
                    } else if (action == "Create") {
                        AppDatabase.getDatabase(this@CreateOrEditBookActivity).bookDao()
                            .insertBook(book)
                    }
                    withContext(Dispatchers.Main) {
                        val intent =
                            Intent(this@CreateOrEditBookActivity, BookCatalogActivity::class.java)
                        startActivity(intent)
                    }
                }
            } else {
                Toast.makeText(this, "Fill in the fields!", Toast.LENGTH_SHORT).show()
            }
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete -> {
                    if (bookId != -1L) {
                        CoroutineScope(Dispatchers.IO).launch {
                            AppDatabase.getDatabase(this@CreateOrEditBookActivity).bookDao()
                                .deleteBook(bookId)
                        }
                        val intent = Intent(this, BookCatalogActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@CreateOrEditBookActivity,
                            "You cannot delete an uncreated book!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    true
                }

                else -> false
            }
        }
    }

    private fun isFieldsNotEmpty(): Boolean {
        return bookNameEditText.text?.isNotBlank() == true && bookDescriptionEditText.text?.isNotBlank() == true &&
                bookTypeEditText.text?.isNotBlank() == true && bookYearEditText.text?.isNotBlank() == true &&
                bookAuthorsEditText.text?.isNotBlank() == true
    }

    private fun byteArrayToDrawable(byteArray: ByteArray?): Drawable? {
        if (byteArray == null) return null

        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        return BitmapDrawable(Resources.getSystem(), bitmap)
    }

    private fun drawableToByteArray(drawable: Drawable): ByteArray {
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
            return byteArrayOutputStream.toByteArray()
        } else {
            throw IllegalArgumentException("Drawable must be instance of BitmapDrawable")
        }
    }

    private fun pickImageFromGallery() {
        pickImage.launch("image/*")
    }
}