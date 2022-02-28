package polsl.pam.partyplanner

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import polsl.pam.partyplanner.dto.PartyEvent
import android.content.Intent
import android.text.InputType
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.setPadding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import polsl.pam.partyplanner.dto.UserView


class EventInfo : AppCompatActivity() {
    lateinit var eventUid: String
    lateinit var database: DatabaseReference
    lateinit var textTitle: TextInputEditText
    lateinit var textOrganizer: TextInputEditText
    lateinit var textDescription: TextInputEditText
    lateinit var textDate: TextInputEditText
    lateinit var textAddress: TextInputEditText
    lateinit var buttonGuestList: Button
    lateinit var buttonBankInfo: Button
    lateinit var buttonFundsList: Button
    var textBankNumber: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        eventUid = intent.getStringExtra("event_uid").toString()
        setContentView(R.layout.activity_event_info)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        buttonGuestList = findViewById(R.id.buttonDecline)
        buttonBankInfo = findViewById(R.id.buttonBankInfo)
        buttonFundsList = findViewById(R.id.buttonFundsList)
        textTitle = findViewById(R.id.textTitle)
        textDescription = findViewById(R.id.textDescription)
        textAddress = findViewById(R.id.textAddress)
        textDate = findViewById(R.id.textDate)
        textOrganizer = findViewById(R.id.textOrganizer)
        disableField(textTitle)
        disableField(textDescription)
        disableField(textAddress)
        disableField(textDate)
        disableField(textOrganizer)

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
                    textBankNumber = event?.bankInfo.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        baseContext,
                        "Database error: " + error.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        buttonGuestList.setOnClickListener {
            val intent = Intent(baseContext, GuestList::class.java)
            intent.putExtra("event_uid", eventUid)
            startActivity(intent)
        }

        buttonBankInfo.setOnClickListener {
            MaterialAlertDialogBuilder(this ,R.style.AlertDialogTheme)
                .setTitle("Bank Account Number: ")
                .setMessage(textBankNumber)
                .setPositiveButton("Copy") { dialog, which ->
                    copyTextToClipboard()
                }
                .show()
        }

        buttonFundsList.setOnClickListener{
            val intent = Intent(baseContext, FundsList::class.java)
            intent.putExtra("event_uid", eventUid)
            startActivity(intent)
        }
    }

    private fun disableField(text: TextInputEditText) {
        text.isFocusable = false
        text.isFocusableInTouchMode = false
        //text.inputType = InputType.TYPE_NULL
    }

    private fun copyTextToClipboard() {
        val textToCopy = textBankNumber

        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(this, "Number copied to clipboard", Toast.LENGTH_LONG).show()
    }

}