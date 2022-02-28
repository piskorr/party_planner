package polsl.pam.partyplanner

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import polsl.pam.partyplanner.dto.PartyEvent
import polsl.pam.partyplanner.dto.PartyEventView
import polsl.pam.partyplanner.dto.UserView

class EventInvite : AppCompatActivity() {
    lateinit var eventUid: String
    lateinit var database: DatabaseReference
    lateinit var textTitle: TextInputEditText
    lateinit var textDescription: TextInputEditText
    lateinit var textDate: TextInputEditText
    lateinit var textAddress: TextInputEditText
    lateinit var textOrganizer: TextInputEditText
    lateinit var buttonAccept: Button
    lateinit var buttonDecline: Button
    lateinit var userView: UserView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        eventUid = intent.getStringExtra("event_uid").toString()
        setContentView(R.layout.activity_event_invite)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        buttonAccept = findViewById(R.id.buttonAccept)
        buttonDecline = findViewById(R.id.buttonDecline)
        textTitle = findViewById(R.id.textTitle)
        textDescription = findViewById(R.id.textDescription)
        textAddress = findViewById(R.id.textAddress)
        textOrganizer = findViewById(R.id.textOrganizer)
        textDate = findViewById(R.id.textDate)
        database = Firebase.database.reference
        disableField(textTitle)
        disableField(textDescription)
        disableField(textAddress)
        disableField(textDate)
        disableField(textOrganizer)

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userView = dataSnapshot.getValue<UserView>()!!
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(Firebase.auth.currentUser!!.uid)
            .addListenerForSingleValueEvent(userListener)

        database = Firebase.database.reference
        database.child("events").child(eventUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val event = snapshot.getValue(PartyEvent::class.java)
                    val host = snapshot.child("owner_info").getValue(UserView::class.java)
                    textTitle.setText(event?.title)
                    textDescription.setText(event?.description)
                    textDate.setText(event?.date)
                    textAddress.setText(event?.address)
                    textOrganizer.setText(host?.login)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        baseContext,
                        "Database error: " + error.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        buttonAccept.setOnClickListener {
            database.child("events")
                .child(eventUid)
                .child("guests")
                .child(Firebase.auth.currentUser!!.uid)
                .setValue(userView)

            database.child("users")
                .child(Firebase.auth.currentUser!!.uid)
                .child("events")
                .child("attending")
                .child(eventUid)
                .setValue(
                    PartyEventView(
                        textTitle.text.toString(),
                        textDate.text.toString(),
                        eventUid
                    )
                )

            database.child("users")
                .child(Firebase.auth.currentUser!!.uid)
                .child("invites")
                .child(eventUid)
                .removeValue()

            super.onBackPressed()
        }

        buttonDecline.setOnClickListener {
            database.child("users")
                .child(Firebase.auth.currentUser!!.uid)
                .child("invites")
                .child(eventUid)
                .removeValue()

            super.onBackPressed()
        }
    }

    private fun disableField(text: TextInputEditText) {
        text.isFocusable = false
        text.isFocusableInTouchMode = false
        text.inputType = InputType.TYPE_NULL
    }
}