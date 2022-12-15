package com.nurul.rentalmobil.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.github.drjacky.imagepicker.ImagePicker
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.nurul.rentalmobil.R
import com.nurul.rentalmobil.core.data.model.ResponseModel
import com.nurul.rentalmobil.core.data.source.ApiConfig
import com.nurul.rentalmobil.ui.auth.LoginActivity
import com.nurul.rentalmobil.ui.content.AlamatActivity
import com.nurul.rentalmobil.ui.content.BantuanActivity
import com.nurul.rentalmobil.ui.content.TentangActivity
import com.nurul.rentalmobil.ui.edit.UbahPassword
import com.nurul.rentalmobil.ui.edit.UbahProfile
import com.nurul.rentalmobil.util.SharedPref
import com.nurul.rentalmobil.util.Util
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileFragment : Fragment() {
    lateinit var img_user: ImageView
    lateinit var nama: TextView
    lateinit var phone: TextView
    lateinit var email: TextView
    lateinit var btn_ubahpassword: RelativeLayout
    lateinit var btn_ubahprofil: RelativeLayout
    lateinit var btn_alamat: RelativeLayout
    lateinit var btn_tentang: RelativeLayout
    lateinit var btn_bantuan: RelativeLayout
    lateinit var btn_logout: TextView
    lateinit var btn_upload: ImageView
    lateinit var s: SharedPref
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)
        s = SharedPref(requireActivity())
        setinit(view)
        setDisplay()
        setButton()
        return view
    }

    private fun setButton() {
        btn_logout.setOnClickListener {
            logout()
        }
        btn_ubahpassword.setOnClickListener {
            startActivity(Intent(requireContext(), UbahPassword::class.java))
        }
        btn_ubahprofil.setOnClickListener {
            startActivity(Intent(requireContext(), UbahProfile::class.java))
        }
        btn_alamat.setOnClickListener {
            startActivity(Intent(requireContext(), AlamatActivity::class.java))
        }
        btn_tentang.setOnClickListener {
            startActivity(Intent(requireContext(), TentangActivity::class.java))
        }
        btn_bantuan.setOnClickListener {
            startActivity(Intent(requireContext(), BantuanActivity::class.java))
        }
        btn_upload.setOnClickListener {
            imagePick()
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                // Use the uri to load the image
                Log.d("TAG", "URL Image: $uri")
                val fileUri: Uri = uri
                dialogUpload(File(fileUri.path))
            }
        }
    var alertDialog: AlertDialog? = null

    private fun dialogUpload(file: File) {
        val view = layoutInflater
        val layout = view.inflate(R.layout.upload_gambar, null)

        val imageView: ImageView = layout.findViewById(R.id.image)
        val btnUpload: LinearLayout = layout.findViewById(R.id.btn_upload)
        val btnGambar: LinearLayout = layout.findViewById(R.id.btn_gambarlain)

        Picasso.get()
            .load(file)
            .into(imageView)

        btnUpload.setOnClickListener {
            upload(file)
        }

        btnGambar.setOnClickListener {
            imagePick()
        }
        alertDialog = AlertDialog.Builder(requireContext()).create()
        alertDialog!!.setView(layout)
        alertDialog!!.setCancelable(true)
        alertDialog!!.show()
    }

    fun File?.toMultipartBody(name: String = "image"): MultipartBody.Part? {
        if (this == null) return null
        val reqFile: RequestBody = this.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(name, this.name, reqFile)
    }

    private fun upload(file: File) {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(requireContext(), DialogTypes.TYPE_ERROR)
                .setTitle("Loading...")
                .setDescription("Harap tunggu sebentar")
                .build()
        alertDialog.setCancelable(false)
        alertDialog.show()

        val fileImage = file.toMultipartBody()
        ApiConfig.instanceRetrofit.uploadFoto(s.getUser()!!.id, fileImage!!)
            .enqueue(object : Callback<ResponseModel> {
                override fun onResponse(
                    call: Call<ResponseModel>,
                    response: Response<ResponseModel>,
                ) {
                    alertDialog.dismiss()
                    if (response.body() == null) {
                        alertDialog.dismiss()
                        setError("Gambar tidak ditemukan")
                    } else {
                        val res = response.body()!!
                        if (res.status == 1) {
                            setSukses("Foto berhasil diupload!")
                        } else {
                            alertDialog.dismiss()
                            setError(res.message)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    alertDialog.dismiss()
                    setError("Terjadi kesalahan koneksi!")
                    Log.d("Response", "Error: " + t.message)
                }
            })
    }

    private fun imagePick() {
        ImagePicker.with(requireActivity())
            .crop()
            .maxResultSize(512, 512)
            .createIntentFromDialog { launcher.launch(it) }
    }

    private fun setSukses(pesan: String) {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(requireContext(), DialogTypes.TYPE_SUCCESS)
                .setTitle("Sukses")
                .setDescription(pesan)
                .setPositiveText("Oke")
                .setPositiveTextColor(Color.WHITE)
                .setPositiveListener(object : ClickListener {
                    override fun onClick(dialog: LottieAlertDialog) {
                        alertDialog!!.dismiss()
                        s.setStatusLogin(false)
                        val intent = Intent(requireActivity(), LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        requireActivity().finish()
                        dialog.dismiss()
                    }

                })
                .build()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun setError(pesan: String) {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(requireContext(), DialogTypes.TYPE_ERROR)
                .setTitle("Something error")
                .setDescription(pesan)
                .setPositiveText("Oke")
                .setPositiveTextColor(Color.WHITE)
                .setPositiveListener(object : ClickListener {
                    override fun onClick(dialog: LottieAlertDialog) {
                        alertDialog!!.dismiss()
                        dialog.dismiss()
                    }

                })
                .build()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun logout() {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(requireActivity(), DialogTypes.TYPE_ERROR)
                .setTitle("Apakah anda yakin ingin keluar?")
                .setPositiveText("Iya")
                .setPositiveTextColor(Color.WHITE)
                .setPositiveListener(object : ClickListener {
                    override fun onClick(dialog: LottieAlertDialog) {
                        s.setStatusLogin(false)
                        val intent = Intent(requireActivity(), LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        requireActivity().finish()
                        dialog.dismiss()
                    }
                })
                .setNegativeText("Tidak")
                .setNegativeTextColor(Color.WHITE)
                .setNegativeListener(object : ClickListener {
                    override fun onClick(dialog: LottieAlertDialog) {
                        dialog.dismiss()
                    }
                })
                .build()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun setDisplay() {
        if (s.getStatusLogin()) {
            val user = s.getUser()!!
            nama.text = user.nama
            phone.text = user.phone
            email.text = user.email
            Picasso.get()
                .load(Util.logouser + user.image)
                .error(R.drawable.ic_user2)
                .placeholder(R.drawable.ic_user2)
                .into(img_user)
        } else {
            nama.visibility = View.GONE
            phone.visibility = View.GONE
            email.visibility = View.GONE
            img_user.visibility = View.GONE
        }
    }

    private fun setinit(view: View) {
        img_user = view.findViewById(R.id.img_user)
        nama = view.findViewById(R.id.nama)
        phone = view.findViewById(R.id.phone)
        email = view.findViewById(R.id.email)
        btn_ubahpassword = view.findViewById(R.id.btn_ubahpassword)
        btn_ubahprofil = view.findViewById(R.id.btn_ubahprofil)
        btn_alamat = view.findViewById(R.id.btn_alamat)
        btn_tentang = view.findViewById(R.id.btn_tentang)
        btn_bantuan = view.findViewById(R.id.btn_bantuan)
        btn_logout = view.findViewById(R.id.btn_logout)
        btn_upload = view.findViewById(R.id.btn_upload)
    }
}