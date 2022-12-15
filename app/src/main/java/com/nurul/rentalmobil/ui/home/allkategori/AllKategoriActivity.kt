package com.nurul.rentalmobil.ui.home.allkategori

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.nurul.rentalmobil.R
import com.nurul.rentalmobil.core.data.model.Mobil
import com.nurul.rentalmobil.core.data.model.ResponseModel
import com.nurul.rentalmobil.core.data.source.ApiConfig
import com.nurul.rentalmobil.ui.home.adapter.AdapterMobil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllKategoriActivity : AppCompatActivity() {
    lateinit var btn_kembali: ImageView
    lateinit var nama: TextView
    lateinit var sw_data: SwipeRefreshLayout
    lateinit var rc_data: RecyclerView
    lateinit var animationView4: LottieAnimationView
    lateinit var search: SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_kategori)
        setinit()
        setDisplay()
        setButton()
    }

    private fun setButton() {
        btn_kembali.setOnClickListener {
            onBackPressed()
        }
    }

    private var listmobilkategori: ArrayList<Mobil> = ArrayList()
    private fun setDisplay() {
        nama.text = intent.getStringExtra("merk")
        val data2 = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rc_data.setHasFixedSize(true)
        rc_data.adapter = AdapterMobil(this, listmobilkategori)
        rc_data.layoutManager = data2

        sw_data.setOnRefreshListener {
            getAllkategori()
        }
        val adapter = AdapterMobil(this, listmobilkategori)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.getSearchData().filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }

    private fun getAllkategori() {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(this, DialogTypes.TYPE_LOADING)
                .setTitle("Loading...")
                .setDescription("Harap tunggu sebentar!")
                .build()
        alertDialog.setCancelable(false)
        alertDialog.show()
        sw_data.isRefreshing = true

        val id = intent.getIntExtra("merk_id", 0)
        ApiConfig.instanceRetrofit.mobil_kategori(id).enqueue(object : Callback<ResponseModel> {
            override fun onResponse(
                call: Call<ResponseModel>,
                response: Response<ResponseModel>,
            ) {
                alertDialog.dismiss()
                sw_data.isRefreshing = false
                animationView4.visibility = View.GONE
                if (response.body() == null) {
                    alertDialog.dismiss()
                    animationView4.visibility = View.VISIBLE
                    sw_data.isRefreshing = false
                } else {
                    animationView4.visibility = View.GONE
                    val res = response.body()!!
                    if (res.status == 1) {
                        listmobilkategori = res.kategori_mobil
                        setDisplay()
                    } else {
                        alertDialog.dismiss()
                        setError(res.message)
                        sw_data.isRefreshing = false
                    }
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                alertDialog.dismiss()
                sw_data.isRefreshing = false
                animationView4.visibility = View.GONE
                setError("Terjadi kesalahan koneksi!")
                Log.d("Response", "Error: " + t.message)
            }
        })
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

    private fun setinit() {
        btn_kembali = findViewById(R.id.btn_kembali)
        nama = findViewById(R.id.nama)
        rc_data = findViewById(R.id.rc_data)
        sw_data = findViewById(R.id.sw_data)
        animationView4 = findViewById(R.id.animationView4)
        search = findViewById(R.id.search)
    }

    override fun onResume() {
        super.onResume()
        getAllkategori()
    }
}