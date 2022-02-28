package polsl.pam.partyplanner.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import polsl.pam.partyplanner.R
import polsl.pam.partyplanner.UserInfo
import polsl.pam.partyplanner.dto.FundView
import polsl.pam.partyplanner.dto.UserView

open class FundsListAdapter (
    private var fundsList: ArrayList<Pair<String, FundView>>
) :
    RecyclerView.Adapter<FundsListAdapter.ViewHolder>() {
    private lateinit var context: Context

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fundTitleTextView: TextView = itemView.findViewById<TextView>(R.id.fund_title_text)
        val fundValueTextView: TextView = itemView.findViewById<TextView>(R.id.fund_value_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.fund_item, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val fund: FundView = fundsList[position].second
        viewHolder.fundTitleTextView.text = fund.fundTitle
        viewHolder.fundValueTextView.text = fund.fundValue + " zł"
    }

    override fun getItemCount(): Int {
        return fundsList.size
    }

    fun updateList(fundsList: ArrayList<Pair<String, FundView>>) {
        this.fundsList = fundsList
        notifyDataSetChanged()
    }

    fun removeItem(i: Int){
        fundsList.removeAt(i)
        notifyDataSetChanged()
    }


}