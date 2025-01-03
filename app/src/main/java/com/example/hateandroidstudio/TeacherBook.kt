package com.example.hateandroidstudio

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.launch

class TeacherBook : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recomend: RecyclerView
    private lateinit var booksAdapter: BooksAdapter
    private lateinit var recmendAdapter: BooksAdapter
    private lateinit var db: AppDatabase
    private val booksList = mutableListOf<Books>()
    private val recomendList = mutableListOf<Books>()
    private lateinit var database: AppDatabase

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_book)
        this.findViewById<ImageView>(R.id.onLog).setOnClickListener {
            val intent = Intent(this, TeacherActivity::class.java)
            startActivity(intent)
        }
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "user-db").build()
        database = Room.databaseBuilder(this, AppDatabase::class.java, "books.db").build()

        recyclerView = findViewById(R.id.libView)
        recomend = findViewById(R.id.recView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recomend.layoutManager = LinearLayoutManager(this)

        booksAdapter = BooksAdapter(booksList, { book -> }, { book -> })
        recmendAdapter = BooksAdapter(recomendList, { book -> }, { book -> })

        recyclerView.adapter = booksAdapter
        recomend.adapter = recmendAdapter



        loadBooks()
    }
    private fun loadBooks() {
        lifecycleScope.launch {
            val bookDao = database.bookDao()
            val savedBooks = bookDao.getAllBooks()
            booksList.clear()
            recomendList.clear()

            savedBooks.forEach { book ->
                if (book.recom == true) {
                    recomendList.add(book)
                } else {
                    booksList.add(book)
                }
            }
            booksAdapter.notifyDataSetChanged()
            recmendAdapter.notifyDataSetChanged()
        }
    }

}