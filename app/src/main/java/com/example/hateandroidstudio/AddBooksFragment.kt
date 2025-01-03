package com.example.hateandroidstudio

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.launch
import org.json.JSONObject
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddBooksFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddBooksFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var booksAdapter: BooksAdapter
    private lateinit var recyclerView: RecyclerView
    private val booksList = mutableListOf<Books>()
    private lateinit var titleTextView: TextView
    private lateinit var titleEditText: EditText
    private lateinit var sectionEditText: EditText
    private lateinit var daysEditText: EditText
    private lateinit var electronicCheckBox: CheckBox
    private lateinit var coverImageView: ImageView
    private lateinit var authorTextView: TextView
    private lateinit var saveButton: Button
    private lateinit var searchButton: Button
    private lateinit var removeButton: Button
    private lateinit var clearButton: Button
    private lateinit var database: AppDatabase
    private lateinit var rec: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_add_books, container, false)

        removeButton = view.findViewById(R.id.deleteButton)
        titleTextView = view.findViewById(R.id.titleTextView)
        sectionEditText = view.findViewById(R.id.sectionEditText)
        titleEditText = view.findViewById(R.id.titleEditText)
        daysEditText = view.findViewById(R.id.daysEditText)
        electronicCheckBox = view.findViewById(R.id.electronicCheckBox)
        coverImageView = view.findViewById(R.id.coverImageView)
        authorTextView = view.findViewById(R.id.authorTextView)
        saveButton = view.findViewById(R.id.saveButton)
        searchButton = view.findViewById(R.id.searchButton)
        clearButton = view.findViewById(R.id.clearButton)
        rec = view.findViewById(R.id.recomendationCheckBox)

        database = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "books.db").build()
        val bookDao = database.bookDao()

        loadBooks()

        saveButton.setOnClickListener { saveBook() }
        searchButton.setOnClickListener { fetchBookInfo() }
        clearButton.setOnClickListener { clearBooks() }

        recyclerView = view.findViewById(R.id.recyclerView)
        booksAdapter = BooksAdapter(booksList, { book -> }, { book -> })
        recyclerView.adapter = booksAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        removeButton.setOnClickListener {
            booksAdapter.removeSelectedBook(bookDao, viewLifecycleOwner.lifecycleScope)
        }
        view.findViewById<ImageView>(R.id.onLog).setOnClickListener {
            val intent = Intent(requireActivity(), Register::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun loadBooks() {
        lifecycleScope.launch {
            val bookDao = database.bookDao()
            val savedBooks = bookDao.getAllBooks()
            booksList.clear()
            booksList.addAll(savedBooks)
            booksAdapter.notifyDataSetChanged()
        }
    }

    private fun saveBook() {
        val title = titleTextView.text.toString()
        val author = authorTextView.text.toString().removePrefix("Director: ")
        val section = sectionEditText.text.toString()
        val maxDays = daysEditText.text.toString().toIntOrNull() ?: 0
        val isElectronic = electronicCheckBox.isChecked
        val rec = rec.isChecked


        if (title.isEmpty() || author.isEmpty() || section.isEmpty() || maxDays.toString().isEmpty()) {
            Toast.makeText(requireContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
        }
        else
        {
            if (maxDays < 0 || maxDays > 30)
            {
                Toast.makeText(requireContext(), "Количество дней некоррктно. Введите значение до 30", Toast.LENGTH_SHORT).show()
            }
            else {
                val book = Books(
                    title = title,
                    author = author,
                    coverImage = "",
                    section = section,
                    maxDays = maxDays,
                    isElectronic = isElectronic,
                    recom = rec
                )

                lifecycleScope.launch {
                    val bookDao = database.bookDao()
                    bookDao.insert(book)


                    booksAdapter.addBook(book)
                    Toast.makeText(
                        requireContext(),
                        "Книга добавлена в библиотеку!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun fetchBookInfo() {
        if (titleEditText.text.toString().isNotEmpty()) {
            val title = titleEditText.text.toString()
            val url = "https://www.omdbapi.com/?apikey=8424b5c9&t=${title.replace(" ", "+")}"

            val queue = Volley.newRequestQueue(requireContext())
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val obj = JSONObject(response)
                    if (obj.getString("Response") == "True") {
                        val movieTitle = obj.getString("Title")
                        val authors = obj.getString("Director") // Using Director as the author
                        val coverUrl = obj.optString("Poster", null)

                        titleTextView.text = movieTitle
                        authorTextView.text = "Director: $authors"

                        if (coverUrl.isNotEmpty()) {
                            Glide.with(requireContext())
                                .load(coverUrl)
                                .into(coverImageView)
                        } else {
                            coverImageView.setImageResource(R.drawable.error)
                        }
                    } else {
                        titleTextView.text = "Книга не найдена"
                        authorTextView.text = ""
                        coverImageView.setImageResource(R.drawable.error)
                    }
                },
                { error ->
                    titleTextView.text = "Что-то не так"
                    authorTextView.text = ""
                    coverImageView.setImageResource(R.drawable.error)
                }
            )

            queue.add(stringRequest)
        } else {
            Toast.makeText(
                requireContext(),
                "Пожалуйста, введите название книги!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun clearBooks() {
        lifecycleScope.launch {
            val bookDao = database.bookDao()
            bookDao.deleteAllBooks()
            booksList.clear()
            booksAdapter.notifyDataSetChanged()
            Toast.makeText(requireContext(), "Все книги удалены!", Toast.LENGTH_SHORT).show()
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddBooksFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddBooksFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }




}