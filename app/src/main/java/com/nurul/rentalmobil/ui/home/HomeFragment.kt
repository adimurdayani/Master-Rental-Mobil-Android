package com.nurul.rentalmobil.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.nurul.rentalmobil.R
import com.nurul.rentalmobil.core.data.model.Merk
import com.nurul.rentalmobil.core.data.model.Mobil
import com.nurul.rentalmobil.core.data.model.ResponseModel
import com.nurul.rentalmobil.core.data.source.ApiConfig
import com.nurul.rentalmobil.ui.home.adapter.AdapterKategori
import com.nurul.rentalmobil.ui.home.adapter.AdapterMobil
import com.nurul.rentalmobil.ui.home.all.AllMobil
import com.nurul.rentalmobil.util.SharedPref
import com.nurul.rentalmobil.util.Util
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    lateinit var rc_data1: RecyclerView
    lateinit var btn_viewall2: TextView
    lateinit var rc_data2: RecyclerView
    lateinit var sw_data: SwipeRefreshLayout
    lateinit var img_user: ImageView
    lateinit var nodata: ImageView
    lateinit var div_search: LinearLayout
    lateinit var div_kategori: RelativeLayout
    lateinit var div_mobil: RelativeLayout
    lateinit var text_show: TextView
    lateinit var alamat: TextView
    lateinit var s: SharedPref
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        s = SharedPref(requireActivity());
        setinit(view)
        setDisplay()

        return view
    }

    private var listkategori: ArrayList<Merk> = ArrayList()
    private var listmobillimit: ArrayList<Mobil> = ArrayList()
    private fun setDisplay() {
        val data1 = LinearLayoutManager(activity)
        data1.orientation = LinearLayoutManager.HORIZONTAL
        rc_data1.setHasFixedSize(true)
        rc_data1.adapter = AdapterKategori(requireActivity(), listkategori)
        rc_data1.layoutManager = data1

        val data2 = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rc_data2.setHasFixedSize(true)
        rc_data2.adapter = AdapterMobil(requireActivity(), listmobillimit)
        rc_data2.layoutManager = data2

        sw_data.setOnRefreshListener {
            setLimitMobil()
        }

        if (s.getStatusLogin()) {
            val user = s.getUser()!!
            Picasso.get()
                .load(Util.logouser + user.image)
                .error(R.drawable.ic_user2)
                .placeholder(R.drawable.ic_user2)
                .into(img_user)
            alamat.text = user.alamat
        }

        div_search.setOnClickListener {
            startActivity(Intent(requireContext(), AllMobil::class.java))
        }
        btn_viewall2.setOnClickListener {
            startActivity(Intent(requireContext(), AllMobil::class.java))
        }

    }

    private fun setKategori() {

        sw_data.isRefreshing = true
        ApiConfig.instanceRetrofit.kategori().enqueue(object : Callback<ResponseModel> {
            override fun onResponse(
                call: Call<ResponseModel>,
                response: Response<ResponseModel>,
            ) {
                sw_data.isRefreshing = false
                if (response.body() == null) {
                    div_kategori.visibility = View.GONE
                    text_show.visibility = View.VISIBLE
                    nodata.visibility = View.VISIBLE
                    sw_data.isRefreshing = false
                } else {
                    val res = response.body()!!
                    if (res.status == 1) {
                        listkategori = res.kategori
                        setDisplay()
                    } else {
                        setError(res.message)
                        sw_data.isRefreshing = false
                    }
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                sw_data.isRefreshing = false
                setError("Terjadi kesalahan koneksi!")
                Log.d("Response", "Error: " + t.message)
            }
        })
    }

    private fun setLimitMobil() {
        sw_data.isRefreshing = true

        ApiConfig.instanceRetrofit.mobil_limit().enqueue(object : Callback<ResponseModel> {
            override fun onResponse(
                call: Call<ResponseModel>,
                response: Response<ResponseModel>,
            ) {
                sw_data.isRefreshing = false
                if (response.body() == null) {
                    sw_data.isRefreshing = false
                    div_mobil.visibility = View.GONE
                    text_show.visibility = View.VISIBLE
                    nodata.visibility = View.VISIBLE
                } else {
                    val res = response.body()!!
                    if (res.status == 1) {
                        listmobillimit = res.mobil_limit
                        setDisplay()
                    } else {
                        setError(res.message)
                        sw_data.isRefreshing = false
                    }
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                sw_data.isRefreshing = false
                setError("Terjadi kesalahan koneksi!")
                Log.d("Response", "Error: " + t.message)
            }
        })
    }

    private fun setError(pesan: String) {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(requireActivity(), DialogTypes.TYPE_ERROR)
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

    private fun setinit(view: View) {
        rc_data1 = view.findViewById(R.id.rc_data1)
        btn_viewall2 = view.findViewById(R.id.btn_viewall2)
        rc_data2 = view.findViewById(R.id.rc_data2)
        sw_data = view.findViewById(R.id.sw_data)
        img_user = view.findViewById(R.id.img_user)
        div_search = view.findViewById(R.id.div_search)
        div_kategori = view.findViewById(R.id.div_kategori)
        div_mobil = view.findViewById(R.id.div_mobil)
        text_show = view.findViewById(R.id.text_show)
        nodata = view.findViewById(R.id.nodata)
        alamat = view.findViewById(R.id.alamat)
    }

    override fun onResume() {
        super.onResume()
        setKategori()
        setLimitMobil()

    }

    override fun onStart() {
        super.onStart()
        setKategori()
        setLimitMobil()
    }
}