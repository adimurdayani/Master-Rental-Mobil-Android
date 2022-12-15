package com.nurul.rentalmobil.core.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "keranjang")
public class MobilList {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idTb")
    public int idTb;

    public String color;
    public String created_at;
    public String denda;
    public int harga = 0;
    public int id;
    public String image;
    public String merk;
    public int merk_id;
    public String nama;
    public String nama_mobil;
    public String no_plat;
    public String status;
    public String tahun;
    public String alamat;
    public String image_user;
    public String nama_toko;
    public String phone;
    public int toko_id;

    public int jumlah = 1;
    public boolean selected = true;
}
