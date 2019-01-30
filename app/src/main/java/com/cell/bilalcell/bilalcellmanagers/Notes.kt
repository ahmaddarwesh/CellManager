package com.cell.bilalcell.bilalcellmanagers

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_notes.*
import java.text.SimpleDateFormat
import java.util.*
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent

class Notes : AppCompatActivity() {

    private var db = FirebaseFirestore.getInstance()
    private var NoteD = db.collection("Notes")
    private var realTime = FirebaseDatabase.getInstance()
    private var Count = realTime.getReference("CountNotes")

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        val ID = intent.extras.getInt("ID")

        if (ID == 0) {
            cons_add.visibility = View.VISIBLE
            cons_view.visibility = View.GONE

            Count.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val count = p0.value.toString()
                    Cookies().SaveInt(this@Notes, "IDNotes", count.toInt())
                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })

            const_focus.setOnClickListener {
                content.setFocusable(true);
                content.setFocusableInTouchMode(true);
                content.requestFocus()
            }

            btn_done.setOnClickListener {

                val id = Cookies().getInt(this, "IDNotes")

                val date = DateNow()
                val time = TimeNow()

                val myData = HashMap<String, Any>()
                myData.put("TITLE", Title.text.toString())
                myData.put("CONTENT", content.text.toString())
                myData.put("DATE", date)
                myData.put("TIME", time)
                myData.put("ID", id)

                db.collection("Notes").document("note$id").set(myData)
                        .addOnCompleteListener {
                            SweetAlert().sweetAlertDialog(this, "Done", "Successfully added", SweetAlertDialog.SUCCESS_TYPE, "OK")
                                    .setConfirmButton("OK") {
                                        it.dismiss()
                                        Cookies().SaveInt(this, "ID", id + 1)
                                        Count.setValue(id + 1)
                                        finish()
                                    }.show()
                        }.addOnFailureListener {
                            SweetAlert().sweetAlertDialog(this, "Error", "Error ${it.message}", SweetAlertDialog.ERROR_TYPE, "OK")
                                    .setConfirmButton("OK") {
                                        it.dismiss()
                                    }.show()
                        }

            }

        } else {
            cons_add.visibility = View.GONE
            cons_view.visibility = View.GONE
            progresLoad.visibility = View.VISIBLE
            val idNote = intent.extras.getString("idNote")

            imgDelete.setOnClickListener {
                SweetAlert().sweetAlertConf(this, "Are you sure?", "Are you sure to delete this note from system?",
                        SweetAlertDialog.WARNING_TYPE,
                        "YES",
                        "NO")
                        .setConfirmButton("YES") {
                            it.dismiss()
                            NoteD.document("note$idNote").delete()
                                    .addOnCompleteListener {
                                        SweetAlert().sweetAlertDialog(this, "Deleted", "The note was successfully deleted",
                                                SweetAlertDialog.SUCCESS_TYPE, "OK")
                                                .setConfirmButton("OK") {
                                                    it.dismiss()
                                                    finish()
                                                }.show()
                                    }.addOnFailureListener {
                                        SweetAlert().sweetAlertDialog(this, "Error", "The Note was not deleted",
                                                SweetAlertDialog.ERROR_TYPE, "OK")
                                                .setConfirmButton("OK") {
                                                    it.dismiss()
                                                }.show()
                                    }
                        }.setCancelButton("No") {
                            it.dismiss()
                        }.show()

            }

            imgCopy.setOnClickListener {
                val all = "Title: ${textTitle.text} \n Date: ${textDate.text} \n ${textContent.text}"
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("label", all)
                clipboard!!.primaryClip = clip
                Toast.makeText(this, "Text Copied", Toast.LENGTH_LONG).show()
            }

            imgShare.setOnClickListener {
                val all = "Title: ${textTitle.text} \n Date: ${textDate.text} \n ${textContent.text}"
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Notes")
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, all)
                startActivity(Intent.createChooser(sharingIntent, "Share using "))
            }



            NoteD.document("note$idNote").get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val title = it.result!!.get("TITLE")
                    val content = it.result!!.get("CONTENT")
                    val date = it.result!!.get("DATE")
                    val time = it.result!!.get("TIME")
                    textTitle.text = title.toString()
                    textContent.text = content.toString()
                    textDate.text = "$date | $time"
                    cons_view.visibility = View.VISIBLE
                    progresLoad.visibility = View.GONE
                } else {
                    cons_view.visibility = View.GONE
                    progresLoad.visibility = View.GONE
                    Toast.makeText(this, "Error ${it.result.toString()}", Toast.LENGTH_LONG).show()
                }
            }

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
        val formatter = SimpleDateFormat("hh:mm:ss")
        val date = Date()
        return formatter.format(date)
    }

}
