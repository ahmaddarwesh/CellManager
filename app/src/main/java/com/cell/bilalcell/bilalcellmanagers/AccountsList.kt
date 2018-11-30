package com.cell.bilalcell.bilalcellmanagers

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_accounts_list.*
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import maes.tech.intentanim.CustomIntent
import cn.pedant.SweetAlert.SweetAlertDialog
import com.squareup.picasso.Callback
import kotlinx.android.synthetic.main.activity_home.view.*
import java.lang.Exception


class AccountsList : AppCompatActivity() {

    private var db = FirebaseFirestore.getInstance()
    private var UsersD = db.collection("Users")
    private val list = ArrayList<clients>()
    private var t: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts_list)

        if(!isOnline()){
            val sandbar = Snackbar
                    .make(con_al, "Check your internet connection", Snackbar.LENGTH_LONG)
            sandbar.view.setBackgroundColor(Color.RED)
            sandbar.duration = 1500
            sandbar.show()
        }

        if (!intent.extras.isEmpty) {
            val type = intent.extras.getInt("type")
            t = type
        }


        progressBar.visibility = View.VISIBLE


        UsersD.orderBy("ID").limit(10).get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result!!) {
                    val name = document.get("Name")
                    val number = document.get("PhoneNumber")
                    val ID = document.get("ID")
                    list.add(clients(ID.toString(), name.toString(), number.toString()))
                }
                if (list.isEmpty()) {
                    recy.visibility = View.GONE
                    lay_empty.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                } else {
                    recy.visibility = View.VISIBLE
                    lay_empty.visibility = View.GONE
                    recy.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
                    recy.adapter = AdapterUsers(this, list)
                    progressBar.visibility = View.GONE
                }

            } else {
                Toast.makeText(this, "ERROR ${it.exception}", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
            }
        }

        name_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                loadAllData()
                filtering(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        search_client.setOnClickListener {
            filtering(name_search.text.toString())
        }

    }

    private fun loadAllData() {
        progressLoad.visibility = View.VISIBLE
        Handler().postDelayed({
            UsersD.whereGreaterThan("ID", list.size).orderBy("ID")
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            for (document in it.result!!) {
                                val name = document.get("Name")
                                val number = document.get("PhoneNumber")
                                val ID = document.get("ID")
                                list.add(clients(ID.toString(), name.toString(), number.toString()))
                            }
                            recy.adapter.notifyDataSetChanged()
                            progressLoad.visibility = View.GONE
                        } else {
                            Toast.makeText(this, "ERROR ${it.exception}", Toast.LENGTH_LONG).show()
                            progressLoad.visibility = View.GONE
                        }

                    }

        }, 2000)
    }

    fun filtering(s: String) {
        val items = ArrayList<clients>()
        for (item in list) {
            if (item.username.toLowerCase().contains(s.toLowerCase())) {
                items.add(item)
            }
        }
        recy.adapter = AdapterUsers(this, items)
    }

    inner class AdapterUsers(var context: Context, var mylist: ArrayList<clients>) : RecyclerView.Adapter<AdapterUsers.MyHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
            val myView = LayoutInflater.from(parent.context).inflate(R.layout.card_list_users, parent, false)
            return MyHolder(myView)
        }

        override fun getItemCount(): Int {
            return mylist.size
        }

        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            val list = mylist[position]
            holder.name.text = list.username
            holder.ID.text = list.id
            holder.number.text = list.mobile
            holder.more.setOnClickListener {
                if (t == 0) {
                    context.startActivity(Intent(context, ProfileClient::class.java).putExtra("ID", list.id.toString())
                            .putExtra("Name", list.username)
                            .putExtra("Number", list.mobile))
                    CustomIntent.customType(context, "up-to-bottom")
                } else {
                    startActivity(Intent(context, ProById::class.java).putExtra("ID", list.id).putExtra("nameOfUser",list.username))
                    CustomIntent.customType(context, "up-to-bottom")
                }

            }
            Picasso.get().load(R.drawable.loading_profile).into(holder.profile_Image)
            UsersD.document("USER_${list.id}").get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val img = Uri.parse(it.result!!.get("img_url").toString())
                    it.addOnCompleteListener {
                        if (it.result!!.data!!.isEmpty()) {
                            Picasso.get().load(R.drawable.loading_profile).into(holder.profile_Image)
                        }
                        Picasso.get().load(img).into(holder.tset, object : Callback {
                            override fun onSuccess() {
                                Picasso.get().load(img).into(holder.profile_Image)
                            }

                            override fun onError(e: Exception?) {
//                                Toast.makeText(this@AccountsList,"E ${e!!.message}",Toast.LENGTH_LONG).show()
                                Picasso.get().load(R.drawable.loading_profile).into(holder.profile_Image)
                            }
                        })
                    }


                } else {
                    Toast.makeText(this@AccountsList, "ERROR ${it.exception!!.message}", Toast.LENGTH_LONG).show()
                }
            }

            holder.more.setOnLongClickListener(object : View.OnLongClickListener {
                override fun onLongClick(v: View?): Boolean {

                    val menuPop = PopupMenu(context, holder.more)
                    menuPop.inflate(R.menu.user_menu)
                    menuPop.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                        override fun onMenuItemClick(item: MenuItem?): Boolean {
                            when (item!!.itemId) {
                                R.id.edit_user -> {

                                }
                                R.id.delete_user -> {
                                    sweetAlertConf("Are you sure?", "Are you sure you want to delete this client?",
                                            SweetAlertDialog.WARNING_TYPE,
                                            "Yes", "No").setConfirmButton("Yes") { it2 ->
                                        it2.dismiss()
                                        UsersD.document("USER_${list.id}")
                                                .delete()
                                                .addOnSuccessListener { it ->
                                                    sweetAlertDialog("Deleted",
                                                            "The Client Successfully Deleted!",
                                                            SweetAlertDialog.SUCCESS_TYPE,
                                                            "Done").setConfirmButton("Done") {
                                                        it.dismiss()
                                                        startActivity(Intent(this@AccountsList, AccountsList::class.java))
                                                        finish()
                                                    }.show()
                                                }
                                                .addOnFailureListener { it3 ->
                                                    sweetAlertDialog("Not Deleted",
                                                            "The Client is not Deleted because:\n${it3.message}",
                                                            SweetAlertDialog.SUCCESS_TYPE,
                                                            "Done").setConfirmButton("Done") {
                                                        it.dismiss()
                                                    }.show()
                                                }

                                    }.setCancelButton("No") {
                                        it.dismiss()
                                    }.show()

                                }
                                R.id.share_info -> {

                                }
                            }
                            return false
                        }

                    })
                    menuPop.show()
                    return true
                }


            })

        }

        inner class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name = view.findViewById<TextView>(R.id.Name_c)!!
            val number = view.findViewById<TextView>(R.id.Num_c)!!
            val ID = view.findViewById<TextView>(R.id.ID)!!
            val more = view.findViewById<ConstraintLayout>(R.id.More_c)!!
            var profile_Image = view.findViewById<ImageView>(R.id.profile_image_c)
            var tset = view.findViewById<ImageView>(R.id.img_test)
        }

    }

    private fun sweetAlertDialog(Title: String, Content: String, Type: Int, ConfermText: String): SweetAlertDialog {
        val s = SweetAlertDialog(this, Type)
        s.titleText = Title
        s.contentText = Content
        s.confirmText = ConfermText

        return s
    }

    private fun sweetAlertConf(Title: String, Content: String, Type: Int, ConfermText: String, cancelText: String): SweetAlertDialog {
        val s = SweetAlertDialog(this, Type)
        s.titleText = Title
        s.contentText = Content
        s.confirmText = ConfermText
        s.cancelText = cancelText
        return s
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, "fadein-to-fadeout")
    }
    fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}