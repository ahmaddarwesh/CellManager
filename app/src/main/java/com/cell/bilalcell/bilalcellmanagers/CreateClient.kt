package com.cell.bilalcell.bilalcellmanagers

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Window
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_create_client.*
import cn.pedant.SweetAlert.SweetAlertDialog
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore

import java.util.*

import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.support.design.widget.Snackbar
import android.view.View
import com.google.firebase.storage.FirebaseStorage

import com.google.firebase.storage.StorageReference
import maes.tech.intentanim.CustomIntent


class CreateClient : AppCompatActivity() {

    private var requst = 15
    private var requstGallery = 1
    private var realTime = FirebaseDatabase.getInstance()
    private var Count = realTime.getReference("Count")
    private var urlImage: Uri? = null
    private var db = FirebaseFirestore.getInstance()
    private var Users = db.collection("Users")
    private var mStorageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_client)

        if(!isOnline()){
            val sandbar = Snackbar
                    .make(con_cc, "Check your internet connection", Snackbar.LENGTH_LONG)
            sandbar.view.setBackgroundColor(Color.RED)
            sandbar.duration = 1500
            sandbar.show()
        }


        Count.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val count = p0.value.toString()
                Cookies().SaveInt(this@CreateClient, "ID", count.toInt())
            }

            override fun onCancelled(p0: DatabaseError) {
                SweetAlert().sweetAlertDialog(this@CreateClient,"Error",
                        p0.message,
                        SweetAlertDialog.ERROR_TYPE,
                        "Ok").setConfirmButton("Ok") {
                    it.dismiss()
                }.show()
            }

        })

        profile_info_image.setOnClickListener {
             showDialog(this)
        }



        imgSuccess.setOnClickListener { it ->
             if (edt_fullname.text.trim().isEmpty() || edt_phonenumber.text.trim().isEmpty()
                    || edt_address.text.trim().isEmpty() || desc.text.trim().isEmpty()) {
                SweetAlert().sweetAlertDialog(this,"Field Required!",
                        "You cannot leave field empty.\nCompletion info to continue",
                        SweetAlertDialog.ERROR_TYPE,
                        "Ok").setConfirmButton("Ok") {
                    it.dismiss()
                }.show()
            } else {
                progressBarLoading.visibility = View.VISIBLE
                imgSuccess.visibility = View.GONE


                val id: Int = Cookies().getInt(this, "ID")
                val name = edt_fullname.text.toString()
                val phone = edt_phonenumber.text.toString()
                val address = edt_address.text.toString()
                val desc = desc.text.toString()
                val User = HashMap<String, Any>()
                User.put("ID", id)
                User.put("Name", name)
                User.put("PhoneNumber", phone)
                User.put("Address", address)
                User.put("Description", desc)
                User.put("CountProduct", 0)
                User.put("img_url", urlImage.toString())

                if (!isOnline()) {
                    progressBarLoading.visibility = View.GONE
                    imgSuccess.visibility = View.VISIBLE
                    SweetAlert().sweetAlertDialog(this,"Error",
                            "Check Your Internet Connection!",
                            SweetAlertDialog.ERROR_TYPE,
                            "Ok").setConfirmButton("Ok") {
                        it.dismiss()
                    }.show()
                } else {
                    Users.document("USER_$id").set(User)
                            .addOnSuccessListener { it0 ->
                                uploadImage()
                                progressBarLoading.visibility = View.GONE
                                imgSuccess.visibility = View.VISIBLE
                                val id1 = id + 1
                                Cookies().SaveInt(this, "ID", id1)
                                Count.setValue(id1)

                                SweetAlert().sweetAlertDialog(this,"Success",
                                        "Successfully added client",
                                        SweetAlertDialog.SUCCESS_TYPE,
                                        "Done").setConfirmButton("Done") {

                                    startActivity(Intent(this, ProfileClient::class.java)
                                            .putExtra("ID", id.toString())
                                            .putExtra("Name", name).putExtra("Number", phone))
                                    finish()
                                }.show()

                            }
                            .addOnFailureListener { it1 ->
                                SweetAlert().sweetAlertDialog(this,"Error",
                                        "${it1.message}",
                                        SweetAlertDialog.ERROR_TYPE,
                                        "Ok").setConfirmButton("Ok") {
                                    it.dismiss()
                                }.show()
                                progressBarLoading.visibility = View.GONE
                                imgSuccess.visibility = View.VISIBLE

                            }
                }
            }


        }

    }



    fun showDialog(activity: Activity) {
        try {
            val dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.choose_image)

            val imgCam = dialog.findViewById<ImageView>(R.id.img_camera)
            val imgGall = dialog.findViewById<ImageView>(R.id.img_gallery)

            imgCam.setOnClickListener {
                val camera = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                dialog.dismiss()
                startActivityForResult(camera, requst)

            }
            imgGall.setOnClickListener {
                val photoPickerIntent = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//                photoPickerIntent.type = "image/*"
                dialog.dismiss()
                startActivityForResult(photoPickerIntent, requstGallery)

            }
            dialog.show()
        } catch (E: Exception) {
            SweetAlert().sweetAlertDialog(this,"Error",
                    "${E.message}",
                    SweetAlertDialog.ERROR_TYPE,
                    "Ok").setConfirmButton("Ok") {
                it.dismiss()
            }.show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        try {
            if (resultCode != Activity.RESULT_CANCELED) {
                if (requestCode == requst) {

                    val bit: Bitmap = data!!.extras.get("data") as Bitmap
                    val selectedImage = MediaStore.Images.Media.insertImage(contentResolver, bit, "any", null);
                    urlImage = Uri.parse(selectedImage)
                    profile_info_image.setImageBitmap(bit)


                } else if (requestCode == requstGallery) {

                    val selectedImage = data!!.data
                    urlImage = selectedImage
                    profile_info_image.setImageURI(selectedImage)


                } else {
                    Toast.makeText(this, "From else", Toast.LENGTH_LONG).show()
                    val selectedImage = data!!.data
                    urlImage = selectedImage
                }
            }else{
                SweetAlert().sweetAlertDialog(this,"No Selected!",
                        "No image Selected Please Select to continue!",
                        SweetAlertDialog.ERROR_TYPE,
                        "Ok").setConfirmButton("Ok") {
                    it.dismiss()
                }.show()
            }


        } catch (e: Exception) {
            SweetAlert().sweetAlertDialog(this,"Error",
                    "No image Selected ${e.message}",
                    SweetAlertDialog.ERROR_TYPE,
                    "Ok").setConfirmButton("Ok") {
                it.dismiss()
            }.show()
        }

    }

    fun uploadImage() {
        try {
            mStorageRef = FirebaseStorage.getInstance().reference

            val file = urlImage
            val id = Cookies().getInt(this, "ID")
            val ref = mStorageRef!!.child("profile_img/$id")

            val uploadTask = ref.putFile(file!!)

            uploadTask.continueWithTask { task ->

                if (!task.isSuccessful) {
                    throw task.exception!!
                } else {
                    ref.downloadUrl
                }

            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result!!
                    Users.document("USER_$id").update("img_url", downloadUri.toString())
                } else {
                    SweetAlert().sweetAlertDialog(this,"Error",
                            "Image not Uploaded",
                            SweetAlertDialog.ERROR_TYPE,
                            "Ok").setConfirmButton("Ok") {
                        it.dismiss()
                    }.show()
                }
            }

        } catch (E: Exception) {
            SweetAlert().sweetAlertDialog(this,"Error",
                    "${E.message}",
                    SweetAlertDialog.ERROR_TYPE,
                    "Ok").setConfirmButton("Ok") {
                it.dismiss()
            }.show()
        }


    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, "fadein-to-fadeout")
    }

    override fun onBackPressed() {
        if (edt_fullname.text.trim().isEmpty() && edt_phonenumber.text.trim().isEmpty()
                && edt_address.text.trim().isEmpty() && desc.text.trim().isEmpty()) {
            finish()
        } else {
            SweetAlert().sweetAlertConf(this,"Discard info!", "Are you sure discard information you typed?",
                    SweetAlertDialog.WARNING_TYPE, "Cancel", "Discard").setConfirmButton("Cancel") {
                it.dismiss()
            }.setCancelButton("Discard") {
                finish()
            }.show()
        }

    }

    fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}
