package com.nurul.rentalmobil.ui.home.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.nurul.rentalmobil.R
import com.nurul.rentalmobil.core.data.model.Mobil
import com.nurul.rentalmobil.ui.detail.DetailMobilActivity
import com.nurul.rentalmobil.util.Helper
import com.nurul.rentalmobil.util.Util
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class AdapterMobil(var activity: Activity, var data: ArrayList<Mobil>) :
    RecyclerView.Adapter<AdapterMobil.HolderData>() {
    class HolderData(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.image)
        val nama_produk = view.findViewById<TextView>(R.id.nama_produk)
        val harga = view.findViewById<TextView>(R.id.harga)
        val status_mobil = view.findViewById<TextView>(R.id.status_mobil)
        val img_logo = view.findViewById<ImageView>(R.id.img_logo)
        val toko = view.findViewById<TextView>(R.id.toko)
        val layout = view.findViewById<CardView>(R.id.layout)
        val div_status = view.findViewById<LinearLayout>(R.id.div_status)
    }

    lateinit var contex: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
        contex = parent.context
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_produk, parent, false)
        return HolderData(view)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: HolderData, position: Int) {
        val m = data[position]
        holder.nama_produk.text = m.nama_mobil
        holder.status_mobil.text = m.status
        holder.harga.text = Helper().formatRupiah(m.harga) + "/hari"
        holder.toko.text = m.alamat

        var color = contex.getDrawable(R.drawable.bg_status_1)
        if (m.status == "tersedia") {
            color = contex.getDrawable(R.drawable.bg_status_1)
        } else if (m.status == "terpakai") {
            color = contex.getDrawable(R.drawable.bg_status_2)
        }
        holder.div_status.background = color

        Picasso.get()
            .load(Util.mobilUrl + m.image)
            .error(R.drawable.logo_open)
            .placeholder(R.drawable.logo_open)
            .into(holder.image)

        holder.layout.setOnClickListener {
            val intent = Intent(activity, DetailMobilActivity::class.java)
            val str = Gson().toJson(data[position], Mobil::class.java)
            intent.putExtra("extra", str)
            activity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private var searchData: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val searchList: java.util.ArrayList<Mobil> = java.util.ArrayList<Mobil>()
            if (constraint.toString().isEmpty()) {
                searchList.addAll(data)
            } else {
                for (getRekamMedik in data) {
                    if (getRekamMedik.nama_mobil.toLowerCase(Locale.ROOT)
                            .contains(constraint.toString().toLowerCase(Locale.ROOT))
                    ) {
                        searchList.add(getRekamMedik)
                    }
                }
            }
            val results = FilterResults()
            results.values = searchList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            data.clear()
            data.addAll(results.values as Collection<Mobil>)
            notifyDataSetChanged()
        }
    }

    fun getSearchData(): Filter {
        return searchData
    }
}