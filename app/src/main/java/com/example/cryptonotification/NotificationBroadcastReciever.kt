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
import com.android.volley.DefaultRetryPolicy
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

        Log.d("NOTIFICATION", "ENTERED ON RECIEVE !")

        val db = db(context = context!!)
        val notificationsArray = db.getAllNotifications()

        if (notificationsArray.isEmpty()) {
            Log.i("NOTIFICATION", "Array empty, no notifications, canceling alarm")
            cancelAlarm(context, intent)
            return
        }

        for (n in notificationsArray) {

            Log.d("NOTIFICATION", "checking price for ${n.coinName} ")
            var priceFromApi: Double = -1.0
            val url =
                "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=${n.coinName}"

            val queue = Volley.newRequestQueue(context)
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val api = APIhandler(context)
                    priceFromApi = api.JSONtoPrice(response)
                    Log.d("NOTIFICATION","Coin ${n.coinName} has a current price of: $priceFromApi")
                    if (priceFromApi <= 0)
                        return@StringRequest

                    Log.d("NOTIFICATION", "Comparing ${n.coinName} with saved price of ${n.priceTarget}")
                    if (n.alertType == NotificationPrice.PRICE_RISES_TO) {
                        if (priceFromApi >= n.priceTarget) {
                            triggerNotification(n,context)
                            db.deleteOneNotification(n.id)
                            Log.d("NOTIFICATION", "Price above target, deleting")
                        }else{
                            Log.d("NOTIFICATION", "${n.coinName} price ($priceFromApi) is not above its target (${n.priceTarget}) ")
                        }
                    } else if (n.alertType == NotificationPrice.PRICE_DROPS_TO) {
                        if (priceFromApi <= n.priceTarget) {
                            triggerNotification(n,context)
                            db.deleteOneNotification(n.id)
                            Log.d("NOTIFICATION", "Price below target, deleting")
                        }else{
                            Log.d("NOTIFICATION", "${n.coinName} price ($priceFromApi) is not below its target (${n.priceTarget}) ")
                        }
                    }
                },
                {
                    Log.d("NOTIFICATION", "Network error, cannot access api")
                    resetAlarm(context);
                })
            stringRequest.retryPolicy = DefaultRetryPolicy(
                6000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            queue.add(stringRequest)

        }
        resetAlarm(context);
    }

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
        Log.d("NOTIFICATION", "Starting alarm")
        //everytime you call this method, the alarm gets overwritten since its pendingIntent has the same requestCode,
        val intent = Intent(context, NotificationBroadcastReciever::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context,111,intent,PendingIntent.FLAG_UPDATE_CURRENT)


        val alarmManager : AlarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val timenow = System.currentTimeMillis()

        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, timenow+1000*60*4, 1000*60*4,pendingIntent)
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timenow + 1000*60*4, pendingIntent)
    }


}