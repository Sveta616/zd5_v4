package com.example.hateandroidstudio

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Books(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val author: String,
    val coverImage: String,
    val section: String,
    val maxDays: Int,
    val isElectronic: Boolean,
    val recom: Boolean
)