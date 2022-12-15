package com.nurul.rentalmobil.ui.keranjang.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.nurul.rentalmobil.R
import com.nurul.rentalmobil.core.data.model.MobilList
import com.nurul.rentalmobil.core.data.room.MyDatabase
import com.nurul.rentalmobil.util.Helper
import com.nurul.rentalmobil.util.Util
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AdapterKeranjang(
    var activity: Activity,
    var data: ArrayList<MobilList>,
    var listener: Listeners
) :
    RecyclerView.Adapter<AdapterKeranjang.HolderData>() {
    class HolderData(view: View) : RecyclerView.ViewHolder(view) {
        val layout = view.findViewById<CardView>(R.id.layout)
        val cek = view.findViewById<CheckBox>(R.id.cek)
        val image = view.findViewById<ImageView>(R.id.image)
        val nama_mobil = view.findViewById<TextView>(R.id.nama_mobil)
        val alamat = view.findViewById<TextView>(R.id.alamat)
        val total = view.findViewById<TextView>(R.id.total)
        val deskripsi = view.findViewById<TextView>(R.id.deskripsi)
        val btn_delete = view.findViewById<ImageView>(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_keranjang, parent, false)
        return HolderData(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HolderData, position: Int) {
        val mobil = data[position]
        val harga = Integer.valueOf(mobil.harga)

        holder.nama_mobil.text = mobil.nama_mobil
        holder.total.text =
            Helper().formatRupiah(harga).format(Integer.valueOf(mobil.harga)) + " /Hari"
        holder.alamat.text = mobil.alamat
        holder.deskripsi.text = "Harga sewa mobil " +
                mobil.harga + ", " +
                " Denda perhari " +
                mobil.denda + ", " +
                "Tahun keluar mobil " +
                mobil.tahun + ", " +
                " Warna mobil " +
                mobil.color + ", " +
                " Plat mobil " +
                mobil.no_plat

        Picasso.get()
            .load(Util.mobilUrl + mobil.image)
            .error(R.drawable.logo_open)
            .into(holder.image)

        holder.btn_delete.setOnClickListener {
            delete(mobil)
            listener.onDelete(position)
        }

        holder.cek.isChecked = mobil.selected
        holder.cek.setOnCheckedChangeListener { buttonView, isChecked ->
            mobil.selected = isChecked
            update(mobil)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface Listeners {
        fun onUpdate()
        fun onDelete(position: Int)
    }

    private fun update(mobilData: MobilList) {
        val myDb = MyDatabase.getInstance(activity)
        CompositeDisposable().add(Observable.fromCallable {
            myDb!!.daoKeranjang().update(mobilData)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                listener.onUpdate()
            })

    }

    private fun delete(mobilData: MobilList) {
        val myDb = MyDatabase.getInstance(activity)
        CompositeDisposable().add(Observable.fromCallable {
            myDb!!.daoKeranjang().delete(mobilData)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
            })

    }

}