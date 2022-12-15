package com.nurul.rentalmobil.ui.content

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.nurul.rentalmobil.R

class AlamatActivity : AppCompatActivity() {
    lateinit var btn_kembali: ImageView
    lateinit var l_alamat: TextInputLayout
    lateinit var e_alamat: TextInputEditText
    lateinit var btn_simpan: LinearLayout
    lateinit var progress: ProgressBar
    lateinit var txt_simpan: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alamat)
        setinit()
        setButton()
        cekvalidasi()
    }

    private fun setButton() {
        btn_kembali.setOnClickListener {
            onBackPressed()
        }
        btn_simpan.setOnClickListener {
            if (validasi()){
                simpan()
            }
        }
    }

    private fun simpan() {

    }

    private fun cekvalidasi() {
        e_alamat.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_alamat.text.toString().isEmpty()) {
                    l_alamat.isErrorEnabled = false
                } else if (e_alamat.text.toString().isNotEmpty()) {
                    l_alamat.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun validasi(): Boolean {
        if (e_alamat.text.toString().isEmpty()) {
            l_alamat.isErrorEnabled = true
            l_alamat.error = "Kolom alamat tidak boleh kosong!"
            e_alamat.requestFocus()
            return false
        }
        return true
    }

    private fun setinit() {
        btn_kembali = findViewById(R.id.btn_kembali)
        l_alamat = findViewById(R.id.l_alamat)
        e_alamat = findViewById(R.id.e_alamat)
        btn_simpan = findViewById(R.id.btn_simpan)
        progress = findViewById(R.id.progress)
        txt_simpan = findViewById(R.id.txt_simpan)
    }
}