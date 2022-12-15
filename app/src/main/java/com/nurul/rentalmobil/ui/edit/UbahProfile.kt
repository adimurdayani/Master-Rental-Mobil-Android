package com.nurul.rentalmobil.ui.edit

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.nurul.rentalmobil.R
import com.nurul.rentalmobil.core.data.model.ResponseModel
import com.nurul.rentalmobil.core.data.source.ApiConfig
import com.nurul.rentalmobil.ui.auth.LoginActivity
import com.nurul.rentalmobil.util.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UbahProfile : AppCompatActivity() {
    lateinit var btn_kembali: ImageView
    lateinit var l_name: TextInputLayout
    lateinit var e_name: TextInputEditText
    lateinit var l_email: TextInputLayout
    lateinit var e_email: TextInputEditText
    lateinit var l_phone: TextInputLayout
    lateinit var e_phone: TextInputEditText
    lateinit var l_username: TextInputLayout
    lateinit var e_username: TextInputEditText
    lateinit var btn_simpan: LinearLayout
    lateinit var progress: ProgressBar
    lateinit var txt_simpan: TextView
    lateinit var s: SharedPref
    lateinit var l_nik: TextInputLayout
    lateinit var e_nik: TextInputEditText
    lateinit var kelamin: Spinner
    lateinit var l_alamat: TextInputLayout
    lateinit var e_alamat: TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_profile)
        s = SharedPref(this)
        setinit()
        setButton()
        cekvalidasi()
        setSpinner()
    }

    var j_kelamin = ""
    private fun setSpinner() {
        kelamin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d("Response", "Kelamin: " + kelamin.selectedItem.toString())
                j_kelamin = kelamin.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun setButton() {
        btn_kembali.setOnClickListener {
            onBackPressed()
        }
        btn_simpan.setOnClickListener {
            if (validasi()) {
                simpan()
            }
        }
    }

    private fun simpan() {
        val id = s.getUser()!!.id
        val Tnama = e_name.text.toString()
        val Temail = e_email.text.toString()
        val Tusername = e_username.text.toString()
        val Tphone = e_phone.text.toString()
        val Talamat = e_alamat.text.toString()
        val Tnik = e_nik.text.toString()

        progress.visibility = View.VISIBLE
        txt_simpan.visibility = View.GONE
        ApiConfig.instanceRetrofit.ubahprofile(
            id,
            Tnama,
            Tusername,
            Temail,
            Tphone,
            Talamat,
            Tnik,
            j_kelamin
        )
            .enqueue(object : Callback<ResponseModel> {
                override fun onResponse(
                    call: Call<ResponseModel>,
                    response: Response<ResponseModel>
                ) {
                    progress.visibility = View.GONE
                    txt_simpan.visibility = View.VISIBLE
                    if (response.body() == null){
                        progress.visibility = View.GONE
                        txt_simpan.visibility = View.VISIBLE
                        showError("Tolong lengkapi data profile anda!")
                    }else{
                        val respon = response.body()!!
                        if (respon.status == 1) {
                            showSukses("Profile anda berhasil diupdate!")
                        } else {
                            progress.visibility = View.GONE
                            txt_simpan.visibility = View.VISIBLE
                            showError(respon.message)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    progress.visibility = View.GONE
                    txt_simpan.visibility = View.VISIBLE
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
                        s.setStatusLogin(false)
                        val intent = Intent(this@UbahProfile, LoginActivity::class.java)
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

    private fun cekvalidasi() {
        e_name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_name.text.toString().isEmpty()) {
                    l_name.isErrorEnabled = false
                } else if (e_name.text.toString().isNotEmpty()) {
                    l_name.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        e_email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_email.text.toString().isEmpty()) {
                    l_email.isErrorEnabled = false
                } else if (Patterns.EMAIL_ADDRESS.matcher(e_email.text.toString()).matches()) {
                    l_email.isErrorEnabled = false
                } else if (e_email.text.toString().isNotEmpty()) {
                    l_email.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        e_username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_username.text.toString().isEmpty()) {
                    l_username.isErrorEnabled = false
                } else if (e_username.text.toString().isNotEmpty()) {
                    l_username.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        e_phone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_phone.text.toString().isEmpty()) {
                    l_phone.isErrorEnabled = false
                } else if (e_phone.text.toString().isNotEmpty()) {
                    l_phone.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun validasi(): Boolean {
        if (e_nik.text.toString().isEmpty()) {
            l_nik.isErrorEnabled = true
            l_nik.error = "Kolom NIK tidak boleh kosong!"
            e_nik.requestFocus()
            return false
        }
        if (e_name.text.toString().isEmpty()) {
            l_name.isErrorEnabled = true
            l_name.error = "Kolom nama tidak boleh kosong!"
            e_name.requestFocus()
            return false
        }
        if (e_email.text.toString().isEmpty()) {
            l_email.isErrorEnabled = true
            l_email.error = "Kolom email tidak boleh kosong!"
            e_email.requestFocus()
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(e_email.text.toString()).matches()) {
            l_email.isErrorEnabled = true
            l_email.error = "Format email salah!. Contoh: gunakan @example.com"
            e_email.requestFocus()
            return false
        }
        if (e_username.text.toString().isEmpty()) {
            l_username.isErrorEnabled = true
            l_username.error = "Kolom username tidak boleh kosong!"
            e_username.requestFocus()
            return false
        }
        if (e_phone.text.toString().isEmpty()) {
            l_phone.isErrorEnabled = true
            l_phone.error = "Kolom phone tidak boleh kosong!"
            e_phone.requestFocus()
            return false
        }
        if (e_alamat.text.toString().isEmpty()) {
            l_alamat.isErrorEnabled = true
            l_alamat.error = "Kolom alamat tidak boleh kosong!"
            e_alamat.requestFocus()
            return false
        }
        return true
    }

    private fun setinit() {
        btn_kembali = findViewById(R.id.btn_kembali)
        l_name = findViewById(R.id.l_name)
        e_name = findViewById(R.id.e_name)
        l_email = findViewById(R.id.l_email)
        e_email = findViewById(R.id.e_email)
        l_phone = findViewById(R.id.l_phone)
        e_phone = findViewById(R.id.e_phone)
        l_username = findViewById(R.id.l_username)
        e_username = findViewById(R.id.e_username)
        btn_simpan = findViewById(R.id.btn_simpan)
        progress = findViewById(R.id.progress)
        txt_simpan = findViewById(R.id.txt_simpan)
        l_nik = findViewById(R.id.l_nik)
        e_nik = findViewById(R.id.e_nik)
        kelamin = findViewById(R.id.kelamin)
        l_alamat = findViewById(R.id.l_alamat)
        e_alamat = findViewById(R.id.e_alamat)

        if (s.getStatusLogin()){
            val user = s.getUser()!!
            e_name.setText(user.nama)
            e_email.setText(user.email)
            e_phone.setText(user.phone)
            e_username.setText(user.username)
            e_nik.setText(user.nik)
            e_alamat.setText(user.alamat)
        }
    }
}