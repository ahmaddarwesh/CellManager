package com.cell.bilalcell.bilalcellmanagers

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_product_info.*
import maes.tech.intentanim.CustomIntent
import java.text.SimpleDateFormat
import java.util.*


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
                    } else {
                        SweetAlert().sweetAlertDialog(this, "Error",
                                "Error is:\n ${it.result!!}",
                                SweetAlertDialog.ERROR_TYPE,
                                "Ok").setConfirmButton("Ok") {
                            it.dismiss()
                        }.show()
                    }
                }.addOnFailureListener {
                    SweetAlert().sweetAlertDialog(this, "Error",
                            "Erroring is:\n ${it.message}",
                            SweetAlertDialog.ERROR_TYPE,
                            "Ok").setConfirmButton("Ok") {
                        it.dismiss()

                    }.show()
                }

        tv_pro_client.setOnClickListener {


        }

        add_payment.setOnClickListener {
            DialogAddPay(intent.extras.getString("prod_name"), IdProd, idUser)
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

    fun DialogAddPay(name: String, pos: Int, idUser: String) {
        var typeC1 = 0
        val Products = db.collection("Users")
                .document("USER_$idUser")
                .collection("Products")

        var CountPay: Long? = null

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.add_pay)
        dialog.setCancelable(false)

        val cash = dialog.findViewById<EditText>(R.id.cash)
        val description = dialog.findViewById<EditText>(R.id.desc)
        val addBtn = dialog.findViewById<Button>(R.id.add_btn)
        val edit_product = dialog.findViewById<TextView>(R.id.edit_product)
        val text_type = dialog.findViewById<TextView>(R.id.text_type)
        val img_cancel = dialog.findViewById<ImageView>(R.id.img_cancel)


        edit_product.text = name

        img_cancel.setOnClickListener {
            if (cash.text.trim().isEmpty() || description.text.trim().isEmpty()) {
                dialog.dismiss()
            } else {
                SweetAlert().sweetAlertConf(this, "Discard info!", "Are you sure discard information you typed?",
                        SweetAlertDialog.WARNING_TYPE, "Cancel", "Discard").setConfirmButton("Cancel") {
                    it.dismiss()
                }.setCancelButton("Discard") {
                    it.dismiss()
                    dialog.dismiss()
                }.show()
            }

        }

        text_type.setOnClickListener {
            if (typeC1.equals(0)) {
                text_type.text = "L.L"
                typeC1 = 1
            } else {
                text_type.text = "$"
                typeC1 = 0
            }
        }
        Products.document("Product_$pos")
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        CountPay = it.result!!.getLong("CountPayments")!!.toLong() + 1
                    } else {
                        Toast.makeText(this, "Error else ${it.result}", Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Error F ${it.message}", Toast.LENGTH_LONG).show()
                }

        addBtn.setOnClickListener {


            val payment = HashMap<String, Any>()
            payment.put("PayCash", cash.text.toString() + text_type.text)
            payment.put("PayDescription", description.text.toString())
            payment.put("PaymentDate", DateNow())
            payment.put("PaymentTime", TimeNow())

            if (!cash.text.isEmpty() && !description.text.isEmpty()) {
                Products.document("Product_$pos")
                        .collection("Payments")
                        .document("Payment_$CountPay")
                        .set(payment)
                        .addOnSuccessListener {
                            Products.document("Product_$pos").update("CountPayments", CountPay)
                            dialog.dismiss()
                            SweetAlert().sweetAlertDialog(this, "Added Successfully", "Success the add payment to $name Phone", SweetAlertDialog.SUCCESS_TYPE,
                                    "Ok").setConfirmButton("Ok") {
                                it.dismiss()
                            }.show()
                        }
                        .addOnFailureListener {
                            dialog.dismiss()
                            SweetAlert().sweetAlertDialog(this, "Failed Add!", "Failed the add payment to $name Phone Because:\n ${it.message}", SweetAlertDialog.ERROR_TYPE,
                                    "Ok").setConfirmButton("Ok") {
                                it.dismiss()
                            }.show()
                        }
            } else {
                SweetAlert().sweetAlertDialog(this, "Field Required", "You cannot leave field empty. \nCompletion info to continue", SweetAlertDialog.ERROR_TYPE,
                        "Ok").setConfirmButton("Ok") {
                    it.dismiss()
                }.show()
            }


        }

        dialog.show()
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

}
