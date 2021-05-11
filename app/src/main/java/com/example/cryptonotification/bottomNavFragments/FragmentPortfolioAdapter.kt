package com.example.cryptonotification.bottomNavFragments

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptonotification.Models.Coin
import com.example.cryptonotification.Models.NotificationPrice
import com.example.cryptonotification.R
import com.example.cryptonotification.coinDetails.CoinDetails
import com.example.cryptonotification.db
import com.google.android.material.card.MaterialCardView
import java.math.BigDecimal
import java.math.RoundingMode

class FragmentPortfolioAdapter(private val dataSet: ArrayList<Coin>) :
    RecyclerView.Adapter<FragmentPortfolioAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: LinearLayout = view.findViewById(R.id.lineallayout_portfolio_coins)
        val textViewName: TextView = view.findViewById(R.id.portfoliorow_coinName)
        val textViewOwned: TextView = view.findViewById(R.id.portfoliorow_owned)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.coin_portfolio_row, viewGroup, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position % 2 == 0)
            holder.layout.setBackgroundColor(Color.WHITE)

        holder.textViewName.text = dataSet[position].name
        val owned = round(dataSet[position].owned)
        holder.textViewOwned.text = owned.toBigDecimal().toPlainString()

        holder.layout.setOnClickListener(View.OnClickListener {

            val context = holder.textViewName.context
            val intent = Intent(context, CoinDetails::class.java)
            intent.putExtra("coin_name",dataSet[position].name)
            context.startActivity(intent)

        })

        holder.layout.setOnLongClickListener(View.OnLongClickListener {

            val builder = AlertDialog.Builder(holder.textViewName.context)
            builder.setTitle("Confirmation")
            builder.setMessage("Delete all records from '${dataSet[position].name}' ?")
            builder.setPositiveButton("Yes") { dialog, which ->
                val db = db(holder.textViewName.context)
                db.deleteOneCoinByName(dataSet[position].name)
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

    private fun round(value: Double): Double {
        var bd: BigDecimal = BigDecimal.valueOf(value)
        bd = bd.setScale(8, RoundingMode.HALF_UP)
        return bd.toDouble()
    }

}