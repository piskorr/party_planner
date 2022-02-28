package polsl.pam.partyplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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

class FundsList : AppCompatActivity() {
    lateinit var eventUid: String
    lateinit var database: DatabaseReference
    lateinit var recyclerView: RecyclerView
    lateinit var adapterRef: FundsListAdapter
    lateinit var defaultText: TextView
    var fundsList: ArrayList<Pair<String, FundView>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        eventUid = intent.getStringExtra("event_uid").toString()
        setContentView(R.layout.activity_funds_list)
        defaultText = findViewById(R.id.defaultText)

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        database = Firebase.database.reference
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        getFundList()
    }

    private fun setupAdapter(data: ArrayList<Pair<String, FundView>>) {
        adapterRef = FundsListAdapter(data)
        recyclerView.adapter = adapterRef
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
}