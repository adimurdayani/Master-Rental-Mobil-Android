package com.nurul.rentalmobil.ui.metode.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nurul.rentalmobil.R
import com.nurul.rentalmobil.core.data.model.Rekening
import com.nurul.rentalmobil.util.Util
import com.squareup.picasso.Picasso
import kotlin.collections.ArrayList

class AdapterBank(
    var data: ArrayList<Rekening>,
    var listener: Listeners
) :
    RecyclerView.Adapter<AdapterBank.HolderData>() {
    class HolderData(view: View) : RecyclerView.ViewHolder(view) {
        val img_bank = view.findViewById<ImageView>(R.id.img_bank)
        val bank = view.findViewById<TextView>(R.id.nama_bank)
        val nama_penerima = view.findViewById<TextView>(R.id.nama_penerima)
        val nomo_rekening = view.findViewById<TextView>(R.id.nomo_rekening)
        val rb_pengiriman = view.findViewById<RadioButton>(R.id.rb_pengiriman)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_rekening, parent, false)
        return HolderData(view)
    }

    override fun onBindViewHolder(holder: HolderData, position: Int) {
        val a = data[position]

        holder.bank.text = a.nama_bank
        holder.nama_penerima.text = a.nama
        holder.nomo_rekening.text = a.rekening
        holder.rb_pengiriman.isChecked = a.isActive

        Picasso.get()
            .load(Util.logobank + a.image_bank)
            .error(R.drawable.bri)
            .placeholder(R.drawable.bri)
            .into(holder.img_bank)

        holder.rb_pengiriman.setOnClickListener {
            a.isActive = true
            listener.onClicked(a, holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface Listeners {
        fun onClicked(data: Rekening, index: Int)
    }
}