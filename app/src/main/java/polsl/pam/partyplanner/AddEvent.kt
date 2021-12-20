package polsl.pam.partyplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import polsl.pam.partyplanner.dto.Event

class AddEvent : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    lateinit var buttonAccept : MaterialButton
    lateinit var textTitle : TextInputEditText
    lateinit var textDescription : TextInputEditText
    lateinit var texTheme : TextInputEditText
    lateinit var textAddress : TextInputEditText


    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        database = Firebase.database.reference
        textTitle = findViewById(R.id.textTitle)
        textDescription = findViewById(R.id.textDescription)
        texTheme = findViewById(R.id.textTheme)
        textAddress = findViewById(R.id.textAddress)
        buttonAccept = findViewById(R.id.buttonAccept)

        buttonAccept.setOnClickListener {
            addNewEvent()
            startActivity(Intent(this, MainPanel::class.java))
        }

    }

    private fun addNewEvent() {
        val key = database.child("events").push().key

        val event = Event(
            key!!,
            Firebase.auth.currentUser!!.uid,
            textTitle.text.toString(),
            textDescription.text.toString(),
            texTheme.text.toString(),
            textAddress.text.toString()
        )

        database.child("events").child(key).setValue(event)
    }



}