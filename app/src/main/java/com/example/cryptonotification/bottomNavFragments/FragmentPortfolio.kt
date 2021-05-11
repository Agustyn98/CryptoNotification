package com.example.cryptonotification.bottomNavFragments

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptonotification.Models.Coin
import com.example.cryptonotification.Models.DisplayCoin
import com.example.cryptonotification.R
import com.example.cryptonotification.db


class FragmentPortfolio : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_portfolio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBtnAddListener()
        setBtnDeleteAllListener()

    }

    override fun onResume() {
        super.onResume()
        setRecyclerView()

    }

    private fun setRecyclerView() {
        if (view == null)
            return
        val db = db(view!!.context)
        val array = db.getAllCoinsGroupedByName()
        val recyclerView: RecyclerView? = view?.findViewById(R.id.recyclerViewPortfolio)
        if (recyclerView != null) {
            recyclerView.adapter = FragmentPortfolioAdapter(array)
            recyclerView.layoutManager = LinearLayoutManager(view!!.context)
        }
    }

    private fun setBtnAddListener() {
        val btn: ImageButton = view!!.findViewById(R.id.portfolio_buttonAdd)
        btn.setOnClickListener(View.OnClickListener {

            val builder = AlertDialog.Builder(view!!.context)
            val inflater = layoutInflater
            builder.setTitle("Add coin")
            val dialogLayout = inflater.inflate(R.layout.alert_dialog_portfolio, null)
            val editTextCoinName =
                dialogLayout.findViewById<EditText>(R.id.customAlertPortfolio_editTextCoinName)
            val editTextAmount =
                dialogLayout.findViewById<EditText>(R.id.customAlertPortfolio_editTextAmount)
            val editTextPrice =
                dialogLayout.findViewById<EditText>(R.id.customAlertPortfolio_editTextPriceBought)
            val editTextPair =
                dialogLayout.findViewById<EditText>(R.id.customAlertPortfolio_editTextPair)

            builder.setView(dialogLayout)
            builder.setPositiveButton("Add") { dialogInterface, i ->

                val name = editTextCoinName.text.toString()
                val amount = editTextAmount.text.toString()
                val price = editTextPrice.text.toString()
                val pair = editTextPair.text.toString()
                if (name.isBlank() || amount.isBlank() || price.isBlank() || pair.isBlank())
                    return@setPositiveButton
                val enteredCoin = Coin(
                    name = name,
                    owned = amount.toDouble(),
                    priceBought = price.toDouble(),
                    pairBought = pair
                )
                val db = db(view!!.context)
                db.addOneCoin(enteredCoin)
                setRecyclerView()

            }
            builder.setNegativeButton("Cancel") { dialogInterface, i -> }
            builder.show()

        })
    }

    private fun setBtnDeleteAllListener() {
        val btn: Button = view!!.findViewById(R.id.portfolio_buttonDeleteAll)
        btn.setOnClickListener(View.OnClickListener {

            val builder = AlertDialog.Builder(view!!.context)
            builder.setTitle("Confirmation")
            builder.setMessage("Delete everything ?")
            builder.setPositiveButton("Yes") { dialog, which ->
                val db = db(view!!.context)
                db.deleteAllCoin()
                setRecyclerView()

            }
            builder.setNegativeButton("Cancel") { dialog, which ->
            }
            builder.show()
        })
    }
}