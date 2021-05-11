package com.example.cryptonotification.Models

class DisplayCoin(
    var id: Int = -1,
    var name: String = "",
    var symbol: String = "",
    var price: Double = 0.0,
    var change24h: Double = 0.0,
    var change7d: Double = 0.0,
    var change30d: Double = 0.0
)