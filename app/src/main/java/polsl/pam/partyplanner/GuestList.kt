package polsl.pam.partyplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import polsl.pam.partyplanner.adapters.UserListAdapter
import polsl.pam.partyplanner.dto.UserView

class GuestList : AppCompatActivity() {
    lateinit var eventUid: String
    lateinit var database: DatabaseReference
    lateinit var recyclerView: RecyclerView
    lateinit var adapterRef: UserListAdapter
    lateinit var defaultText: TextView
    var userViewList: ArrayList<Pair<String,UserView>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        eventUid = intent.getStringExtra("event_uid").toString()
        setContentView(R.layout.activity_guest_list)
        defaultText = findViewById(R.id.defaultText)
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        database = Firebase.database.reference
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        getGuestList()
    }

    private fun setupAdapter(data: ArrayList<Pair<String,UserView>>) {
        adapterRef = UserListAdapter(data)
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
}

