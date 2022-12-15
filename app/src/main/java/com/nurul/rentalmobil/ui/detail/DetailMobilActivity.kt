package com.nurul.rentalmobil.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.nurul.rentalmobil.R
import com.nurul.rentalmobil.core.data.model.Mobil
import com.nurul.rentalmobil.core.data.model.MobilList
import com.nurul.rentalmobil.core.data.model.ResponseModel
import com.nurul.rentalmobil.core.data.room.MyDatabase
import com.nurul.rentalmobil.core.data.source.ApiConfig
import com.nurul.rentalmobil.ui.home.adapter.AdapterMobil
import com.nurul.rentalmobil.util.Helper
import com.nurul.rentalmobil.util.SharedPref
import com.nurul.rentalmobil.util.Util
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailMobilActivity : AppCompatActivity() {
    lateinit var btn_kembali: ImageView
    lateinit var nama: TextView
    lateinit var mobil: MobilList
    lateinit var btn_notifikasi: ImageView
    lateinit var div_angka: RelativeLayout
    lateinit var tv_angka: TextView
    lateinit var image_mobil: ImageView
    lateinit var nama_p: TextView
    lateinit var harga_mobil: TextView
    lateinit var merk: TextView
    lateinit var warna: TextView
    lateinit var denda: TextView
    lateinit var phone: TextView
    lateinit var deskripsi: TextView
    lateinit var img_logo: ImageView
    lateinit var nama_toko: TextView
    lateinit var alamat: TextView
    lateinit var rc_data: RecyclerView
    lateinit var btn_keranjang: ImageView
    lateinit var btn_booking: LinearLayout
    lateinit var div_status: LinearLayout
    lateinit var status_mobil: TextView
    lateinit var myDb: MyDatabase
    lateinit var s: SharedPref

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_mobil)
        s = SharedPref(this)
        myDb = MyDatabase.getInstance(this)!!
        setinit()
        setButton()
        setDisplay()
        cekkeranjang()
    }

    @SuppressLint("SetTextI18n")
    private fun cekkeranjang() {
        val datakeranjang = myDb.daoKeranjang().getAll()
        if (datakeranjang.isNotEmpty()) {
            div_angka.visibility = View.VISIBLE
            tv_angka.text = "" + datakeranjang.size
        } else {
            div_angka.visibility = View.GONE
        }
    }

    private fun setButton() {
        val data = intent.getStringExtra("extra")
        mobil = Gson().fromJson<MobilList>(data, MobilList::class.java)

        btn_kembali.setOnClickListener {
            onBackPressed()
        }
        btn_keranjang.setOnClickListener {
            if (s.getStatusLogin()) {
                if (mobil.status == "terpakai") {
                    setPringatan("Maaf mobil telah dipakai!")
                } else {
                    val mobilData = myDb.daoKeranjang().getProduk(mobil.id)
                    if (mobilData == null) {
                        insert()
                    } else {
                        mobilData.jumlah = mobilData.jumlah + 1
                        update(mobilData)
                    }
                }
            } else {
                setPringatan("Maaf, anda harus melakukan proses login terlebih dahulu agar bisa melakukan booking kendaraan!")
            }
        }
        btn_notifikasi.setOnClickListener {
            val intent = Intent("event:keranjang")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            onBackPressed()
        }
        btn_booking.setOnClickListener {
            if (s.getStatusLogin()) {
                if (mobil.status == "terpakai") {
                    setPringatan("Maaf mobil telah dipakai!")
                } else {
                    val mobilData = myDb.daoKeranjang().getProduk(mobil.id)
                    if (mobilData == null) {
                        insert()
                    } else {
                        mobilData.jumlah = mobilData.jumlah + 1
                        update(mobilData)
                    }
                    val intent = Intent("event:keranjang")
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                    onBackPressed()
                }
            } else {
                setPringatan("Maaf, anda harus melakukan proses login terlebih dahulu agar bisa melakukan booking kendaraan!")
            }

        }
    }

    private fun insert() {
        CompositeDisposable().add(Observable.fromCallable { myDb.daoKeranjang().insert(mobil) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                cekkeranjang()
                Log.d("respons", "data inserted" + mobil.toko_id)
            })
    }

    private fun update(mobilData: MobilList) {
        CompositeDisposable().add(Observable.fromCallable { myDb.daoKeranjang().update(mobilData) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                cekkeranjang()
                Log.d("respons", "data inserted")
            })
    }

    private fun setLimitMobil() {

        ApiConfig.instanceRetrofit.mobil_limit().enqueue(object : Callback<ResponseModel> {
            override fun onResponse(
                call: Call<ResponseModel>,
                response: Response<ResponseModel>,
            ) {
                val res = response.body()!!
                if (res.status == 1) {
                    listmobillimit = res.mobil_limit
                    setDisplay()
                } else {
                    setError(res.message)
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                setError("Terjadi kesalahan koneksi!")
                Log.d("Response", "Error: " + t.message)
            }
        })
    }

    private fun setError(pesan: String) {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(this, DialogTypes.TYPE_ERROR)
                .setTitle("Ooopss")
                .setDescription(pesan)
                .setPositiveText("Oke")
                .setPositiveTextColor(Color.WHITE)
                .setPositiveListener(object : ClickListener {
                    override fun onClick(dialog: LottieAlertDialog) {
                        dialog.dismiss()
                    }

                })
                .build()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun setPringatan(pesan: String) {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(this, DialogTypes.TYPE_WARNING)
                .setTitle("Ooopss")
                .setDescription(pesan)
                .setPositiveText("Oke")
                .setPositiveTextColor(Color.WHITE)
                .setPositiveListener(object : ClickListener {
                    override fun onClick(dialog: LottieAlertDialog) {
                        dialog.dismiss()
                    }

                })
                .build()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private var listmobillimit: ArrayList<Mobil> = ArrayList()

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun setDisplay() {
        val data = intent.getStringExtra("extra")
        mobil = Gson().fromJson<MobilList>(data, MobilList::class.java)

        phone.text = mobil.phone
        nama.text = mobil.nama_mobil
        nama_p.text = mobil.nama_mobil
        harga_mobil.text = Helper().formatRupiah(mobil.harga) + "/hari"
        merk.text = mobil.merk
        warna.text = mobil.color
        denda.text = Helper().formatRupiah(mobil.denda)
        status_mobil.text = mobil.status

        var color = this.getDrawable(R.drawable.bg_btn_login)
        if (mobil.status == "tersedia") {
            color = this.getDrawable(R.drawable.bg_status_1)
        } else if (mobil.status == "terpakai") {
            color = this.getDrawable(R.drawable.bg_status_2)
        }
        div_status.background = color

        deskripsi.text =
            "Harga sewa mobil " +
                    Helper().formatRupiah(mobil.harga) + ", " +
                    " Denda perjam " +
                    Helper().formatRupiah(mobil.denda) + ", " +
                    "Tahun keluar mobil " +
                    mobil.tahun + ", " +
                    " Warna mobil " +
                    mobil.color + ", " +
                    " Plat mobil " +
                    mobil.no_plat

        nama_toko.text = mobil.nama
        alamat.text = mobil.alamat
        Log.d(
            "Response",
            "Alamat: " + mobil.alamat + " Gambar: " + mobil.image_user + " Nama: " + mobil.nama
        )

        Picasso.get()
            .load(Util.mobilUrl + mobil.image)
            .error(R.drawable.logo_open)
            .placeholder(R.drawable.logo_open)
            .into(image_mobil)

        Picasso.get()
            .load(Util.logouser + mobil.image_user)
            .error(R.drawable.ic_user2)
            .placeholder(R.drawable.ic_user2)
            .into(img_logo)

        val data2 = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rc_data.setHasFixedSize(true)
        rc_data.adapter = AdapterMobil(this, listmobillimit)
        rc_data.layoutManager = data2
    }

    private fun setinit() {
        btn_kembali = findViewById(R.id.btn_kembali)
        nama = findViewById(R.id.nama)
        btn_notifikasi = findViewById(R.id.btn_notifikasi)
        div_angka = findViewById(R.id.div_angka)
        tv_angka = findViewById(R.id.tv_angka)
        image_mobil = findViewById(R.id.image_mobil)
        nama_p = findViewById(R.id.nama_p)
        harga_mobil = findViewById(R.id.harga_mobil)
        merk = findViewById(R.id.merk)
        warna = findViewById(R.id.warna)
        denda = findViewById(R.id.denda)
        phone = findViewById(R.id.phone)
        deskripsi = findViewById(R.id.deskripsi)
        img_logo = findViewById(R.id.img_logo)
        nama_toko = findViewById(R.id.nama_toko)
        alamat = findViewById(R.id.alamat)
        rc_data = findViewById(R.id.rc_data)
        btn_keranjang = findViewById(R.id.btn_keranjang)
        btn_booking = findViewById(R.id.btn_booking)
        div_status = findViewById(R.id.div_status)
        status_mobil = findViewById(R.id.status_mobil)
    }

    override fun onResume() {
        super.onResume()
        setLimitMobil()
    }
}