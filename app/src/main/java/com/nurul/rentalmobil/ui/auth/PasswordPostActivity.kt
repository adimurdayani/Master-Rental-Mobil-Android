package com.nurul.rentalmobil.ui.auth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.gson.Gson
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.nurul.rentalmobil.R
import com.nurul.rentalmobil.core.data.model.ResponseModel
import com.nurul.rentalmobil.core.data.model.User
import com.nurul.rentalmobil.core.data.source.ApiConfig
import com.nurul.rentalmobil.util.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordPostActivity : AppCompatActivity() {

    private lateinit var btn_kembali: ImageView
    private lateinit var password: EditText
    private lateinit var konfir_password: EditText
    private lateinit var btn_kirim: LinearLayout
    private lateinit var progress: ProgressBar
    private lateinit var text_kirim: TextView
    private lateinit var s: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_post)
        s = SharedPref(this)
        setInit()
        setButton()
    }

    private fun setButton() {
        btn_kembali.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btn_kirim.setOnClickListener {
            if (validasi()) {
                ubahPassword()
            }
        }
    }

    private fun ubahPassword() {
        val extra = intent.getStringExtra("extra")
        val json = Gson().fromJson(extra, User::class.java)
        val Tpassword = password.text.toString()

        progress.visibility = View.VISIBLE
        text_kirim.visibility = View.GONE
        ApiConfig.instanceRetrofit.ubahpassword(json.id, Tpassword)
            .enqueue(object : Callback<ResponseModel> {
                override fun onResponse(
                    call: Call<ResponseModel>,
                    response: Response<ResponseModel>
                ) {
                    progress.visibility = View.GONE
                    text_kirim.visibility = View.VISIBLE
                    val respon = response.body()!!
                    if (respon.status == 1) {
                        showSukses("Password anda berhasil diubah!")
                    } else {
                        progress.visibility = View.GONE
                        text_kirim.visibility = View.VISIBLE
                        showError(respon.message)
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    progress.visibility = View.GONE
                    text_kirim.visibility = View.VISIBLE
                    Log.d("Respon", "Pesan: " + t.message)
                    showError(t.message.toString())
                }
            })
    }

    private fun showSukses(pesan: String) {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(this, DialogTypes.TYPE_SUCCESS)
                .setTitle("Sukses")
                .setDescription(pesan)
                .setPositiveText("Oke")
                .setPositiveTextColor(Color.WHITE)
                .setPositiveButtonColor(Color.RED)
                .setPositiveListener(object : ClickListener {
                    override fun onClick(dialog: LottieAlertDialog) {
                        val intent = Intent(this@PasswordPostActivity, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                        dialog.dismiss()
                    }

                })
                .build()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun showError(pesan: String) {
        val alertDialog: LottieAlertDialog = LottieAlertDialog.Builder(this, DialogTypes.TYPE_ERROR)
            .setTitle("Something error")
            .setDescription(pesan)
            .setPositiveText("Oke")
            .setPositiveTextColor(Color.WHITE)
            .setPositiveButtonColor(Color.RED)
            .setPositiveListener(object : ClickListener {
                override fun onClick(dialog: LottieAlertDialog) {
                    dialog.dismiss()
                }

            })
            .build()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun validasi(): Boolean {
        if (password.text.toString().isEmpty()) {
            password.error = "Kolom password tidak boleh kosong!"
            password.requestFocus()
            return false
        } else if (password.text.toString().length < 6) {
            password.error = "Password tidak boleh kurang dari 6 karakter!"
            password.requestFocus()
            return false
        }
        if (konfir_password.text.toString().isEmpty()) {
            konfir_password.error = "Kolom konfirmasi password tidak boleh kosong!"
            konfir_password.requestFocus()
            return false
        } else if (konfir_password.text.toString().length < 6) {
            konfir_password.error = "Konfirmasi password tidak boleh kurang dari 6 karakter!"
            konfir_password.requestFocus()
            return false
        } else if (!konfir_password.text.toString().matches(password.text.toString().toRegex())) {
            konfir_password.error = "Konfirmasi password tidak sama dengan password"
            konfir_password.requestFocus()
            return false
        }
        return true
    }

    private fun setInit() {
        btn_kembali = findViewById(R.id.btn_kembali)
        btn_kirim = findViewById(R.id.btn_kirim)
        password = findViewById(R.id.password)
        konfir_password = findViewById(R.id.konfir_password)
        progress = findViewById(R.id.progress)
        text_kirim = findViewById(R.id.text_kirim)
    }
}