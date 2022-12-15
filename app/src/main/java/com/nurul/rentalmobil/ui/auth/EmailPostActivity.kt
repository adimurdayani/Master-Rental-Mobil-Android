package com.nurul.rentalmobil.ui.auth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
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

class EmailPostActivity : AppCompatActivity() {
    private lateinit var btn_kembali: ImageView
    private lateinit var btn_kirim: LinearLayout
    private lateinit var progress: ProgressBar
    private lateinit var text_kirim: TextView
    private lateinit var email: EditText
    private lateinit var s: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_post)
        s = SharedPref(this)
        setInit()
        setButton()
    }

    private fun setButton() {
        btn_kembali.setOnClickListener {
            onBackPressed()
        }
        btn_kirim.setOnClickListener {
            if (validasi()) {
                emailPost()
            }
        }
    }

    private fun emailPost() {
        val Temail = email.text.toString()

        progress.visibility = View.VISIBLE
        text_kirim.visibility = View.GONE
        ApiConfig.instanceRetrofit.emailPost(Temail)
            .enqueue(object : Callback<ResponseModel> {
                override fun onResponse(
                    call: Call<ResponseModel>,
                    response: Response<ResponseModel>
                ) {
                    progress.visibility = View.GONE
                    text_kirim.visibility = View.VISIBLE
                    if (response.body() == null) {
                        progress.visibility = View.GONE
                        text_kirim.visibility = View.VISIBLE
                        showError("Data anda masih belum lengkap!")
                    } else {
                        val respon = response.body()!!
                        if (respon.status == 1) {
                            val json = Gson().toJson(respon.data, User::class.java)
                            val intent =
                                Intent(this@EmailPostActivity, PasswordPostActivity::class.java)
                            intent.putExtra("extra", json)
                            startActivity(intent)
                            showSukses("Sukses!")
                        } else {
                            progress.visibility = View.GONE
                            text_kirim.visibility = View.VISIBLE
                            showError(respon.message)
                        }
                    }

                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    progress.visibility = View.GONE
                    text_kirim.visibility = View.VISIBLE
                    Log.d("Respon", "Pesan: " + t.message.toString())
                    showError(t.message.toString())
                }
            })
    }

    private fun showSukses(pesan: String) {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(this, DialogTypes.TYPE_SUCCESS)
                .setTitle("Sukses")
                .setDescription(pesan)
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
        if (email.text.toString().isEmpty()) {
            email.error = "Kolom email tidak boleh kosong!"
            email.requestFocus()
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            email.error = "Format email salah!. Contoh: gunakan @example.com"
            email.requestFocus()
            return false
        }
        return true
    }

    private fun setInit() {
        btn_kembali = findViewById(R.id.btn_kembali)
        btn_kirim = findViewById(R.id.btn_kirim)
        progress = findViewById(R.id.progress)
        text_kirim = findViewById(R.id.text_kirim)
        email = findViewById(R.id.email)
    }
}