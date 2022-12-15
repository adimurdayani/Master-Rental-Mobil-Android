package com.nurul.rentalmobil

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nurul.rentalmobil.ui.auth.LoginActivity
import com.nurul.rentalmobil.ui.home.HomeFragment
import com.nurul.rentalmobil.ui.keranjang.KeranjangFragment
import com.nurul.rentalmobil.ui.profile.ProfileFragment
import com.nurul.rentalmobil.ui.riwayat.RiwayatFragment
import com.nurul.rentalmobil.util.SharedPref

class HomeActivity : AppCompatActivity() {
    val fragmentHome: Fragment = HomeFragment()
    val fragmentKeranjang: Fragment = KeranjangFragment()
    val fragmentRiwayat: Fragment = RiwayatFragment()
    val fragmentProfile: Fragment = ProfileFragment()
    val fm: FragmentManager = supportFragmentManager
    var active: Fragment = fragmentHome
    private var dariDetail: Boolean = false
    private var dariSukses: Boolean = false

    private lateinit var menu: Menu
    private lateinit var menuItem: MenuItem
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var s: SharedPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        s = SharedPref(this)
        setOptButtonNav()

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(message, IntentFilter("event:keranjang"))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(messageSukses, IntentFilter("event:riwayat"))
    }

    private val message: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            dariDetail = true
        }
    }
    private val messageSukses: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            dariSukses = true
        }
    }

    private fun setOptButtonNav() {
        fm.beginTransaction().add(R.id.frm_home, fragmentHome).show(fragmentHome).commit()
        fm.beginTransaction().add(R.id.frm_home, fragmentKeranjang).hide(fragmentKeranjang).commit()
        fm.beginTransaction().add(R.id.frm_home, fragmentRiwayat).hide(fragmentRiwayat).commit()
        fm.beginTransaction().add(R.id.frm_home, fragmentProfile).hide(fragmentProfile).commit()

        bottomNavigationView = findViewById(R.id.btn_navigasi)
        menu = bottomNavigationView.menu
        menuItem = menu.getItem(0)
        menuItem.isChecked = true

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_home -> {
                    callFragment(0, fragmentHome)
                }
                R.id.item_keranjang -> {
                    callFragment(1, fragmentKeranjang)
                }
                R.id.item_riwayat -> {
                    callFragment(2, fragmentRiwayat)
                }
                R.id.item_user -> {
                    if (s.getStatusLogin()) {
                        callFragment(3, fragmentProfile)
                    } else {
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                }
            }
            false
        }
    }

    private fun callFragment(int: Int, fragment: Fragment) {
        menuItem = menu.getItem(int)
        menuItem.isChecked = true
        fm.beginTransaction().hide(active).show(fragment).commit()
        active = fragment
    }

    override fun onResume() {
        if (dariDetail) {
            dariDetail = false
            callFragment(1, fragmentKeranjang)
        } else if (dariSukses) {
            dariSukses = false
            callFragment(2, fragmentRiwayat)
        }
        super.onResume()
    }
}