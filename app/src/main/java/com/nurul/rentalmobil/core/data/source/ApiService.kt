package com.nurul.rentalmobil.core.data.source

import com.nurul.rentalmobil.core.data.model.ResponseModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("auth/register")
    fun register(
        @Field("nama") nama: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("password") password: String
//        @Field("fcm") fcmString: String
    ): Call<ResponseModel>

    @FormUrlEncoded
    @POST("auth/email_user")
    fun emailPost(
        @Field("email") email: String
    ): Call<ResponseModel>

    @FormUrlEncoded
    @POST("auth/login")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String,
//        @Field("fcm") fcmString: String
    ): Call<ResponseModel>

    @FormUrlEncoded
    @POST("user/password")
    fun ubahpassword(
        @Field("id") id: Int,
        @Field("password") password: String,
    ): Call<ResponseModel>

    @FormUrlEncoded
    @POST("user/profile/")
    fun ubahprofile(
        @Field("id") id: Int,
        @Field("nama") nama: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("alamat") alamat: String,
        @Field("nik") nik: String,
        @Field("kelamin") kelamin: String
    ): Call<ResponseModel>

    @FormUrlEncoded
    @POST("transaksi")
    fun kirimTransaksi(
        @Field("kostumer_id") kostumer_id: Int,
        @Field("mobil_id") mobil_id: Int,
        @Field("toko_id") toko_id: Int,
        @Field("tgl_rental") tgl_rental: String,
        @Field("tgl_kembali") tgl_kembali: String,
        @Field("harga") harga: String,
        @Field("denda") denda: String,
        @Field("bank") bank: String,
        @Field("no_rekening") no_rekening: String,
        @Field("supir_id") supir_id: Int? = 0,
        @Field("harga_supir") harga_supir: Int? = 0
    ): Call<ResponseModel>

    @FormUrlEncoded
    @POST("transaksi/batal")
    fun batalTransaksi(
        @Field("id") id: Int,
        @Field("mobil_id") mobil_id: Int
    ): Call<ResponseModel>

    @Multipart
    @POST("transaksi/upload/{id}")
    fun buktiTransfer(
        @Path("id") id: Int,
        @Part bukti_transfer: MultipartBody.Part
    ): Call<ResponseModel>

    @Multipart
    @POST("user/upload/{id}")
    fun uploadFoto(
        @Path("id") id: Int,
        @Part image_user: MultipartBody.Part
    ): Call<ResponseModel>

    @GET("kategori")
    fun kategori(): Call<ResponseModel>

    @GET("transaksi/{kostumer_id}")
    fun transaksi(
        @Path("kostumer_id") kostumer_id: Int
    ): Call<ResponseModel>

    @GET("supir/{user_id}")
    fun supir(
        @Path("user_id") user_id: Int
    ): Call<ResponseModel>

    @GET("mobil")
    fun mobil(): Call<ResponseModel>

    @GET("mobil/limit")
    fun mobil_limit(): Call<ResponseModel>

    @GET("mobil/kategori/{merk_id}")
    fun mobil_kategori(
        @Path("merk_id") merk_id: Int
    ): Call<ResponseModel>

    @GET("rekening/{user_id}")
    fun rekening(
        @Path("user_id") user_id: Int
    ): Call<ResponseModel>
}