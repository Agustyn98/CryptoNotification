package com.example.cryptonotification.bottomNavFragments

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptonotification.Models.NotificationPrice
import com.example.cryptonotification.R
import com.example.cryptonotification.db
import com.google.android.material.card.MaterialCardView

class FragmentNotificationAdapter(private val dataSet: ArrayList<NotificationPrice>) :
    RecyclerView.Adapter<FragmentNotificationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.notificationRow_coinName)
        val textViewPriceTarget: TextView = view.findViewById(R.id.notificationRow_priceTarget)
        val textViewAlertType: TextView = view.findViewById(R.id.notificationRow_alertType)
        val cardView : MaterialCardView = view.findViewById(R.id.notification_cardView)

        init {
            // Define click listener for the ViewHolder's View.
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.notifications_list_row, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(position % 2 != 0){
            holder.cardView.setBackgroundColor(Color.GRAY)
        }

        holder.textViewName.text = dataSet[position].coinName
        holder.textViewPriceTarget.text = dataSet[position].priceTarget.toString()
        holder.textViewAlertType.text = if (dataSet[position].alertType == 1) "above" else "below"
        holder.textViewAlertType.setTextColor(if (dataSet[position].alertType == 1) Color.GREEN else Color.RED)


        holder.cardView.setOnLongClickListener(View.OnLongClickListener {

            val builder = AlertDialog.Builder(holder.cardView.context)
            builder.setTitle("Delete notification")
            builder.setMessage("Delete '${dataSet[position].coinName} ${holder.textViewAlertType.text} ${dataSet[position].priceTarget}' ?")
            builder.setPositiveButton("Yes") { dialog, which ->
                val db = db(holder.cardView.context)
                val result : Int = db.deleteOneNotification(dataSet[position].id)

                dataSet.removeAt(position)
                notifyItemRemoved(position)

            }
            builder.setNegativeButton("Cancel") { dialog, which ->
            }
            builder.show()
            true
        })
    }

    override fun getItemCount() = dataSet.size


}