package com.cell.bilalcell.bilalcellmanagers

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_product.*
import maes.tech.intentanim.CustomIntent
import java.text.SimpleDateFormat
import java.util.*

class CreateProduct : AppCompatActivity() {

    var araylist: ArrayList<CompanyItems>? = null
    var Adapter: ComAdapter? = null

    var typeC1 = 0
    var typeC2 = 0

    private var realTime = FirebaseDatabase.getInstance()
    val db = FirebaseFirestore.getInstance()
    val Users = db.collection("Users")

    var name_of_com = "Other"
    var countP: Long = 0

    var i = 0
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_product)

        val id = intent.extras!!.getString("ID")
        initCompanies()

        if (!isOnline()) {
            val sandbar = Snackbar
                    .make(con_cp, "Check your internet connection", Snackbar.LENGTH_LONG)
            sandbar.view.setBackgroundColor(Color.RED)
            sandbar.duration = 1500
            sandbar.show()
        }


//get Count Product of user
        Users.document("USER_$id").get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        countP = it.result!!.getLong("CountProduct")!!.toLong() + 1
                    } else {
                        Toast.makeText(this, "ERROR ${it.exception!!.message}", Toast.LENGTH_LONG).show()
                    }
                }


        Adapter = ComAdapter(this, araylist)
        spinner.adapter = Adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val conuntryitem: CompanyItems = parent!!.getItemAtPosition(position) as CompanyItems
                val name = conuntryitem.Name
                if (name != "Select!") {
                    name_of_pro.isEnabled = true
                    price_of_pro.isEnabled = true
                    first_pay.isEnabled = true
                    name_of_com = name
                } else {
                    name_of_pro.isEnabled = false
                    price_of_pro.isEnabled = false
                    first_pay.isEnabled = false
                }
            }

        }
        //Done Spinner Here

        type_of_coin.setOnClickListener {
            if (typeC1.equals(0)) {
                type_of_coin.text = "L.L"
                typeC1 = 1
            } else {
                type_of_coin.text = "$"
                typeC1 = 0
            }
        }
        type_of_coin2.setOnClickListener {
            if (typeC2.equals(0)) {
                type_of_coin2.text = "L.L"
                typeC2 = 1
            } else {
                type_of_coin2.text = "$"
                typeC2 = 0
            }
        }
        //Done type Here


        Done_pro.setOnClickListener { it ->
            val Products = db.collection("Users")
                    .document("USER_$id")
                    .collection("Products")

            if (name_of_pro.text.trim().isEmpty() || price_of_pro.text.trim().isEmpty() || first_pay.text.trim().isEmpty()) {
                SweetAlert().sweetAlertDialog(this, "Field Required", "You cannot leave field empty. \n Completion info to continue", SweetAlertDialog.ERROR_TYPE,
                        "Ok").setConfirmButton("Ok") {
                    it.dismiss()
                }.show()
            } else {

                val CompanyName = name_of_com
                val NameOfPro = name_of_pro.text.toString()
                val PriceOfPro = "${price_of_pro.text} ${type_of_coin.text}"
                val FirstPay = "${first_pay.text} ${type_of_coin2.text}"

                val date = DateNow()
                val t = TimeNow()
                val Product = HashMap<String, Any>()
                Product.put("CompanyName", CompanyName)
                Product.put("ProductName", NameOfPro)
                Product.put("ProductPrice", PriceOfPro)
                Product.put("ProductDate", date)
                Product.put("CountPayments", 1)
                Product.put("ProductTime", t)
                Product.put("ID", countP)
                Product.put("IsDone", "0")

                val payment = HashMap<String, Any>()
                payment.put("PayCash", FirstPay)
                payment.put("PayDescription", "First Payment")
                payment.put("PaymentDate", DateNow())
                payment.put("PaymentTime", TimeNow())

                if (!isOnline()) {
                    SweetAlert().sweetAlertDialog(this, "Error",
                            "Check Your Internet Connection!",
                            SweetAlertDialog.ERROR_TYPE,
                            "Ok").setConfirmButton("Ok") {
                        it.dismiss()
                    }.show()
                } else {
                    Users.document("USER_$id").collection("Products").document("Product_$countP").set(Product)
                            .addOnSuccessListener { it2 ->
                                Products.document("Product_$countP")
                                        .collection("Payments")
                                        .document("Payment_1")
                                        .set(payment)


                                SweetAlert().sweetAlertDialog(this, "Successful", "Done Successfully add Product", SweetAlertDialog.SUCCESS_TYPE,
                                        "Done").setConfirmButton("Done") {
                                    Users.document("USER_$id").update(
                                            "CountProduct", countP
                                    )
                                    it.dismiss()
                                    finish()
                                }.show()

                                Products.document()
                            }
                            .addOnFailureListener { it3 ->
                                SweetAlert().sweetAlertDialog(this, "Fail", "Error : ${it3.message}", SweetAlertDialog.ERROR_TYPE,
                                        "Ok").setConfirmButton("Ok") {
                                    it.dismiss()
                                }.show()
                            }
                }

            }

        }

    }

    @SuppressLint("SimpleDateFormat")
    fun DateNow(): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val date = Date()
        return formatter.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    fun TimeNow(): String {
        val formatter = SimpleDateFormat("hh:mm")
        val date = Date()
        return formatter.format(date)
    }

    fun initCompanies() {
        araylist = ArrayList()
        araylist!!.add(CompanyItems("Select!", R.drawable.other))
        araylist!!.add(CompanyItems("SAMSUNG", R.drawable.samsung))
        araylist!!.add(CompanyItems("APPLE", R.drawable.apple))
        araylist!!.add(CompanyItems("HUAWEI", R.drawable.huawei))
        araylist!!.add(CompanyItems("SONY", R.drawable.sony))
        araylist!!.add(CompanyItems("GOOGLE", R.drawable.google))
        araylist!!.add(CompanyItems("LG", R.drawable.lg))
        araylist!!.add(CompanyItems("HTC", R.drawable.htc))
        araylist!!.add(CompanyItems("NOKIA", R.drawable.nokia))
        araylist!!.add(CompanyItems("Other..", R.drawable.other))
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, "fadein-to-fadeout")
    }


    override fun onBackPressed() {
        if (name_of_pro.text.isEmpty() && price_of_pro.text.isEmpty()
                && first_pay.text.isEmpty()) {
            finish()
        } else {
            SweetAlert().sweetAlertConf(this, "Discard info?", "Are you sure discard information typed?",
                    SweetAlertDialog.WARNING_TYPE, "Cancel", "Discard").setConfirmButton("Cancel") {
                it.dismiss()
            }.setCancelButton("Discard") {
                finish()
            }.show()
        }

    }


    fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

}