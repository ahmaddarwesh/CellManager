package com.cell.bilalcell.bilalcellmanagers

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_sim_services.*
import maes.tech.intentanim.CustomIntent
import java.util.ArrayList


class SimServices : AppCompatActivity() {

    var myArrayItems = ArrayList<card_services>()
    var araylist: ArrayList<CompanyItems>? = null
    var Adapter: ComAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sim_services)

        initC()
        Adapter = ComAdapter(this, araylist)

        spinner2.adapter = Adapter
        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                myArrayItems.clear()
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val comName: CompanyItems = parent!!.getItemAtPosition(position) as CompanyItems
                val name = comName.Name
                if (name == "Select!") {
                    myArrayItems.clear()
                    recyServices.layoutManager = LinearLayoutManager(this@SimServices, LinearLayout.VERTICAL, false)
                    recyServices.adapter = AdapterServices(myArrayItems, this@SimServices)
                } else if (name == "MTC") {
                    myArrayItems.clear()
                    mtc_init(myArrayItems)
                    recyServices.layoutManager = LinearLayoutManager(this@SimServices, LinearLayout.VERTICAL, false)
                    recyServices.adapter = AdapterServices(myArrayItems, this@SimServices)
                } else if (name == "ALFA") {
                    myArrayItems.clear()
                    alfa_init(myArrayItems)
                    recyServices.layoutManager = LinearLayoutManager(this@SimServices, LinearLayout.VERTICAL, false)
                    recyServices.adapter = AdapterServices(myArrayItems, this@SimServices)
                }
            }
        }
    }

    inner class AdapterServices(var list: ArrayList<card_services>, var conx: Context) : RecyclerView.Adapter<AdapterServices.MyHolder>() {

        inner class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
            var img = view.findViewById<ImageView>(R.id.img_com)
            var name = view.findViewById<TextView>(R.id.name_com)
            var type = view.findViewById<TextView>(R.id.type_service)
            var valuePrice = view.findViewById<TextView>(R.id.val_price)
            var keyMb = view.findViewById<TextView>(R.id.kye_mb)
            var valMb = view.findViewById<TextView>(R.id.val_mb)
            var keyTime = view.findViewById<TextView>(R.id.kye_time)
            var valTime = view.findViewById<TextView>(R.id.val_time)
            var valAuto = view.findViewById<TextView>(R.id.val_Auto)
            var valMsg = view.findViewById<TextView>(R.id.val_msg)
            var valSendTo = view.findViewById<TextView>(R.id.val_send_to)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterServices.MyHolder {
            val myView = LayoutInflater.from(conx).inflate(R.layout.card_services, parent, false)
            return MyHolder(myView)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: AdapterServices.MyHolder, position: Int) {
            val mylist = list[position]
            holder.img.setImageResource(mylist.image)
            holder.name.text = mylist.name
            holder.type.text = mylist.type_serv
            holder.valuePrice.text = mylist.val_price
            holder.keyMb.text = mylist.key_mb
            holder.valMb.text = mylist.val_mb
            holder.keyTime.text = mylist.key_time
            holder.valTime.text = mylist.val_time
            holder.valAuto.text = mylist.val_auto
            holder.valMsg.text = mylist.val_msg
            holder.valSendTo.text = mylist.val_send_to
        }
    }

    fun alfa_init(arr: ArrayList<card_services>) {
        arr.add(card_services(R.drawable.alfa, "ALFA", "internet", "10$", "MB", "500", "Month", "1", "NO", "Mi500", "1050"))
        arr.add(card_services(R.drawable.alfa, "ALFA", "internet", "17$", "GB", "1.5", "Month", "1", "NO", "Mi1.5", "1050"))
        arr.add(card_services(R.drawable.alfa, "ALFA", "internet", "23$", "GB", "5", "Month", "1", "NO", "Mi5", "1050"))
    }

    fun mtc_init(arr: ArrayList<card_services>) {
        arr.add(card_services(R.drawable.touch, "MTC (touch)", "internet", "10$", "MB", "500", "Month", "1", "NO", "Hs1", "1188"))
        arr.add(card_services(R.drawable.touch, "MTC (touch)", "internet", "19$", "GB", "1.75", "Month", "1", "YES", "Hs2", "1188"))
        arr.add(card_services(R.drawable.touch, "MTC (touch)", "internet", "26$", "GB", "6", "Month", "1", "NO", "Hs3", "1188"))

    }

    fun initC() {
        araylist = ArrayList()
        araylist!!.add(CompanyItems("Select!", R.drawable.other))
        araylist!!.add(CompanyItems("MTC", R.drawable.touch))
        araylist!!.add(CompanyItems("ALFA", R.drawable.alfa))
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this,"fadein-to-fadeout")
    }
}