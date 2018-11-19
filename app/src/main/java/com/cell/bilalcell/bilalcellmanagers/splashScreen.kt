package com.cell.bilalcell.bilalcellmanagers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_splash_screen.*
import android.os.Looper
import android.os.Message
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import maes.tech.intentanim.CustomIntent


class splashScreen : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 2000

        val animationA = AnimationSet(true)
        animationA.addAnimation(fadeIn)
//
//
        img_splash1.animation = animationA
        img_splash2.animation = animationA
        img_splash3.animation = animationA

        var s = 20
        try {
            object : Thread() {
                @SuppressLint("SetTextI18n")
                override fun run() {
                    for (i in 1..100) {
                        progressBarSplash.progress = i
                        runOnUiThread {
                            when (i) {
                                25 -> {
                                    tv_statuse.text = "Checking Internet Connection.."
                                    if (!isOnline()) {
                                        Toast.makeText(this@splashScreen, "Offline, Please Check your internet connection", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(this@splashScreen, "Online", Toast.LENGTH_LONG).show()
                                    }
                                    s = 50
                                }


                                50 -> {
                                    tv_statuse.text = "Loading Files.."
                                    s = 25
                                }
                                90 -> {
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

                    startActivity(Intent(this@splashScreen, MainActivity::class.java))
                    finish()
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
