package polsl.pam.partyplanner.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import polsl.pam.partyplanner.EventInfo
import polsl.pam.partyplanner.adapters.EventListAdapter
import polsl.pam.partyplanner.R
import polsl.pam.partyplanner.dto.PartyEventView


class PartiesFragment(val database: DatabaseReference) : Fragment() {

    lateinit var recyclerView: RecyclerView
    var eventList: ArrayList<PartyEventView> = ArrayList()
    lateinit var defaultText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_parties, container, false)
        recyclerView = view.findViewById(R.id.recyclerview)
        defaultText = view.findViewById(R.id.defaultText)
        recyclerView.setHasFixedSize(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventList.clear()
                for (postSnapshot in snapshot.children) {
                    val partyEvent: PartyEventView? =
                        postSnapshot.getValue(PartyEventView::class.java)
                    eventList.add(partyEvent!!)
                }
                if (eventList.size > 0) {
                    defaultText.isVisible = false
                    setupAdapter()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    context,
                    "Database error: " + databaseError.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        database.child("users")
            .child(Firebase.auth.currentUser!!.uid)
            .child("events")
            .child("attending")
            .addValueEventListener(eventListener)
    }

    private fun setupAdapter() {
        recyclerView.adapter = Adapter(eventList)
    }

    inner class Adapter(eventList: ArrayList<PartyEventView>) : EventListAdapter(eventList) {
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            super.onBindViewHolder(viewHolder, position)

            viewHolder.itemView.setOnClickListener {
                val clickedEvent: PartyEventView = eventList[position]
                val intent = Intent(context, EventInfo::class.java)
                intent.putExtra("event_uid", clickedEvent.uid)
                requireContext().startActivity(intent)
            }

            viewHolder.itemView.setOnLongClickListener {
                MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                    .setTitle("Cancel confirmation?")
                    .setMessage("Are you sure?")
                    .setPositiveButton("YES") { dialog, which ->
                        database
                            .child("users")
                            .child(Firebase.auth.currentUser!!.uid)
                            .child("events")
                            .child("attending")
                            .child(eventList[position].uid)
                            .removeValue()

                        deleteFromGuestList(eventList[position].uid)
                        removeItem(position)
                    }
                    .setNeutralButton("CANCEL") { dialog, which -> }.show()

                true
            }
        }
    }

    private fun deleteFromGuestList(uid: String) {
        val deleteGuestListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    if (postSnapshot.key == Firebase.auth.currentUser!!.uid)
                        postSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    context,
                    "Database error: " + databaseError.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        database
            .child("events")
            .child(uid)
            .child("guests")
            .addListenerForSingleValueEvent(deleteGuestListener)
    }

}