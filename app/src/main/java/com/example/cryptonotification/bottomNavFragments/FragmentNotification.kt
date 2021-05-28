package com.example.cryptonotification.bottomNavFragments

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptonotification.Models.NotificationPrice
import com.example.cryptonotification.NotificationBroadcastReciever
import com.example.cryptonotification.R
import com.example.cryptonotification.db


class FragmentNotification : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        setButtonAddListener()
        setBtnDeleteAllListener()
        createNotificationChannel()


    }

    private fun setNotification(){
        if(view == null || activity == null)
            return;
        Log.i("","Calling Alarm...")
        //everytime you call this method, the alarm gets overwritten since its pendingIntent has the same requestCode,
        val intent = Intent(view!!.context, NotificationBroadcastReciever::class.java)
        val pendingIntent = PendingIntent.getBroadcast(view!!.context,111,intent,PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager : AlarmManager = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val timeNow = System.currentTimeMillis()
        val startTime = timeNow + 1000 * 3
        val intervalTime : Long = 1000 * 60 * 4
        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime, intervalTime,pendingIntent)
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeNow+1000*60*4, pendingIntent)

    }


    private fun createNotificationChannel(){
        val channelId = "Price-alerts"
        val notificationChannel : NotificationChannel =
            NotificationChannel(channelId, "Price alerts", NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.enableVibration(true)
        notificationChannel.setShowBadge(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        val notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

    }

    private fun setButtonAddListener(){

        val button : ImageButton = view!!.findViewById(R.id.notification_buttonAdd)
        button.setOnClickListener(View.OnClickListener {

            val builder = AlertDialog.Builder(view!!.context)
            val inflater = layoutInflater
            builder.setTitle("Add notification")
            val dialogLayout = inflater.inflate(R.layout.alert_dialog_add_notification, null)
            val editTextName = dialogLayout.findViewById<EditText>(R.id.customAlertNotif_editTextCoinName)
            val editTextPrice = dialogLayout.findViewById<EditText>(R.id.customAlertNotif_editTextPriceTarget)
            val radioUpTo = dialogLayout.findViewById<RadioButton>(R.id.customAlertNotif_radioButtonRisesTo)

            builder.setView(dialogLayout)
            builder.setPositiveButton("Add"){dialogInterface,i->

                if(editTextName.text.toString().isBlank() || editTextPrice.text.toString().isBlank())
                    return@setPositiveButton

                val coinName = editTextName.text.toString()
                val priceTarget = editTextPrice.text.toString().toDouble()

                val alertType = if(radioUpTo.isChecked) 1 else 2
                if(priceTarget<0)
                    return@setPositiveButton
                val db = db(view!!.context)
                val notif = NotificationPrice(coinName = coinName,priceTarget = priceTarget, alertType = alertType)
                db.addOneNotification(notif)

                Toast.makeText(view!!.context, "Notification added", Toast.LENGTH_LONG).show()
                setRecyclerView()
                setNotification()


            }
            builder.setNegativeButton("Cancel"){ dialogInterface, i -> }
            builder.show()

        })

    }

    private fun setBtnDeleteAllListener(){
        val button : Button = view!!.findViewById(R.id.notification_buttonDeleteAll)
        button.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(view!!.context)
            val inflater = layoutInflater
            builder.setTitle("Confirmation")
            builder.setMessage("Are you sure?")
            builder.setPositiveButton("Yes"){ dialogInterface: DialogInterface, i: Int ->
                val db = db(view!!.context)
                db.deleteAllNotification()
                setRecyclerView()
                Toast.makeText(view!!.context, "Deleted", Toast.LENGTH_LONG).show()

            }
            builder.setNegativeButton("Cancel"){ dialogInterface, i -> }
            builder.show()


        })
    }

    private fun setRecyclerView() {
        if(view == null)
             return
        val db = db(view!!.context)
        val array = db.getAllNotifications()
        val recyclerView: RecyclerView? = view?.findViewById(R.id.notifications_RecyclerView)
        if (recyclerView != null) {
            recyclerView.adapter = FragmentNotificationAdapter(array)
            recyclerView.layoutManager = LinearLayoutManager(view?.context)
        }
    }

}