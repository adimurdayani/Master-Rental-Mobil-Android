package com.nurul.rentalmobil.ui.home.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nurul.rentalmobil.R
import com.nurul.rentalmobil.core.data.model.Merk
import com.nurul.rentalmobil.ui.home.allkategori.AllKategoriActivity

class AdapterKategori(var activity: Activity, var data: ArrayList<Merk>) :
    RecyclerView.Adapter<AdapterKategori.HolderData>() {
    class HolderData(view: View) : RecyclerView.ViewHolder(view) {
        val layout = view.findViewById<LinearLayout>(R.id.layout)
        val nama_kategori = view.findViewById<TextView>(R.id.nama_kategori)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_brand, parent, false)
        return HolderData(view)
    }

    override fun onBindViewHolder(holder: HolderData, position: Int) {
        val d = data[position]
        holder.nama_kategori.text = d.merk

        holder.layout.setOnClickListener {
            val intent = Intent(activity, AllKategoriActivity::class.java)
            intent.putExtra("merk_id", d.id)
            intent.putExtra("merk", d.merk)
            Log.d("REsponse", "id =" + d.id)
            activity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}