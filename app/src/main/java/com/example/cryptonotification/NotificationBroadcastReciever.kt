package com.example.cryptonotification

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cryptonotification.Models.NotificationPrice


class NotificationBroadcastReciever : BroadcastReceiver() {
    private val channelId = "Price-alerts"
    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent != null) {
            if ("android.intent.action.BOOT_COMPLETED" == intent.action || "android.intent.action.QUICKBOOT_POWERON" == intent.action) {
                resetAlarm(context)
            }
        }
        /*
        val random = Math.random()
        triggerNotification(NotificationPrice(coinName = "$random",priceTarget =  random, alertType =  1),context)
        return;*/

        Log.i("s", "ENTERED ON RECIEVE")

        val db = db(context = context!!)
        val notificationsArray = db.getAllNotifications()

        if (notificationsArray.isEmpty()) {
            Log.i("", "Array empty, no notifications, canceling alarm")
            cancelAlarm(context, intent)
            return
        }

        for (n in notificationsArray) {

            var priceFromApi: Double = -1.0
            val url =
                "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=${n.coinName}"

            val queue = Volley.newRequestQueue(context)
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val api = APIhandler(context)
                    priceFromApi = api.JSONtoPrice(response)
                    println("Coin ${n.coinName} has a current price of: $priceFromApi")
                    if (priceFromApi <= 0)
                        return@StringRequest

                    Log.i("", "Checking price for: ${n.coinName}")
                    if (n.alertType == NotificationPrice.PRICE_RISES_TO) {
                        if (priceFromApi >= n.priceTarget) {
                            triggerNotification(n,context)
                            db.deleteOneNotification(n.id)
                            Log.i("", "Price above target, deleting")
                        }
                    } else if (n.alertType == NotificationPrice.PRICE_DROPS_TO) {
                        if (priceFromApi <= n.priceTarget) {
                            triggerNotification(n,context)
                            db.deleteOneNotification(n.id)
                            Log.i("", "Price below target, deleting")

                        }
                    }
                },
                { Log.e("error", "Network error, cannot access api") })
            queue.add(stringRequest)

        }

    }

    /*
    private fun checkPrice(n: NotificationPrice, priceFromApi:Double) {
        println("Coin ${n.coinName} has a current price of: $priceFromApi")


        Log.i("", "Checking price for: ${n.coinName}")
        if (n.alertType == NotificationPrice.PRICE_RISES_TO) {
            if (priceFromApi >= n.priceTarget) {
                triggerNotification(n)
                db.deleteOneNotification(n.id)
                Log.i("", "Price above target, deleting")
            }
        } else if (n.alertType == NotificationPrice.PRICE_DROPS_TO) {
            if (priceFromApi <= n.priceTarget) {
                triggerNotification(n)
                db.deleteOneNotification(n.id)
                Log.i("", "Price below target, deleting")

            }
        }
    }

     */


    private fun buildNotification(
        context: Context?,
        notificationPrice: NotificationPrice
    ): Notification.Builder {
        val contents : String = if (notificationPrice.alertType == 1){
            "${notificationPrice.coinName.uppercase()} above ${notificationPrice.priceTarget}"
        }else{
            "${notificationPrice.coinName.uppercase()} below ${notificationPrice.priceTarget}"
        }
        //All notifications have a "channel" or category, my app has a single category of notifications called "Price alerts"
        val intentNotif = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                intentNotif,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        //All notifications have a "channel" or category, my app has a single category of notifications called "Price alerts"
        val builder: Notification.Builder = Notification.Builder(context, channelId)
            .setSmallIcon(R.drawable.rounded_button)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context!!.resources,
                    R.drawable.rounded_button,
                )
            )
            .setContentTitle("Price Alert")
            .setContentText(contents)
            .setContentIntent(pendingIntent)

        return builder;
    }

    private fun cancelAlarm(context: Context?, intent: Intent?) {
        val pendingIntent2: PendingIntent =
            PendingIntent.getBroadcast(context, 111, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager: AlarmManager =
            context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent2)
    }

    private fun triggerNotification(notifPrice: NotificationPrice, context: Context?) {
        if (context == null)
            return

        val notificationBuilder: Notification.Builder = buildNotification(context, notifPrice)

        val notifManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifManager.notify(200, notificationBuilder.build())

        //cancelAlarm(context,intent)

        Log.i("s", "CONDITION MET !!")

    }

    private fun resetAlarm(context: Context?){
        println("Calling Alarm...")
        //everytime you call this method, the alarm gets overwritten since its pendingIntent has the same requestCode,
        val intent = Intent(context, NotificationBroadcastReciever::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context,111,intent,PendingIntent.FLAG_UPDATE_CURRENT)

        if(context == null)
            return
        val alarmManager : AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val timenow = System.currentTimeMillis()

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, timenow+1000*30, 1000*60*2,pendingIntent)
    }


}