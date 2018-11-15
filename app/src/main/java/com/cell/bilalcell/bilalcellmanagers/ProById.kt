package com.cell.bilalcell.bilalcellmanagers

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_pro_by_id.*

class ProById : AppCompatActivity() {
    private var db = FirebaseFirestore.getInstance()
    private var MyList = ArrayList<products>()
    private var Docu = db.collection("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pro_by_id)
        val id = intent.extras.getString("ID")


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
                                    , ProductName.toString(), ProductPrice.toString(),date.toString(),time.toString()))
                        }

                        if (MyList.isEmpty()) {
                            lay_nofound.visibility = View.VISIBLE
                            recycler_pro.visibility = View.GONE
                        }

                        recycler_pro.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
                        val adapter = AdapterOfProducts2(this, MyList)
                        recycler_pro.adapter = adapter

                    }

                }
                .addOnFailureListener {
                    Toast.makeText(this, "ERROR : ${it.message}", Toast.LENGTH_LONG).show()
                }


    }

    class AdapterOfProducts2(var conx: Context, var list: ArrayList<products>) : RecyclerView.Adapter<AdapterOfProducts2.MyHolderPro2>() {


        override fun onBindViewHolder(holder: MyHolderPro2, position: Int) {
            val myL = list[position]
            holder.NamePro.text = myL.ProductName
            holder.ComName.text = myL.CompanyName
            holder.date.text = myL.Date
            holder.lay.setOnClickListener {

            }
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
            var date =view.findViewById<TextView>(R.id.tv_date_product)
        }

    }
}
