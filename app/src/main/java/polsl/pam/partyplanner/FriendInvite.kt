package polsl.pam.partyplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import polsl.pam.partyplanner.dto.UserView

class FriendInvite : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    lateinit var buttonAccept: MaterialButton
    lateinit var buttonDecline: MaterialButton
    lateinit var textLogin: TextView
    lateinit var textFullName: TextView
    lateinit var userUid: String
    lateinit var userView: UserView
    lateinit var currentUserView: UserView

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        userUid = intent.getStringExtra("user_uid").toString()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_friend_invite)
        textLogin = findViewById(R.id.textLogin)
        textFullName = findViewById(R.id.textFullName)
        buttonAccept = findViewById(R.id.buttonAccept)
        buttonDecline = findViewById(R.id.buttonDecline)
        database = Firebase.database.reference

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userView = dataSnapshot.getValue<UserView>()!!
                textFullName.text = userView.fullName
                textLogin.text = userView.login
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    baseContext,
                    "Database error: " + databaseError.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val currentUserListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                currentUserView = dataSnapshot.getValue<UserView>()!!
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    baseContext,
                    "Database error: " + databaseError.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        database.child("users").child(userUid)
            .addListenerForSingleValueEvent(userListener)

        database.child("users").child(Firebase.auth.currentUser!!.uid)
            .addListenerForSingleValueEvent(currentUserListener)

        buttonAccept.setOnClickListener {
            database.child("users")
                .child(Firebase.auth.currentUser!!.uid)
                .child("friends")
                .child(userUid)
                .setValue(userView)

            database.child("users")
                .child(userUid)
                .child("friends")
                .child(Firebase.auth.currentUser!!.uid)
                .setValue(currentUserView)

            database.child("users")
                .child(Firebase.auth.currentUser!!.uid)
                .child("invites")
                .child(userUid)
                .removeValue()

            super.onBackPressed()
        }

        buttonDecline.setOnClickListener {
            database.child("users")
                .child(Firebase.auth.currentUser!!.uid)
                .child("invites")
                .child(userUid)
                .removeValue()

            super.onBackPressed()
        }
    }


}