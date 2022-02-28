package polsl.pam.partyplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import polsl.pam.partyplanner.adapters.UserListAdapter
import polsl.pam.partyplanner.dto.InviteView
import polsl.pam.partyplanner.dto.InviteType
import polsl.pam.partyplanner.dto.UserView

class InviteGuests : AppCompatActivity() {

    lateinit var eventUid: String
    lateinit var username: TextInputEditText
    lateinit var database: DatabaseReference
    lateinit var recyclerView: RecyclerView
    lateinit var buttonFind: Button
    var userViewList: ArrayList<Pair<String, UserView>> = ArrayList()
    var friendsList: ArrayList<UserView> = ArrayList()
    lateinit var adapterRef: UserListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        eventUid = intent.getStringExtra("event_uid").toString()
        setContentView(R.layout.activity_invite_friends)
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        username = findViewById(R.id.username)
        database = Firebase.database.reference
        buttonFind = findViewById(R.id.buttonFind)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        getFriends()

        buttonFind.setOnClickListener {
            getUsersByName(username.text.toString())
        }
    }

    private fun setupAdapter(data: ArrayList<Pair<String, UserView>>) {
        adapterRef = Adapter(data)
        recyclerView.adapter = adapterRef
    }

    private fun getUsersByName(str: String) {
        database.child("users").child(Firebase.auth.currentUser!!.uid).child("friends")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userViewList.clear()
                    if (str.isNotBlank()) {
                        for (postSnapshot in snapshot.children) {
                            val userView: UserView? = postSnapshot.getValue(UserView::class.java)
                            if (str in userView!!.login)
                                userViewList.add(Pair(postSnapshot.key.toString(), userView))
                        }
                    } else {
                        for (postSnapshot in snapshot.children) {
                            val userView: UserView? = postSnapshot.getValue(UserView::class.java)
                            userViewList.add(Pair(postSnapshot.key.toString(), userView!!))
                        }
                    }
                    adapterRef.updateList(userViewList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        baseContext,
                        "Database error: " + databaseError.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun getFriends() {
        database.child("users").child(Firebase.auth.currentUser!!.uid).child("friends")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userViewList.clear()
                    for (postSnapshot in snapshot.children) {
                        val userView: UserView? = postSnapshot.getValue(UserView::class.java)
                        friendsList.add(userView!!)
                        userViewList.add(Pair(postSnapshot.key.toString(), userView) as Pair<String, UserView>)
                    }
                    setupAdapter(userViewList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        baseContext,
                        "Database error: " + databaseError.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun checkIfContainUser(username: String, list: ArrayList<UserView>): Boolean {
        for (u in list) {
            if (u.login == username)
                return true
        }
        return false
    }

    inner class Adapter(userViewList: ArrayList<Pair<String, UserView>>) : UserListAdapter(userViewList) {
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            super.onBindViewHolder(viewHolder, position)
            viewHolder.itemView.setOnClickListener {

                val invite = InviteView(
                    eventUid,
                    InviteType.EVENT.toString()
                )

                database.child("users")
                    .child(userViewList[position].first)
                    .child("invites")
                    .child(eventUid).setValue(invite).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(baseContext, "Invite sent!", Toast.LENGTH_SHORT).show()
                            userViewList.removeAt(position)
                            notifyItemRemoved(position)
                        } else {
                            Toast.makeText(
                                baseContext,
                                "Something went wrong...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }
}