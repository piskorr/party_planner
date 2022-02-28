package polsl.pam.partyplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import polsl.pam.partyplanner.dto.UserView

class UserInfo : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    lateinit var buttonBack: MaterialButton
    lateinit var textLogin: TextView
    lateinit var textFullName: TextView
    lateinit var userUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        userUid = intent.getStringExtra("user_uid").toString()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_user_info)
        textLogin = findViewById(R.id.textLogin)
        textFullName = findViewById(R.id.textFullName)
        buttonBack = findViewById(R.id.buttonBack)
        database = Firebase.database.reference

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userView: UserView = dataSnapshot.getValue<UserView>()!!
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

        database.child("users").child(userUid)
            .addListenerForSingleValueEvent(userListener)

        buttonBack.setOnClickListener {
            super.onBackPressed()
        }
    }
}