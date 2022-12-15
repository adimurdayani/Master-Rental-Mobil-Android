package com.nurul.rentalmobil.ui.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.nurul.rentalmobil.HomeActivity
import com.nurul.rentalmobil.R
import com.nurul.rentalmobil.core.data.model.ResponseModel
import com.nurul.rentalmobil.core.data.source.ApiConfig
import com.nurul.rentalmobil.util.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var btn_kembali: ImageView
    lateinit var username: EditText
    lateinit var password: EditText
    lateinit var btn_login: LinearLayout
    lateinit var progress: ProgressBar
    lateinit var text_login: TextView
    lateinit var btn_register: TextView
    lateinit var btn_lupapassword: TextView
    lateinit var s: SharedPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        s = SharedPref(this)
        setInit()
        setButton()
    }

    private fun setButton() {
        btn_kembali.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        btn_register.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }

        btn_login.setOnClickListener {
            if (validasi()) {
                login()
            }
        }

        btn_lupapassword.setOnClickListener {
            startActivity(Intent(this, EmailPostActivity::class.java))
        }

    }

    private fun login() {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(this, DialogTypes.TYPE_LOADING)
                .setTitle("Loading...")
                .setDescription("Harap tunggu sebentar!")
                .build()
        alertDialog.setCancelable(false)
        alertDialog.show()

        val e_username = username.text.toString()
        val e_password = password.text.toString()

        progress.visibility = View.VISIBLE
        text_login.visibility = View.GONE
        ApiConfig.instanceRetrofit.login(e_username, e_password)
            .enqueue(object : Callback<ResponseModel> {
                override fun onResponse(
                    call: Call<ResponseModel>,
                    response: Response<ResponseModel>
                ) {
                    progress.visibility = View.GONE
                    text_login.visibility = View.VISIBLE
                    alertDialog.dismiss()
                    if (response.body() == null) {
                        progress.visibility = View.GONE
                        text_login.visibility = View.VISIBLE
                        alertDialog.dismiss()
                        showError("Username atau password anda salah!")
                    } else {
                        val respon = response.body()!!
                        if (respon.status == 1) {
                            s.setStatusLogin(true)
                            s.setUser(respon.data)
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            finish()
                        } else {
                            progress.visibility = View.GONE
                            text_login.visibility = View.VISIBLE
                            alertDialog.dismiss()
                            showError(respon.message)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    progress.visibility = View.GONE
                    text_login.visibility = View.VISIBLE
                    alertDialog.dismiss()
                    showError(t.message.toString())
                }
            })

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
        return true
    }

    private fun setInit() {
        btn_kembali = findViewById(R.id.btn_kembali)
        btn_lupapassword = findViewById(R.id.btn_lupapassword)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        btn_login = findViewById(R.id.btn_login)
        progress = findViewById(R.id.progress)
        text_login = findViewById(R.id.text_login)
        btn_register = findViewById(R.id.btn_register)
    }
}