package com.nurul.rentalmobil.core.data.room

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.nurul.rentalmobil.core.data.model.MobilList

@Dao
interface DaoKeranjang {

    @Insert(onConflict = REPLACE)
    fun insert(data: MobilList)

    @Delete
    fun delete(data: MobilList)

    @Delete
    fun delete(data: List<MobilList>)

    @Update
    fun update(data: MobilList): Int

    @Query("SELECT * from keranjang ORDER BY id ASC")
    fun getAll(): List<MobilList>

    @Query("SELECT * FROM keranjang WHERE id = :id LIMIT 1")
    fun getProduk(id: Int): MobilList

    @Query("DELETE FROM keranjang WHERE id = :id")
    fun deleteById(id: Int): Int

    @Query("DELETE FROM keranjang")
    fun deleteAll(): Int
}
