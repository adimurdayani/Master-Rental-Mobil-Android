package com.nurul.rentalmobil.ui.riwayat

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.nurul.rentalmobil.R
import com.nurul.rentalmobil.core.data.model.ResponseModel
import com.nurul.rentalmobil.core.data.model.Transaksi
import com.nurul.rentalmobil.core.data.source.ApiConfig
import com.nurul.rentalmobil.ui.riwayat.adapter.AdapterRiwayat
import com.nurul.rentalmobil.util.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatFragment : Fragment() {
    lateinit var sw_data: SwipeRefreshLayout
    lateinit var rc_data: RecyclerView
    lateinit var search: SearchView
    lateinit var total_list: TextView
    lateinit var img_nodata: ImageView
    lateinit var s: SharedPref
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_riwayat, container, false)
        s = SharedPref(requireActivity())
        setinit(view)
        setDisplay()
        return view
    }

    private var listRiwayat: ArrayList<Transaksi> = ArrayList()
    private fun setTransaksi() {

        if (s.getStatusLogin()){
            sw_data.isRefreshing = true
            val user = s.getUser()!!
            ApiConfig.instanceRetrofit.transaksi(user.id).enqueue(object : Callback<ResponseModel> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ResponseModel>,
                    response: Response<ResponseModel>,
                ) {
                    sw_data.isRefreshing = false
                    if (response.body() == null) {
                        img_nodata.visibility = View.VISIBLE
                        sw_data.visibility = View.GONE
                        sw_data.isRefreshing = false
                    } else {
                        img_nodata.visibility = View.GONE
                        sw_data.visibility = View.VISIBLE
                        val res = response.body()!!
                        if (res.status == 1) {
                            listRiwayat = res.list_transaksi
                            total_list.text = listRiwayat.size.toString() + " Invoice found"
                            setDisplay()
                        } else {
                            sw_data.isRefreshing = false
                            setError(res.message)
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
    }

    private fun setDisplay() {
        val data = LinearLayoutManager(activity)
        data.orientation = LinearLayoutManager.VERTICAL
        rc_data.setHasFixedSize(true)
        rc_data.adapter = AdapterRiwayat(requireActivity(), listRiwayat)
        rc_data.layoutManager = data

        sw_data.setOnRefreshListener {
            if (s.getStatusLogin()){
                setTransaksi()
            }
        }

        val adapter = AdapterRiwayat(requireActivity(), listRiwayat)
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
        sw_data = view.findViewById(R.id.sw_data)
        rc_data = view.findViewById(R.id.rc_data)
        search = view.findViewById(R.id.search)
        total_list = view.findViewById(R.id.total_list)
        img_nodata = view.findViewById(R.id.img_nodata)
    }

    override fun onResume() {
        if (s.getStatusLogin()){
            setTransaksi()
        }
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
        if (s.getStatusLogin()){
            setTransaksi()
        }
    }
}