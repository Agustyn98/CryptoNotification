package com.example.cryptonotification

import android.app.NotificationManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.cryptonotification.bottomNavFragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    var fragmentCoins = FragmentCoins()
    var fragmentNotification = FragmentNotification()
    var fragmentPortfolio = FragmentPortfolio()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setBottomNavListener()
        setCurrentFragment(fragmentCoins)
    }

    private fun setBottomNavListener() {
        val navigation: BottomNavigationView = findViewById(R.id.main_bottomNav)
        navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.coinsItem -> {
                    setCurrentFragment(fragmentCoins)
                }
                R.id.notificationsItem -> {
                    setCurrentFragment(fragmentNotification)
                }
                R.id.portfolioItem -> {
                    setCurrentFragment(fragmentPortfolio)
                }
                else -> {
                    setCurrentFragment(fragmentCoins)
                }
            }
            true
        })
    }

    private fun setCurrentFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container_framelayout, fragment)
        //transaction.addToBackStack(null)
        transaction.commit()

    }


}