package com.example.hateandroidstudio


import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM User WHERE email = :email AND login = :login AND password = :password AND role = :role LIMIT 1")
    suspend fun getUser(email: String, login: String, password: String, role: String): User?

    @Query("SELECT COUNT(*) FROM User")
    suspend fun getUserCount(): Int
    @Query("SELECT * FROM User WHERE role = 'Студент'")
    suspend fun getStudents(): List<User>

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM user WHERE login = :login OR email = :email LIMIT 1")
    suspend fun findByLoginOrEmail(login: String, email: String): User?
    @Query("SELECT COUNT(*) FROM User WHERE role = :role")
    suspend fun countUsersByRole(role: String): Int
    @Query("SELECT COUNT(*) FROM User WHERE role = 'Студент'")
    suspend fun getStudentCount(): Int

}
