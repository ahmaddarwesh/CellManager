package com.cell.bilalcell.bilalcellmanagers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sim_services.*
import maes.tech.intentanim.CustomIntent
import java.util.*


class SimServices : AppCompatActivity() {

    var myArrayItems = ArrayList<services>()
    var araylist: ArrayList<CompanyItems>? = null
    var Adapter: ComAdapter? = null
    var arrNotes = ArrayList<Note>()
    private var db = FirebaseFirestore.getInstance()
    private var NoteD = db.collection("Notes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sim_services)

        val id = intent.extras.getInt("id")

        if (id == 0) {
            spinner2.visibility = View.VISIBLE
            floatingActionButton.visibility = View.GONE
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
        } else {
            floatingActionButton.visibility = View.VISIBLE
            spinner2.visibility = View.GONE

        }

        floatingActionButton.setOnClickListener {
            startActivity(Intent(this, Notes::class.java).putExtra("ID", 0))

        }

    }

    inner class AdapterServices(var list: ArrayList<services>, var conx: Context) : RecyclerView.Adapter<AdapterServices.MyHolder>() {

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

    inner class AdapterNotes(var list: ArrayList<Note>, var conx: Context) : RecyclerView.Adapter<AdapterNotes.Holder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val myView = LayoutInflater.from(conx).inflate(R.layout.card_notes, parent, false)
            return Holder(myView)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val mylist = list[position]
            holder.title.text = mylist.title
            holder.date.text = mylist.date
            holder.id.text = mylist.id
            holder.card.setOnClickListener {
                startActivity(Intent(conx, Notes::class.java).putExtra("ID", 1).putExtra("idNote", mylist.id))

            }
        }

        inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
            var title = view.findViewById<TextView>(R.id.title)
            var date = view.findViewById<TextView>(R.id.date)
            var card = view.findViewById<CardView>(R.id.card_note)
            var id = view.findViewById<TextView>(R.id.txt_id)

        }

    }

    fun alfa_init(arr: ArrayList<services>) {
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "1$", "MB", "50", "Days", "1", "NO", "D1 ", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "3$", "MB", "600", "Days", "3", "NO", "D3 ", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "5$", "TB", "NoLimit", "Hours", "2", "NO", "D5", "1050"))

        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "5$", "MB", "500", "Days", "7", "NO", "WDB500", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "7$", "GB", "1.5", "Days", "7", "NO", "WDB1.5", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "15$", "GB", "5", "Days", "7", "NO", "WDB5", "1050"))

        arr.add(services(R.drawable.alfa, "ALFA", "WhatsApp", "5$", "MB", "300", "Month", "1", "Yes", "WA300", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "10$", "MB", "500", "Month", "1", "NO", "MI500", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "17$", "GB", "1.5", "Month", "1", "NO", "MI1.5", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "23$", "GB", "5", "Month", "1", "NO", "MI5", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "30$", "GB", "10", "Month", "1", "NO", "MI10", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "45$", "GB", "20", "Month", "1", "NO", "MI20", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "56$", "GB", "25", "Month", "1", "NO", "MI25", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "67$", "GB", "30", "Month", "1", "NO", "MI30", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "80$", "GB", "40", "Month", "1", "NO", "MI40", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "100$", "GB", "50", "Month", "1", "NO", "MI50", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "112$", "GB", "70", "Month", "1", "NO", "MI70", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "130$", "GB", "100", "Month", "1", "NO", "MI100", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "230$", "GB", "200", "Month", "1", "NO", "MI200", "1050"))

        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "59$", "GB", "20", "Month", "2", "NO", "2MI20", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "66$", "GB", "25", "Month", "2", "NO", "2MI25", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "72$", "GB", "30", "Month", "2", "NO", "2MI30", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "84$", "GB", "40", "Month", "2", "NO", "2MI40", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "102$", "GB", "50", "Month", "2", "NO", "2MI50", "1050"))

        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "139$", "GB", "70", "Month", "3", "NO", "3MI70", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "179$", "GB", "100", "Month", "3", "NO", "3MI100", "1050"))
        arr.add(services(R.drawable.alfa, "ALFA", "Internet", "259$", "GB", "200", "Month", "3", "NO", "3MI200", "1050"))


    }

    fun mtc_init(arr: ArrayList<services>) {
        arr.add(services(R.drawable.touch, "MTC (touch)", "WhatsApp", "4$", "MB", "200", "Month", "1", "YES", "Wa", "1100"))
        arr.add(services(R.drawable.touch, "MTC (touch)", "WhatsApp", "6$", "MB", "300", "Month", "1", "YES", "Wa2", "1100"))

        arr.add(services(R.drawable.touch, "MTC (touch)", "internet", "1$", "MB", "50", "Days", "1", "NO", "DD", "1188"))
        arr.add(services(R.drawable.touch, "MTC (touch)", "internet", "2$", "MB", "200", "Days", "2", "NO", "DD2", "1188"))
        arr.add(services(R.drawable.touch, "MTC (touch)", "internet", "4$", "MB", "700", "Days", "4", "NO", "DD4", "1188"))
        arr.add(services(R.drawable.touch, "MTC (touch)", "internet", "7$", "GB", "1", "Days", "7", "NO", "WD", "1188"))
        arr.add(services(R.drawable.touch, "MTC (touch)", "Voice call", "2$", "Minutes", "60", "Days", "2", "NO", "2DV", "1188"))
        arr.add(services(R.drawable.touch, "MTC (touch)", "Social Data", "7$", "MB", "300", "Month", "1", "YES", "S", "1100"))

        arr.add(services(R.drawable.touch, "MTC (touch)", "internet", "10$", "MB", "500", "Month", "1", "NO", "Hs1", "1188"))
        arr.add(services(R.drawable.touch, "MTC (touch)", "internet", "19$", "GB", "1.75", "Month", "1", "YES", "Hs2", "1188"))
        arr.add(services(R.drawable.touch, "MTC (touch)", "internet", "26$", "GB", "6", "Month", "1", "NO", "Hs3", "1188"))
        arr.add(services(R.drawable.touch, "MTC (touch)", "internet", "39$", "GB", "10", "Month", "1", "NO", "Hs4", "1188"))
        arr.add(services(R.drawable.touch, "MTC (touch)", "internet", "59$", "GB", "20", "Month", "1", "NO", "Hs5", "1188"))
        arr.add(services(R.drawable.touch, "MTC (touch)", "internet", "79$", "GB", "30", "Month", "1", "NO", "Hs6", "1188"))
        arr.add(services(R.drawable.touch, "MTC (touch)", "internet", "99$", "GB", "40", "Month", "1", "NO", "Hs7", "1188"))
        arr.add(services(R.drawable.touch, "MTC (touch)", "internet", "119$", "GB", "60", "Month", "1", "NO", "Hs8", "1188"))

    }

    fun initC() {
        araylist = ArrayList()
        araylist!!.add(CompanyItems("Select!", R.drawable.other))
        araylist!!.add(CompanyItems("MTC", R.drawable.touch))
        araylist!!.add(CompanyItems("ALFA", R.drawable.alfa))
    }

    fun initNotes() {

        arrNotes.clear()

        NoteD.get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (douc in it.result!!) {
                    val title = douc.get("TITLE")
                    val date = douc.get("DATE")
                    val id = douc.get("ID")
                    arrNotes.add(Note(title.toString(), date.toString(), id.toString()))
                }
                recyServices.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
                recyServices.adapter = AdapterNotes(arrNotes, this)
                if (!arrNotes.isEmpty()) {
                    recyServices.visibility = View.VISIBLE
                    cons_noNotes.visibility = View.GONE

                } else {
                    recyServices.visibility = View.GONE
                    cons_noNotes.visibility = View.VISIBLE
                }

            } else {
                Toast.makeText(this, "Error ${it.result.toString()}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (intent.extras.getInt("id") != 0) {
            initNotes()
        }

    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, "fadein-to-fadeout")
    }

}