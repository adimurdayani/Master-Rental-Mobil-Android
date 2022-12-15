package com.nurul.rentalmobil.util

import android.annotation.SuppressLint
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class Helper {
    fun formatRupiah(string: String): String {
        return NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(Integer.valueOf(string))
    }

    fun formatRupiah(value: Int): String {
        return NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(value)
    }

    @SuppressLint("SimpleDateFormat")
    fun convertTanggal(
        tanggal: String,
        formatBaru: String,
        formatLama: String = "yyyy-MM-dd kk:mm:ss"
    ): String {

//        format tanggal
        val dateFormat = SimpleDateFormat(formatLama)
        val convertDate = dateFormat.parse(tanggal)
        dateFormat.applyPattern(formatBaru)
        return dateFormat.format(convertDate)
    }
}