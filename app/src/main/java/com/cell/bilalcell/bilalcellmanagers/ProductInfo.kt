package com.cell.bilalcell.bilalcellmanagers

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_product_info.*
import maes.tech.intentanim.CustomIntent


class ProductInfo : AppCompatActivity() {


    private var db = FirebaseFirestore.getInstance()
    private var docs = db.collection("Users")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_info)

        tv_pro_name.text = intent.extras.getString("prod_name")
        tv_com_name.text = intent.extras.getString("com_name")
        tv_pro_date.text = intent.extras.getString("prod_date")
        val idUser = intent.extras.getString("IdUser")
        val IdProd = intent.extras.getInt("IdProd")
        tv_pro_client.text = intent.extras.getString("NameUser")


        docs.document("USER_$idUser").collection("Products").document("Product_$IdProd").get()
                .addOnCompleteListener {
                    if (it.isSuccessful && it.result!!.exists()) {
                        tv_pro_price.text = it.result!!.getString("ProductPrice").toString()
                        tv_pro_time.text = it.result!!.getString("ProductTime").toString()
                        count.text = it.result!!.getString("CountPayments").toString()
                    }else{
                        sweetAlertDialog("Error",
                                "Error is:\n ${it.result!!}",
                                SweetAlertDialog.SUCCESS_TYPE,
                                "Ok").setConfirmButton("Ok") {
                            it.dismiss()
                        }.show()
                    }
                }.addOnFailureListener {
                    sweetAlertDialog("Error",
                            "Error is:\n ${it.message}",
                            SweetAlertDialog.SUCCESS_TYPE,
                            "Ok").setConfirmButton("Ok") {
                        it.dismiss()

                    }.show()
                }


    }


    class AdapterPayments(var conx: Context, var list: ArrayList<String>) : RecyclerView.Adapter<AdapterPayments.MyHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPayments.MyHolder {
            return MyHolder(parent)
        }

        override fun getItemCount(): Int {
            return 1
        }

        override fun onBindViewHolder(holder: AdapterPayments.MyHolder, position: Int) {
        }

        class MyHolder(view: View) : RecyclerView.ViewHolder(view) {

        }
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, "bottom-to-up")
    }


    private fun sweetAlertDialog(Title: String, Content: String, Type: Int, ConfermText: String): SweetAlertDialog {
        val s = SweetAlertDialog(this, Type)
        s.titleText = Title
        s.contentText = Content
        s.confirmText = ConfermText
        return s
    }
}
