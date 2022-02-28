package polsl.pam.partyplanner.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import polsl.pam.partyplanner.GuestList
import polsl.pam.partyplanner.MainPanel
import polsl.pam.partyplanner.R
import polsl.pam.partyplanner.UserInfo
import polsl.pam.partyplanner.dto.UserView

open class UserListAdapter(
    private var friendsList: ArrayList<Pair<String, UserView>>
) :
    RecyclerView.Adapter<UserListAdapter.ViewHolder>() {
    private lateinit var context: Context

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val friendTextView: TextView = itemView.findViewById<TextView>(R.id.friend_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.friend_item, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val friend: UserView = friendsList[position].second
        viewHolder.friendTextView.text = friend.login

        viewHolder.itemView.setOnClickListener {
            val intent = Intent(context, UserInfo::class.java)
            intent.putExtra("user_uid", friendsList[position].first)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return friendsList.size
    }

    fun updateList(friendsList: ArrayList<Pair<String, UserView>>) {
        this.friendsList = friendsList
        notifyDataSetChanged()
    }

    fun removeItem(i: Int){
        friendsList.removeAt(i)
        notifyDataSetChanged()
    }


}