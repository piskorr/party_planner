package polsl.pam.partyplanner

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import polsl.pam.partyplanner.dto.UserView
import android.widget.Button

import com.google.firebase.auth.ktx.auth
import polsl.pam.partyplanner.adapters.UserListAdapter
import polsl.pam.partyplanner.dto.InviteView
import polsl.pam.partyplanner.dto.InviteType

class AddFriend : AppCompatActivity() {

    lateinit var userSnapshot: DataSnapshot
    lateinit var username: TextInputEditText
    lateinit var database: DatabaseReference
    lateinit var recyclerView: RecyclerView
    lateinit var buttonFind: Button
    lateinit var adapterRef: UserListAdapter
    var userViewList: ArrayList<Pair<String, UserView>> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_add_friend)
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

//        val currentUserListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                userSnapshot = dataSnapshot
//            }
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
//            }
//        }
//        database.child("users").child(Firebase.auth.currentUser!!.uid)
//            .addListenerForSingleValueEvent(currentUserListener)

        buttonFind.setOnClickListener {
            getUsersByName(username.text.toString())
        }
    }

    private fun setupAdapter(data: ArrayList<Pair<String, UserView>>) {
        adapterRef = Adapter(data)
        recyclerView.adapter = adapterRef
    }

    private fun getUsersByName(str: String) {
        database.child("users")
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userViewList.clear()
                    for (postSnapshot in snapshot.children) {
                        val userView: UserView? = postSnapshot.getValue(UserView::class.java)
                        if (str in userView!!.login) {
                            if(!checkIfAlreadyInvited(postSnapshot) &&
                                !checkIfCurrentUser(postSnapshot.key.toString()) &&
                                !checkIfAlreadyInFriends(postSnapshot))
                            userViewList.add(Pair(postSnapshot.key.toString(), userView))
                        }
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

    private fun checkIfAlreadyInFriends(postSnapshot: DataSnapshot?): Boolean {
        for(u in postSnapshot!!.child("friends").children)
            if(u.key == Firebase.auth.currentUser!!.uid)
                return true

        return false
    }

    private fun checkIfCurrentUser(uid: String): Boolean {
        if(uid == Firebase.auth.currentUser!!.uid)
            return true

        return false
    }

    private fun checkIfAlreadyInvited(postSnapshot: DataSnapshot?): Boolean {
        for(u in postSnapshot!!.child("invites").children)
            if(u.key == Firebase.auth.currentUser!!.uid)
                return true

        return false
    }

    inner class Adapter(private val list: ArrayList<Pair<String, UserView>>) : UserListAdapter(list) {
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            super.onBindViewHolder(viewHolder, position)
            viewHolder.itemView.setOnClickListener {

                val invite = InviteView(
                    Firebase.auth.currentUser!!.uid,
                    InviteType.FRIEND.toString()
                )

                database.child("users")
                    .child(list[position].first)
                    .child("invites")
                    .child(Firebase.auth.currentUser!!.uid).setValue(invite).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(baseContext, "Invite sent!", Toast.LENGTH_SHORT).show()
                            removeItem(position)
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