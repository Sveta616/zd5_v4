package com.example.hateandroidstudio

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import kotlinx.coroutines.launch
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope

class BooksAdapter(
    private val booksList: MutableList<Books>,
    private val onDelete: (Books) -> Unit,
    private val onItemClick: (Books) -> Unit
) : RecyclerView.Adapter<BooksAdapter.BookViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.bookTitleTextView)
        val authorTextView: TextView = itemView.findViewById(R.id.bookAuthorTextView)
        val r: TextView = itemView.findViewById(R.id.r)
        val day: TextView = itemView.findViewById(R.id.day)
        var checkBox: TextView = itemView.findViewById(R.id.bookCheckBox)
        var recom: TextView = itemView.findViewById(R.id.recom)

        init {
            itemView.setOnClickListener {

                selectedPosition = if (adapterPosition == selectedPosition) {
                    RecyclerView.NO_POSITION
                } else {
                    adapterPosition
                }
                notifyDataSetChanged()
            }
        }

        fun bind(book: Books) {
            titleTextView.text = book.title
            authorTextView.text = book.author
            r.text = book.section
            day.text = book.maxDays.toString()
            checkBox.text = if (book.isElectronic != true) "Физическая" else "Электронная"
            recom.text = book.recom.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(booksList[position])
        holder.itemView.setBackgroundColor(
            if (position == selectedPosition) Color.CYAN else Color.WHITE
        )
    }

    override fun getItemCount(): Int {
        return booksList.size
    }

    fun addBook(book: Books) {
        booksList.add(book)
        notifyItemInserted(booksList.size - 1)
    }

    fun removeSelectedBook(bookDao: BookDao, lifecycleScope: CoroutineScope) {
        if (selectedPosition != RecyclerView.NO_POSITION) {
            val bookToRemove = booksList[selectedPosition]
            booksList.removeAt(selectedPosition)
            notifyItemRemoved(selectedPosition)
            selectedPosition = RecyclerView.NO_POSITION
            onDelete(bookToRemove)

            lifecycleScope.launch {
                bookDao.delete(bookToRemove)
            }
        }
    }
}

