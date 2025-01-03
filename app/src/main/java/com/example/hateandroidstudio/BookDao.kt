package com.example.hateandroidstudio


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BookDao {
    @Update
    suspend fun updateRecommendation(book: Books)

    @Delete
    suspend fun delete(book: Books)

    @Insert
    suspend fun insert(book: Books)

    @Query("SELECT * FROM books")
    suspend fun getAllBooks(): List<Books>

    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBookById(bookId: Int): Books?

    @Query("DELETE FROM books")
    suspend fun deleteAllBooks()

    @Query("UPDATE books SET recom = true WHERE id = :bookId")
    suspend fun updateRecommendation(bookId: Int)
}
