package polsl.pam.partyplanner

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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


class Profile : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    lateinit var buttonLogout: MaterialButton
    lateinit var buttonProfile: MaterialButton
    lateinit var textFullName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_profile)

        buttonLogout = findViewById(R.id.buttonLogout)
        buttonProfile = findViewById(R.id.buttonPersonalInformation)
        textFullName = findViewById(R.id.textFullName)
        database = Firebase.database.reference

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue<UserView>()
                textFullName.text = user?.fullName

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(Firebase.auth.currentUser!!.uid)
            .addListenerForSingleValueEvent(userListener)

        buttonLogout.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, Login::class.java))
        }

        buttonProfile.setOnClickListener {
            val intent = Intent(baseContext, UserInfo::class.java)
            intent.putExtra("user_uid", Firebase.auth.currentUser!!.uid)
            startActivity(intent)
        }

    }


}