package com.cell.bilalcell.bilalcellmanagers

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import kotlinx.android.synthetic.main.activity_home.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_splash_screen.*
import maes.tech.intentanim.CustomIntent


class HomeActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        mAuth = FirebaseAuth.getInstance()

        if(!isOnline()){
            val sandbar = Snackbar
                    .make(con_home, "Check your internet connection", Snackbar.LENGTH_LONG)
            sandbar.view.setBackgroundColor(Color.RED)
            sandbar.duration = 1500
            sandbar.show()
        }

        if (CheckPer()) {

        } else {
            requstPer()
        }


        addClient.setOnClickListener {
            startActivity(Intent(this, CreateClient::class.java))
            CustomIntent.customType(this, "fadein-to-fadeout")
        }

        addNew.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AccountsList::class.java)
                    .putExtra("type",1))
        }

        Clients.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AccountsList::class.java)
                    .putExtra("type",0))
            CustomIntent.customType(this, "fadein-to-fadeout")
        }

        imgSetting.setOnClickListener {
            mAuth!!.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            CustomIntent.customType(this, "fadein-to-fadeout")
        }

        Servises_Sim.setOnClickListener {
            startActivity(Intent(this, SimServices::class.java))
            CustomIntent.customType(this, "fadein-to-fadeout")
        }

        searchProduct.setOnClickListener {
            print("Searching Product")
        }

        imgStatistics.setOnClickListener {
            println("img print ")
        }

        Pyment.setOnClickListener {

        }
    }

    fun CheckPer(): Boolean {
        val p1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val p2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val p3 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val Genrated = PackageManager.PERMISSION_GRANTED
        return p1 == Genrated && p2 == Genrated && p3 == Genrated
    }

    fun requstPer() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1)
    }
    fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    override fun onBackPressed() {

        SweetAlert().sweetAlertConf(this,"Close Management", "Are you sure close the Management really?",
                SweetAlertDialog.WARNING_TYPE, "Cancel", "Exit").setConfirmButton("Cancel") {
            it.dismiss()
        }.setCancelButton("Exit") {
             finish()
            System.exit(0)
        }.show()
    }

}
