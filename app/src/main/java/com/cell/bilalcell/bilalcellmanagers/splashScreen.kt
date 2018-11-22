package com.cell.bilalcell.bilalcellmanagers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_splash_screen.*
import android.net.ConnectivityManager
import com.google.firebase.auth.FirebaseAuth


class splashScreen : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        var s = 50

        mAuth = FirebaseAuth.getInstance()

        if (mAuth!!.currentUser != null) {
            s = 5
        }


        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 1000

        val animationA = AnimationSet(true)
        animationA.addAnimation(fadeIn)
//
        img_splash1.animation = animationA
        img_splash2.animation = animationA
        img_splash3.animation = animationA


        try {
            object : Thread() {
                @SuppressLint("SetTextI18n")
                override fun run() {
                    for (i in 1..100) {
                        progressBarSplash.progress = i
                        runOnUiThread {
                            when (i) {
                                50-> {
                                    tv_statuse.text = "Checking Internet Connection.."
                                    if (!isOnline()) {
                                        Toast.makeText(this@splashScreen, "Offline, Please Check your internet connection", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(this@splashScreen, "Online", Toast.LENGTH_LONG).show()
                                    }
                                    s = 50
                                }

                                60 -> {
                                    tv_statuse.text = "Loading Files.."
                                    s = 25
                                }
                                95 -> {
                                    tv_statuse.text = "Finishing.."
                                    s = 300
                                }
                                100 ->{
                                    tv_statuse.text = "Done"
                                }
                            }
                        }

                        Thread.sleep(s.toLong())
                    }
                    if (mAuth!!.currentUser != null) {
                        startActivity(Intent(this@splashScreen, HomeActivity::class.java))
                        finish()
                    }else{
                        startActivity(Intent(this@splashScreen, MainActivity::class.java))
                        finish()
                    }

                }
            }.start()


        } catch (e: Exception) {
            Toast.makeText(this, "Error ${e.message}", Toast.LENGTH_LONG).show()
        }


    }

    fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}
