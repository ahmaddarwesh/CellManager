package com.cell.bilalcell.bilalcellmanagers

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import maes.tech.intentanim.CustomIntent

class splashScreen : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        mAuth = FirebaseAuth.getInstance()

        try {
            object : Thread() {
                override fun run() {
                    Thread.sleep(500)
                    runOnUiThread {
                        if (mAuth!!.currentUser != null) {
                            startActivity(Intent(this@splashScreen, HomeActivity::class.java))
                            CustomIntent.customType(this@splashScreen, "fadein-to-fadeout")
                            finish()
                        } else {
                            startActivity(Intent(this@splashScreen, MainActivity::class.java))
                            CustomIntent.customType(this@splashScreen, "fadein-to-fadeout")
                            finish()
                        }
                    }

                }
            }.start()

        } catch (e: Exception) {
            Toast.makeText(this, "Error ${e.message}", Toast.LENGTH_LONG).show()
        }
    }


}
