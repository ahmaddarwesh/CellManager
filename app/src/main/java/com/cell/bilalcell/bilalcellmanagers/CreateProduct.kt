package com.cell.bilalcell.bilalcellmanagers

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.view.View

import android.widget.AdapterView
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_product.*
import maes.tech.intentanim.CustomIntent
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import java.util.*

class CreateProduct : AppCompatActivity() {

    var araylist: ArrayList<CompanyItems>? = null
    var Adapter: ComAdapter? = null

    var typeC1 = 0
    var typeC2 = 0

    val db = FirebaseFirestore.getInstance()
    val Users = db.collection("Users")

    var name_of_com = "Other"

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_product)

        val id = intent.extras.getString("ID")
        initCompanies()


        Adapter = ComAdapter(this, araylist)
        spinner.adapter = Adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val conuntryitem: CompanyItems = parent!!.getItemAtPosition(position) as CompanyItems
                val name = conuntryitem.Name
                if (name != "Select!") {
                    lay_info_product.visibility = View.VISIBLE
                    name_of_com = name
                } else {
                    lay_info_product.visibility = View.GONE
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
            val CompanyName = name_of_com
            val NameOfPro = name_of_pro.text.toString()
            val PriceOfPro = "${price_of_pro.text} ${type_of_coin.text}"
            val FirstPay = "${first_pay.text} ${type_of_coin2.text}"
            val countOfPay = count_of_pay.text.toString()
            val date = DateNow()
            val t = TimeNow()

            val Product = HashMap<String, Any>()
            Product.put("CompanyName", CompanyName)
            Product.put("ProductName", NameOfPro)
            Product.put("ProductPrice", PriceOfPro)
            Product.put("FirstPayment", FirstPay)
            Product.put("CountPayments", countOfPay)
            Product.put("ProductDate", date)
            Product.put("ProductTime",t)

            val time = System.nanoTime()

            Users.document("USER_$id").collection("Products").document("Product_$time").set(Product)
                    .addOnSuccessListener { it2 ->
                        sweetAlertDialog("Successful", "Done Successfully add Product", SweetAlertDialog.SUCCESS_TYPE,
                                "Done").setConfirmButton("Done") {
                            finish()
                        }.show()

                    }
                    .addOnFailureListener { it3 ->
                        sweetAlertDialog("Fail", "Error : ${it3.message}", SweetAlertDialog.ERROR_TYPE,
                                "Ok").setConfirmButton("Ok") {

                        }.show()
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
        val formatter = SimpleDateFormat("HH:mm")
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

    private fun sweetAlertDialog(Title: String, Content: String, Type: Int, ConfermText: String): SweetAlertDialog {
        val s = SweetAlertDialog(this, Type)
        s.titleText = Title
        s.contentText = Content
        s.confirmText = ConfermText

        return s
    }

    override fun onBackPressed() {
        if (name_of_pro.text.isEmpty() && price_of_pro.text.isEmpty()
                && first_pay.text.isEmpty() && count_of_pay.text.isEmpty()) {
            finish()
        } else {
            sweetAlertConf("Discard info?", "Are you sure discard information typed?",
                    SweetAlertDialog.WARNING_TYPE, "Exit", "Cancel").setConfirmButton("Exit") {
                finish()
            }.setCancelButton("Cancel") {
                it.dismiss()
            }.show()
        }

    }

    private fun sweetAlertConf(Title: String, Content: String, Type: Int, ConfermText: String, cancelText: String): SweetAlertDialog {
        val s = SweetAlertDialog(this, Type)
        s.titleText = Title
        s.contentText = Content
        s.confirmText = ConfermText
        s.cancelText = cancelText
        return s
    }

}