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

class UserAdapter(private var studentList: List<User>, private val onDeleteClick: (User) -> Unit) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {


    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val firstName: TextView = view.findViewById(R.id.fam)
        val lastName: TextView = view.findViewById(R.id.name)
        val email: TextView = view.findViewById(R.id.mail)
        fun bind(student: User) {
            lastName.text = student.lastName
            firstName.text = student.firstName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val student = studentList[position]
        holder.bind(student)

        holder.itemView.setOnClickListener {

            onDeleteClick(student)
        }
    }
    fun removeStudent(student: User) {
        val updatedList = studentList.toMutableList()
        updatedList.remove(student)
        studentList = updatedList
        notifyDataSetChanged()
    }

    override fun getItemCount() = studentList.size
}

