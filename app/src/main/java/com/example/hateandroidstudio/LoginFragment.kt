package com.example.hateandroidstudio

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var db: AppDatabase

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "user-db").build()

        val roleSpinner: Spinner = view.findViewById(R.id.roleSpinner)
        val roles = arrayOf("Библиотекарь", "Преподаватель", "Студент")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter

        view.findViewById<Button>(R.id.loginButton).setOnClickListener {
            val email = view.findViewById<EditText>(R.id.emailEditText).text.toString()
            val login = view.findViewById<EditText>(R.id.loginEditText).text.toString()
            val password = view.findViewById<EditText>(R.id.passwordEditText).text.toString()
            val role = roleSpinner.selectedItem.toString()


            if (email.isNotEmpty() && login.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {

                    val userCount = db.userDao().getUserCount()
                    if (userCount == 0) {
                        Toast.makeText(requireContext(), "База данных пуста. Пожалуйста, зарегистрируйтесь.", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    val emailPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$")
                    if(!emailPattern.matches(email)){
                        Toast.makeText(requireContext(), "Введите корректный адрес электронной почты", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        val user = db.userDao().getUser(email, login, password, role)
                        if (user != null) {
                            navigateToRoleScreen(user.role)
                        } else {
                            Toast.makeText(requireContext(), "Неверные данные", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }


        view.findViewById<Button>(R.id.registerButton).setOnClickListener {
            val registerFragment = RegisterFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, registerFragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun navigateToRoleScreen(role: String) {
        when (role) {
            "Библиотекарь" -> navigateToLibrarianScreen()
            "Преподаватель" -> navigateToTeacherScreen()
            "Студент" -> navigateToStudentScreen()
            else -> Toast.makeText(requireContext(), "Неизвестная роль", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToLibrarianScreen() {

        val intent = Intent(requireContext(), LibrarianActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToTeacherScreen() {

        val intent = Intent(requireContext(), TeacherActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToStudentScreen() {
        val intent = Intent(requireContext(), StudentActivity::class.java)
        startActivity(intent)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}