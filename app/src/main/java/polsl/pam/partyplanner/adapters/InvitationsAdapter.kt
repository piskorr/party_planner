package polsl.pam.partyplanner.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import polsl.pam.partyplanner.R
import polsl.pam.partyplanner.dto.InviteView

open class InvitationsAdapter(
    private var inviteViewList: ArrayList<Pair<String, InviteView>>
) :
    RecyclerView.Adapter<InvitationsAdapter.ViewHolder>() {
    private lateinit var context: Context

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val inviteTextView: TextView = itemView.findViewById<TextView>(R.id.invite_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.invite_item, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val inviteView: InviteView = inviteViewList[position].second
        viewHolder.inviteTextView.text = "NEW " + inviteView.itemType.toString() + " INVITE!"
    }

    override fun getItemCount(): Int {
        return inviteViewList.size
    }

    fun updateList(inviteViewList: ArrayList<Pair<String, InviteView>>) {
        this.inviteViewList = inviteViewList
        this.notifyDataSetChanged()
    }
}