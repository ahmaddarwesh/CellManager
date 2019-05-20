package com.cell.bilalcell.bilalcellmanagers

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_accounts_list.*
import maes.tech.intentanim.CustomIntent


class AccountsList : AppCompatActivity() {


    private var db = FirebaseFirestore.getInstance()
    private var UsersD = db.collection("Users")
    private var t: Int = 0
    private var OnFilter = false
    var Adapter: newAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts_list)
        getClients()
        if (!isOnline()) {
            val sandbar = Snackbar
                    .make(con_al, "Check your internet connection", Snackbar.LENGTH_LONG)
            sandbar.view.setBackgroundColor(Color.RED)
            sandbar.duration = 1500
            sandbar.show()
        }

        if (!intent.extras!!.isEmpty) {
            val type = intent.extras!!.getInt("type")
            t = type
        }

        name_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {


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

    private fun getClients() {
        val Myquery: Query = UsersD
        val option = FirestoreRecyclerOptions.Builder<Clients>()
                .setQuery(Myquery, Clients::class.java)
                .build()
        Adapter = newAdapter(this, option)

        val recy = findViewById<RecyclerView>(R.id.recy)
        recy.layoutManager = LinearLayoutManager(this)
        recy.setHasFixedSize(true)
        recy.adapter = Adapter
        Adapter!!.startListening()
        Adapter!!.notifyDataSetChanged()
    }

    fun filtering(s: String) {
        OnFilter = true
        val Myquery: Query = UsersD.whereEqualTo("Name", s)

        val option = FirestoreRecyclerOptions.Builder<Clients>()
                .setQuery(Myquery, Clients::class.java)
                .build()

        Adapter = newAdapter(this, option)
        val recy = findViewById<RecyclerView>(R.id.recy)
        recy.layoutManager = LinearLayoutManager(this)
        recy.setHasFixedSize(true)
        recy.adapter = Adapter
        Adapter!!.startListening()
        Adapter!!.notifyDataSetChanged()
    }


    override fun onBackPressed() {
        if (OnFilter) {
            getClients()
            OnFilter = false
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    fun dialogOptions(id: String, Name: String, Num: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.card_options)

        val txt = dialog.findViewById<TextView>(R.id.tv_edit)
        txt.text = "View Profile"
        val icon = dialog.findViewById<ImageView>(R.id.icon_edit)
        icon.setImageResource(R.drawable.ic_person_outline_black_24dp)
        dialog.findViewById<TextView>(R.id.txt_options).setText(Name)


        val edit = dialog.findViewById<LinearLayout>(R.id.Lin_Edit)
        edit.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, ProfileClient::class.java).putExtra("ID", id)
                    .putExtra("Name", Name)
                    .putExtra("Number", Num))
            CustomIntent.customType(this, "up-to-bottom")

        }

        val delete = dialog.findViewById<LinearLayout>(R.id.Lin_Del)
        delete.setOnClickListener {
            dialog.dismiss()
            SweetAlert().sweetAlertConf(this, "Are you sure?", "Are you sure you want to delete this client?",
                    SweetAlertDialog.WARNING_TYPE,
                    "Yes", "No").setConfirmButton("Yes") { it2 ->
                it2.dismiss()
                UsersD.document("USER_$id")
                        .delete()
                        .addOnSuccessListener { it1 ->
                            SweetAlert().sweetAlertDialog(this, "Deleted",
                                    "The Client Successfully Deleted!",
                                    SweetAlertDialog.SUCCESS_TYPE,
                                    "Done").setConfirmButton("Done") {
                                it.dismiss()
                                finish()
                            }.show()
                        }

                        .addOnFailureListener { it ->
                            SweetAlert().sweetAlertDialog(this, "Not Deleted",
                                    "The Client is not Deleted because:\n${it.message}",
                                    SweetAlertDialog.SUCCESS_TYPE,
                                    "Done").setConfirmButton("Done") {
                                it.dismiss()

                            }.show()
                        }
            }.setCancelButton("No") {
                it.dismiss()
            }.show()
        }

        val share = dialog.findViewById<LinearLayout>(R.id.Lin_Sh)
        share.setOnClickListener {
            dialog.dismiss()
            val shareBody = "Name: $Name\nPhone Number: $Num"
            val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Client Cell Manager")
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Share using"))

        }

        dialog.show()
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

    inner class newAdapter(var conx: Context, options: FirestoreRecyclerOptions<Clients>?) :
            FirestoreRecyclerAdapter<Clients, newAdapter.HolderMe>(options!!) {

        override fun onBindViewHolder(holder: HolderMe, position: Int, model: Clients) {
            holder.name.text = model.Name
            holder.number.text = model.PhoneNumber
            holder.ID.text = model.ID.toString()
            holder.more.setOnClickListener {
                if (t == 0) {
                    conx.startActivity(Intent(conx, ProfileClient::class.java)
                            .putExtra("ID", model.ID.toString())
                            .putExtra("Name", model.Name)
                            .putExtra("Number", model.PhoneNumber))
                    CustomIntent.customType(conx, "up-to-bottom")
                } else {
                    conx.startActivity(Intent(conx, ProById::class.java)
                            .putExtra("ID", model.ID.toString())
                            .putExtra("nameOfUser", model.Name))
                    CustomIntent.customType(conx, "up-to-bottom")
                }
            }

            holder.more.setOnLongClickListener {
                dialogOptions(model.ID.toString(), model.Name, model.PhoneNumber)
                false
            }

            Picasso.get()
                    .load(R.drawable.loading_profile)
                    .into(holder.profile_Image)
            UsersD.document("USER_${model.ID}").get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val img = Uri.parse(it.result!!.get("img_url").toString())
                    it.addOnCompleteListener {
                        if (it.result!!.data!!.isEmpty()) {
                            Picasso.get().load(R.drawable.loading_profile).into(holder.profile_Image)
                        }
                        Picasso.get()
                                .load(img)
                                .into(holder.tset, object : Callback {
                                    override fun onSuccess() {
                                        Picasso.get().load(img).into(holder.profile_Image)
                                    }

                                    override fun onError(e: Exception?) {
                                        Picasso.get().load(R.drawable.loading_profile).into(holder.profile_Image)
                                    }
                                })
                    }

                } else {
                    Toast.makeText(this@AccountsList, "ERROR ${it.exception!!.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): HolderMe {
            val myView = LayoutInflater.from(conx).inflate(R.layout.card_list_users, p0, false)
            return HolderMe(myView)
        }


        inner class HolderMe(view: View) : RecyclerView.ViewHolder(view) {
            val name = view.findViewById<TextView>(R.id.Name_c)
            val number = view.findViewById<TextView>(R.id.Num_c)
            val ID = view.findViewById<TextView>(R.id.ID)
            val more = view.findViewById<ConstraintLayout>(R.id.More_c)
            val profile_Image = view.findViewById<ImageView>(R.id.profile_image_c)
            val tset = view.findViewById<ImageView>(R.id.img_test)
        }


    }


    override fun onStart() {
        super.onStart()
        Adapter!!.startListening()
    }

    override fun onPause() {
        super.onPause()
        Adapter!!.stopListening()
    }
}

