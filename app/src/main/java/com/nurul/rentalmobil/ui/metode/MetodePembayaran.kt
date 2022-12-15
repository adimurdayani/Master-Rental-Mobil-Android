package com.nurul.rentalmobil.ui.metode

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.nurul.rentalmobil.R
import com.nurul.rentalmobil.core.data.model.Mobil
import com.nurul.rentalmobil.core.data.model.Rekening
import com.nurul.rentalmobil.core.data.model.ResponseModel
import com.nurul.rentalmobil.core.data.model.Supir
import com.nurul.rentalmobil.core.data.room.MyDatabase
import com.nurul.rentalmobil.core.data.source.ApiConfig
import com.nurul.rentalmobil.ui.metode.adapter.AdapterBank
import com.nurul.rentalmobil.util.Helper
import com.nurul.rentalmobil.util.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MetodePembayaran : AppCompatActivity() {
    lateinit var sw_data: SwipeRefreshLayout
    lateinit var rc_metode: RecyclerView
    lateinit var harga_mobil: TextView
    lateinit var jml_mobil: TextView
    lateinit var total_bayar: TextView
    lateinit var btn_kirim: LinearLayout
    lateinit var progress: ProgressBar
    lateinit var text_kirim: TextView
    lateinit var harga_supir: TextView
    lateinit var tv_harga_supir: TextView
    lateinit var tv_id_supir: TextView
    lateinit var tv_tgl: TextView
    lateinit var tv_tgl_rental: TextView
    lateinit var btn_kembali: ImageView
    lateinit var supir: Spinner

    lateinit var btn_tgl_rental: ImageView
    lateinit var tgl_rental: TextView
    lateinit var btn_tgl_kembali: ImageView
    lateinit var tgl_kembali: TextView
    lateinit var datePickerDialog: DatePickerDialog
    lateinit var s: SharedPref
    var getsupir = Supir()

    lateinit var myDb: MyDatabase
    var totalHarga = 0
    var jumlah_mobil = 0
    var get_hargasupir = 0
    var supirId = 0
    var total = 0
    var total_tanggal = 0
    var mobil = Mobil()
    var rek = Rekening()
    var nama_bank = ""
    var rekening = ""
    var image_bank = ""
    var nama = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metode_pembayaran)
        myDb = MyDatabase.getInstance(this)!!
        s = SharedPref(this)
        val json = intent.getStringExtra("extra")!!.toString()
        mobil = Gson().fromJson(json, Mobil::class.java)
        setInit()
        setSpinner()
        setButton()
        getBank()
    }

    @SuppressLint("SetTextI18n")
    private fun setDisplay(arrayList: ArrayList<Rekening>) {

        harga_mobil.text = Helper().formatRupiah(mobil.harga)
        jml_mobil.text = "1" + " Unit"
        val total =
            Integer.valueOf(mobil.harga) * Integer.valueOf(intent.getStringExtra("jml_mobil"))
        total_bayar.text = Helper().formatRupiah(total)

        var getBankArray = ArrayList<Rekening>()
        for (i in arrayList.indices) {
            val getBank = arrayList[i]
            if (i == 0) {
                getBank.isActive = true
            }
            getBankArray.add(getBank)
        }

        nama_bank = getBankArray[0].nama_bank
        rekening = getBankArray[0].rekening
        image_bank = getBankArray[0].image_bank
        nama = getBankArray[0].nama
        Log.d("Response", "Nama: " + nama_bank + " Rekening: " + rekening)

        rek.nama_bank = nama_bank
        rek.rekening = rekening
        rek.image_bank = image_bank
        rek.nama = nama

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        var adapter: AdapterBank? = null
        adapter = AdapterBank(getBankArray, object : AdapterBank.Listeners {
            override fun onClicked(data: Rekening, index: Int) {
                val newarrayRekening = ArrayList<Rekening>()
                for (b in getBankArray) {
                    b.isActive = data.id == b.id
                    newarrayRekening.add(b)
                }
                getBankArray = newarrayRekening
                adapter!!.notifyDataSetChanged()
                nama_bank = data.nama_bank
                rekening = data.rekening
                image_bank = data.image_bank
                nama = data.nama
                Log.d("Response", "Nama: " + nama_bank + " Rekening: " + rekening)
            }

        })

        rc_metode.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        rc_metode.adapter = adapter
        rc_metode.layoutManager = layoutManager

        sw_data.setOnRefreshListener {
            getBank()
        }
    }

    private fun getBank() {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(this, DialogTypes.TYPE_LOADING)
                .setTitle("Loading...")
                .setDescription("Harap tunggu sebentar!")
                .build()
        alertDialog.setCancelable(false)
        alertDialog.show()

        sw_data.isRefreshing = true
        ApiConfig.instanceRetrofit.rekening(mobil.toko_id)
            .enqueue(object : Callback<ResponseModel> {
                override fun onResponse(
                    call: Call<ResponseModel>,
                    response: Response<ResponseModel>,
                ) {
                    sw_data.isRefreshing = false
                    alertDialog.dismiss()
                    if (response.body() == null) {
                        sw_data.isRefreshing = false
                        alertDialog.dismiss()
                    } else {
                        val res = response.body()!!
                        if (res.status == 1) {
                            setDisplay(res.rekening)
                        } else {
                            sw_data.isRefreshing = false
                            alertDialog.dismiss()
                            setError(res.message)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    alertDialog.dismiss()
                    sw_data.isRefreshing = false
                    setError("Terjadi kesalahan koneksi!")
                    Log.d("Response", "Error: " + t.message)
                }
            })
    }


    private val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    private val dateFormatterKembali = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    private val tanggalOnly = SimpleDateFormat("d", Locale.US)
    private val tanggalOnlyRental = SimpleDateFormat("d", Locale.US)
    private val newCalendar = Calendar.getInstance()

    @SuppressLint("SetTextI18n")
    private fun setButton() {
        btn_tgl_rental.setOnClickListener {
            datePickerDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    val newDate = Calendar.getInstance()
                    newDate[year, monthOfYear] = dayOfMonth
                    tgl_rental.text = dateFormatter.format(newDate.time)
                    tv_tgl_rental.text = tanggalOnlyRental.format(newDate.time)
                },
                newCalendar[Calendar.YEAR],
                newCalendar[Calendar.MONTH],
                newCalendar[Calendar.DAY_OF_MONTH]
            )

            datePickerDialog.show()
        }
        btn_tgl_kembali.setOnClickListener {
            if (tgl_rental.text.toString().isNotEmpty()) {

                datePickerDialog = DatePickerDialog(
                    this,
                    { view, year, monthOfYear, dayOfMonth ->
                        val newDate = Calendar.getInstance()
                        newDate[year, monthOfYear] = dayOfMonth
                        tgl_kembali.text = dateFormatterKembali.format(newDate.time)
                        tv_tgl.text = tanggalOnly.format(newDate.time)
                        setTanggal()
                        sw_data.setOnRefreshListener {
                            setSpinner()
                        }
                    },
                    newCalendar[Calendar.YEAR],
                    newCalendar[Calendar.MONTH],
                    newCalendar[Calendar.DAY_OF_MONTH]
                )

                datePickerDialog.show()
            } else {
                setPringatan("Tentukan tanggal rental terlebih dahulu.")
            }
        }

        btn_kirim.setOnClickListener {
            if (tgl_rental.text.toString().isEmpty()) {
                setPringatan("Maaf, anda harus menetunkan tanggal rental!")
            } else if (tgl_kembali.text.toString().isEmpty()) {
                setPringatan("Maaf, anda harus menetunkan tanggal rental!")
            } else {
                kirimTransaksi()
            }
        }
        btn_kembali.setOnClickListener {
            onBackPressed()
        }
    }

    fun setTanggal() {
        val hitung =
            Integer.valueOf(tv_tgl.text.toString()) - Integer.valueOf(tv_tgl_rental.text.toString())
        Log.d("Response", "msg: $hitung")

        if (hitung == 0) {
            total_bayar.text = Helper().formatRupiah(mobil.harga)
        } else {
            total_tanggal = Integer.valueOf(mobil.harga) * Integer.valueOf(hitung)
            Log.d("Response", "harga total: $total_tanggal")
            total_bayar.text = Helper().formatRupiah(total_tanggal)
        }
    }

    private fun kirimTransaksi() {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(this, DialogTypes.TYPE_LOADING)
                .setTitle("Loading..")
                .setDescription("Harap tunggu sebentar!")
                .build()
        alertDialog.setCancelable(false)
        alertDialog.show()

        val kostumer = s.getUser()!!.id
        val tanggal_rental = tgl_rental.text.toString()
        val tanggal_kembali = tgl_kembali.text.toString()
        val id_supir = tv_id_supir.text.toString()
        val harga_supir = tv_harga_supir.text.toString()

        progress.visibility = View.VISIBLE
        text_kirim.visibility = View.GONE
        ApiConfig.instanceRetrofit.kirimTransaksi(
            kostumer,
            mobil.id,
            mobil.toko_id,
            tanggal_rental,
            tgl_kembali.text.toString(),
            mobil.harga.toString(),
            mobil.denda,
            nama_bank,
            rekening,
            id_supir.toIntOrNull(),
            harga_supir.toIntOrNull()
        ).enqueue(object : Callback<ResponseModel> {
            override fun onResponse(
                call: Call<ResponseModel>,
                response: Response<ResponseModel>,
            ) {
                alertDialog.dismiss()
                progress.visibility = View.GONE
                text_kirim.visibility = View.VISIBLE
                if (response.body() == null) {
                    progress.visibility = View.GONE
                    text_kirim.visibility = View.VISIBLE
                    setError("Data anda tidak lengkap")
                } else {
                    val res = response.body()!!
                    if (res.status == 1) {
                        showSukses("Data berhasil tersimpan")
                    } else {
                        progress.visibility = View.GONE
                        text_kirim.visibility = View.VISIBLE
                        alertDialog.dismiss()
                        setError(res.message)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                alertDialog.dismiss()
                sw_data.isRefreshing = false
                progress.visibility = View.GONE
                text_kirim.visibility = View.VISIBLE
                setError("Message: " + t.message)
                Log.d("Response", "Error: " + t.message)
            }
        })
    }

    private fun setSpinner() {
        ApiConfig.instanceRetrofit.supir(mobil.toko_id)
            .enqueue(object : Callback<ResponseModel> {
                override fun onResponse(
                    call: Call<ResponseModel>,
                    response: Response<ResponseModel>,
                ) {
                    if (response.isSuccessful) {
                        val res = response.body()!!
                        val arrayString = ArrayList<String>()
                        arrayString.add("Pilih Supir")
                        val listsupir = res.list_supir
                        for (drive in listsupir) {
                            arrayString.add(drive.nama_supir + " - " + drive.phone)
                        }
                        val adapter = ArrayAdapter<Any>(
                            this@MetodePembayaran,
                            R.layout.item_spinner,
                            arrayString.toTypedArray()
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        supir.adapter = adapter
                        supir.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long,
                                ) {
                                    if (position != 0) {
                                        getsupir = listsupir[position - 1]
                                        val harga = getsupir.harga
                                        val id_supir = getsupir.id
                                        getHargaSupir(harga, id_supir)
                                    } else {
                                        val harga = position
                                        val id_supir = position
                                        getHargaSupir(harga, id_supir)
                                    }
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {}

                            }

                    } else {
                        Log.d("Error", "gagal memuat data" + response.message())
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    Log.d("Error", "gagal memuat data" + t.message)
                    error(t.message.toString())
                }

            })
    }

    private fun getHargaSupir(harga: Int?, supirId: Int?) {
        harga_supir.text = Helper().formatRupiah(harga!!)
        tv_id_supir.text = supirId!!.toString()
        tv_harga_supir.text = harga.toString()

        val harga_supir = harga_supir.text.toString().toIntOrNull() ?: 0
        Log.d("Response", "message: $harga_supir")

        if (total_tanggal == 0) {
            total = Integer.valueOf(mobil.harga) + harga
            total_bayar.text = Helper().formatRupiah(total)
        } else {
            total = total_tanggal + harga
            total_bayar.text = Helper().formatRupiah(total)
        }
    }

    private fun showSukses(pesan: String) {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(this, DialogTypes.TYPE_SUCCESS)
                .setTitle("Sukses")
                .setDescription(pesan)
                .setPositiveText("OK")
                .setPositiveTextColor(Color.WHITE)
                .setPositiveButtonColor(Color.BLUE)
                .setPositiveListener(object : ClickListener {
                    override fun onClick(dialog: LottieAlertDialog) {
                        val jumlah = intent.getStringExtra("totalharga")
                        val jsonMobil = Gson().toJson(mobil, Mobil::class.java)
                        val jsonBank = Gson().toJson(rek, Rekening::class.java)
                        val intent = Intent(this@MetodePembayaran, SuksesActivity::class.java)
                        intent.putExtra("jumlah", total.toString())
                        intent.putExtra("extraRekening", jsonBank)
                        intent.putExtra("extraMobil", jsonMobil)
                        intent.putExtra("tgl", tgl_rental.text.toString())
                        Log.d("Response Rekening", jsonBank)
                        Log.d("Response Mobil", jsonMobil)
                        Log.d("Response Harga Bayar", total.toString())
                        startActivity(intent)
                        finish()
                        dialog.dismiss()
                    }

                })
                .build()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun setInit() {
        sw_data = findViewById(R.id.sw_data)
        rc_metode = findViewById(R.id.rc_metode)
        harga_mobil = findViewById(R.id.harga_mobil)
        jml_mobil = findViewById(R.id.jml_mobil)
        total_bayar = findViewById(R.id.total_bayar)
        btn_kirim = findViewById(R.id.btn_kirim)
        progress = findViewById(R.id.progress)
        text_kirim = findViewById(R.id.text_kirim)
        btn_kembali = findViewById(R.id.btn_kembali)
        btn_tgl_rental = findViewById(R.id.btn_tgl_rental)
        tgl_rental = findViewById(R.id.tgl_rental)
        btn_tgl_kembali = findViewById(R.id.btn_tgl_kembali)
        tgl_kembali = findViewById(R.id.tgl_kembali)
        supir = findViewById(R.id.supir)
        harga_supir = findViewById(R.id.harga_supir)
        tv_harga_supir = findViewById(R.id.tv_harga_supir)
        tv_id_supir = findViewById(R.id.tv_id_supir)
        tv_tgl = findViewById(R.id.tv_tgl)
        tv_tgl_rental = findViewById(R.id.tv_tgl_rental)
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

    private fun setPringatan(pesan: String) {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(this, DialogTypes.TYPE_WARNING)
                .setTitle("Oooppss")
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

    override fun onResume() {
        getBank()
        setSpinner()
        super.onResume()
    }
}