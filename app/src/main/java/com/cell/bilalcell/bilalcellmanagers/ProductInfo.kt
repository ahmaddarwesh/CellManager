package com.cell.bilalcell.bilalcellmanagers

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
 import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_product_info.*


class ProductInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_info)


        tv_pro_name.text = intent.extras.getString("prod_name")
        tv_com_name.text = intent.extras.getString("com_name")
        tv_pro_date.text = intent.extras.getString("prod_date")



    }


    class AdapterPayments(var conx:Context,var list:ArrayList<String>) : RecyclerView.Adapter<AdapterPayments.MyHolder>() {


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
}
