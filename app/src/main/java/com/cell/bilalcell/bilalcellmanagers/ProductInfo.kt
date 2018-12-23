package com.cell.bilalcell.bilalcellmanagers

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
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
import kotlin.collections.ArrayList


class ProductInfo : AppCompatActivity() {


    private var db = FirebaseFirestore.getInstance()
    private var docs = db.collection("Users")
    private var listPay = ArrayList<Payment>()

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


        docs.document("USER_$idUser").collection("Products").document("Product_$IdProd").collection("Payments").get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (Pay in it.result!!) {
                    val cash = Pay.get("PayCash").toString()
                    val desc = Pay.get("PayDescription").toString()
                    val date = Pay.get("PaymentDate").toString()
                    val time = Pay.get("PaymentTime").toString()
                    listPay.add(Payment(cash, desc, date, time))
                }
                recy_payment.layoutManager = LinearLayoutManager(this,LinearLayout.VERTICAL,false)
                recy_payment.adapter = AdapterPayments(this,listPay)
                count.text = listPay.size.toString()
            } else {
                Toast.makeText(this,"Error Else \n${it.result}",Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this,"Error Failur \n${it.message}",Toast.LENGTH_LONG).show()
        }


        tv_pro_client.setOnClickListener {

        }

        add_payment.setOnClickListener {
            DialogAddPay(intent.extras.getString("prod_name"), IdProd, idUser)
        }


    }

    fun getPayments(idUser:String,IdProd:String){
        listPay.clear()
        docs.document("USER_$idUser").collection("Products").document("Product_$IdProd").collection("Payments").get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (Pay in it.result!!) {
                    val cash = Pay.get("PayCash").toString()
                    val desc = Pay.get("PayDescription").toString()
                    val date = Pay.get("PaymentDate").toString()
                    val time = Pay.get("PaymentTime").toString()
                    listPay.add(Payment(cash, desc, date, time))
                }
                recy_payment.layoutManager = LinearLayoutManager(this,LinearLayout.VERTICAL,false)
                recy_payment.adapter = AdapterPayments(this,listPay)
                count.text = listPay.size.toString()
            } else {
                Toast.makeText(this,"Error Else \n${it.result}",Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this,"Error Failur \n${it.message}",Toast.LENGTH_LONG).show()
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

    fun DialogAddPay(name: String, pos: Int, idUser: String ) {
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
        val addBtn = dialog.findViewById<ImageView>(R.id.add_btn)
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
                                getPayments(idUser,pos.toString())
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


    class AdapterPayments(var conx: Context, var list: ArrayList<Payment>) : RecyclerView.Adapter<AdapterPayments.MyHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPayments.MyHolder {
            val myView = LayoutInflater.from(conx).inflate(R.layout.card_payment,parent,false)
            return MyHolder(myView)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: AdapterPayments.MyHolder, position: Int) {

            val list = list[position]
            holder.cash.text = list.cash
            holder.desc.text = list.desc
            holder.date.text = list.date
            holder.time.text = list.time

        }

        class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
            var cash = view.findViewById<TextView>(R.id.Cash_Pay)
            var desc = view.findViewById<TextView>(R.id.Desc_Pay)
            var date = view.findViewById<TextView>(R.id.Date_Pay)
            var time = view.findViewById<TextView>(R.id.Time_Pay)
        }
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, "bottom-to-up")
    }

}
