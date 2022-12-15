package com.nurul.rentalmobil.ui.riwayat.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.nurul.rentalmobil.R
import com.nurul.rentalmobil.core.data.model.Transaksi
import com.nurul.rentalmobil.ui.riwayat.DetailRiwayat
import com.nurul.rentalmobil.util.Helper
import java.util.*
import kotlin.collections.ArrayList

class AdapterRiwayat(var activity: Activity, var data: ArrayList<Transaksi>) :
    RecyclerView.Adapter<AdapterRiwayat.HolderData>() {
    class HolderData(view: View) : RecyclerView.ViewHolder(view) {
        val nama = view.findViewById<TextView>(R.id.nama)
        val harga = view.findViewById<TextView>(R.id.harga)
        val tanggal_rental = view.findViewById<TextView>(R.id.tanggal_rental)
        val tanggal_kembali = view.findViewById<TextView>(R.id.tanggal_kembali)
        val status = view.findViewById<TextView>(R.id.status)
        val detail = view.findViewById<TextView>(R.id.detail)
        val tgl_pembayaran = view.findViewById<TextView>(R.id.tgl_pembayaran)

    }

    lateinit var contex: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
        contex = parent.context
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_riwayat, parent, false)
        return HolderData(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HolderData, position: Int) {
        val t = data[position]
        holder.nama.text = t.nama
        if (t.jumlah.toInt() == 0) {
            val harga_total = t.harga + t.harga_supir
            holder.harga.text = Helper().formatRupiah(harga_total)
        } else {
            holder.harga.text = Helper().formatRupiah(t.jumlah)
        }
        holder.tanggal_rental.text = t.tgl_rental
        holder.tanggal_kembali.text = t.tgl_kembali
        holder.status.text = t.status

        var color = contex.getColor(R.color.blue1)
        if (t.status == "diproses") {
            color = contex.getColor(R.color.blue1)
        } else if (t.status == "dibayar") {
            color = contex.getColor(R.color.orange)
        } else if (t.status == "dibatalkan") {
            color = contex.getColor(R.color.red)
        } else if (t.status == "selesai") {
            color = contex.getColor(R.color.green)
        }
        holder.status.setTextColor(color)

        holder.detail.setOnClickListener {
            val intent = Intent(activity, DetailRiwayat::class.java)
            val json = Gson().toJson(t, Transaksi::class.java)
            intent.putExtra("extra", json)
            activity.startActivity(intent)
        }

        if (t.bukti_transfer == "") {
            holder.tgl_pembayaran.text =
                "Mobil dapat diambil jika pembayaran telah dilakukan. Batas pengambilan mobil " + t.tgl_kembali
        } else {
            holder.tgl_pembayaran.text =
                "Anda dapat mengambil mobil rental pada tanggal: " + t.tgl_rental + ", jam: " + t.jam_ambil
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private var searchData: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val searchList: ArrayList<Transaksi> = ArrayList<Transaksi>()
            if (constraint.toString().isEmpty()) {
                searchList.addAll(data)
            } else {
                for (getdata in data) {
                    if (getdata.nama.toLowerCase(Locale.ROOT)
                            .contains(constraint.toString().toLowerCase(Locale.ROOT)) ||
                        getdata.tgl_rental.toLowerCase(Locale.ROOT)
                            .contains(constraint.toString().toLowerCase(Locale.ROOT)) ||
                        getdata.tgl_kembali.toLowerCase(Locale.ROOT)
                            .contains(constraint.toString().toLowerCase(Locale.ROOT)) ||
                        getdata.status.toLowerCase(Locale.ROOT)
                            .contains(constraint.toString().toLowerCase(Locale.ROOT))
                    ) {
                        searchList.add(getdata)
                    }
                }
            }
            val results = FilterResults()
            results.values = searchList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            data.clear()
            data.addAll(results.values as Collection<Transaksi>)
            notifyDataSetChanged()
        }
    }

    fun getSearchData(): Filter {
        return searchData
    }
}