package com.example.cryptonotification.coinDetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptonotification.Models.Coin
import com.example.cryptonotification.R
import com.example.cryptonotification.bottomNavFragments.FragmentPortfolioAdapter
import com.example.cryptonotification.db

class CoinDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_details)

        setRecyclerView()
        setTitle()
        setBtnEditNameListener()
        setBtnAddListener()

    }

    private fun setTitle(title:String = ""){
        val text: TextView = findViewById(R.id.coinDetails_title)
        if(title.isNullOrBlank()) {
            text.text = getCoinName()
        }else{
            text.text = title
        }
    }

    private fun getCoinName() : String?{
        val intent = intent
        return intent.getStringExtra("coin_name")
    }

    private fun setRecyclerView(){
        val db = db(this)
        val coinName = getCoinName()
        if(coinName.isNullOrBlank())
            return
        val array = db.getAllCoinsByName(coinName)

        val recycler : RecyclerView = findViewById(R.id.recyclerViewCoinDetails)
        recycler.adapter = CoinDetailsAdapter(array)
        recycler.layoutManager = LinearLayoutManager(this)

    }

    private fun setBtnEditNameListener(){
        val btn : Button = findViewById(R.id.coinDetails_buttonEditName)
        btn.setOnClickListener(View.OnClickListener {

            val db = db (this)
            var currentName = getCoinName()
            if(currentName.isNullOrBlank())
                currentName = ""

            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            builder.setTitle("Edit name")
            val dialogLayout = inflater.inflate(R.layout.alert_dialog_editname, null)
            val editTextCoinName =
                dialogLayout.findViewById<EditText>(R.id.customAlert_renameCoin)

            builder.setView(dialogLayout)
            builder.setPositiveButton("Change") { dialogInterface, i ->

                val newName = editTextCoinName.text.toString()

                if (newName.isBlank())
                    return@setPositiveButton

                db.updateOneCoinName(currentName,newName)
                setTitle(newName.uppercase())

            }
            builder.setNegativeButton("Cancel") { dialogInterface, i -> }
            builder.show()

        })
    }

    private fun setBtnAddListener(){

        val btn : ImageButton = findViewById(R.id.coinDetails_buttonAdd)
        btn.setOnClickListener(View.OnClickListener {

            val builder = AlertDialog.Builder(this)
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

            editTextCoinName.setText(getCoinName())
            editTextCoinName.isEnabled = false

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
                val db = db(this)
                db.addOneCoin(enteredCoin)
                setRecyclerView()

            }
            builder.setNegativeButton("Cancel") { dialogInterface, i -> }
            builder.show()

        })

    }
}