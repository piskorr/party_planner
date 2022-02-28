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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import polsl.pam.partyplanner.*
import polsl.pam.partyplanner.adapters.InvitationsAdapter
import polsl.pam.partyplanner.dto.InviteView
import polsl.pam.partyplanner.dto.InviteType

class InvitesFragment(val database: DatabaseReference) : Fragment() {

    lateinit var inviteViewList: ArrayList<Pair<String, InviteView>>
    lateinit var recyclerView: RecyclerView
    lateinit var adapterRef: InvitationsAdapter
    lateinit var defaultText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_invites, container, false)
        recyclerView = view.findViewById(R.id.recyclerview)
        defaultText = view.findViewById(R.id.defaultText)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inviteViewList = ArrayList()
        database.child("users").child(Firebase.auth.currentUser!!.uid).child("invites")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    inviteViewList.clear()
                    for (postSnapshot in snapshot.children) {
                        val inviteView: InviteView? = postSnapshot.getValue(InviteView::class.java)
                        inviteViewList.add(Pair(postSnapshot.key.toString(), inviteView!!))
                    }
                    if (inviteViewList.size > 0) {
                        defaultText.isVisible = false
                        setupAdapter(inviteViewList)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        context,
                        "Database error: " + databaseError.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

    }

    private fun setupAdapter(data: ArrayList<Pair<String, InviteView>>) {
        adapterRef = Adapter(data)
        recyclerView.adapter = adapterRef
    }


    inner class Adapter(inviteViewList: ArrayList<Pair<String, InviteView>>) :
        InvitationsAdapter(inviteViewList) {
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            super.onBindViewHolder(viewHolder, position)

            viewHolder.itemView.setOnClickListener {
                if (inviteViewList[position].second.itemType == InviteType.EVENT.toString()){
                    val intent = Intent(requireContext(), EventInvite::class.java)
                    intent.putExtra("event_uid", inviteViewList[position].first)
                    startActivity(intent)
                }
                else if(inviteViewList[position].second.itemType == InviteType.FRIEND.toString()){
                    val intent = Intent(requireContext(), FriendInvite::class.java)
                    intent.putExtra("user_uid", inviteViewList[position].first)
                    startActivity(intent)
                }
            }
        }
    }
}