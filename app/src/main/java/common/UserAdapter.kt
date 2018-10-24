package common

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.java.note.mvpkotlin.R
import model.User
import java.util.ArrayList

class UserAdapter: RecyclerView.Adapter<UserAdapter.UserHolder>() {

    val data: MutableList<User> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        holder.bind(data[position])
    }

    fun setData(users: List<User>) {
        data.clear()
        data.addAll(users)
        notifyDataSetChanged()
        Log.d("TAG", "size  = $itemCount")
    }

    class UserHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var userInfo: TextView = itemView.findViewById(R.id.text)

        fun bind(user: User) {
            userInfo.text = String.format("id: ${user.id}, name: ${user.name}, email: ${user.email}")
        }
    }
}