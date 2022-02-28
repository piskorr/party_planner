package polsl.pam.partyplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import polsl.pam.partyplanner.dto.PartyEvent
import java.text.SimpleDateFormat
import java.util.*

class EditEvent : AppCompatActivity() {
    lateinit var eventUid: String
    lateinit var database: DatabaseReference
    lateinit var textTitle: TextInputEditText
    lateinit var textDescription: TextInputEditText
    lateinit var textDate: TextInputEditText
    lateinit var textAddress: TextInputEditText
    lateinit var textBankInfo: TextInputEditText
    lateinit var buttonGuestList: Button
    lateinit var buttonAccept: Button
    lateinit var buttonFunds: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        eventUid = intent.getStringExtra("event_uid").toString()
        setContentView(R.layout.activity_edit_event)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        textTitle = findViewById(R.id.textTitle)
        textDescription = findViewById(R.id.textDescription)
        textAddress = findViewById(R.id.textAddress)
        textDate = findViewById(R.id.textDate)
        textBankInfo = findViewById(R.id.textBankInfo)
        buttonGuestList = findViewById(R.id.buttonDecline)
        buttonAccept = findViewById(R.id.buttonAccept)
        buttonFunds = findViewById(R.id.buttonFunds)
        database = Firebase.database.reference
        getEventInfo()
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .build()

        textDate.setOnClickListener {
            datePicker.show(supportFragmentManager, datePicker.toString())
        }

        datePicker.addOnPositiveButtonClickListener {
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            val date = Date(datePicker.selection!!)
            textDate.setText(formatter.format(date), TextView.BufferType.EDITABLE)
        }

        buttonGuestList.setOnClickListener {
            val intent = Intent(baseContext, GuestListOwner::class.java)
            intent.putExtra("event_uid", eventUid)
            startActivity(intent)
        }

        buttonFunds.setOnClickListener {
            val intent = Intent(baseContext, FundsListOwner::class.java)
            intent.putExtra("event_uid", eventUid)
            startActivity(intent)
        }

        buttonAccept.setOnClickListener {
            applyChanges()
        }

    }

    private fun applyChanges() {
        val event = PartyEvent(
            eventUid,
            Firebase.auth.currentUser!!.uid,
            textTitle.text.toString(),
            textDescription.text.toString(),
            textDate.text.toString(),
            textAddress.text.toString(),
            textBankInfo.text.toString()
        )
        database.child("events").child(eventUid).setValue(event)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Changes saved!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(baseContext, "Something went wrong...", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

        private fun getEventInfo() {
            database.child("events").child(eventUid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val event = snapshot.getValue(PartyEvent::class.java)
                        textTitle.setText(event?.title)
                        textDescription.setText(event?.description)
                        textDate.setText(event?.date)
                        textAddress.setText(event?.address)
                        textBankInfo.setText(event?.bankInfo)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            baseContext,
                            "Database error: " + error.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }


    }