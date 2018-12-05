package com.cell.bilalcell.bilalcellmanagers

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.auth.FirebaseUser


class MainActivity : AppCompatActivity() {


    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        if (mAuth!!.currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        btnLogin.setOnClickListener {
            CheckLogin()
        }
    }

    fun CheckLogin() {
        mAuth = FirebaseAuth.getInstance()


        if(edt_username.text.isEmpty() || edt_password.text.isEmpty()){
            SweetAlert().sweetAlertDialog(this,"Field Empty!",
                    "Do not leave any field empty to continue!!",
                    SweetAlertDialog.ERROR_TYPE,
                    "Ok").setConfirmButton("Ok") {
                it.dismiss()
            }.show()
        }
        else{
            val email = edt_username.text.toString()
            val password = edt_password.text.toString()
            if (!isOnline()) {
                SweetAlert().sweetAlertDialog(this,"Error",
                        "Please Check your Internet Connection!",
                        SweetAlertDialog.ERROR_TYPE,
                        "Ok").setConfirmButton("Ok") {
                    it.dismiss()
                }.show()
            } else {
                mAuth!!.signInWithEmailAndPassword(email.trim(), password.trim())
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this@MainActivity, "Welcome",
                                        Toast.LENGTH_SHORT).show()
                                val user = mAuth!!.currentUser
                                finish()
                                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                                val UId = user!!.uid
                                Cookies().saveString(this, "UID", UId)
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(this@MainActivity, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show()
                                SweetAlert().sweetAlertDialog(this,"Authentication failed",
                                        "Please check Email or Password and try again.",
                                        SweetAlertDialog.ERROR_TYPE,
                                        "Ok").setConfirmButton("Ok") {
                                    it.dismiss()
                                }.show()
                            }

                        }
            }

        }
        

    }

    fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

}