package com.cell.bilalcell.bilalcellmanagers

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import maes.tech.intentanim.CustomIntent


class HomeActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        FirebaseApp.initializeApp(this)
        mAuth = FirebaseAuth.getInstance()

        if (!isOnline()) {
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
        addClient.setOnLongClickListener {
            val n = Toast.makeText(this@HomeActivity, "Add new Client", Toast.LENGTH_SHORT)
            n.setGravity(Gravity.TOP, 0, 0)

            n.show()
            false
        }

        addNews.setOnClickListener {

            startActivity(Intent(this@HomeActivity, AccountsList::class.java)
                    .putExtra("type", 1))
            CustomIntent.customType(this, "fadein-to-fadeout")
        }

        addNews.setOnLongClickListener {
            val n = Toast.makeText(this@HomeActivity, "Add new Payment", Toast.LENGTH_SHORT)
            n.setGravity(Gravity.TOP, 0, 0)

            n.show()
            false
        }

        Clients.setOnClickListener {

            startActivity(Intent(this@HomeActivity, AccountsList::class.java)
                    .putExtra("type", 0))
            CustomIntent.customType(this, "fadein-to-fadeout")
        }

        Clients.setOnLongClickListener {
            val n = Toast.makeText(this@HomeActivity, "Show All Clients", Toast.LENGTH_SHORT)
            n.setGravity(Gravity.TOP, 0, 0)

            n.show()
            false
        }

        imgSetting.setOnClickListener {

            startActivity(Intent(this, Settings::class.java))
            CustomIntent.customType(this, "fadein-to-fadeout")
        }
        imgSetting.setOnLongClickListener {
            val n = Toast.makeText(this@HomeActivity, "Settings", Toast.LENGTH_SHORT)
            n.setGravity(Gravity.TOP, 0, 0)
            n.show()
            false
        }

        Servises_Sim.setOnClickListener {

            startActivity(Intent(this, SimServices::class.java).putExtra("id", 0))
            CustomIntent.customType(this, "fadein-to-fadeout")
        }
        Servises_Sim.setOnLongClickListener {
            val n = Toast.makeText(this@HomeActivity, "Services for sim card", Toast.LENGTH_SHORT)
            n.setGravity(Gravity.TOP, 0, 0)

            n.show()
            false
        }

        searchProduct.setOnClickListener {

            startActivity(Intent(this, SimServices::class.java).putExtra("id", 1))
        }
        searchProduct.setOnLongClickListener {
            val n = Toast.makeText(this@HomeActivity, "Add notes", Toast.LENGTH_SHORT)
            n.setGravity(Gravity.TOP, 0, 0)

            n.show()
            false
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

        SweetAlert().sweetAlertConf(this, "Close Management", "Are you sure close the Management really?",
                SweetAlertDialog.WARNING_TYPE, "Cancel", "Exit").setConfirmButton("Cancel") {
            it.dismiss()
        }.setCancelButton("Exit") {
            finish()

        }.show()
    }

}
