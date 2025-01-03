package com.example.hateandroidstudio

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [User::class, Books::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun bookDao(): BookDao
}
