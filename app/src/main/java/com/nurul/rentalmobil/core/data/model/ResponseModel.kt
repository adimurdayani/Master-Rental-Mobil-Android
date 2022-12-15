package com.nurul.rentalmobil.core.data.model

class ResponseModel {
    var status = 0
    var message = ""
    var data = User()
    var kategori: ArrayList<Merk> = ArrayList()
    var mobil_limit: ArrayList<Mobil> = ArrayList()
    var kategori_mobil: ArrayList<Mobil> = ArrayList()
    var mobil: ArrayList<Mobil> = ArrayList()
    var rekening: ArrayList<Rekening> = ArrayList()
    var list_transaksi: ArrayList<Transaksi> = ArrayList()
    var transaksi = Transaksi()
    var list_supir : ArrayList<Supir> = ArrayList()
}
