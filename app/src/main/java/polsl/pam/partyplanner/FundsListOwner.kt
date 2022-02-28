package polsl.pam.partyplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import polsl.pam.partyplanner.adapters.FundsListAdapter
import polsl.pam.partyplanner.dto.FundView

class FundsListOwner : AppCompatActivity() {
    lateinit var eventUid: String
    lateinit var database: DatabaseReference
    lateinit var recyclerView: RecyclerView
    lateinit var buttonAddFund: FloatingActionButton
    lateinit var adapterRef: FundsListAdapter
    lateinit var defaultText: TextView
    var fundsList: ArrayList<Pair<String, FundView>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        eventUid = intent.getStringExtra("event_uid").toString()
        setContentView(R.layout.activity_funds_list_owner)
        defaultText = findViewById(R.id.defaultText)
        buttonAddFund = findViewById(R.id.buttonAddFund)
        if (intent.getBooleanExtra("button_flag", false)) {
            buttonAddFund.isEnabled = false
            buttonAddFund.visibility = View.INVISIBLE
        }
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        database = Firebase.database.reference
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        getFundList()

        buttonAddFund.setOnClickListener {


            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.VERTICAL

            val title = EditText(this)
            title.setSingleLine()
            title.hint = "Enter title"
            layout.addView(title)

            val value = EditText(this)
            value.setSingleLine()
            value.hint = "Enter value"
            value.inputType = InputType.TYPE_CLASS_NUMBER
            layout.addView(value)

            layout.setPadding(50, 40, 50, 10)

            MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setTitle("Add new fund:")
                .setView(layout)
                .setPositiveButton("Confirm") { dialog, which ->
                    addNewFund(title, value)
                }
                .setNeutralButton("Cancel") { dialog, which -> }
                .show()
        }
    }

    private fun setupAdapter(data: ArrayList<Pair<String, FundView>>) {
        adapterRef = Adapter(data)
        recyclerView.adapter = adapterRef
    }

    private fun addNewFund(title: EditText, value: EditText) {
        val key = database.child("events").child(eventUid).child("funds").push().key

        database
            .child("events")
            .child(eventUid)
            .child("funds")
            .child(key.toString())
            .setValue(FundView(title.text.toString(), value.text.toString()))
    }

    private fun getFundList() {
        database.child("events").child(eventUid).child("funds")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    fundsList.clear()
                    for (postSnapshot in snapshot.children) {
                        val fund: FundView? = postSnapshot.getValue(FundView::class.java)
                        fundsList.add(Pair(postSnapshot.key.toString(), fund!!))
                    }
                    if (fundsList.size > 0) {
                        defaultText.isVisible = false
                        setupAdapter(fundsList)
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

    inner class Adapter(private val fundsList: ArrayList<Pair<String, FundView>>) :
        FundsListAdapter(fundsList) {
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            super.onBindViewHolder(viewHolder, position)

            viewHolder.itemView.setOnLongClickListener {
                MaterialAlertDialogBuilder(this@FundsListOwner, R.style.AlertDialogTheme)
                    .setTitle("Remove this position?")
                    .setMessage("Are you sure?")
                    .setPositiveButton("YES") { dialog, which ->
                        database
                            .child("events")
                            .child(eventUid)
                            .child("funds")
                            .child(this.fundsList[position].first)
                            .removeValue()

                        fundsList.removeAt(position)
                        notifyItemRemoved(position)
                    }
                    .setNeutralButton("CANCEL") { dialog, which -> }.show()
                true
            }
        }
    }

}