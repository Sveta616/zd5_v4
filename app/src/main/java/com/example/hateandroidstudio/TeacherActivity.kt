package com.example.hateandroidstudio

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.launch
class TeacherActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var db: AppDatabase

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher)

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "user-db").build()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadStudents()

        findViewById<Button>(R.id.registerButton).setOnClickListener {
            Save()
        }
        this.findViewById<ImageView>(R.id.onLog).setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.lib).setOnClickListener {
            val intent = Intent(this, TeacherBook::class.java)
            startActivity(intent)
        }
    }

    private fun Save() {
        lifecycleScope.launch {
            val studentCount = db.userDao().getStudentCount()
            if (studentCount >= 25) {
                Toast.makeText(this@TeacherActivity, "Достигнуто максимальное количество студентов (25)", Toast.LENGTH_SHORT).show()
                return@launch
            }


            val lastName = findViewById<EditText>(R.id.lastNameEditText).text.toString()
            val firstName = findViewById<EditText>(R.id.firstNameEditText).text.toString()
            val email = findViewById<EditText>(R.id.emailEditText).text.toString()
            val login = findViewById<EditText>(R.id.loginEditText).text.toString()
            val password = findViewById<EditText>(R.id.passwordEditText).text.toString()

            if (lastName.isEmpty() || firstName.isEmpty() || email.isEmpty() || login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@TeacherActivity, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            } else {
                val emailPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$")
                if (!emailPattern.matches(email)) {
                    Toast.makeText(this@TeacherActivity, "Введите корректный адрес электронной почты", Toast.LENGTH_SHORT).show()
                } else if (password.length < 8) {
                    Toast.makeText(this@TeacherActivity, "Пароль должен содержать не менее 8 символов", Toast.LENGTH_SHORT).show()
                } else {
                    val user = User(
                        lastName = lastName,
                        firstName = firstName,
                        email = email,
                        login = login,
                        password = password,
                        role = "Студент"
                    )

                    db.userDao().insert(user)
                    Toast.makeText(this@TeacherActivity, "Регистрация успешна", Toast.LENGTH_SHORT).show()
                    loadStudents()
                }
            }
        }
    }

    private fun loadStudents() {
        lifecycleScope.launch {
            val students = db.userDao().getStudents()
            userAdapter = UserAdapter(students) { student ->
                deleteStudent(student)
            }
            recyclerView.adapter = userAdapter
        }
    }

    private fun deleteStudent(student: User) {
        lifecycleScope.launch {
            db.userDao().delete(student)
            userAdapter.removeStudent(student)
            Toast.makeText(this@TeacherActivity, "Студент удалён", Toast.LENGTH_SHORT).show()
        }
    }

}

