package com.nurul.rentalmobil.ui.keranjang

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.nurul.rentalmobil.R
import com.nurul.rentalmobil.core.data.model.Mobil
import com.nurul.rentalmobil.core.data.model.MobilList
import com.nurul.rentalmobil.core.data.room.MyDatabase
import com.nurul.rentalmobil.ui.auth.LoginActivity
import com.nurul.rentalmobil.ui.keranjang.adapter.AdapterKeranjang
import com.nurul.rentalmobil.ui.metode.MetodePembayaran
import com.nurul.rentalmobil.util.Helper
import com.nurul.rentalmobil.util.SharedPref
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class KeranjangFragment : Fragment() {
    lateinit var btn_delete: ImageView
    lateinit var img_nodata: ImageView
    lateinit var btn_bayar: LinearLayout
    lateinit var total_harga: TextView
    lateinit var rc_data: RecyclerView
    lateinit var cekall: CheckBox
    lateinit var myDb: MyDatabase
    lateinit var s: SharedPref
    lateinit var adapter: AdapterKeranjang
    lateinit var sw_data: SwipeRefreshLayout
    var listMobil = ArrayList<MobilList>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_keranjang, container, false)
        myDb = MyDatabase.getInstance(requireActivity())!!
        s = SharedPref(requireActivity())
        setinit(view)
        setButton()
        return view
    }

    private fun setDisplay() {
        if (s.getStatusLogin()) {
            sw_data.isRefreshing = true
            listMobil = myDb.daoKeranjang().getAll() as ArrayList

            val layoutManager = LinearLayoutManager(activity)
            layoutManager.orientation = LinearLayoutManager.VERTICAL

            adapter = AdapterKeranjang(
                requireActivity(),
                listMobil,
                object : AdapterKeranjang.Listeners {
                    override fun onUpdate() {
                        hitungTotal()
                    }

                    override fun onDelete(position: Int) {
                        listMobil.removeAt(position)
                        adapter.notifyDataSetChanged()
                        hitungTotal()
                    }

                })
            sw_data.isRefreshing = false
            rc_data.adapter = adapter
            rc_data.layoutManager = layoutManager
        }

        if (listMobil.size == 0) {
            img_nodata.visibility = View.VISIBLE
        } else {
            img_nodata.visibility = View.GONE
        }
    }

    var totalHarga = 0

    @SuppressLint("SetTextI18n")
    fun hitungTotal() {
        val listMobil = myDb.daoKeranjang().getAll() as ArrayList
        totalHarga = 0

        var isSelectedAll = true
        for (mobil in listMobil) {
            if (mobil.selected) {
                val harga = Integer.valueOf(mobil.harga)
                totalHarga += (harga * mobil.jumlah)
            } else {
                isSelectedAll = false
            }
        }

        cekall.isChecked = isSelectedAll
        total_harga.text = Helper().formatRupiah(totalHarga) + " /Hari"
    }

    private fun setButton() {
        btn_bayar.setOnClickListener {
            bayar()
        }
        btn_delete.setOnClickListener {
            hapus()
        }

        cekall.setOnClickListener {
            for (i in listMobil.indices) {
                val mobil = listMobil[i]
                mobil.selected = cekall.isChecked
                listMobil[i] = mobil
            }
            adapter.notifyDataSetChanged()
        }

        sw_data.setOnRefreshListener {
            setDisplay()
        }
    }

    private fun bayar() {
        var jml_selecd = 0
        var toko_id = 0
        var mobil_id = 0
        var denda = ""
        var harga_mobil = 0

        if (s.getStatusLogin()) {
            var isThereProduk = false
            for (p in listMobil) {
                if (p.selected) {
                    isThereProduk = true
                    jml_selecd++
                    toko_id = p.toko_id
                    mobil_id = p.id
                    denda = p.denda
                    harga_mobil = p.harga
                }
            }

            val mobil = Mobil()
            mobil.toko_id = toko_id
            mobil.id = mobil_id
            mobil.denda = denda
            mobil.harga = harga_mobil

            if (jml_selecd > 1) {
                setPringatan("Maaf, anda hanya bisa melakukan satukali transaksi dalam satu mobil yang akan digunakan.")
            } else {
                if (isThereProduk) {
                    val json = Gson().toJson(mobil, Mobil::class.java)
                    val intent = Intent(activity, MetodePembayaran::class.java)
                    intent.putExtra("jml_mobil", "" + jml_selecd)
                    intent.putExtra("extra", json)
                    Log.d("Response", json)
                    startActivity(intent)
                } else {
                    setError("Tidak ada produk yang pilih")
                }
            }
        } else {
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
        }
    }

    private fun hapus() {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(requireContext(), DialogTypes.TYPE_QUESTION)
                .setTitle("Apakah anda yakin ingin?")
                .setDescription("Data anda akan terhapus secara permanen!")
                .setPositiveText("Hapus")
                .setPositiveTextColor(Color.WHITE)
                .setPositiveListener(object : ClickListener {
                    override fun onClick(dialog: LottieAlertDialog) {
                        val listDelete = ArrayList<MobilList>()
                        for (p in listMobil) {
                            if (p.selected) listDelete.add(p)
                        }
                        delete(listDelete)
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

    private fun delete(listDelete: java.util.ArrayList<MobilList>) {
        CompositeDisposable().add(Observable.fromCallable {
            myDb.daoKeranjang().delete(listDelete)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                listMobil.clear()
                listMobil.addAll(myDb.daoKeranjang().getAll() as ArrayList)
                adapter.notifyDataSetChanged()
            })
    }

    private fun setError(pesan: String) {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(requireContext(), DialogTypes.TYPE_ERROR)
                .setTitle("Ooops...")
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
            LottieAlertDialog.Builder(requireContext(), DialogTypes.TYPE_WARNING)
                .setTitle("Ooops...")
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
        btn_delete = view.findViewById(R.id.btn_delete)
        btn_bayar = view.findViewById(R.id.btn_bayar)
        total_harga = view.findViewById(R.id.total_harga)
        rc_data = view.findViewById(R.id.rc_data)
        cekall = view.findViewById(R.id.cekall)
        sw_data = view.findViewById(R.id.sw_data)
        img_nodata = view.findViewById(R.id.img_nodata)
    }

    override fun onResume() {
        setDisplay()
        hitungTotal()
        super.onResume()
    }
}