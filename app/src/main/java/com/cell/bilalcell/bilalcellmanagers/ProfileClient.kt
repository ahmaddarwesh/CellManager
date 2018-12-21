package com.cell.bilalcell.bilalcellmanagers

import android.app.Dialog
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.*
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_client.*
import kotlinx.android.synthetic.main.show_image.*
import maes.tech.intentanim.CustomIntent
import java.io.File
import java.io.FileOutputStream


class ProfileClient : AppCompatActivity() {

    private var db = FirebaseFirestore.getInstance()
    private var Docu = db.collection("Users")
    private var MyList = ArrayList<products>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_client)


        val id = intent.extras.getString("ID")

        val name = intent.extras.getString("Name")
        val number = intent.extras.getString("Number")

        tv_name.text = name
        tv_number.text = number

        Picasso.get().load(R.drawable.loading_profile).into(profile_info_image)


        val user_info = Docu.document("USER_$id")
        user_info.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val address = it.result!!.get("Address")
                val Desc = it.result!!.get("Description")
                tv_address.text = address.toString()
                tv_disc.text = Desc.toString()

            } else {
                Toast.makeText(this, "ERROR ${it.exception}", Toast.LENGTH_LONG).show()
            }
        }

        var img: Uri? = null
        Docu.document("USER_$id").get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        img = Uri.parse(it.result!!.get("img_url").toString().trim())
                        Picasso.get().load(img).into(test2, object : Callback {
                            override fun onSuccess() {
                                Picasso.get().load(img).into(profile_info_image)
                            }

                            override fun onError(e: java.lang.Exception?) {
                                Picasso.get().load(R.drawable.loading_profile).into(profile_info_image)
                            }

                        })
                    } else {
                        Toast.makeText(this, "ERROR ${it.exception!!.message}", Toast.LENGTH_LONG).show()
                    }
                }

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

                        recy_pro.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
                        val adapter = AdapterOfProducts(this, MyList, id, name)
                        recy_pro.adapter = adapter
                        if (!MyList.isEmpty()) {
                            recy_pro.visibility = View.VISIBLE
                        }
                    }

                }
                .addOnFailureListener {
                    Toast.makeText(this, "ERROR : ${it.message}", Toast.LENGTH_LONG).show()
                }


        img_caling.setOnClickListener {
            val call = Uri.parse("tel:$number")
            val surf = Intent(Intent.ACTION_DIAL, call)
            startActivity(surf)
        }

        img_sms.setOnClickListener {
            val sms_uri = Uri.parse("smsto:$number")
            val sms_intent = Intent(Intent.ACTION_SENDTO, sms_uri)
            startActivity(sms_intent)
        }

        tv_name.setOnClickListener {
            val mIntent = intent
            finish()
            startActivity(mIntent)

        }

        take_pic.setOnClickListener {
            saveImage(TakeNow(lin3), id)
        }

        profile_info_image.setOnClickListener {
            dialogImageShow(img)

        }

        addNew.setOnClickListener {
            try {
                val dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.daialog_addnew)

                val addPayment = dialog.findViewById<ImageView>(R.id.img_camera)
                val addProduct = dialog.findViewById<ImageView>(R.id.img_gallery)

                addPayment.setOnClickListener {
                    dialog.dismiss()
                    startActivity(Intent(this@ProfileClient, ProById::class.java)
                            .putExtra("ID", id)
                            .putExtra("nameOfUser", name))
                    CustomIntent.customType(this, "up-to-bottom")

                }

                addProduct.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        dialog.dismiss()
                        startActivity(Intent(this@ProfileClient, CreateProduct::class.java)
                                .putExtra("ID", id))

                        CustomIntent.customType(this@ProfileClient, "fadein-to-fadeout")
                    }

                })
                dialog.show()
            } catch (E: Exception) {
                Toast.makeText(this, E.message, Toast.LENGTH_LONG).show()
            }


        }

        menuOptions.setOnClickListener { it ->
            val menuPop = PopupMenu(this, menuOptions)
            menuPop.inflate(R.menu.user_menu)
            menuPop.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    when (item!!.itemId) {
                        R.id.edit_user -> {
                            Toast.makeText(this@ProfileClient, "Edit", Toast.LENGTH_LONG).show()
                        }
                        R.id.delete_user -> {
                            SweetAlert().sweetAlertConf(this@ProfileClient, "Are you sure?", "Are you sure you want to delete this client?",
                                    SweetAlertDialog.WARNING_TYPE,
                                    "Yes", "No").setConfirmButton("Yes") { it2 ->
                                it2.dismiss()
                                Docu.document("USER_$id")
                                        .delete()
                                        .addOnSuccessListener { it1 ->
                                            SweetAlert().sweetAlertDialog(this@ProfileClient, "Deleted",
                                                    "The Client Successfully Deleted!",
                                                    SweetAlertDialog.SUCCESS_TYPE,
                                                    "Done").setConfirmButton("Done") {
                                                it.dismiss()
                                                startActivity(Intent(this@ProfileClient, AccountsList::class.java))
                                                finish()
                                            }.show()
                                        }

                                        .addOnFailureListener { it ->
                                            SweetAlert().sweetAlertDialog(this@ProfileClient, "Not Deleted",
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
                        R.id.share_info -> {
                            Toast.makeText(this@ProfileClient, "Share", Toast.LENGTH_LONG).show()
                        }
                    }

                    return false
                }

            })
            menuPop.show()
        }

    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, "bottom-to-up")
    }

    fun dialogImageShow(image: Uri?) {
        try {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.show_image)

            val imageShow = dialog.findViewById<ImageView>(R.id.image_show)

            Picasso.get().load(image!!).into(imageShow)

            dialog.show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error Catch ${e.message}", Toast.LENGTH_LONG).show()
        }

    }

    fun createBitScreen(view: View): Bitmap {
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bit = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        return bit
    }

    fun TakeNow(v: View): Bitmap {
        return createBitScreen(v)
    }

    fun saveImage(bmp: Bitmap, id: String) {
        try {
            val file_path2 = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)}/CellManager"
            val dir = File(file_path2)
            if (!dir.exists())
                dir.mkdirs()
            val file = File(dir, "USER_$id.jpeg")
            val fOut = FileOutputStream(file)

            bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
            MediaStore.Images.Media.insertImage(contentResolver, bmp, "USER_$id.jpeg", "Cell manager Clients");
            fOut.flush()
            fOut.close()
            SweetAlert().sweetAlertDialog(this, "Success saved",
                    "Image is successfully saved in:\n $dir",
                    SweetAlertDialog.SUCCESS_TYPE,
                    "Done").setConfirmButton("Done") {
                it.dismiss()

            }.show()
            Toast.makeText(this, "Success saved in $dir", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            SweetAlert().sweetAlertDialog(this, "Not Saved!",
                    "Image Not Saved Because:\n${e.message}",
                    SweetAlertDialog.SUCCESS_TYPE,
                    "Done").setConfirmButton("Done") {
                it.dismiss()
            }.show()
        }


    }

    inner class AdapterOfProducts(var conx: Context, var list: ArrayList<products>, var idUser: String, var name_of_user: String) : RecyclerView.Adapter<AdapterOfProducts.MyHolderPro>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolderPro {
            val myView = LayoutInflater.from(conx).inflate(R.layout.card_list_products, parent, false)
            return MyHolderPro(myView)
        }

        override fun onBindViewHolder(holder: MyHolderPro, position: Int) {
            val myL = list[position]
            holder.NamePro.text = myL.ProductName
            holder.ComName.text = myL.CompanyName
            holder.Date.text = myL.Date

            holder.lay.setOnClickListener {
                val pos = position + 1
                Toast.makeText(this@ProfileClient, "Y $pos", Toast.LENGTH_LONG).show()
                val nameOfUser = name_of_user

                conx.startActivity(Intent(conx, ProductInfo::class.java)
                        .putExtra("prod_name", myL.ProductName)
                        .putExtra("com_name", myL.CompanyName)
                        .putExtra("prod_date", myL.Date)
                        .putExtra("IdUser", idUser)
                        .putExtra("IdProd", pos)
                        .putExtra("NameUser", nameOfUser))
                CustomIntent.customType(conx, "up-to-bottom")
            }

        }


        override fun getItemCount(): Int {
            return list.size
        }


        inner class MyHolderPro(view: View) : RecyclerView.ViewHolder(view) {
            var NamePro = view.findViewById<TextView>(R.id.tv_name_product)
            var ComName = view.findViewById<TextView>(R.id.tv_company_name)
            var Date = view.findViewById<TextView>(R.id.tv_date_product)
            var lay = view.findViewById<ConstraintLayout>(R.id.lay_product)
        }

    }


}