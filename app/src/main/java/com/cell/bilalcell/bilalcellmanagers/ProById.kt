package com.cell.bilalcell.bilalcellmanagers

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_pro_by_id.*
import maes.tech.intentanim.CustomIntent
import java.text.SimpleDateFormat
import java.util.*

class ProById : AppCompatActivity() {
    private var db = FirebaseFirestore.getInstance()
    private var MyList = ArrayList<products>()
    private var Docu = db.collection("Users")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pro_by_id)
        val id = intent.extras.getString("ID")
        val name = intent.extras.getString("nameOfUser")


        Docu.document("USER_$id").collection("Products").get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        for (doc in it.result!!) {
                            val companyName = doc.get("CompanyName")
                            val CountPayments = doc.get("CountPayments")
                            val FirstPayment = doc.get("FirstPayment")
                            val ProductName = doc.get("ProductName")
                            val ProductPrice = doc.get("ProductPrice")
                            val date = doc.get("ProductDate")
                            val time = doc.get("ProductTime")

                            MyList.add(products(companyName.toString(), CountPayments.toString(), FirstPayment.toString()
                                    , ProductName.toString(), ProductPrice.toString(), date.toString(), time.toString()))
                        }

                        if (MyList.isEmpty()) {
                            lay_nofound.visibility = View.VISIBLE
                            recycler_pro.visibility = View.GONE
                        }

                        recycler_pro.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
                        val adapter = AdapterOfProducts2(this, MyList, id, name)
                        recycler_pro.adapter = adapter

                    }

                }
                .addOnFailureListener {
                    Toast.makeText(this, "ERROR : ${it.message}", Toast.LENGTH_LONG).show()
                }


    }


    class AdapterOfProducts2(var conx: Context, var list: ArrayList<products>, var idUser: String, var nameOfUser: String) : RecyclerView.Adapter<AdapterOfProducts2.MyHolderPro2>() {
        var typeC1 = 0
        var db = FirebaseFirestore.getInstance()
        var Products = db.collection("Users")
                .document("USER_$idUser")
                .collection("Products")

        var CountPay: Long? = null
        override fun onBindViewHolder(holder: MyHolderPro2, position: Int) {
            val myL = list[position]
            holder.NamePro.text = myL.ProductName
            holder.ComName.text = myL.CompanyName
            holder.date.text = myL.Date
            holder.lay.setOnClickListener {
                val pos = position + 1
//                conx.startActivity(Intent(conx, ProductInfo::class.java).
//                        putExtra("prod_name", myL.ProductName).
//                        putExtra("com_name", myL.CompanyName).
//                        putExtra("prod_date", myL.Date).
//                        putExtra("IdUser", idUser).
//                        putExtra("IdProd", pos).
//                        putExtra("NameUser", nameOfUser))

                DialogAddPay(myL.ProductName, pos)


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

        fun DialogAddPay(name: String, pos: Int) {


            val dialog = Dialog(conx)
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

                    SweetAlert().sweetAlertConf(conx, "Discard info!", "Are you sure discard information you typed?",
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
                            Toast.makeText(conx, "Error else ${it.result}", Toast.LENGTH_LONG).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(conx, "Error F ${it.message}", Toast.LENGTH_LONG).show()
                    }

            addBtn.setOnClickListener {
                val payment = HashMap<String, Any>()
                payment.put("PayCash", "${cash.text} ${text_type.text}")
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
                                SweetAlert().sweetAlertDialog(conx, "Added Successfully", "Success the add payment to $name Phone", SweetAlertDialog.SUCCESS_TYPE,
                                        "Ok").setConfirmButton("Ok") {
                                    it.dismiss()
                                }.show()
                            }
                            .addOnFailureListener {
                                dialog.dismiss()
                                SweetAlert().sweetAlertDialog(conx, "Failed Add!", "Failed the add payment to $name Phone Because:\n ${it.message}", SweetAlertDialog.ERROR_TYPE,
                                        "Ok").setConfirmButton("Ok") {
                                    it.dismiss()
                                }.show()
                            }

                } else {
                    SweetAlert().sweetAlertDialog(conx, "Field Required", "You cannot leave field empty. \nCompletion info to continue", SweetAlertDialog.ERROR_TYPE,
                            "Ok").setConfirmButton("Ok") {
                        it.dismiss()
                    }.show()
                }

            }

            dialog.show()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolderPro2 {
            val myView = LayoutInflater.from(conx).inflate(R.layout.card_list_products, parent, false)
            return MyHolderPro2(myView)
        }


        override fun getItemCount(): Int {
            return list.size
        }

        class MyHolderPro2(view: View) : RecyclerView.ViewHolder(view) {
            var NamePro = view.findViewById<TextView>(R.id.tv_name_product)
            var ComName = view.findViewById<TextView>(R.id.tv_company_name)
            var lay = view.findViewById<ConstraintLayout>(R.id.lay_product)
            var date = view.findViewById<TextView>(R.id.tv_date_product)
        }

    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, "bottom-to-up")
    }


}
