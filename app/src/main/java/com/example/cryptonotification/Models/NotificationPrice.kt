package com.example.cryptonotification.Models

class NotificationPrice(
    var id: Int = -1,
    var coinName: String,
    var priceTarget: Double,
    var alertType: Int
) {

    companion object {
        val PRICE_RISES_TO = 1;
        val PRICE_DROPS_TO = 2;
    }
}
