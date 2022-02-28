package polsl.pam.partyplanner.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import polsl.pam.partyplanner.AddFriend
import polsl.pam.partyplanner.R
import polsl.pam.partyplanner.adapters.UserListAdapter
import polsl.pam.partyplanner.dto.UserView

class FriendsFragment(val database: DatabaseReference) : Fragment() {

    lateinit var friendsList: ArrayList<Pair<String, UserView>>
    lateinit var recyclerView: RecyclerView
    lateinit var buttonAddFriend: MaterialButton
    lateinit var defaultText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_friends, container, false)
        recyclerView = view.findViewById(R.id.recyclerview)
        defaultText = view.findViewById(R.id.defaultText)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        buttonAddFriend = view.findViewById(R.id.buttonAddFriend)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friendsList = ArrayList()
        database.child("users").child(Firebase.auth.currentUser!!.uid).child("friends")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    friendsList.clear()
                    for (postSnapshot in snapshot.children) {
                        val userView: UserView? = postSnapshot.getValue(UserView::class.java)
                        friendsList.add(Pair(postSnapshot.key.toString(), userView!!))
                    }
                    if (friendsList.size > 0) {
                        defaultText.isVisible = false
                        setupAdapter(friendsList)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        context,
                        "Database error: " + databaseError.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        buttonAddFriend.setOnClickListener {
            startActivity(Intent(requireContext(), AddFriend::class.java))
        }
    }

    private fun setupAdapter(data: ArrayList<Pair<String, UserView>>) {
        recyclerView.adapter = Adapter(friendsList)
    }

    inner class Adapter(friendsList: ArrayList<Pair<String, UserView>>) : UserListAdapter(friendsList) {
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            super.onBindViewHolder(viewHolder, position)

            viewHolder.itemView.setOnLongClickListener {
                MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                    .setTitle("Remove friend?")
                    .setMessage("Are you sure")
                    .setPositiveButton("YES") { dialog, which ->
                        database
                            .child("users")
                            .child(Firebase.auth.currentUser!!.uid)
                            .child("friends")
                            .child(friendsList[position].first)
                            .removeValue()

                        database
                            .child("users")
                            .child(friendsList[position].first)
                            .child("friends")
                            .child(Firebase.auth.currentUser!!.uid)
                            .removeValue()

                        friendsList.removeAt(position)
                        notifyItemRemoved(position)
                    }
                    .setNeutralButton("CANCEL") { dialog, which -> }.show()

                true
            }
        }
    }

}