package com.example.cryptonotification.bottomNavFragments

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptonotification.Models.DisplayCoin
import com.example.cryptonotification.R
import com.example.cryptonotification.db

class FragmentCoinsAdapter(private val dataSet: ArrayList<DisplayCoin>) :
    RecyclerView.Adapter<FragmentCoinsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewSymbol: TextView = view.findViewById(R.id.pricerow_symbol)
        val textViewPrice: TextView = view.findViewById(R.id.pricerow_price)
        val textView24h: TextView = view.findViewById(R.id.pricerow_24h)
        val textView7d: TextView = view.findViewById(R.id.pricerow_7d)
        val textView30d: TextView = view.findViewById(R.id.pricerow_30d)

        init {
            // Define click listener for the ViewHolder's View.
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.coins_list_row, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

         fun deleteAlert(){
            val builder = AlertDialog.Builder(viewHolder.textViewSymbol.context)
            builder.setTitle("Delete coin")
            builder.setMessage("Are you sure you want to delete '${viewHolder.textViewSymbol.text}'")
            builder.setPositiveButton("Yes") { dialog, which ->
                val db = db(viewHolder.textViewSymbol.context)
                val result : Int = db.deleteOneDisplayCoin( dataSet[position].name )

                dataSet.removeAt(position)
                notifyItemRemoved(position)

            }
            builder.setNegativeButton("Cancel") { dialog, which ->
            }
            builder.show()
        }


        viewHolder.textViewSymbol.setOnLongClickListener(View.OnLongClickListener {
            deleteAlert()
            true
        })

        viewHolder.textViewPrice.setOnLongClickListener(View.OnLongClickListener {

            deleteAlert()
            true
        })

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textViewSymbol.text = dataSet[position].symbol
        viewHolder.textViewPrice.text = dataSet[position].price.toString()

        val change24h = dataSet[position].change24h
        viewHolder.textView24h.text = change24h.toString()
        if (change24h >= 0)
            viewHolder.textView24h.setTextColor(Color.GREEN)
        else
            viewHolder.textView24h.setTextColor(Color.RED)

        val change7d = dataSet[position].change7d
        viewHolder.textView7d.text = change7d.toString()
        if (change7d >= 0)
            viewHolder.textView7d.setTextColor(Color.GREEN)
        else
            viewHolder.textView7d.setTextColor(Color.RED)

        val change30d = dataSet[position].change30d
        viewHolder.textView30d.text = change30d.toString()
        if (change30d >= 0)
            viewHolder.textView30d.setTextColor(Color.GREEN)
        else
            viewHolder.textView30d.setTextColor(Color.RED)

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size




}
