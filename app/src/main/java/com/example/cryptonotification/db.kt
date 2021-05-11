package com.example.cryptonotification

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.cryptonotification.Models.Coin
import com.example.cryptonotification.Models.NotificationPrice
import kotlin.Exception

class db(context: Context) : SQLiteOpenHelper(context, "crypto_notification", null, 1) {

    private val TABLE_COIN_DISPLAY = "displayed_coins"
    private val COIN_DISPLAY_NAME = "name"

    private val TABLE_NOTIFICATION = "notification"
    private val NOTIFICATION_ID = "id"
    private val NOTIFICATION_COIN = "coin"
    private val NOTIFICATION_PRICE_TARGET = "price_target"
    private val NOTIFICATION_ALERT_TYPE = "alert_type"

    private val TABLE_COIN = "coin"
    private val COIN_ID = "id"
    private val COIN_NAME = "name"
    private val COIN_OWNED = "owned"
    private val COIN_PRICE_BOUGHT = "price_bought"
    private val COIN_PAIR_BOUGHT = "pair_bought"


    override fun onCreate(db: SQLiteDatabase?) {
        val createTableStatementCoin =
            "CREATE TABLE $TABLE_COIN_DISPLAY ($COIN_DISPLAY_NAME TEXT NOT NULL UNIQUE);"

        val createTableNotification =
            "CREATE TABLE $TABLE_NOTIFICATION ($NOTIFICATION_ID INTEGER NOT NULL,$NOTIFICATION_COIN TEXT,$NOTIFICATION_PRICE_TARGET REAL,$NOTIFICATION_ALERT_TYPE INTEGER, PRIMARY KEY ($NOTIFICATION_ID ));"

        val createTablePortfolioCoin =
            "CREATE TABLE $TABLE_COIN ($COIN_ID INTEGER NOT NULL,$COIN_NAME TEXT,$COIN_OWNED REAL,$COIN_PRICE_BOUGHT REAL,$COIN_PAIR_BOUGHT TEXT, PRIMARY KEY ($NOTIFICATION_ID));"

        if (db == null)
            return
        db.execSQL(createTableStatementCoin)
        db.execSQL(createTableNotification)
        db.execSQL(createTablePortfolioCoin)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun addOneDisplayCoin(coinName: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COIN_DISPLAY_NAME, coinName)

        val result = db.insertOrThrow(TABLE_COIN_DISPLAY, null, contentValues)

        db.close()
        return result
    }

    fun addOneCoin(coin: Coin): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COIN_NAME, coin.name.uppercase())
        contentValues.put(COIN_OWNED, coin.owned)
        contentValues.put(COIN_PRICE_BOUGHT, coin.priceBought)
        contentValues.put(COIN_PAIR_BOUGHT, coin.pairBought.uppercase())

        val result = db.insertOrThrow(TABLE_COIN, null, contentValues)

        db.close()
        return result
    }

    fun addOneNotification(notification: NotificationPrice): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(NOTIFICATION_COIN, notification.coinName)
        contentValues.put(NOTIFICATION_PRICE_TARGET, notification.priceTarget)
        contentValues.put(NOTIFICATION_ALERT_TYPE, notification.alertType)

        val result = db.insert(TABLE_NOTIFICATION, null, contentValues)
        db.close()
        return result
    }

    fun getAllDisplayCoins(): ArrayList<String> {
        val array = ArrayList<String>()
        val db = this.readableDatabase
        val queryString = "SELECT * FROM $TABLE_COIN_DISPLAY";

        //Cursor = like a complex array of items that stores the query results
        val cursor: Cursor = db.rawQuery(queryString, null)
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(0)
                array.add(name)

            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return array
    }

    fun getAllCoins(): ArrayList<Coin> {
        val array = ArrayList<Coin>()
        val db = this.readableDatabase
        val queryString = "SELECT * FROM $TABLE_COIN";

        //Cursor = like a complex array of items that stores the query results
        val cursor: Cursor = db.rawQuery(queryString, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val name = cursor.getString(1)
                val owned = cursor.getDouble(2)
                val bought = cursor.getDouble(3)
                val pair = cursor.getString(4)
                val coin = Coin(id, name, owned, bought, pair)
                array.add(coin)

            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return array
    }

    fun getAllCoinsByName(name: String): ArrayList<Coin> {
        val array = ArrayList<Coin>()
        val db = this.readableDatabase
        val queryString = """SELECT * FROM $TABLE_COIN WHERE $COIN_NAME = '$name'"""

        //Cursor = like a complex array of items that stores the query results
        val cursor: Cursor = db.rawQuery(queryString, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val name = cursor.getString(1)
                val owned = cursor.getDouble(2)
                val bought = cursor.getDouble(3)
                val pair = cursor.getString(4)
                val coin = Coin(id, name, owned, bought, pair)
                array.add(coin)

            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return array
    }

    fun getAllCoinsGroupedByName(): ArrayList<Coin> {
        val array = ArrayList<Coin>()

        val db = this.readableDatabase
        val queryString = """SELECT name,SUM(owned) FROM coin GROUP BY name"""

        val cursor: Cursor = db.rawQuery(queryString, null)
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(0)
                val owned = cursor.getDouble(1)

                val coin = Coin(-1, name, owned, -1.0, "")
                array.add(coin)

            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return array
    }

    fun getOneCoin(id: Int): Coin {
        val db = this.readableDatabase
        val queryString = "SELECT * FROM $TABLE_COIN WHERE id = $id";
        var coin = Coin()
        val cursor: Cursor = db.rawQuery(queryString, null)
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            val owned = cursor.getDouble(2)
            val bought = cursor.getDouble(3)
            val pair = cursor.getString(4)
            coin = Coin(id, name, owned, bought, pair)
        }

        cursor.close()
        db.close()
        return coin
    }

    fun getAllNotifications(): ArrayList<NotificationPrice> {
        val array = ArrayList<NotificationPrice>()
        val db = this.readableDatabase
        val queryString = "SELECT * FROM $TABLE_NOTIFICATION";

        val cursor: Cursor = db.rawQuery(queryString, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val name = cursor.getString(1)
                val priceTarget = cursor.getDouble(2)
                val alertType = cursor.getInt(3)
                val notification = NotificationPrice(id, name, priceTarget, alertType)
                array.add(notification)

            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return array
    }


    fun deleteOneDisplayCoin(name: String): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_COIN_DISPLAY, "$COIN_DISPLAY_NAME = '$name'", null);
        db.close()
        return result;
    }

    fun deleteAllDisplayCoin(): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_COIN_DISPLAY, null, null);
        db.close()
        return result;
    }

    fun deleteOneCoin(id: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_COIN, "$COIN_ID = $id", null);
        db.close()
        return result;
    }

    fun deleteOneCoinByName(name: String): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_COIN, "$COIN_NAME = '$name'", null);
        db.close()
        return result
    }

    fun deleteAllCoin(): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_COIN, null, null);
        db.close()
        return result;
    }


    fun deleteOneNotification(id: Int?): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NOTIFICATION, "$NOTIFICATION_ID = $id", null);
        db.close()
        return result
    }

    fun deleteAllNotification(): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NOTIFICATION, null, null);
        db.close()
        return result
    }

    fun updateOneCoinName(currentName: String, newName:String): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COIN_NAME, newName.uppercase())

        val success = db.update(TABLE_COIN, contentValues, "$COIN_NAME = '$currentName'", null)
        db.close()
        return success
    }

}