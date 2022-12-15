package com.nurul.rentalmobil.ui.content

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.nurul.rentalmobil.R

class TentangActivity : AppCompatActivity() {
    lateinit var btn_kembali: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tentang)
        setinit()
    }

    private fun setinit() {
        btn_kembali = findViewById(R.id.btn_kembali)
        btn_kembali.setOnClickListener {
            onBackPressed()
        }
    }
}