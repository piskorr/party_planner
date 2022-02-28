package polsl.pam.partyplanner.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import polsl.pam.partyplanner.EventInfo
import polsl.pam.partyplanner.R
import polsl.pam.partyplanner.dto.PartyEvent
import polsl.pam.partyplanner.dto.PartyEventView


open class EventListAdapter(private val eventList: ArrayList<PartyEventView>) :
    RecyclerView.Adapter<EventListAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.event_title)
        val dateTextView: TextView = itemView.findViewById(R.id.event_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)

        val contactView = inflater.inflate(R.layout.event_card_layout, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val event: PartyEventView = eventList[position]
        viewHolder.titleTextView.text = event.title
        viewHolder.dateTextView.text = event.date
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

     fun removeItem(i: Int){
         eventList.removeAt(i)
         notifyDataSetChanged()
    }
}

