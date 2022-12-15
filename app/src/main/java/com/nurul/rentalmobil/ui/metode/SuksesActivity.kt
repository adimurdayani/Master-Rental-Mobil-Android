package com.nurul.rentalmobil.ui.metode

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.nurul.rentalmobil.HomeActivity
import com.nurul.rentalmobil.R
import com.nurul.rentalmobil.core.data.model.Mobil
import com.nurul.rentalmobil.core.data.model.Rekening
import com.nurul.rentalmobil.core.data.room.MyDatabase
import com.nurul.rentalmobil.util.Helper
import com.nurul.rentalmobil.util.Util
import com.squareup.picasso.Picasso

class SuksesActivity : AppCompatActivity() {
    lateinit var tgl: TextView
    lateinit var img_bank: ImageView
    lateinit var nomor_bank: TextView
    lateinit var nama_penerima: TextView
    lateinit var btn_copy: ImageView
    lateinit var total_pembayaran: TextView
    lateinit var btn_copy2: ImageView
    lateinit var btn_kembali: ImageView
    lateinit var btn_cekstatus: LinearLayout
    var mobil = Mobil()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sukses)
        setinit()
        setDisplay()
        setButton()
    }

    private fun setButton() {
        btn_kembali.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        btn_cekstatus.setOnClickListener {
            val intent = Intent("event:riwayat")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            onBackPressed()
        }

        btn_copy.setOnClickListener {
            copyText(nomor_bank.text.toString())
        }
        btn_copy2.setOnClickListener {
            copyText(nomor_bank.text.toString())
        }
    }

    private fun copyText(toString: String) {
        val copyManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val copyText = ClipData.newPlainText("text", toString)
        copyManager.setPrimaryClip(copyText)
        Toast.makeText(this, "Copy to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun setDisplay() {
        val jsonBank = intent.getStringExtra("extraRekening")!!.toString()
        val jsonMobil = intent.getStringExtra("extraMobil")!!.toString()
        val tgl_bayar = intent.getStringExtra("tgl")!!.toString()
        val mobil = Gson().fromJson(jsonMobil, Mobil::class.java)
        val rek = Gson().fromJson(jsonBank, Rekening::class.java)
        tgl.text = "Mohon selesaikan pembayaran anda pada tanggal: " + tgl_bayar
        nomor_bank.text = rek.rekening
        nama_penerima.text = rek.nama

        val totalHarga = Integer.valueOf(intent.getStringExtra("jumlah")!!)
        if (totalHarga == 0) {
            total_pembayaran.text = Helper().formatRupiah(mobil.harga)
        } else {
            total_pembayaran.text = Helper().formatRupiah(totalHarga)
        }

        val img = rek.image_bank
        Picasso.get()
            .load(Util.logobank + img)
            .error(R.drawable.bri)
            .placeholder(R.drawable.bri)
            .into(img_bank)

        //        hapus keranjang
        val myDb = MyDatabase.getInstance(this)!!
        myDb.daoKeranjang().deleteById(mobil.id)
    }

    private fun setinit() {
        tgl = findViewById(R.id.tgl)
        img_bank = findViewById(R.id.img_bank)
        nomor_bank = findViewById(R.id.nomor_bank)
        nama_penerima = findViewById(R.id.nama_penerima)
        btn_copy = findViewById(R.id.btn_copy)
        total_pembayaran = findViewById(R.id.total_pembayaran)
        btn_copy2 = findViewById(R.id.btn_copy2)
        btn_kembali = findViewById(R.id.btn_kembali)
        btn_cekstatus = findViewById(R.id.btn_cekstatus)
    }
}