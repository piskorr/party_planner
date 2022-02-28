package polsl.pam.partyplanner

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import polsl.pam.partyplanner.dto.PartyEvent
import polsl.pam.partyplanner.dto.PartyEventView
import polsl.pam.partyplanner.dto.UserView
import java.text.SimpleDateFormat
import java.util.*

class AddEvent : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private var user: UserView = UserView()
    lateinit var buttonAccept: MaterialButton
    lateinit var textTitle: TextInputEditText
    lateinit var textDescription: TextInputEditText
    lateinit var textDate: TextInputEditText
    lateinit var textAddress: TextInputEditText
    lateinit var textBankInfo: TextInputEditText

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

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .build()

        textAddress = findViewById(R.id.textAddress)
        textDate = findViewById(R.id.textDate)
        textBankInfo = findViewById(R.id.textBankInfo)
        buttonAccept = findViewById(R.id.buttonDecline)

        getCurrentUser()

        textDate.setOnClickListener {
            datePicker.show(supportFragmentManager, datePicker.toString())
        }

        datePicker.addOnPositiveButtonClickListener {
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            val date = Date(datePicker.selection!!)
            textDate.setText(formatter.format(date), TextView.BufferType.EDITABLE)
        }

        buttonAccept.setOnClickListener {
            if (validateFields()) {
                addNewEvent()
                startActivity(Intent(this, MainPanel::class.java))
            }
        }

    }

    private fun validateFields(): Boolean {
        if (textTitle.text.toString().isNullOrBlank() ||
            textDescription.text.toString().isNullOrBlank() ||
            textDate.text.toString().isNullOrBlank() ||
            textAddress.text.toString().isNullOrBlank() ||
            textBankInfo.text.toString().isNullOrBlank()
        ) {
            Toast.makeText(this, "Fill all fields!", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun getCurrentUser() {
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue<UserView>()!!
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(Firebase.auth.currentUser!!.uid)
            .addListenerForSingleValueEvent(userListener)
    }

    private fun addNewEvent() {

        val key = database.child("events").push().key

        val event = PartyEvent(
            key!!,
            Firebase.auth.currentUser!!.uid,
            textTitle.text.toString(),
            textDescription.text.toString(),
            textDate.text.toString(),
            textAddress.text.toString(),
            textBankInfo.text.toString()
        )

        val eventView = PartyEventView(textTitle.text.toString(), textDate.text.toString(), key)

        database.child("events")
            .child(key)
            .setValue(event)

        database.child("events")
            .child(key)
            .child("owner_info")
            .setValue(user)

        database.child("users")
            .child(Firebase.auth.currentUser!!.uid)
            .child("events")
            .child("planning")
            .child(key)
            .setValue(eventView)
    }


}