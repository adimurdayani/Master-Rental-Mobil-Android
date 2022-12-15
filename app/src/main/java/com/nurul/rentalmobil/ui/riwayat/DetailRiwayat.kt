package com.nurul.rentalmobil.ui.riwayat

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.github.drjacky.imagepicker.ImagePicker
import com.google.gson.Gson
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.nurul.rentalmobil.R
import com.nurul.rentalmobil.core.data.model.ResponseModel
import com.nurul.rentalmobil.core.data.model.Transaksi
import com.nurul.rentalmobil.core.data.source.ApiConfig
import com.nurul.rentalmobil.util.Helper
import com.nurul.rentalmobil.util.Util
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class DetailRiwayat : AppCompatActivity() {
    lateinit var btn_kembali: ImageView
    lateinit var nama: TextView
    lateinit var image_mobil: ImageView
    lateinit var nama_p: TextView
    lateinit var deskripsi: TextView
    lateinit var img_logo: ImageView
    lateinit var nama_toko: TextView
    lateinit var alamat: TextView
    lateinit var harga_mobil: TextView
    lateinit var harga_supir: TextView
    lateinit var jml_mobil: TextView
    lateinit var total_bayar: TextView
    lateinit var tgl_pembayaran: TextView
    lateinit var btn_batal: LinearLayout
    lateinit var btn_upload: LinearLayout
    var transaksi = Transaksi()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_riwayat)
        val json = intent.getStringExtra("extra")
        transaksi = Gson().fromJson(json, Transaksi::class.java)
        setinit()
        setButton()
        setDisplay()
    }

    @SuppressLint("SetTextI18n")
    private fun setDisplay() {
        nama.text = transaksi.nama_mobil
        nama_p.text = transaksi.nama_mobil
        deskripsi.text = "Harga sewa mobil " +
                Helper().formatRupiah(transaksi.harga) + ", " +
                " Denda perjam " +
                Helper().formatRupiah(transaksi.denda) + ", " +
                "Tahun keluar mobil " +
                transaksi.tahun + ", " +
                " Warna mobil " +
                transaksi.color + ", " +
                " Plat mobil " +
                transaksi.no_plat
        nama_toko.text = transaksi.nama_toko
        alamat.text = transaksi.alamat
        harga_mobil.text = Helper().formatRupiah(transaksi.harga)
        jml_mobil.text = "1 Unit"
        harga_supir.text = Helper().formatRupiah(transaksi.harga_supir)

        val total = transaksi.harga + transaksi.harga_supir
        total_bayar.text = Helper().formatRupiah(total)

        if (transaksi.bukti_transfer == "") {
            tgl_pembayaran.text =
                "Batas pembayaran: " + transaksi.tgl_rental + " Mobil dapat diambil jika pembayaran telah dilakukan. Batas pengambilan mobil " + transaksi.tgl_kembali
        } else {
            tgl_pembayaran.text =
                "Transaksi berhasil pada tanggal: " + transaksi.created_at + ". Anda dapat mengambil mobil rental pada tanggal: " + transaksi.tgl_rental + ", jam: " + transaksi.jam_ambil
        }

        Picasso.get()
            .load(Util.mobilUrl + transaksi.image)
            .error(R.drawable.logo_open)
            .placeholder(R.drawable.logo_open)
            .into(image_mobil)

        Picasso.get()
            .load(Util.mobilUrl + transaksi.image_user)
            .error(R.drawable.logo_open)
            .placeholder(R.drawable.logo_open)
            .into(img_logo)

        if (transaksi.status == "selesai" || transaksi.status == "dibatalkan") {
            btn_batal.visibility = View.GONE
            btn_upload.visibility = View.GONE
        } else {
            btn_batal.visibility = View.VISIBLE
            btn_upload.visibility = View.VISIBLE
        }
    }

    private fun setButton() {
        btn_kembali.setOnClickListener {
            onBackPressed()
        }
        btn_batal.setOnClickListener {
            batal()
        }
        btn_upload.setOnClickListener {
            imagePick()
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                // Use the uri to load the image
                Log.d("TAG", "URL Image: $uri")
                val fileUri: Uri = uri
                dialogUpload(File(fileUri.path))
            }
        }
    var alertDialog: AlertDialog? = null

    private fun dialogUpload(file: File) {
        val view = layoutInflater
        val layout = view.inflate(R.layout.upload_gambar, null)

        val imageView: ImageView = layout.findViewById(R.id.image)
        val btnUpload: LinearLayout = layout.findViewById(R.id.btn_upload)
        val btnGambar: LinearLayout = layout.findViewById(R.id.btn_gambarlain)

        Picasso.get()
            .load(file)
            .into(imageView)

        btnUpload.setOnClickListener {
            upload(file)
        }

        btnGambar.setOnClickListener {
            imagePick()
        }
        alertDialog = AlertDialog.Builder(this).create()
        alertDialog!!.setView(layout)
        alertDialog!!.setCancelable(true)
        alertDialog!!.show()
    }

    fun File?.toMultipartBody(name: String = "bukti_transfer"): MultipartBody.Part? {
        if (this == null) return null
        val reqFile: RequestBody = this.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(name, this.name, reqFile)
    }

    private fun upload(file: File) {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(this, DialogTypes.TYPE_ERROR)
                .setTitle("Loading...")
                .setDescription("Harap tunggu sebentar")
                .build()
        alertDialog.setCancelable(false)
        alertDialog.show()

        val fileImage = file.toMultipartBody()
        ApiConfig.instanceRetrofit.buktiTransfer(transaksi.id, fileImage!!)
            .enqueue(object : Callback<ResponseModel> {
                override fun onResponse(
                    call: Call<ResponseModel>,
                    response: Response<ResponseModel>,
                ) {
                    alertDialog.dismiss()
                    if (response.body() == null) {
                        alertDialog.dismiss()
                        setError("Gambar tidak ditemukan")
                    } else {
                        val res = response.body()!!
                        if (res.status == 1) {
                            setSukses("Bukti transfer berhasil diupload!. Silahkan ke lokasi rental untuk mengambil yang telah dibooking.")
                        } else {
                            alertDialog.dismiss()
                            setError(res.message)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    alertDialog.dismiss()
                    setError("Terjadi kesalahan koneksi!")
                    Log.d("Response", "Error: " + t.message)
                }
            })
    }

    private fun imagePick() {
        ImagePicker.with(this)
            .crop()
            .maxResultSize(512, 512)
            .createIntentFromDialog { launcher.launch(it) }
    }

    private fun setSukses(pesan: String) {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(this, DialogTypes.TYPE_SUCCESS)
                .setTitle("Sukses")
                .setDescription(pesan)
                .setPositiveText("Oke")
                .setPositiveTextColor(Color.WHITE)
                .setPositiveListener(object : ClickListener {
                    override fun onClick(dialog: LottieAlertDialog) {
                        onBackPressed()
                        dialog.dismiss()
                    }

                })
                .build()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun setError(pesan: String) {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(this, DialogTypes.TYPE_ERROR)
                .setTitle("Something error")
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

    private fun batal() {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(this, DialogTypes.TYPE_QUESTION)
                .setTitle("Pembatalan transaksi!")
                .setDescription("Anda yakin ingin membatalkan transaksi anda?")
                .setPositiveText("Ya, batalkan!")
                .setPositiveTextColor(Color.WHITE)
                .setNegativeButtonColor(Color.parseColor("#EB5757"))
                .setPositiveListener(object : ClickListener {
                    override fun onClick(dialog: LottieAlertDialog) {
                        updateStatus()
                        dialog.dismiss()
                    }

                })
                .setNegativeText("Tidak")
                .setNegativeTextColor(Color.WHITE)
                .setNegativeListener(object : ClickListener {
                    override fun onClick(dialog: LottieAlertDialog) {
                        dialog.dismiss()
                    }

                })
                .build()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun updateStatus() {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(this, DialogTypes.TYPE_ERROR)
                .setTitle("Loading...")
                .setDescription("Harap tunggu sebentar")
                .build()
        alertDialog.setCancelable(false)
        alertDialog.show()

        ApiConfig.instanceRetrofit.batalTransaksi(transaksi.id, transaksi.mobil_id)
            .enqueue(object : Callback<ResponseModel> {
                override fun onResponse(
                    call: Call<ResponseModel>,
                    response: Response<ResponseModel>,
                ) {
                    alertDialog.dismiss()
                    val res = response.body()!!
                    if (res.status == 1) {
                        setSukses("Transaksi anda berhasil dibatalkan!!")
                    } else {
                        alertDialog.dismiss()
                        setError(res.message)
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    alertDialog.dismiss()
                    setError("Terjadi kesalahan koneksi!")
                    Log.d("Response", "Error: " + t.message)
                }
            })
    }

    private fun setinit() {
        btn_kembali = findViewById(R.id.btn_kembali)
        nama = findViewById(R.id.nama)
        image_mobil = findViewById(R.id.image_mobil)
        nama_p = findViewById(R.id.nama_p)
        deskripsi = findViewById(R.id.deskripsi)
        img_logo = findViewById(R.id.img_logo)
        nama_toko = findViewById(R.id.nama_toko)
        alamat = findViewById(R.id.alamat)
        harga_mobil = findViewById(R.id.harga_mobil)
        jml_mobil = findViewById(R.id.jml_mobil)
        total_bayar = findViewById(R.id.total_bayar)
        btn_batal = findViewById(R.id.btn_batal)
        btn_upload = findViewById(R.id.btn_booking)
        harga_supir = findViewById(R.id.harga_supir)
        tgl_pembayaran = findViewById(R.id.tgl_pembayaran)
    }
}