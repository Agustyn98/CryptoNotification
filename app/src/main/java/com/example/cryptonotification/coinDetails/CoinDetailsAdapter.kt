package com.example.cryptonotification.coinDetails

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptonotification.Models.Coin
import com.example.cryptonotification.R
import com.example.cryptonotification.db

class CoinDetailsAdapter(private val dataSet: ArrayList<Coin>) :
    RecyclerView.Adapter<CoinDetailsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: LinearLayout = view.findViewById(R.id.layout_coinDetailsRow)
        val textViewAmount: TextView = view.findViewById(R.id.coinDetailRow_Amount)
        val textViewBought: TextView = view.findViewById(R.id.coinDetailRow_boughtAt)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.coin_details_row, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position % 2 == 0)
            holder.layout.setBackgroundColor(Color.WHITE)

        holder.textViewAmount.text = dataSet[position].owned.toBigDecimal().toPlainString()
        val boughtPrice = dataSet[position].priceBought.toBigDecimal().toPlainString()
        val pairBought = dataSet[position].pairBought
        val boughtTxt = "$boughtPrice $pairBought"
        holder.textViewBought.text = boughtTxt

        holder.layout.setOnLongClickListener(View.OnLongClickListener {

            val builder = AlertDialog.Builder(holder.textViewAmount.context)
            builder.setTitle("Confirmation")
            builder.setMessage("Delete this record ?")
            builder.setPositiveButton("Yes") { dialog, which ->
                val db = db(holder.textViewAmount.context)
                db.deleteOneCoin(dataSet[position].id)
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