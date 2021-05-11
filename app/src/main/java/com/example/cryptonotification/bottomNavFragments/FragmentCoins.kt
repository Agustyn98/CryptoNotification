package com.example.cryptonotification.bottomNavFragments

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cryptonotification.APIhandler
import com.example.cryptonotification.Models.DisplayCoin
import com.example.cryptonotification.R
import com.example.cryptonotification.db
import kotlin.Exception


class FragmentCoins : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_coins, container, false)
    }

    private var flag = false;

    //You initialize your views here, since it has the view returned from onCreateView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        makeApiCall()
        setBtnAddListener()
        setBtnDeleteAllListener()
        if (flag)
            return
        else
            flag = true

        val handler = Handler()
        val delay: Long = 60000

        handler.postDelayed(object : Runnable {
            override fun run() {
                makeApiCall()
                handler.postDelayed(this, delay)
            }
        }, delay)

    }

    private fun setBtnAddListener() {
        val button: AppCompatImageButton? = view?.findViewById(R.id.coinlist_buttonAdd)
        button?.setOnClickListener(View.OnClickListener {

            val builder = AlertDialog.Builder(view!!.context)
            val inflater = layoutInflater
            builder.setTitle("Add coin")
            val dialogLayout = inflater.inflate(R.layout.alert_dialog_edittext, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.customAlert_editText)
            builder.setView(dialogLayout)
            builder.setPositiveButton("Add") { dialogInterface, i ->

                val db = db(view!!.context)
                val enteredCoin = editText.text.toString()
                if(enteredCoin.isBlank())
                    return@setPositiveButton

                try {
                    db.addOneDisplayCoin(enteredCoin.lowercase())
                    Toast.makeText(view!!.context, "Added $enteredCoin", Toast.LENGTH_LONG).show()
                    makeApiCall()

                } catch (e: SQLiteConstraintException) {
                    Toast.makeText(
                        view!!.context,
                        "$enteredCoin already exists!",
                        Toast.LENGTH_SHORT
                    ).show()

                } catch (e: Exception) {
                    Toast.makeText(view!!.context, "${e.message}", Toast.LENGTH_SHORT).show()

                }

            }
            builder.setNegativeButton("Cancel") { dialogInterface, i -> }
            builder.show()

        })

    }

    fun makeApiCall() {
        if (view == null)
            return
        val api = APIhandler(view!!.context)
        val url = api.getUrl()

        if (url == "") {
            Toast.makeText(view!!.context, "Add a coin!", Toast.LENGTH_SHORT).show()
            return
        }
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(view!!.context)

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                val coinsArray = api.JSONtoCoins(response)
                setRecyclerView(coinsArray)
            },
            { Toast.makeText(view!!.context, "Network Error", Toast.LENGTH_LONG).show() })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    private fun setRecyclerView(array: ArrayList<DisplayCoin>) {
        val recyclerView: RecyclerView? = view?.findViewById(R.id.recyclerViewCoinPrices)
        if (recyclerView != null) {
            recyclerView.adapter = FragmentCoinsAdapter(array)
            recyclerView.layoutManager = LinearLayoutManager(view?.context)
        }
    }

    private fun setBtnDeleteAllListener(){
        val button: Button = view!!.findViewById(R.id.coinlist_buttonDeleteAll)
        button.setOnClickListener(View.OnClickListener {


            val builder = AlertDialog.Builder(view!!.context)
            builder.setTitle("Confirmation")
            builder.setMessage("Delete everything ?")
            builder.setPositiveButton("Yes") { dialog, which ->
                val db = db(view!!.context)
                db.deleteAllDisplayCoin()
                val coinList = ArrayList<DisplayCoin>()
                setRecyclerView(coinList)
            }
            builder.setNegativeButton("Cancel") { dialog, which ->
            }
            builder.show()
        })
    }

}