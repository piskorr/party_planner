package polsl.pam.partyplanner

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import polsl.pam.partyplanner.adapters.UserListAdapter
import polsl.pam.partyplanner.dto.UserView

class GuestListOwner : AppCompatActivity() {
    lateinit var eventUid: String
    lateinit var database: DatabaseReference
    lateinit var recyclerView: RecyclerView
    lateinit var buttonAddGuest: MaterialButton
    lateinit var adapterRef: UserListAdapter
    lateinit var defaultText: TextView
    var userViewList: ArrayList<Pair<String,UserView>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        eventUid = intent.getStringExtra("event_uid").toString()
        setContentView(R.layout.activity_guest_list_owner)
        defaultText = findViewById(R.id.defaultText)
        buttonAddGuest = findViewById(R.id.buttonAddGuest)
        if (intent.getBooleanExtra("button_flag", false)) {
            buttonAddGuest.isEnabled = false
            buttonAddGuest.visibility = View.INVISIBLE
        }
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        database = Firebase.database.reference
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        getGuestList()

        buttonAddGuest.setOnClickListener {
            val intent = Intent(baseContext, InviteGuests::class.java)
            intent.putExtra("event_uid", eventUid)
            startActivity(intent)
        }
    }

    private fun setupAdapter(data: ArrayList<Pair<String,UserView>>) {
        adapterRef = Adapter(data)
        recyclerView.adapter = adapterRef
    }

    private fun getGuestList() {
        database.child("events").child(eventUid).child("guests")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userViewList.clear()
                    for (postSnapshot in snapshot.children) {
                        val userView: UserView? = postSnapshot.getValue(UserView::class.java)
                        userViewList.add(Pair(postSnapshot.key.toString(), userView!!))
                    }
                    if (userViewList.size > 0) {
                        defaultText.isVisible = false
                        setupAdapter(userViewList)
                    }
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

    inner class Adapter(private val friendsList: ArrayList<Pair<String,UserView>>) : UserListAdapter(friendsList) {
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            super.onBindViewHolder(viewHolder, position)

            viewHolder.itemView.setOnLongClickListener {
                MaterialAlertDialogBuilder(this@GuestListOwner, R.style.AlertDialogTheme)
                    .setTitle("Remove person from event?")
                    .setMessage("Are you sure?")
                    .setPositiveButton("YES") { dialog, which ->
                        database
                            .child("events")
                            .child(eventUid)
                            .child("guests")
                            .child(this.friendsList[position].first)
                            .removeValue()

                        database
                            .child("users")
                            .child(friendsList[position].first)
                            .child("events")
                            .child("attending")
                            .child(eventUid)
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