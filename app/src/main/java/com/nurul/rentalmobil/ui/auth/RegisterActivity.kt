package com.nurul.rentalmobil.ui.auth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.nurul.rentalmobil.R
import com.nurul.rentalmobil.core.data.model.ResponseModel
import com.nurul.rentalmobil.core.data.source.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    lateinit var btn_kembali: ImageView
    lateinit var nama: EditText
    lateinit var email: EditText
    lateinit var phone: EditText
    lateinit var username: EditText
    lateinit var password: EditText
    lateinit var konfir_password: EditText
    lateinit var btn_register: LinearLayout
    lateinit var progress: ProgressBar
    lateinit var text_register: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setInit()
        setButton()
    }

    private fun setButton() {
        btn_kembali.setOnClickListener { onBackPressed() }
        btn_register.setOnClickListener {
            if (validasi()) {
                register()
            }
        }
    }

    private fun register() {
        val Tnama = nama.text.toString()
        val Tusername = username.text.toString()
        val Temail = email.text.toString()
        val Tphone = phone.text.toString()
        val Tpassword = password.text.toString()

        progress.visibility = View.VISIBLE
        text_register.visibility = View.GONE
        ApiConfig.instanceRetrofit.register(Tnama, Tusername, Temail, Tphone, Tpassword)
            .enqueue(object : Callback<ResponseModel> {
                override fun onResponse(
                    call: Call<ResponseModel>,
                    response: Response<ResponseModel>
                ) {
                    progress.visibility = View.GONE
                    text_register.visibility = View.VISIBLE
                    if (response.body() == null){
                        progress.visibility = View.GONE
                        text_register.visibility = View.VISIBLE
                        showError("Harap lengkapi data anda!")
                    }else{
                        val respon = response.body()!!
                        if (respon.status == 1) {
                            showSukses("Anda telah berhasil registrasi, klik tombol untuk login!")
                        } else {
                            progress.visibility = View.GONE
                            text_register.visibility = View.VISIBLE
                            showError(respon.message)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    progress.visibility = View.GONE
                    text_register.visibility = View.VISIBLE
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
                .setPositiveText("Login")
                .setPositiveTextColor(Color.WHITE)
                .setPositiveButtonColor(Color.RED)
                .setPositiveListener(object : ClickListener {
                    override fun onClick(dialog: LottieAlertDialog) {
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
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
        if (nama.text.toString().isEmpty()) {
            nama.error = "Kolom nama tidak boleh kosong!"
            nama.requestFocus()
            return false
        }
        if (email.text.toString().isEmpty()) {
            email.error = "Kolom email tidak boleh kosong!"
            email.requestFocus()
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            email.error = "Format email salah!. Contoh: gunakan @example.com"
            email.requestFocus()
            return false
        }
        if (username.text.toString().isEmpty()) {
            username.error = "Kolom username tidak boleh kosong!"
            username.requestFocus()
            return false
        }
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
        nama = findViewById(R.id.nama)
        email = findViewById(R.id.email)
        phone = findViewById(R.id.phone)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        konfir_password = findViewById(R.id.konfir_password)
        btn_register = findViewById(R.id.btn_register)
        progress = findViewById(R.id.progress)
        text_register = findViewById(R.id.text_register)
    }
}