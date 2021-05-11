package com.example.cryptonotification

import android.content.Context
import com.example.cryptonotification.Models.DisplayCoin
import org.json.JSONArray
import java.math.BigDecimal
import java.math.RoundingMode


class APIhandler(private val appContext: Context) {

    private fun stringIds(): String {

        val db = db(context = appContext)
        val coinSymbols = db.getAllDisplayCoins()

        if (coinSymbols.isEmpty())
            return ""
/*
        var idsString = ""
        for (coinSymbol in coinSymbols) {
            idsString += "%2C$coinSymbol"
        }

 */
        var idsString = ""
        val length = coinSymbols.size
        for (i in 0 until length) {
            if (i == 0) {
                idsString += coinSymbols[0]
            } else {
                idsString += "%2C${coinSymbols[i]}"
            }
        }
        return idsString
    }

    fun getUrl(): String {
        val idsString = stringIds()
        if(idsString=="")
            return ""
        val url =
            "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=$idsString&price_change_percentage=24h%2C7d%2C30d"
        return url
    }

    fun JSONtoCoins(json: String): ArrayList<DisplayCoin> {
        val jArray: JSONArray = JSONArray(json)
        val arrayLength = jArray.length()
        val array = ArrayList<DisplayCoin>()
        for (i in 0 until arrayLength) {
            val coin = DisplayCoin()
            val oneObject = jArray.getJSONObject(i)
            coin.price = oneObject.getDouble("current_price")
            coin.name = oneObject.getString("id")
            coin.symbol = oneObject.getString("symbol").uppercase()
            coin.change24h = round(oneObject.getDouble("price_change_percentage_24h_in_currency"))
            coin.change7d = round(oneObject.getDouble("price_change_percentage_7d_in_currency"))
            coin.change30d = round(oneObject.getDouble("price_change_percentage_30d_in_currency"))

            array.add(coin)
        }
        return array
    }

    fun JSONtoPrice(json: String): Double {

        val jArray: JSONArray = JSONArray(json)
        var price = -1.0;

        if (jArray.length() <= 0 || jArray.isNull(0))
            return -1.0
        val oneObject = jArray.getJSONObject(0)
        price = oneObject.getDouble("current_price")
        return price
    }

    private fun round(value: Double): Double {
        var bd: BigDecimal = BigDecimal.valueOf(value)
        bd = bd.setScale(1, RoundingMode.HALF_UP)
        return bd.toDouble()
    }


}